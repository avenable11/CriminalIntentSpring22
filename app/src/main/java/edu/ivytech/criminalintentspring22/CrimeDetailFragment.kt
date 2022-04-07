package edu.ivytech.criminalintentspring22

import android.Manifest
import android.content.ActivityNotFoundException
import android.content.ClipData
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.text.Editable
import android.text.TextWatcher
import android.text.format.DateFormat
import android.util.Log
import android.view.DragEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.datepicker.MaterialDatePicker
import edu.ivytech.criminalintentspring22.database.Crime
import edu.ivytech.criminalintentspring22.databinding.FragmentCrimeDetailBinding
import java.io.FileOutputStream
import java.util.*

class CrimeDetailFragment : Fragment() {
    private var _binding: FragmentCrimeDetailBinding? = null
    private val binding get() = _binding!!
    private var item : Crime? = null
    private var isNew : Boolean = true
    private val crimeDetailVM : CrimeDetailViewModel by viewModels()
    private val getSuspect = registerForActivityResult(ActivityResultContracts.PickContact()) {
        uri:Uri? ->
        val queryField = arrayOf(ContactsContract.Contacts.DISPLAY_NAME)
        val cursor = uri?.let{
            uri ->
            requireActivity().contentResolver.query(uri,queryField, null, null, null)
        }
        cursor?.use{
            result->
            if(result.count == 0){
                return@registerForActivityResult
            }
            result.moveToFirst()
            val suspect = result.getString(0)
            item?.suspect = suspect
            crimeDetailVM.saveCrime(item!!)
            binding.crimeSuspectBtn?.text = suspect
        }
    }
    private val permission = registerForActivityResult(ActivityResultContracts.RequestPermission()){
        granted->
        when {
            granted -> {
                //open the camera
                camera.launch()
            }
            else -> {
                Log.e("Crime Detail", "Camera Permission not granted")
                binding.cameraBtn?.isEnabled = false
            }

        }
    }
    private val camera = registerForActivityResult(ActivityResultContracts.TakePicturePreview()) {
        bitmap ->
        binding.crimeImage?.setImageBitmap(bitmap)
        val photoFile = crimeDetailVM.getPhotoFile(item!!)
        val fileOutputStream:FileOutputStream = FileOutputStream(photoFile)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream)

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let{
            if(it.containsKey(ARG_ITEM_ID)) {
                val crimeId = it.getSerializable(ARG_ITEM_ID) as UUID
                crimeDetailVM.loadCrime(crimeId)
            }
        }
    }
    private val dragListener = View.OnDragListener { v, event ->
        if(event.action == DragEvent.ACTION_DROP) {
            val clipDataItem:ClipData.Item = event.clipData.getItemAt(0)
            val dragData = clipDataItem.text
            val crimeId = UUID.fromString(dragData.toString())
            crimeDetailVM.loadCrime(crimeId)

        }
        true
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCrimeDetailBinding.inflate(inflater, container, false)
        updateUI()
        binding.root.setOnDragListener(dragListener)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        crimeDetailVM.crimeLiveData.observe(viewLifecycleOwner) {
            crime -> item = crime
            updateUI()
        }
    }
    override fun onStart() {
        super.onStart()
        binding.titleEditText.addTextChangedListener(object:TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                item?.title = s.toString()
            }
            override fun afterTextChanged(s: Editable?) {}
        })
        binding.solvedSwitch.setOnCheckedChangeListener { buttonView, isChecked -> item?.isSolved = isChecked  }
        binding.dateButton.setOnClickListener {
            val datePicker: MaterialDatePicker<Long> = MaterialDatePicker
                .Builder
                .datePicker()
                .setTitleText(R.string.date_picker_title)
                .setSelection(item?.date?.time)
                .build()
            datePicker.show(requireActivity().supportFragmentManager, "DATE_PICKER")
            datePicker.addOnPositiveButtonClickListener {
                cDate ->
                    item!!.date = Date(cDate)
                    binding.dateButton.text = DateFormat.format("EEEE, MMM dd, yyyy", item?.date)
            }
        }
        binding.shareBtn?.setOnClickListener {
            val intent = Intent(Intent.ACTION_SEND)
            intent.type = "text/plain"
            intent.putExtra(Intent.EXTRA_TEXT, getCrimeReport())
            intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.crime_report_subject))
            val chooserIntent = Intent.createChooser(intent, getString(R.string.send_report))
            startActivity(chooserIntent)
        }
        binding.crimeSuspectBtn?.setOnClickListener {
            try {
                getSuspect.launch()
            } catch(e: ActivityNotFoundException) {
                Log.e("Crime Detail", "Could not get contact", e)
                binding.crimeSuspectBtn?.isEnabled = false
            }
        }
        binding.cameraBtn?.setOnClickListener{
            permission.launch(Manifest.permission.CAMERA)
        }
    }

    override fun onStop() {
        super.onStop()
        if(isNew && !binding.titleEditText.text.isNullOrEmpty()) {
            crimeDetailVM.addCrime(item!!)
        } else if(!isNew) {
            crimeDetailVM.saveCrime(item!!)
        }

    }

    private fun updateUI()
    {
        if(item != null){
            binding.toolbarLayout?.title = "Edit Crime"
            binding.titleEditText.setText(item?.title)
            binding.solvedSwitch.isChecked = item?.isSolved == true
            if(item!!.suspect.isNotBlank()) {
                binding.crimeSuspectBtn?.text = item?.suspect
            }
            val photoFile = crimeDetailVM.getPhotoFile(item!!)
            if(photoFile.isFile) {
                val bitmap : Bitmap = BitmapFactory.decodeFile(photoFile.path)
                binding.crimeImage?.setImageBitmap(bitmap)
            }

            isNew = false
            if(!item!!.canEdit)
            {
                binding.titleEditText.isEnabled = false
                binding.solvedSwitch.isEnabled = false
                binding.dateButton.isEnabled = false
                binding.crimeSuspectBtn?.isEnabled= false
                binding.cameraBtn?.isEnabled= false
            }
        } else {
            binding.toolbarLayout?.title = "New Crime"
            item = Crime()

        }
        binding.dateButton.text = DateFormat.format("EEEE, MMM dd, yyyy",item?.date)

    }

    private fun getCrimeReport():String{
        val solvedString = if(item!!.isSolved) {
            getString(R.string.crime_report_solved)
        } else {
            getString(R.string.crime_report_unsolved)
        }
        val suspectString = if(item!!.suspect.isBlank()) {
            getString(R.string.crime_report_no_suspect)
        } else {
            getString(R.string.crime_report_suspect,item!!.suspect)
        }
        val dateString = DateFormat.format("EEEE, MMM dd, yyyy", item!!.date).toString()
        return getString(R.string.crime_report, item!!.title, dateString, solvedString, suspectString)
    }
    companion object {
        const val ARG_ITEM_ID = "item_id"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
package edu.ivytech.criminalintentspring22

import android.content.ClipData
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.text.format.DateFormat
import android.view.DragEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.datepicker.MaterialDatePicker
import edu.ivytech.criminalintentspring22.database.Crime
import edu.ivytech.criminalintentspring22.databinding.FragmentCrimeDetailBinding
import java.util.*

class CrimeDetailFragment : Fragment() {
    private var _binding: FragmentCrimeDetailBinding? = null
    private val binding get() = _binding!!
    private var item : Crime? = null
    private var isNew : Boolean = true
    private val crimeDetailVM : CrimeDetailViewModel by viewModels()
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
            isNew = false
            if(!item!!.canEdit)
            {
                binding.titleEditText.isEnabled = false
                binding.solvedSwitch.isEnabled = false
                binding.dateButton.isEnabled = false
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
        val dateString = DateFormat.format("EEEE, MMM dd, yyyy", item!!.date).toString()
        return getString(R.string.crime_report, item!!.title, dateString, solvedString, getString(R.string.crime_report_no_suspect))
    }
    companion object {
        const val ARG_ITEM_ID = "item_id"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
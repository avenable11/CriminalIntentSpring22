package edu.ivytech.criminalintentspring22

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import edu.ivytech.criminalintentspring22.databinding.FragmentCrimeDetailBinding
import java.util.*

class CrimeDetailFragment : Fragment() {
    private var _binding: FragmentCrimeDetailBinding? = null
    private val binding get() = _binding!!
    private var item : Crime? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let{
            if(it.containsKey(ARG_ITEM_ID)) {
                item = CrimeList.ITEM_MAP[it.getSerializable(ARG_ITEM_ID) as UUID]
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCrimeDetailBinding.inflate(inflater, container, false)
        updateUI()

        return binding.root
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
    }

    private fun updateUI()
    {
        if(item != null){
            binding.toolbarLayout.title = "Edit Crime"
            binding.titleEditText.setText(item?.title)
            binding.solvedSwitch.isChecked = item?.isSolved == true
        } else {
            binding.toolbarLayout.title = "New Crime"
            item = Crime()
            CrimeList.ITEM_MAP[item!!.id] = item!!
            CrimeList.ITEMS.add(item!!)
        }
        binding.dateButton.text = DateFormat.format("EEEE, MMM dd, yyyy",item?.date)

    }
    companion object {
        const val ARG_ITEM_ID = "item_id"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
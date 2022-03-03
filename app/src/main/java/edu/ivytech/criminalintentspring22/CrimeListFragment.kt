package edu.ivytech.criminalintentspring22

import android.os.Bundle
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import edu.ivytech.criminalintentspring22.databinding.FragmentCrimeListBinding
import edu.ivytech.criminalintentspring22.databinding.ListItemCrimeBinding

class CrimeListFragment : Fragment() {
    private var _binding: FragmentCrimeListBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCrimeListBinding.inflate(inflater, container, false)
        binding.crimeList.layoutManager = LinearLayoutManager(context)
        return binding.root
    }

    private inner class CrimeHolder (val itemBinding:ListItemCrimeBinding) :
        RecyclerView.ViewHolder(itemBinding.root), View.OnClickListener {
            private lateinit var crime : Crime
            init {
                itemBinding.root.setOnClickListener(this)
            }
            fun bind(crime: Crime) {
                this.crime = crime
                itemBinding.crimeTitleList.text = crime.title
                itemBinding.crimeDateList.text = DateFormat.format("EEEE, MMM dd, yyyy", crime.date)
                itemBinding.solvedImageList.visibility = if(crime.isSolved) View.VISIBLE else View.INVISIBLE
            }

        override fun onClick(v: View?) {
            Toast.makeText(context, "${crime.title} pressed", Toast.LENGTH_SHORT).show()
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
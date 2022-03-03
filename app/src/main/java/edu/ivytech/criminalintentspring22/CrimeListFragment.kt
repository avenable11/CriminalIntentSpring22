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
    private var adapter : CrimeAdapter? = CrimeAdapter(emptyList())

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCrimeListBinding.inflate(inflater, container, false)
        binding.crimeList.layoutManager = LinearLayoutManager(context)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        adapter = CrimeAdapter(CrimeList.ITEMS)
        binding.crimeList.adapter = adapter
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
    private inner class CrimeAdapter(var crimes : List<Crime>) : RecyclerView.Adapter<CrimeHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CrimeHolder {
            val view = ListItemCrimeBinding.inflate(layoutInflater, parent, false)
            return CrimeHolder(view)
        }

        override fun onBindViewHolder(holder: CrimeHolder, position: Int) {
            val crime = crimes[position]
            holder.bind(crime)
        }

        override fun getItemCount(): Int {
            return crimes.size
        }

    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
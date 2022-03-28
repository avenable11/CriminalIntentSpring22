package edu.ivytech.criminalintentspring22

import android.content.ClipData
import android.content.ClipDescription
import android.os.Build
import android.os.Bundle
import android.text.format.DateFormat
import android.view.ContextMenu
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import edu.ivytech.criminalintentspring22.database.Crime
import edu.ivytech.criminalintentspring22.databinding.FragmentCrimeListBinding
import edu.ivytech.criminalintentspring22.databinding.ListItemCrimeBinding

class CrimeListFragment : Fragment() {
    private var _binding: FragmentCrimeListBinding? = null
    private val binding get() = _binding!!
    private var adapter : CrimeAdapter? = CrimeAdapter(emptyList())

    private val crimeListVM : CrimeListViewModel by lazy {
        ViewModelProvider(requireActivity()).get(CrimeListViewModel::class.java)
    }

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
        binding.addCrimeBtn?.setOnClickListener {
            if(binding.itemDetailNavContainer != null){
                val bundle = Bundle()
                bundle.putString("title", "New Crime")
                view.findNavController().navigate(R.id.action_List_to_Detail, bundle)
            } else {
                view.findNavController().navigate(R.id.action_List_to_Detail)
            }
        }
        crimeListVM.crimesLiveData.observe(viewLifecycleOwner) {
            crimes -> setupRecyclerView(crimes)
        }
    }

    private fun setupRecyclerView(crimes : List<Crime>) {
        adapter = CrimeAdapter(crimes)
        binding.crimeList.adapter = adapter
    }

    private inner class CrimeHolder (val itemBinding:ListItemCrimeBinding) :
        RecyclerView.ViewHolder(itemBinding.root), View.OnClickListener, View.OnCreateContextMenuListener {
            private lateinit var crime : Crime
            init {
                itemBinding.root.setOnClickListener(this)
                if(binding.itemDetailNavContainer != null) {
                    itemBinding.root.setOnLongClickListener { v ->
                        val clipItem = ClipData.Item(crime.id.toString())
                        val dragData = ClipData(
                            v.tag as? CharSequence,
                            arrayOf(ClipDescription.MIMETYPE_TEXT_PLAIN),
                            clipItem
                        )
                        if (Build.VERSION.SDK_INT >= 24) {
                            v.startDragAndDrop(dragData, View.DragShadowBuilder(v), null, 0)
                        } else {
                            v.startDrag(dragData, View.DragShadowBuilder(v), null, 0)
                        }
                    }
                } else {
                    itemBinding.root.setOnCreateContextMenuListener(this)
                }
            }
            fun bind(crime: Crime) {
                this.crime = crime
                itemBinding.crimeTitleList.text = crime.title
                itemBinding.crimeDateList.text = DateFormat.format("EEEE, MMM dd, yyyy", crime.date)
                itemBinding.solvedImageList.visibility = if(crime.isSolved) View.VISIBLE else View.INVISIBLE
            }

        override fun onClick(v: View?) {
            Toast.makeText(context, "${crime.title} pressed", Toast.LENGTH_SHORT).show()
            val bundle = Bundle()
            bundle.putSerializable(CrimeDetailFragment.ARG_ITEM_ID, crime.id)
            if(binding.itemDetailNavContainer != null){
                binding.itemDetailNavContainer!!.findNavController().navigate(R.id.crimeDetailFragment2, bundle)
            } else {
                itemView.findNavController().navigate(R.id.action_List_to_Detail, bundle)
            }
        }

        override fun onCreateContextMenu(
            menu: ContextMenu?,
            v: View?,
            menuInfo: ContextMenu.ContextMenuInfo?
        ) {
            menu?.add("Context Menu Item")?.setOnMenuItemClickListener {
                Toast.makeText(context, "Clicked Context Menu Item ${crime.title}", Toast.LENGTH_SHORT).show()
                true
            }
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
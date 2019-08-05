package edu.rosehulman.liningpan.arbiostruct.detailprotein

import android.content.Context
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import edu.rosehulman.liningpan.arbiostruct.Protein

import edu.rosehulman.liningpan.arbiostruct.R


private const val ARG_PROTEIN = "arg_protein"

class ProteinDetailSequenceFragment : Fragment() {
    private var protein: Protein? = null
    private lateinit var adapter:SequenceChainListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            protein = it.getParcelable(ARG_PROTEIN)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val recyclerView = inflater.inflate(R.layout.fragment_protein_detail_sequence, container, false) as RecyclerView
        adapter = SequenceChainListAdapter(context!!, protein!!)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(context)
        return recyclerView
    }

    companion object {

        @JvmStatic
        fun newInstance(protein: Protein) =
            ProteinDetailSequenceFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_PROTEIN, protein)
                }
            }
    }

}

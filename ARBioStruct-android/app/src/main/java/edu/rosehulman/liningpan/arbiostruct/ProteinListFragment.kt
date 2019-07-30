package edu.rosehulman.liningpan.arbiostruct

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_protein_list.*
import kotlinx.android.synthetic.main.fragment_protein_list.view.*


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [ProteinListFragment.OnProteinItemSelectedListener] interface
 * to handle interaction events.
 *
 */
class ProteinListFragment : Fragment() {
    private var listener: OnProteinItemSelectedListener? = null
    lateinit var adapter:ProteinListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_protein_list, container, false)
        adapter = ProteinListAdapter(context, listener)
        val recyclerView:RecyclerView = view.protein_recycler_view
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = adapter

        fab.setOnClickListener{
            adapter.addEditDialog()
        }
        return view
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnProteinItemSelectedListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnProteinItemSelectedListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson [Communicating with Other Fragments]
     * (http://developer.android.com/training/basics/fragments/communicating.html)
     * for more information.
     */
    interface OnProteinItemSelectedListener {

        fun onProteinItemSelected(protein:Protein)
    }

}

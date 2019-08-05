package edu.rosehulman.liningpan.arbiostruct

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_protein_list.view.*

class ProteinListFragment : Fragment() {
    private var listener: OnProteinItemSelectedListener? = null
    lateinit var adapter: ProteinListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_protein_list, container, false)
        adapter = ProteinListAdapter(context, listener, this)
        val recyclerView: RecyclerView = view.protein_recycler_view
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = adapter

        view.fab.setOnClickListener {
            startAddEditActivity(null)
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

    fun startAddEditActivity(protein: Protein?) {
        val addEditIntent = Intent(context, AddEditStructureActivity::class.java)
        if (protein == null) {
            addEditIntent.putExtra(Protein.IS_EDIT_MESSAGE, false)
            startActivityForResult(addEditIntent, ADD_PRO_INFO)
        } else {
            addEditIntent.putExtra(Protein.IS_EDIT_MESSAGE, true)
            protein.putIntent(addEditIntent)
            startActivityForResult(addEditIntent, EDIT_PRO_INFO)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == ADD_PRO_INFO && resultCode == Activity.RESULT_OK) {
            adapter.addProtein(Protein.fromIntent(data))
        } else if (requestCode == EDIT_PRO_INFO && resultCode == Activity.RESULT_OK) {
            adapter.editProtein(Protein.fromIntent(data))
        }

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

        fun onProteinItemSelected(protein: Protein)
    }

}

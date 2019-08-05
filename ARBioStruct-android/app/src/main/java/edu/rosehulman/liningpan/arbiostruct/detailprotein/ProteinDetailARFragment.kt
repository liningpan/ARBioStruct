package edu.rosehulman.liningpan.arbiostruct.detailprotein

import android.content.Context
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import edu.rosehulman.liningpan.arbiostruct.Protein
import edu.rosehulman.liningpan.arbiostruct.R


private const val ARG_PROTEIN = "arg_protein"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [ProteinDetailARFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [ProteinDetailARFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class ProteinDetailARFragment : Fragment() {

    private var protein: Protein? = null


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
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_protein_detail_ar, container, false)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ProteinDetailARFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(protein:Protein) =
            ProteinDetailARFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_PROTEIN, protein)
                }
            }
    }
}

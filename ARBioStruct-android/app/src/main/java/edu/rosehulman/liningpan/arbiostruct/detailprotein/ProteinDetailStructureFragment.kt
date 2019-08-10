package edu.rosehulman.liningpan.arbiostruct.detailprotein

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import edu.rosehulman.liningpan.arbiostruct.Protein
import edu.rosehulman.liningpan.arbiostruct.R
import kotlinx.android.synthetic.main.fragment_protein_detail_structure.view.*


private const val ARG_PROTEIN = "arg_protein"

class ProteinDetailStructureFragment : Fragment() {
    private var protein: Protein? = null
    private var listener: OnSwitchToARClickedListener? = null

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
        val view = inflater.inflate(R.layout.fragment_protein_detail_structure, container, false)
        val webView = view.webView
        webView.settings.javaScriptEnabled = true
        webView.settings.allowFileAccessFromFileURLs = true
        webView.settings.allowUniversalAccessFromFileURLs = true
        webView.loadUrl("file:///android_asset/jsmol/main.htm?dir=file://${context?.getExternalFilesDir(null)}/pdbfile&pdb=${protein?.pdbID}")

        view.ar_full_screen_button.setOnClickListener {
            listener?.onSwitchToARClicked()
        }
        return view
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnSwitchToARClickedListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnSwitchToARClickedListener")
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
    interface OnSwitchToARClickedListener {
        // TODO: Update argument type and name
        fun onSwitchToARClicked()
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ProteinDetailStructureFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(protein: Protein) =
            ProteinDetailStructureFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_PROTEIN, protein)
                }
            }
    }
}

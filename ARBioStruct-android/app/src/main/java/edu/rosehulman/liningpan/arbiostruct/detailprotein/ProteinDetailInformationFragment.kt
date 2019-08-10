package edu.rosehulman.liningpan.arbiostruct.detailprotein

import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import edu.rosehulman.liningpan.arbiostruct.Protein
import edu.rosehulman.liningpan.arbiostruct.R
import kotlinx.android.synthetic.main.fragment_protein_detail_information.view.*


private const val ARG_PROTEIN = "arg_protein"

class ProteinDetailInformationFragment : Fragment() {

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
        val view = inflater.inflate(R.layout.fragment_protein_detail_information, container, false)
        val infoString = ProteinInfoService.fetchPDBDescription(protein!!)
        val text = Html.fromHtml(infoString)
        view.protein_info_text_view.text = text
        return view
    }

    companion object {

        @JvmStatic
        fun newInstance(protein: Protein) =
            ProteinDetailInformationFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_PROTEIN, protein)
                }
            }
    }
}

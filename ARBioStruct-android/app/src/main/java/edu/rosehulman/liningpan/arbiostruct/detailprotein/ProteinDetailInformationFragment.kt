package edu.rosehulman.liningpan.arbiostruct.detailprotein

import android.content.Context
import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import edu.rosehulman.liningpan.arbiostruct.Protein
import edu.rosehulman.liningpan.arbiostruct.R
import kotlinx.android.synthetic.main.fragment_protein_detail_information.view.*
import kotlinx.android.synthetic.main.protein_info_card_view.view.*


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
        val view = inflater.inflate(R.layout.fragment_protein_detail_information, container, false) as RecyclerView
        view.layoutManager = LinearLayoutManager(context)
        view.adapter = InfoAdapter(context, ProteinInfoService.readPDBDescription(context!!, protein!!))
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

    class InfoAdapter(val context: Context?, val information: ArrayList<Map.Entry<String, String>>):RecyclerView.Adapter<InfoViewHolder>(){
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InfoViewHolder {
            val view = LayoutInflater.from(context).inflate(R.layout.protein_info_card_view, parent, false)
            return InfoViewHolder(view)
        }

        override fun getItemCount(): Int = information.size

        override fun onBindViewHolder(holder: InfoViewHolder, position: Int) {
            holder.bind(information[position].key, information[position].value)
        }

    }

    class InfoViewHolder(itemView:View):RecyclerView.ViewHolder(itemView){

        fun bind(infoName: String, infoValue:String){
            itemView.info_entry_name_text_view.text = infoName
            itemView.info_entry_value_text_view.text = infoValue
        }

    }
}

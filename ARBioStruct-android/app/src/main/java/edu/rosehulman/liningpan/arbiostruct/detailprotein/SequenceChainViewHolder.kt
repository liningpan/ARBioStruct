package edu.rosehulman.liningpan.arbiostruct.detailprotein

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.sequence_chain_card_view.view.*

class SequenceChainViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){

    fun bind(sequence:ProteinInfoService.Sequence){
        itemView.chain_name_text_view.text = sequence.chain
        itemView.chain_sequence_text_view.text = sequence.seq
    }
}
package edu.rosehulman.liningpan.arbiostruct

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.protein_card_view.view.*

class ProteinCardViewHolder(itemView : View, adapter: ProteinListAdapter) : RecyclerView.ViewHolder(itemView) {
    var nameTextView =  itemView.cardview_protein_name
    var descriptionTextView = itemView.cardview_protein_description
    init {
        itemView.setOnClickListener{
            adapter.listItemSelected(adapterPosition)
        }
        itemView.setOnLongClickListener {
            adapter.editItemSelected(adapterPosition)
            true
        }
    }

    fun bind(protein:Protein){
        nameTextView.text = protein.name
        descriptionTextView.text = protein.description
    }
}
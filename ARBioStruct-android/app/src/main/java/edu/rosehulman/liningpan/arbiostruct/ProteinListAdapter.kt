package edu.rosehulman.liningpan.arbiostruct

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*

class ProteinListAdapter(var context:Context?, var listener: ProteinListFragment.OnProteinItemSelectedListener?): RecyclerView.Adapter<ProteinCardViewHolder>() {
    var proteins = ArrayList<Protein>()
    var proteinRef = FirebaseFirestore
        .getInstance()
        .collection(Constant.proteins)
    var listenerRegistration: ListenerRegistration

    init {
        listenerRegistration = proteinRef
            .whereEqualTo(Protein.OWN_BY_USER, FirebaseAuth.getInstance().uid)
            .orderBy(Protein.LAST_TOUCHED_KEY, Query.Direction.ASCENDING)
            .addSnapshotListener { querySnapshot, exception ->
                if(exception != null){
                    Log.d(Constant.TAG, exception.toString())
                    return@addSnapshotListener
                } else {
                    processSnapshotChanges(querySnapshot!!)
                }

            }
    }
    private fun processSnapshotChanges(querySnapshot: QuerySnapshot){
        for(documentSnapshot in querySnapshot.documentChanges){
            val protein = Protein.fromSnapshot(documentSnapshot.document)
            when(documentSnapshot.type){
                DocumentChange.Type.ADDED -> {
                    proteins.add(0, protein)
                    notifyItemInserted(0)
                }
                DocumentChange.Type.MODIFIED -> {
                    val index = proteins.indexOfFirst { it.id == protein.id }
                    proteins[index] = protein
                    notifyItemChanged(index)
                }
                DocumentChange.Type.REMOVED -> {
                    val index = proteins.indexOfFirst { it.id == protein.id }
                    proteins.removeAt(index)
                    notifyItemRemoved(index)
                }
            }
        }
    }
    fun addEditDialog(pos:Int = -1){

    }
    /**
     * Called when RecyclerView needs a new [ViewHolder] of the given type to represent
     * an item.
     *
     *
     * This new ViewHolder should be constructed with a new View that can represent the items
     * of the given type. You can either create a new View manually or inflate it from an XML
     * layout file.
     *
     *
     * The new ViewHolder will be used to display items of the adapter using
     * [.onBindViewHolder]. Since it will be re-used to display
     * different items in the data set, it is a good idea to cache references to sub views of
     * the View to avoid unnecessary [View.findViewById] calls.
     *
     * @param parent The ViewGroup into which the new View will be added after it is bound to
     * an adapter position.
     * @param viewType The view type of the new View.
     *
     * @return A new ViewHolder that holds a View of the given view type.
     * @see .getItemViewType
     * @see .onBindViewHolder
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProteinCardViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.protein_card_view, parent, false)
        val viewHolder = ProteinCardViewHolder(view, this)
        return viewHolder
    }

    /**
     * Returns the total number of items in the data set held by the adapter.
     *
     * @return The total number of items in this adapter.
     */
    override fun getItemCount() = proteins.size

    /**
     * Called by RecyclerView to display the data at the specified position. This method should
     * update the contents of the [ViewHolder.itemView] to reflect the item at the given
     * position.
     *
     *
     * Note that unlike [android.widget.ListView], RecyclerView will not call this method
     * again if the position of the item changes in the data set unless the item itself is
     * invalidated or the new position cannot be determined. For this reason, you should only
     * use the `position` parameter while acquiring the related data item inside
     * this method and should not keep a copy of it. If you need the position of an item later
     * on (e.g. in a click listener), use [ViewHolder.getAdapterPosition] which will
     * have the updated adapter position.
     *
     * Override [.onBindViewHolder] instead if Adapter can
     * handle efficient partial bind.
     *
     * @param holder The ViewHolder which should be updated to represent the contents of the
     * item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    override fun onBindViewHolder(holder: ProteinCardViewHolder, position: Int) {
        holder.bind(proteins[position])
    }

    fun listItemSelected(pos:Int){
        listener?.onProteinItemSelected(proteins[pos])
    }


}
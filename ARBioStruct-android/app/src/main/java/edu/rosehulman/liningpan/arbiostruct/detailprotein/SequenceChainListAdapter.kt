package edu.rosehulman.liningpan.arbiostruct.detailprotein

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import edu.rosehulman.liningpan.arbiostruct.Constant
import edu.rosehulman.liningpan.arbiostruct.Protein
import edu.rosehulman.liningpan.arbiostruct.R
import java.io.File

class SequenceChainListAdapter(var context: Context, var protein: Protein) :
    RecyclerView.Adapter<SequenceChainViewHolder>() {

    private var chainList = ArrayList<ProteinInfoService.Sequence>()
    private var fastaFile = File(context.getExternalFilesDir(null), protein.getFASTAFile())

    init {
        Log.d(Constant.TAG, fastaFile.toString())
        if (fastaFile.exists()) {
            readFastaFile()
        } else {
            val builder = AlertDialog.Builder(context)
            builder.setTitle("Error")
            builder.setMessage("Cannot find fasta file")
            builder.create().show()
        }
    }

    fun readFastaFile() {
        chainList = ProteinInfoService.readSequence(context, protein)!!
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SequenceChainViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.sequence_chain_card_view, null, false)
        return SequenceChainViewHolder(view)
    }

    /**
     * Returns the total number of items in the data set held by the adapter.
     *
     * @return The total number of items in this adapter.
     */
    override fun getItemCount(): Int = chainList.size


    override fun onBindViewHolder(holder: SequenceChainViewHolder, position: Int) {
        holder.bind(chainList[position])
    }

}
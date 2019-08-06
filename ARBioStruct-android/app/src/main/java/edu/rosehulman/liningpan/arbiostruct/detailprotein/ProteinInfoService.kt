package edu.rosehulman.liningpan.arbiostruct.detailprotein

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.text.Html
import android.text.Spanned
import android.util.Log
import edu.rosehulman.liningpan.arbiostruct.Constant
import edu.rosehulman.liningpan.arbiostruct.Protein
import edu.rosehulman.liningpan.arbiostruct.ProteinModel
import java.io.File

class ProteinInfoService {
    data class Description(
        var description: String,
        var length: Int,
        var weight: Double,
        var chains: ArrayList<Char>,
        var enzClass: String
    ) {
        fun getFormatedDescription(): String = "<b>Description:</b> $description<br/>" +
                        "<b>Length:</b> $length<br/>" +
                        "<b>Weight:</b> $weight<br/>" +
                        "<b>Chains</b> $chains<br/" +
                        "<b>Enzyme Class</b> $enzClass"
    }
    data class Sequence(var pdbID: String, var chain: String, var seq: String)

    companion object {
        fun fetchPDBDescription(protein: Protein): String {
            /*val description = RCSBDescriptionFactory.get(protein.pdbID)
            val descArrayList = ArrayList<Description>()
            for (poly in description.polymers) {
                descArrayList.add(
                    Description(
                        poly.description,
                        poly.length,
                        poly.weight,
                        ArrayList(poly.chains),
                        poly.enzClass
                    )
                )
//                print(poly.description)
//                print(poly.length)
//                print(poly.weight)
            }
            return descArrayList.joinToString (separator = "<br/>") { it.getFormatedDescription() }*/
            return "Information Placeholder"
        }

        fun fetchPDBFile(context: Context, protein: Protein): Long {
            return downloadWithManager(context,
                "https://files.rcsb.org/download/${protein.pdbID}.pdb",
                protein.getPDBFile())
        }

        fun fetchSequence(context: Context, protein: Protein): Long {
            return downloadWithManager(context,
                "https://www.rcsb.org/pdb/download/downloadFastaFiles.do?structureIdList=${protein.pdbID}&compressionType=uncompressed",
                    protein.getFASTAFile())
        }

        fun fetch3DModel(context: Context, proteinModel:ProteinModel): Long {
            return downloadWithManager(context,
                proteinModel.modelUrl,
                proteinModel.getglTFFile())
        }

        fun downloadWithManager(context: Context, url:String, filename:String):Long{
            val request = DownloadManager.Request(Uri.parse(url))
                .setDestinationInExternalFilesDir(context, null, filename)
            val manager: DownloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            return manager.enqueue(request)
        }



        fun readSequence(context: Context, protein: Protein): ArrayList<Sequence>? {
            val fastaFile = File(context.getExternalFilesDir(null), protein.getFASTAFile())
            Log.d(Constant.TAG, "Filename: $fastaFile, exist: ${fastaFile.exists()}")
            try {
                var result = ArrayList<Sequence>()
                val input = fastaFile.readLines()
                var header: String = ""
                var sequence: String = ""
                for (line in input) {
                    if (line.startsWith(">")) {
                        if (sequence != "") {
                            var name = header.split('|')[0]
                            name = name.removePrefix(">")
                            var name_split = name.split(':')
                            result.add(
                                Sequence(
                                    name_split[0],
                                    name_split[1],
                                    sequence
                                )
                            )
                        }
                        header = line
                        sequence = ""
                        Log.d(Constant.TAG, "header $header")
                    } else {
                        sequence = sequence.plus(line)
                        Log.d(Constant.TAG, "seq $line")
                    }
                }

                var name = header.split('|')[0]
                name = name.removePrefix(">")
                var name_split = name.split(':')
                result.add(
                    Sequence(
                        name_split[0],
                        name_split[1],
                        sequence
                    )
                )

                Log.d(Constant.TAG, "$result")
                return result
            } catch (e: Exception) {
                Log.d(Constant.TAG, e.toString())
            }
            return null
        }
    }
}
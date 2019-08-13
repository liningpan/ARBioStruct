package edu.rosehulman.liningpan.arbiostruct.detailprotein

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.util.Log
import edu.rosehulman.liningpan.arbiostruct.Constant
import edu.rosehulman.liningpan.arbiostruct.Protein
import edu.rosehulman.liningpan.arbiostruct.ProteinModel
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import java.io.File
import java.io.ObjectInput
import java.lang.StringBuilder
import javax.xml.parsers.DocumentBuilderFactory

class ProteinInfoService {

    data class Sequence(var pdbID: String, var chain: String, var seq: String)

    companion object {
        fun getXMLAttributes(parser: XmlPullParser): LinkedHashMap<String, String>{
            val map = LinkedHashMap<String, String>()
            for(i in 0 until parser.attributeCount){
                Log.d(Constant.TAG, "attr: ${parser.getAttributeName(i)}, ${parser.getAttributeValue(i)}")
                map[getUserFriendlyKeyName(parser.getAttributeName(i))] = parser.getAttributeValue(i)
            }
            return map
        }

        val keyName = mapOf("structureId" to "Structure PDB ID",
            "title" to "Title",
            "pubmedId" to "PubMed ID",
            "expMethod" to "Experimental method",
            "resolution" to "Resolution",
            "keywords" to "Keywords",
            "nr_entities" to "Number of entities",
            "nr_residues" to "Number of residues",
            "nr_atoms" to "Number of atoms",
            "deposition_date" to "Deposition date",
            "release_date" to "Release date",
            "last_modification_date" to "Last modification date",
            "structure_authors" to "Structure authors",
            "citation_authors" to "Citation authors",
            "status" to "Status"
            )
        fun getUserFriendlyKeyName(key:String)= keyName.getOrDefault(key, key)
        fun readPDBDescription(context:Context, protein: Protein): ArrayList<Map.Entry<String,String>> {
            val parser = XmlPullParserFactory.newInstance().newPullParser()
            parser.setInput(File(context.getExternalFilesDir(null), protein.getPDBXMLFile()).reader())

            var event = parser.eventType
            var attr : LinkedHashMap<String, String> ? = null
            while(event != XmlPullParser.END_DOCUMENT){
                if (event == XmlPullParser.START_TAG && parser.name == "PDB"){
                    attr = getXMLAttributes(parser)
                }
                event = parser.next()
            }

            return attr?.toArrayList()?: ArrayList()
        }

        fun fetchPDBXMLFile(context: Context, protein: Protein): Long {
            return downloadWithManager(
                context,
                "https://www.rcsb.org/pdb/rest/describePDB?structureId=${protein.pdbID}",
                protein.getPDBXMLFile()
            )
        }

        fun fetchPDBFile(context: Context, protein: Protein): Long {
            return downloadWithManager(
                context,
                "https://files.rcsb.org/download/${protein.pdbID}.pdb",
                protein.getPDBFile()
            )
        }

        fun fetchSequence(context: Context, protein: Protein): Long {
            return downloadWithManager(
                context,
                "https://www.rcsb.org/pdb/download/downloadFastaFiles.do?structureIdList=${protein.pdbID}&compressionType=uncompressed",
                protein.getFASTAFile()
            )
        }

        fun fetch3DModel(context: Context, proteinModel: ProteinModel): Long {
            return downloadWithManager(
                context,
                proteinModel.modelUrl,
                proteinModel.get3dModel()
            )
        }

        fun downloadWithManager(context: Context, url: String, filename: String): Long {
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
fun Map<String, Any>.toFormatedString() : String{
    val builder = StringBuilder()
    for(i in this){
        builder.append("<b>${i.key}:</b><br/>${i.value}<br/>")
    }
    return builder.toString()
}


fun <K,V> Map<K, V>.toArrayList() : ArrayList<Map.Entry<K,V>> {
    val arrayList =  ArrayList<Map.Entry<K,V>>()
    for(i in this){
        arrayList.add(i)
    }
    return arrayList
}
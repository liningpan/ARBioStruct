package edu.rosehulman.liningpan.arbiostruct

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.ServerTimestamp

data class ProteinModel(
    var pdbID: String = "",
    var modelUrl: String = "",
    var status: String = ""
) {
    @get:Exclude
    var id: String = ""
    @ServerTimestamp
    var lastTouched: Timestamp? = null

    companion object {
        const val LAST_TOUCHED_KEY = "lastTouched"
        const val PDBID_KEY = "pdbID"

        const val STATUS_NEW = "new"
        const val STATUS_RENDERING = "rendering"
        const val STATUS_UPLOADING = "uploading"
        const val STATUS_READY = "ready"

        fun fromSnapshot(snapshot: DocumentSnapshot): ProteinModel {
            val structure = snapshot.toObject(ProteinModel::class.java)!!
            structure.id = snapshot.id
            return structure
        }

    }

    @Exclude
    fun getglTFFile() = "/glTF/${pdbID}.gltf"
}
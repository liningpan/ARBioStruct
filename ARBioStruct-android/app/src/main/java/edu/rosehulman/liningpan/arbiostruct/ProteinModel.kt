package edu.rosehulman.liningpan.arbiostruct

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.ServerTimestamp

data class ProteinModel(
    var modelUrl: String = "",
    var status: String = ""
) {
    @Exclude
    var pdbID: String = ""
    @ServerTimestamp
    var updateTime: Timestamp? = null

    companion object {
        const val UPDATE_TIME_KEY = "updateTime"

        const val STATUS_NEW = "new"
        const val STATUS_RENDERING = "rendering"
        const val STATUS_UPLOADING = "uploading"
        const val STATUS_READY = "ready"

        fun fromSnapshot(snapshot: DocumentSnapshot): ProteinModel {
            val structure = snapshot.toObject(ProteinModel::class.java)!!
            structure.pdbID = snapshot.id
            return structure
        }

    }

    /*@Exclude
    fun getglTFFile() = "/glTF/${pdbID}.gltf"*/
    @Exclude
    fun get3dModel() = "/model/${pdbID}.sfb"
}
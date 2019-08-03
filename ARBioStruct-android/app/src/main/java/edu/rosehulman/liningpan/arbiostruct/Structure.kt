package edu.rosehulman.liningpan.arbiostruct

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.ServerTimestamp

data class Structure(var pdbID: String = "",
                     var modelUrl: String = "",
                     var status:String = "") {
    @get:Exclude var id:String = ""
    @ServerTimestamp
    var lastTouched: Timestamp? = null

    companion object{
        const val LAST_TOUCHED_KEY = "lastTouched"

        fun fromSnapshot(snapshot: DocumentSnapshot): Structure {
            val structure = snapshot.toObject(Structure::class.java)!!
            structure.id = snapshot.id
            return structure
        }
    }
}
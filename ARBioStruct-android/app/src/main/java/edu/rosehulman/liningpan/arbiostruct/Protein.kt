package edu.rosehulman.liningpan.arbiostruct

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.ServerTimestamp


data class Protein(
    var name: String = "",
    var pdbID: String = "",
    var description: String = "",
    var ownByUser: String = ""){

    @get:Exclude var id = ""

    @ServerTimestamp var lastTouched: Timestamp? = null

    companion object {
        const val LAST_TOUCHED_KEY = "lastTouched"
        const val OWN_BY_USER = "ownByUser"

        fun fromSnapshot(snapshot: DocumentSnapshot): Protein {
            val protein = snapshot.toObject(Protein::class.java)!!
            protein.id = snapshot.id
            return protein
        }
    }
}
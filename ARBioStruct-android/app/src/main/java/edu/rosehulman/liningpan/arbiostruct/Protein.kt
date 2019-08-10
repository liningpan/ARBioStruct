package edu.rosehulman.liningpan.arbiostruct

import android.content.Intent
import android.os.Parcelable
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.ServerTimestamp
import kotlinx.android.parcel.IgnoredOnParcel
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Protein(
    var name: String = "",
    var pdbID: String = "",
    var description: String = "",
    var ownByUser: String = ""
) : Parcelable {

    @IgnoredOnParcel
    @get:Exclude
    var id = ""

    @IgnoredOnParcel
    @ServerTimestamp
    var lastTouched: Timestamp? = null

    companion object {
        const val LAST_TOUCHED_KEY = "lastTouched"
        const val OWN_BY_USER = "ownByUser"
        const val IS_EDIT_MESSAGE = "isEditMessage"
        const val PROTEIN_NAME_MESSAGE = "proteinNameMessage"
        const val PROTEIN_PDBID_MESSAGE = "proteinPdbIDMessage"
        const val PROTEIN_DESCRIPTION_MESSAGE = "proteinDescriptionMessage"
        const val PROTEIN_ID_MESSAGE = "proteinIdMessage"
        const val PROTEIN_UID_MESSAGE = "proteinUidMessage"

        fun fromSnapshot(snapshot: DocumentSnapshot): Protein {
            val protein = snapshot.toObject(Protein::class.java)!!
            protein.id = snapshot.id
            return protein
        }

        fun fromIntent(intent: Intent?): Protein = Protein().apply {
            name = intent?.getStringExtra(PROTEIN_NAME_MESSAGE) ?: ""
            pdbID = intent?.getStringExtra(PROTEIN_PDBID_MESSAGE) ?: ""
            description = intent?.getStringExtra(PROTEIN_DESCRIPTION_MESSAGE) ?: ""
            id = intent?.getStringExtra(PROTEIN_ID_MESSAGE) ?: ""
            ownByUser = intent?.getStringExtra(PROTEIN_UID_MESSAGE) ?: FirebaseAuth.getInstance().uid!!
        }
    }

    fun putIntent(intent: Intent) {
        intent.putExtra(PROTEIN_NAME_MESSAGE, name)
        intent.putExtra(PROTEIN_PDBID_MESSAGE, pdbID)
        intent.putExtra(PROTEIN_DESCRIPTION_MESSAGE, description)
        intent.putExtra(PROTEIN_ID_MESSAGE, id)
        if (ownByUser == "") {
            intent.putExtra(PROTEIN_UID_MESSAGE, FirebaseAuth.getInstance().uid!!)
        } else {
            intent.putExtra(PROTEIN_UID_MESSAGE, ownByUser)
        }
    }

    @Exclude
    fun getPDBFile() = "/pdbfile/${pdbID}.pdb"

    @Exclude
    fun getFASTAFile() = "/FASTA/${pdbID}.fasta"

    /*@Exclude
    fun getglTFFile() = "/glTF/${pdbID}.gltf"*/

    @Exclude
    fun get3dModel() = "/model/${pdbID}.sfb"
}
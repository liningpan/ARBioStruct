package edu.rosehulman.liningpan.arbiostruct.detailprotein

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.firestore.FirebaseFirestore
import edu.rosehulman.liningpan.arbiostruct.Constant
import edu.rosehulman.liningpan.arbiostruct.Protein
import edu.rosehulman.liningpan.arbiostruct.ProteinModel
import edu.rosehulman.liningpan.arbiostruct.R
import kotlinx.android.synthetic.main.activity_add_edit_structure.*
import kotlinx.android.synthetic.main.download_dialog.view.*
import java.io.File

class ProteinDetailActivity : AppCompatActivity(), ProteinDetailStructureFragment.OnSwitchToARClickedListener {

    private var modelRef = FirebaseFirestore
        .getInstance()
        .collection(Constant.models)

    private lateinit var textMessage: TextView
    private lateinit var protein: Protein
    private val onNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        var fragment: Fragment? = null
        when (item.itemId) {
            R.id.navigation_information -> {
                val infoFile = File(this.getExternalFilesDir(null), protein.getPDBXMLFile())
                if(infoFile.exists()) {
                    fragment = ProteinDetailInformationFragment.newInstance(protein)
                } else {
                    downloadAndSwitchToInfo()
                }
            }
            R.id.navigation_sequence -> {
                val fastaFile = File(this.getExternalFilesDir(null), protein.getFASTAFile())
                if (fastaFile.exists()) {
                    fragment = ProteinDetailSequenceFragment.newInstance(protein)
                } else {
                    downloadAndSwitchToSequence()
                }
            }
            R.id.navigation_structure -> {
                val pdbFile = File(this.getExternalFilesDir(null), protein.getPDBFile())
                if (pdbFile.exists()) {
                    fragment = ProteinDetailStructureFragment.newInstance(protein)
                } else {
                    downloadAndSwitchToStructure()
                }
            }
        }
        if (fragment != null) {
            val ft = supportFragmentManager.beginTransaction()
            ft.replace(R.id.protein_detail_frag_container, fragment)
            ft.commit()
        }
        true
    }

    override fun onSwitchToARClicked() {
        val modelFile = File(this.getExternalFilesDir(null), protein.get3dModel())
        if (modelFile.exists()) {
            switchToArActivity()
        } else {
            modelRef
                .document(protein.pdbID)
                .get()
                .addOnSuccessListener { documentSnapshot ->
                    if(documentSnapshot == null){
                        AlertDialog.Builder(this)
                            .setTitle("Sorry")
                            .setMessage("This structure hasn't been processed by server")
                            .create()
                            .show()
                        return@addOnSuccessListener
                    }
                    val model = ProteinModel.fromSnapshot(documentSnapshot)
                    when (model.status) {
                        ProteinModel.STATUS_NEW, ProteinModel.STATUS_RENDERING, ProteinModel.STATUS_UPLOADING -> {
                            AlertDialog.Builder(this)
                                .setTitle("Sorry")
                                .setMessage("This structure is not ready")
                                .create()
                                .show()
                        }
                        ProteinModel.STATUS_READY -> {
                            downloadAndSwitchToAR(model)
                        }
                        else -> {
                            Log.w(Constant.TAG, "Wrong status ${model.status}")
                        }
                    }
                }.addOnFailureListener {
                    AlertDialog.Builder(this)
                        .setTitle("Sorry")
                        .setMessage("This structure hasn't been processed by server")
                        .create()
                        .show()
                }
        }
    }

    private fun switchToArActivity() {
        val arIntent = Intent(this, ProteinArActivity::class.java)
        protein.putIntent(arIntent)
        startActivity(arIntent)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_protein_detail)
        val navView: BottomNavigationView = findViewById(R.id.nav_view)
        protein = Protein.fromIntent(intent)
        if (protein.name == "") {
            finish()
        }
        setSupportActionBar(toolbar)
        navView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = protein.name

        if (savedInstanceState == null) {
            val infoFile = File(this.getExternalFilesDir(null), protein.getPDBXMLFile())
            if(infoFile.exists()) {
                val ft = supportFragmentManager.beginTransaction()
                ft.replace(R.id.protein_detail_frag_container, ProteinDetailInformationFragment.newInstance(protein))
                ft.commit()
            } else {
                downloadAndSwitchToInfo()
            }
        }
    }


    fun downloadAndSwitchToInfo() {
        val reqId = ProteinInfoService.fetchPDBXMLFile(this, protein)
        launchDownloadDialog(reqId) { success ->
            if (success) {
                val ft = supportFragmentManager.beginTransaction()
                ft.replace(R.id.protein_detail_frag_container, ProteinDetailInformationFragment.newInstance(protein))
                ft.commit()
            }
        }
    }

    fun downloadAndSwitchToSequence() {
        val reqId = ProteinInfoService.fetchSequence(this, protein)
        launchDownloadDialog(reqId) { success ->
            if (success) {
                val ft = supportFragmentManager.beginTransaction()
                ft.replace(R.id.protein_detail_frag_container, ProteinDetailSequenceFragment.newInstance(protein))
                ft.commit()
            }
        }
    }

    fun downloadAndSwitchToStructure() {
        val reqId = ProteinInfoService.fetchPDBFile(this, protein)
        launchDownloadDialog(reqId) { success ->
            if (success) {
                val ft = supportFragmentManager.beginTransaction()
                ft.replace(R.id.protein_detail_frag_container, ProteinDetailStructureFragment.newInstance(protein))
                ft.commit()
            }
        }
    }

    fun downloadAndSwitchToAR(model: ProteinModel) {
        val reqId = ProteinInfoService.fetch3DModel(this, model)
        launchDownloadDialog(reqId) { success ->
            if (success) {
                switchToArActivity()
            }
        }
    }


    fun launchDownloadDialog(reqId: Long, afterDownload: ((Boolean) -> (Unit))) {
        class DownloadListener(
            var context: Context,
            var dialog: AlertDialog,
            var progressBar: ProgressBar,
            var statusTextView: TextView
        ) :
            DownloadMonitor.DownloadMonitorUpdateListener {
            override fun onSuccess() {
                runOnUiThread {
                    dialog.hide()
                    afterDownload(true)
                }
            }

            override fun onFailed() {
                runOnUiThread {
                    dialog.hide()
                    val builder = AlertDialog.Builder(context)
                    builder.setMessage("Download Failed")
                    builder.create().show()
                    afterDownload(false)
                }
            }

            override fun progressChange(progress: DownloadMonitor.Progress) {
                runOnUiThread {
                    progressBar.progress = progress.percent.toInt()
                    statusTextView.text = progress.statusString
                }
            }

        }

        val builder = AlertDialog.Builder(this)
        val view = LayoutInflater.from(this).inflate(R.layout.download_dialog, null, false)
        val progressBar = view.download_progress_bar as ProgressBar
        val statusTextView = view.status_text_view as TextView
        builder.setView(view)
        val dialog = builder.create()
        dialog.show()

        val listener = DownloadListener(this, dialog, progressBar, statusTextView)

        val downloadMonitor = DownloadMonitor(reqId, listener, this)
        downloadMonitor.start()
    }

}

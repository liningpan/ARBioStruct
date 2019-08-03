package edu.rosehulman.liningpan.arbiostruct

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity;

import kotlinx.android.synthetic.main.activity_add_edit_structure.*
import kotlinx.android.synthetic.main.content_add_edit_structure.*

class AddEditStructureActivity : AppCompatActivity() {

    private var protein = Protein()
    private var isEdit = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_edit_structure)
        setSupportActionBar(toolbar)

        isEdit = intent.getBooleanExtra(Protein.IS_EDIT_MESSAGE,false)

        if(isEdit) {
            protein = Protein.fromIntent(intent)
        }

        updateUI()

    }

    private fun updateUI(){
        if(isEdit){
            toolbar.title = getString(R.string.title_activity_edit)
        } else {
            toolbar.title = getString(R.string.title_activity_add)
        }
        protein_name_edit_text.setText(protein.name)
        protein_pdbid_edit_text.setText(protein.pdbID)
        protein_description_edit_text.setText(protein.description)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.protein_info_edit, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            R.id.action_protein_edit_done -> {

                protein.name = protein_name_edit_text.text.toString()
                protein.pdbID = protein_pdbid_edit_text.text.toString()
                protein.description = protein_description_edit_text.text.toString()

                if(protein.name == "" || protein.pdbID == ""){
                    val builder = AlertDialog.Builder(this)
                    builder.setMessage("Name and PDB ID cannot be empty")
                    builder.setNegativeButton(android.R.string.ok, null)
                    builder.create().show()
                }
                else {
                    val resultIntent = Intent(this, MainActivity::class.java)

                    resultIntent.putExtra(Protein.IS_EDIT_MESSAGE, isEdit)
                    protein.putIntent(resultIntent)

                    setResult(Activity.RESULT_OK, resultIntent)
                    finish()
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }


}

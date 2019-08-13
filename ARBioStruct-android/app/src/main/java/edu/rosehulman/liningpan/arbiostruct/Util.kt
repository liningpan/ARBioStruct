package edu.rosehulman.liningpan.arbiostruct

import android.content.Context
import java.io.File

object Util {
    fun deleteDir(dir: File) : Boolean{
        if(!dir.exists()){
            return true
        }
        if (dir.isDirectory) {
            val children = dir.list()
            for (i in children.indices) {
                val suc = deleteDir(File(dir, children[i]))
                if(!suc){
                    return false
                }
            }
        }
        return dir.delete()
    }
    fun deleteCacheDir(context: Context){
        for(i in arrayOf("pdbfile", "FASTA", "model", "rcsbxml")){
            deleteDir(File(context.getExternalFilesDir(null), i))
        }
    }


}
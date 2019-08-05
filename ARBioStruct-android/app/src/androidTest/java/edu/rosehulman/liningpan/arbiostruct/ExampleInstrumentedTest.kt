package edu.rosehulman.liningpan.arbiostruct

import android.app.DownloadManager
import android.content.Context
import android.util.Log
import androidx.test.InstrumentationRegistry
import androidx.test.runner.AndroidJUnit4
import edu.rosehulman.liningpan.arbiostruct.detailprotein.DownloadMonitor
import edu.rosehulman.liningpan.arbiostruct.detailprotein.ProteinInfoService
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getTargetContext()
        assertEquals("edu.rosehulman.liningpan.arbiostruct", appContext.packageName)
    }

    @Test
    fun pdbFileDownload() {
        val appContext = InstrumentationRegistry.getTargetContext()
        val id = ProteinInfoService.fetchPDBFile(appContext, Protein("LDH", "1I10"))
        val query = DownloadManager.Query()
        query.setFilterById(id)
        val manager = appContext.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager

        while (true) {
            val result = manager.query(query)
            if (result.moveToFirst()) {
                val byteDownloaded =
                    result.getInt(result.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR))
                val byteTotal = result.getInt(result.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES))
                val status = result.getInt(result.getColumnIndex(DownloadManager.COLUMN_STATUS))
                val percent = byteDownloaded * 100.0 / byteTotal
                Log.d(
                    Constant.TAG,
                    "Status: $status, Byte Downloaded: $byteDownloaded, Byte Total: $byteTotal, percent: $percent"
                )
                if (status == DownloadManager.STATUS_SUCCESSFUL || status == DownloadManager.STATUS_FAILED) {
                    break
                }
            }
        }

        val file = File(appContext.getExternalFilesDir(null), "/pdbfile/1I10.pdb")
        assertEquals("File should exist", true, file.exists())
        file.delete()
        assertEquals("File should not exist", false, file.exists())
    }

    @Test
    fun pdbFileDownloadWithListener() {
        class MyListener : DownloadMonitor.DownloadMonitorUpdateListener {
            override fun onSuccess() {
                Log.d(Constant.TAG, "Download finished successfully")
            }

            override fun progressChange(progress: DownloadMonitor.Progress) {
                Log.d(Constant.TAG, "Progress: ${progress.statusString}")
            }

            override fun onFailed() {
                Log.d(Constant.TAG, "Download failed")
            }

        }

        val appContext = InstrumentationRegistry.getTargetContext()
        val id = ProteinInfoService.fetchPDBFile(appContext, Protein("LDH", "1I10"))

        DownloadMonitor(id, MyListener(), appContext).start()
        while (true);

    }

    @Test
    fun fastaFileDownloadWithListener() {
        class MyListener : DownloadMonitor.DownloadMonitorUpdateListener {
            override fun onSuccess() {
                Log.d(Constant.TAG, "Download finished successfully")
                val seq =
                    ProteinInfoService.readSequence(InstrumentationRegistry.getTargetContext(), Protein("LDH", "1I10"))

                //Log.d(Constant.TAG, "Sequence : $seq")
            }

            override fun progressChange(progress: DownloadMonitor.Progress) {
                Log.d(Constant.TAG, "Progress: ${progress.statusString}")
            }

            override fun onFailed() {
                Log.d(Constant.TAG, "Download failed")
            }

        }

        val appContext = InstrumentationRegistry.getTargetContext()
        val id = ProteinInfoService.fetchSequence(appContext, Protein("LDH", "1I10"))

        DownloadMonitor(id, MyListener(), appContext).start()
        while (true);

    }
}

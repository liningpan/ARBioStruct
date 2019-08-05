package edu.rosehulman.liningpan.arbiostruct.detailprotein

import android.app.DownloadManager
import android.content.Context
import android.text.format.Formatter
import java.util.*

class DownloadMonitor(val requestId: Long, val listener: DownloadMonitorUpdateListener, val context: Context) {

    data class Progress(val byteDownloaded: Int, val byteTotal: Int, val percent: Double, val statusString: String)

    class MonitorTask(val requestId: Long, val listener: DownloadMonitorUpdateListener, val context: Context) :
        TimerTask() {

        override fun run() {
            val query = DownloadManager.Query()
            query.setFilterById(requestId)
            val manager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            val result = manager.query(query)



            if (!result.moveToFirst()) {
                throw Exception("No result")
            }

            val byteDownloaded = result.getInt(result.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR))
            val byteTotal = result.getInt(result.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES))
            val status = result.getInt(result.getColumnIndex(DownloadManager.COLUMN_STATUS))

            val str_byteDownloaded = Formatter.formatFileSize(context, byteDownloaded.toLong())
            val str_byteTotal = Formatter.formatFileSize(context, byteTotal.toLong())

            val progress = if (byteDownloaded == 0) {
                Progress(0, 0, 0.0, "(0/0)0.0%")
            } else if (byteDownloaded > 0 && byteTotal > 0) {
                val percent = byteDownloaded * 100.0 / byteTotal
                Progress(
                    byteDownloaded,
                    byteTotal,
                    percent,
                    "($str_byteDownloaded/$str_byteTotal)$percent%"
                )
            } else {
                Progress(
                    byteDownloaded,
                    byteTotal,
                    0.0,
                    "($str_byteDownloaded/?)?%"
                )
            }
            listener.progressChange(progress)
            when (status) {
                DownloadManager.STATUS_FAILED -> {
                    listener.onFailed()
                    this.cancel()
                }
                DownloadManager.STATUS_SUCCESSFUL -> {
                    listener.onSuccess()
                    this.cancel()
                }
                else -> {

                }
            }

        }

    }

    fun start() {
        val task =
            MonitorTask(requestId, listener, context)
        val timer = Timer()
        timer.schedule(task, 0, 100)
    }


    interface DownloadMonitorUpdateListener {
        fun onSuccess()
        fun onFailed()
        fun progressChange(progress: Progress)
    }

}
package com.shaun.news

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.net.MalformedURLException
import java.net.URL


enum class DownloadStatus {
    OK, IDLE, NOT_INITIALISED, FAILED_OR_EMPTY, PERMISSION_ERROR, ERROR
}

class GetRawData(private val listener: OnDownloadComplete) {
    private val Tag = "getRawData"
    private var downloadStatus = DownloadStatus.IDLE

    interface OnDownloadComplete {
        fun onDownloadComplete(data: String, status: DownloadStatus,id: Int)
    }

    fun downloadRawData(link: String, id:Int) {

        GlobalScope.launch {
            val result = downloadInBackground(link)
            withContext(Dispatchers.Main) {
                Log.d(Tag,"Download Complete with Result $result")
                listener.onDownloadComplete(result, downloadStatus,id)
            }
        }

    }

    private fun downloadInBackground(link: String): String {
        Log.d(Tag, "downloadInBackground Starts")
        if (link.isEmpty()) {
            downloadStatus = DownloadStatus.NOT_INITIALISED
            return "NO Url Specified"
        }
        try {
            downloadStatus = DownloadStatus.OK
            val data: String? = URL(link).readText()
            Log.d(Tag, data!!)
            return data
        } catch (e: Exception) {
            val errorMessage = when (e) {
                is MalformedURLException -> {
                    downloadStatus = DownloadStatus.NOT_INITIALISED
                    "doInBackground:Invalid URL ${e.message}"
                }
                is IOException -> {
                    downloadStatus = DownloadStatus.FAILED_OR_EMPTY
                    "doInBackground:IO Exception ${e.message}"
                }
                is SecurityException -> {
                    downloadStatus = DownloadStatus.PERMISSION_ERROR
                    "doInBackground:Security Exception : Give the Goddamn permission ${e.message}"
                }
                else
                -> {

                    downloadStatus = DownloadStatus.ERROR
                    "Unkown Error : ${e.message}"

                }
            }
            Log.e(Tag, errorMessage)
            return errorMessage
        }
    }

}
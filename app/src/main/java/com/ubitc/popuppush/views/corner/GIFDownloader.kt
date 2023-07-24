package com.ubitc.popuppush.views.corner

import android.net.Uri
import com.ubitc.popuppush.App
import com.ubitc.popuppush.taskrunner.TaskRunner
import java.io.File
import java.net.URL

class GIFDownloader : TaskRunner<String, Int, ByteArray>() {

    override fun onProgressUpdate(progress: Int) {
    }


    override fun onPreExecute() {

    }

    override fun doInBackground(params: String): ByteArray {
        if (params.startsWith("http")) {
            URL(params).openConnection().apply {

                doInput = true
                useCaches = false
                connect()
            }.getInputStream().buffered().also { stream ->
                    stream.use { stream2 ->
                        stream2.readBytes()
                    }.also { bytes ->
                        stream.close()
                        onPostExecute(bytes)
                        return bytes
                    }
                }
        } else {
            val tempUri = Uri.fromFile(File(params))
            val inputStream = App.activity?.get()!!.contentResolver.openInputStream(tempUri)
            inputStream?.buffered()
                .also { stream ->
                    // stream?.close()
                    stream!!.use { stream2 ->
                        stream2.readBytes()
                    }.also { bytes ->
                            stream.close()
                        inputStream?.close()
                            onPostExecute(bytes)
                            return bytes

                        }

                }
        }
    }

}
package com.ubitc.popuppush.taskrunner

import android.os.Handler
import android.os.Looper
import java.util.concurrent.Callable
import java.util.concurrent.Executor
import java.util.concurrent.Executors

abstract class TaskRunner<Params, Progress, Result> {
    private var onPostExecuteUnit: ((Result?) -> Unit)? = null
    private val executor: Executor =
        Executors.newSingleThreadExecutor() // change according to your requirements
    private val handler: Handler = Handler(Looper.getMainLooper())

    fun executeAsync(params: Params, onPostExecute: ((Result?) -> Unit)? = null) {
        this.onPostExecuteUnit = onPostExecute
        val work = Callable { doInBackground(params) }
        onPreExecute()
        executor.execute {
           work.call()
           // handler.post { onPostExecute(result) }

        }
    }


    abstract fun doInBackground(params: Params): Result

    abstract fun onProgressUpdate(progress: Int)

    open fun onPostExecute(result: Result?) {
        handler.post { onPostExecuteUnit?.invoke(result) }
       // onPostExecuteUnit?.invoke(result)
    }


    abstract fun onPreExecute()
}
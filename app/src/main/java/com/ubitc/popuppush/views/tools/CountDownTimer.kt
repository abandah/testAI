package com.ubitc.popuppush.views.tools

import android.os.Handler
import android.os.Looper
import android.os.Message
import android.os.SystemClock

abstract class CountDownTimer(private var mMillisInFuture: Long = 0){
    private var mStopTimeInFuture: Long = 0
    private var mPauseTime: Long = 0
    private var mCancelled = false
    private var mPaused = false
    private val mCountdownInterval: Long = 1000
    fun cancel() {
        mHandler.removeMessages(MSG)
        mCancelled = true
        mPaused = false
        mStopTimeInFuture = 0
    }

    /**
     * Start the countdown.
     */
    @Synchronized
    fun start(): CountDownTimer {
        if (mMillisInFuture <= 0) {
            //onFinish()
            return this
        }
        mStopTimeInFuture = SystemClock.elapsedRealtime() + mMillisInFuture
        mHandler.sendMessage(mHandler.obtainMessage(MSG))
        mCancelled = false
        mPaused = false
        return this
    }

    /**
     * Pause the countdown.
     */
    fun pause(): Long {
        mPauseTime = mStopTimeInFuture - SystemClock.elapsedRealtime()
        mPaused = true
        return mPauseTime
    }

    /**
     * Resume the countdown.
     */
    fun resume(mMillisInFuture: Long): Long {
        val pastTime = this.mMillisInFuture - millisLeft
        cancel()
       // if(this.mMillisInFuture != mMillisInFuture) {
            this.mMillisInFuture = mMillisInFuture - pastTime
      //  }

        start()
        return mPauseTime
    }

//    fun resume(): Long {
//        mStopTimeInFuture = mPauseTime + SystemClock.elapsedRealtime()
//        mPaused = false
//        mHandler.sendMessage(mHandler.obtainMessage(MSG))
//        return mPauseTime
//    }

    abstract fun onTick(millisUntilFinished: Long)

    abstract fun onFinish()
//    fun prepare() {
//        this.mMillisInFuture = mMillisInFuture
//    }

    var millisLeft = 0L

    private val mHandler: Handler = object : Handler(Looper.myLooper()!!) {
        override fun handleMessage(msg: Message) {
            synchronized(this@CountDownTimer) {
                if (!mPaused) {
                    millisLeft = mStopTimeInFuture - SystemClock.elapsedRealtime()
                    if (millisLeft <= 0) {
                        onFinish()
                    } else if (millisLeft < mCountdownInterval) {
                        // no tick, just delay until done
                        sendMessageDelayed(obtainMessage(MSG), millisLeft)
                    } else {
                        val lastTickStart = SystemClock.elapsedRealtime()
                        onTick(millisLeft)

                        // take into account user's onTick taking time to execute
                        var delay =
                            lastTickStart + mCountdownInterval - SystemClock.elapsedRealtime()

                        // special case: user's onTick took more than interval to
                        // complete, skip to next interval
                        while (delay < 0) delay += mCountdownInterval
                        if (!mCancelled) {
                            sendMessageDelayed(obtainMessage(MSG), delay)
                        }
                    }
                }
            }
        }
    }

    companion object {
        private const val MSG = 1
    }
}
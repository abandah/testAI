package com.ubitc.popuppush.views.hdmi

import android.annotation.SuppressLint
import android.app.Activity
import android.app.MboxOutputModeManager
import android.app.SystemWriteManager
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.text.TextUtils
import android.util.AttributeSet
import android.view.SurfaceHolder
import android.view.View
import android.widget.OverlayView
import com.ubitc.popuppush.models.MediaModel
import com.ubitc.popuppush.views.tools.MyView
import com.ubitc.popuppush.views.tools.ViewListener
import java.util.*

@SuppressLint("WrongConstant") //TODO: fix this
class HDMIView : OverlayView, SurfaceHolder.Callback, MyView {
    override var media: MediaModel?=null
    private var activity: Activity? = null
    private val videoHoleReal = 258
    constructor(context: Context?) : super(context) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    ) {
        init()
    }

    override fun isVisible(): Boolean {
        return this.visibility == View.VISIBLE
    }

    override fun pauseView() {
        onStopHdmiInNormal()
    }

    override fun resumeView() {
        onStartHdmiIn()
    }

    override fun playView(media: MediaModel?) {
       onStartHdmiIn()
    }

    private fun stopHDMI() {
        mSurfaceCreated = false
        mHDMIStopped = true
        onStopHdmiInToPlayDTV()
        onStopHdmiInNormal()
        stopAudioHandleTimer()
        stopHdmiInSizeTimer()
        //mSurfaceHolder!!.surface.release()

     ///   postInvalidate()
       // mSurfaceHolder!!.setFormat(PixelFormat.TRANSPARENT)


    }

    private var mSw: SystemWriteManager? = null
    private var mSurfaceHolder: SurfaceHolder? = null
    private var mSurfaceCreated = false
    private var mHdmiPlugged = false
    private var mIsInternalHdmiIn = true
    private val hdmiInStart = 0x10001
    private val hdmiInStop = 0x10002
    private var mMboxOutputModeManager: MboxOutputModeManager? = null
    private var mSystemWriteManager: SystemWriteManager? = null
    private var mAudioTask: TimerTask? = null
    private var mAudioTimer: Timer? = null
    private var mHdmiInAudioRequired = false
    private var mHdmiInAudioOut = false
    private var mHdmiInSizeHandler: Handler? = null
    private var mHdmiInSizeTask: TimerTask? = null
    private var mHdmiInSizeTimer: Timer? = null
    private var mHdmiInStatus = hdmiInStop
    private val stopMov = 1
    private val startMove = 2
    private val showBlack = 3
    private val exit = 4
    var hdmiInWidth = 0
        private set
    var hdmiInHeight = 0
        private set
    private var mHdmiInInterlace = -1
    private var mHdmiInHz = -1
    private var mHDMIStopped = true
    private var mStartSent = false
    private val mBoxOutputModeService = "mbox_outputmode_service"


    private fun myOverlayView2(viewListener: ViewListener?) {
        mUseVideoLayer = mSw!!.getPropertyBoolean("mbx.hdmiin.videolayer", true)
        mMboxOutputModeManager = activity!!
            .getSystemService(mBoxOutputModeService) as MboxOutputModeManager
        mSystemWriteManager = activity!!
            .getSystemService("system_write") as SystemWriteManager
        viewListener?.viewIsReady(this)
        // stopSatellite();
    }

    val view: OverlayView
        get() = this

    private fun onStartHdmiIn() {
        if (mHDMIStopped) {
            initHdmiInData()
            mHdmiPlugged = false
            mHDMIStopped = false
            init(
                -1, true,
                false
            )
            startHdmiInSizeTimer()
        }
    }

    private fun initHdmiInData() {
        hdmiInWidth = 0
        hdmiInHeight = 0
        mHdmiInInterlace = -1
        mHdmiInHz = -1
    }

    private fun onStopHdmiInToPlayDTV() {
        if (!mHDMIStopped || mStartSent) {
            if (mStartSent) {
                mHandler.removeMessages(hdmiInStart)
                mStartSent = false
            }
            stopAudioHandleTimer()
            stopHdmiInSizeTimer()
            if (mHdmiPlugged) {
                if (!mUseVideoLayer) {
                    displayPip(0, 0, 0, 0)
                    invalidate()
                    stopMov()
                }
            }
            deinit(false)
            mHDMIStopped = true
            mHdmiPlugged = false
        }
    }

    private fun onStopHdmiInNormal() {
        if (!mHDMIStopped || mStartSent) {
            if (mStartSent) {
                mHandler.removeMessages(hdmiInStart)
                mStartSent = false
            }
            stopAudioHandleTimer()
            stopHdmiInSizeTimer()
            if (mHdmiPlugged) {
                if (mUseVideoLayer) {
                    stopVideo()
                } else {
                    displayPip(0, 0, 0, 0)
                    invalidate()
                    stopMov()
                }
            }
            deinit(false)
            mHDMIStopped = true
            mHdmiPlugged = false

        }
    }

    private val isSurfaceAvailable: Boolean
        get() = if (mSurfaceHolder == null) false else isSurfaceAvailable(mSurfaceHolder!!.surface)

    private fun startHdmiInSizeTimer() {
        if (mHdmiInSizeHandler == null) {
            mHdmiInSizeHandler = object : Handler(Looper.getMainLooper()) {
                override fun handleMessage(msg: Message) {
                    if (mHdmiInSizeTimer == null || mHdmiInSizeTask == null) return

                    val signal = hdmiSignal()
                    if (!isEnable || !hdmiPlugged() || !signal) {
                        return
                    }
                    if (!isSurfaceAvailable) return
                    if (mSurfaceCreated) {
                        if (!isEnable || !hdmiPlugged()) {
                            if (mHdmiInStatus == hdmiInStart && mHdmiPlugged) {
                                val message = mHandler.obtainMessage(
                                    hdmiInStop, showBlack, 0
                                )
                                mHandler.sendMessageDelayed(message, 0)
                                mHdmiPlugged = false
                            }
                            return
                        }
                        val hdmiInMode  = if (mIsInternalHdmiIn) {
                            tvinSignalInfo
                        } else {
                            hdmiInSize
                        }
                        var invalidMode = TextUtils.isEmpty(hdmiInMode)

                        var width = 0
                        var height = 0
                        var interlace = -1
                        var hz = -1
                        var hdmiInSize: Array<String>? = null
                        if (!invalidMode) {
                            hdmiInSize = hdmiInMode.split(":").toTypedArray()
                            if (hdmiInSize.isEmpty()) invalidMode = true
                            if (hdmiInSize.size == 1) invalidMode = true
                        }
                        if (invalidMode) {
                            hdmiInWidth = 0
                            hdmiInHeight = 0
                            mHdmiInInterlace = -1
                            mHdmiInHz = -1
                        }
                        if (mIsInternalHdmiIn && !invalidMode) {
                            val signalStatus = hdmiInSize!![2]
                            if (signalStatus != "stable") {
                                invalidMode = true
                            }
                        }
                        if (!invalidMode) {
                            val mode = hdmiInSize!![1]
                            for (i in MODES.indices) {
                                if (mode == MODES[i]) {
                                    width = MODES_WIDTH[i]
                                    height = MODES_HEIGHT[i]
                                    interlace = MODES_INTERLACE[i]
                                    hz = MODES_HZ[i]
                                    break
                                }
                            }

                            if (hdmiPlugged()) {
                                if (!mHdmiPlugged) mHdmiPlugged = true
                                if (width > 0 && height > 0) {
                                    if (hdmiInWidth != width || hdmiInHeight != height || mHdmiInInterlace != interlace || mHdmiInHz != hz || mHDMIStopped) {
                                        var flag = stopMov
                                        if (hdmiInWidth == 0 && hdmiInHeight == 0 && mHdmiInInterlace == -1 && mHdmiInHz == -1) flag =
                                            startMove

                                        stopAudioHandleTimer()
                                        if (flag == stopMov && !mHDMIStopped) {
                                            if (mUseVideoLayer) {
                                                stopVideo()
                                            } else {
                                                displayPip(
                                                    0, 0,
                                                    0, 0
                                                )
                                                invalidate()
                                                stopMov()
                                            }
                                            setStart(false)

                                        }
                                        setStart(true)
                                        hdmiInWidth = width
                                        hdmiInHeight = height
                                        mHdmiInInterlace = interlace
                                        mHdmiInHz = hz

                                        val message = mHandler
                                            .obtainMessage(
                                                hdmiInStart,
                                                flag, 0
                                            )
                                        mHandler.sendMessageDelayed(
                                            message,
                                            500
                                        )
                                        mStartSent = true
                                    }
                                }
                            }
                        } else if ((!hdmiPlugged())) {
                            if (mHdmiInStatus == hdmiInStart) {

                                val message = mHandler.obtainMessage(
                                    hdmiInStop, showBlack, 0
                                )
                                mHandler.sendMessageDelayed(message, 0)
                                mHdmiPlugged = false
                            }
                        }
                    }
                    super.handleMessage(msg)
                }
            }
        }
        if (mHdmiInSizeTask == null) {
            mHdmiInSizeTask = object : TimerTask() {
                override fun run() {
                    val msg = Message()
                    msg.what = 0
                    if (mHdmiInSizeHandler == null) {
                        mHdmiInSizeTask!!.cancel()
                    } else mHdmiInSizeHandler!!.sendMessage(msg)
                }
            }
        }
        if (mHdmiInSizeTimer == null) {
            mHdmiInSizeTimer = Timer()
            mHdmiInSizeTimer!!.schedule(mHdmiInSizeTask, 0, 500)
        }
    }

    private val mHandler: Handler = object : Handler(Looper.getMainLooper()) {
        @SuppressLint("WrongConstant")
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            when (msg.what) {
                hdmiInStart -> {
                    mHdmiInStatus = hdmiInStart
                    mStartSent = false
                    removeMessages(hdmiInStart)
                    if (mHDMIStopped) mHDMIStopped = false
                    if (msg.arg1 == stopMov) {
                        hdmiStopped()
                    }
                    setStart(true)
                    if (mSurfaceHolder != null) {
                        val sur = mSurfaceHolder!!.surface
                        if (mUseVideoLayer) {
                            if (mMboxOutputModeManager == null) mMboxOutputModeManager = activity!!
                                .getSystemService(mBoxOutputModeService) as MboxOutputModeManager
                            if (mSystemWriteManager == null) mSystemWriteManager = activity!!
                                .getSystemService("system_write") as SystemWriteManager
                            val mode = mSystemWriteManager!!
                                .readSysfs(DISPLAY_MODE_PATH)
                            if (TextUtils.isEmpty(mode)) displayPip(0, 0, 1920, 1080) else {
                                val position = mMboxOutputModeManager!!
                                    .getPosition(mode)
                                if (position == null || position.size != 4) displayPip(
                                    0,
                                    0,
                                    1920,
                                    1080
                                ) else displayPip(
                                    position[0],
                                    position[1], position[2] - position[0]
                                            + 1, position[3] - position[1]
                                            + 1
                                )
                            }
                            invalidate()
                            startVideo()
                        } else {

                            displayPip(
                                0, 0, hdmiInWidth,
                                hdmiInHeight
                            )
                            invalidate()
                            if (setPreviewWindow(sur)) startMov()
                        }
                        // mPipBtn.setOnClickListener(mPipBtnListener);
                    }
                }
                hdmiInStop -> {
                    mHdmiInStatus = hdmiInStop
                    if (mStartSent) {
                        this.removeMessages(hdmiInStart)
                        mStartSent = false
                    }
                    if (msg.arg2 == exit) {
                        stopHdmiInSizeTimer()
                    }
                    if (msg.arg1 == showBlack && mSurfaceHolder != null) {
                        hdmiIsBlacked()
                    }
                    stopAudioHandleTimer()
                    if (mUseVideoLayer) {
                        stopVideo()
                    } else {
                        displayPip(0, 0, 0, 0)
                        invalidate()
                        stopMov()
                    }
                    hdmiInWidth = 0
                    hdmiInHeight = 0
                    mHdmiInInterlace = -1
                    mHdmiInHz = -1
                    if (msg.arg2 == exit) {
//					if (RecordDialogBuilder.sIsRecording) {
                        // stopRecord();
//					}
                        deinit(false)
                        mHDMIStopped = true
                        // finish();
                    }
                }
            }
        }
    }

    private fun hdmiIsBlacked() {
        TODO("Not yet implemented")
    }

    private fun hdmiStopped() {
        TODO("Not yet implemented")
    }


    private fun stopHdmiInSizeTimer() {
        if (mHdmiInSizeTimer != null) {
            mHdmiInSizeTimer!!.cancel()
            mHdmiInSizeTimer = null
        }
        if (mHdmiInSizeTask != null) {
            mHdmiInSizeTask!!.cancel()
            mHdmiInSizeTask = null
        }
        //		ScreenPositionControl.getInstance().recoveryData(mContext);
    }

//    override fun setMediaControllerListenerToTheView(mediaControllerListener: MediaControllerListener?) {
//        this.mediaControllerListener = mediaControllerListener
//    }

    fun stopAudioHandleTimer() {
        if (mAudioTimer != null) {
            mAudioTimer!!.cancel()
            mAudioTimer = null
            enableAudio(0)
        }
        if (mAudioTask != null) {
            mAudioTask!!.cancel()
            mAudioTask = null
        }
        abandonAudioOut()
        mHdmiInAudioRequired = false
        try {
            handleAudio()
        } catch (_: Exception) {

        }
    }



    private fun abandonAudioOut() {
        try {
            val musicGain  = setAmaudioLeftGain(0)
            val leftGain = setAmaudioRightGain(0)
            val rightGain = setAmaudioMusicGain(255)

            if (musicGain == 0 && leftGain == 0 && rightGain == 0) {
                mHdmiInAudioOut = false
            }
        } catch (e: Exception) {

            e.printStackTrace()
        }
    }

    override fun surfaceChanged(arg0: SurfaceHolder, arg1: Int, arg2: Int, arg3: Int) {
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
    //    val canvas = holder.lockCanvas()
       // canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.MULTIPLY)
    //    holder.unlockCanvasAndPost(canvas)
        mSurfaceCreated = true
    }

    override fun surfaceDestroyed(arg0: SurfaceHolder) {

        mSurfaceCreated = false
        stopHDMI()
    }



    override fun init() {
        activity = context as Activity
        mIsInternalHdmiIn = false
        //				intent.getBooleanExtra("internal", true);

        mSurfaceCreated = false
        mHDMIStopped = true
        mSurfaceHolder = this.holder
       // mSurfaceHolder!!.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS)
        mSurfaceHolder!!.addCallback(this)

    }

     override fun prepareView(media: MediaModel?, viewListener: ViewListener?) {super.prepareView(media, viewListener)
        super.prepareView(media, viewListener)
        try {
            mSw = activity!!.getSystemService("system_write") as SystemWriteManager
            mUseVideoLayer = mSw!!.getPropertyBoolean("mbx.hdmiin.videolayer", true)
            if (mUseVideoLayer) mSurfaceHolder!!.setFormat(videoHoleReal)
            myOverlayView2(viewListener)
        }catch (e: Exception) {
            e.printStackTrace()
            viewListener?.viewNotSupported(this)
        }

    }


    companion object {
        private var mUseVideoLayer = true
        val MODES = arrayOf(
            "1080p", "1080p50hz", "1080p30hz",
            "1080i", "1080i50hz", "720p", "720p50hz", "480p", "480i", "576p",
            "576i"
        )

        val MODES_INTERLACE = intArrayOf(
            0, 0, 0, 1, 1, 0, 0, 0, 1, 0,
            1
        )
        val MODES_WIDTH = intArrayOf(
            1920, 1920, 1920, 1920, 1920,
            1280, 1280, 720, 720, 720, 720
        )
        val MODES_HEIGHT = intArrayOf(
            1080, 1080, 1080, 1080, 1080,
            720, 720, 480, 480, 576, 576
        )
        val MODES_HZ = intArrayOf(
            60, 50, 30, 60, 50, 60, 50, 0, 0, 0,
            0
        )
        private const val DISPLAY_MODE_PATH = "/sys/class/display/mode"
    }
    override fun mute() {

    }

}
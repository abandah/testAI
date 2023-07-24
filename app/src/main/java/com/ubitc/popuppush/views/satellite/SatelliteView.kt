package com.ubitc.popuppush.views.satellite

import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.SurfaceHolder
import android.view.SurfaceView
import com.amlogic.dvb.DVBClient
import com.amlogic.dvb.DVBEvent
import com.ubitc.popuppush.models.MediaModel
import com.ubitc.popuppush.views.tools.MyView
import com.ubitc.popuppush.views.tools.ViewListener

class SatelliteView : SurfaceView, MyView, SurfaceHolder.Callback {
    private var mDvbClient: DVBClient? = null
    private var serviceTypeTv = 1
    private var mServiceType = serviceTypeTv
    private var mServiceNumber = 0
    private var mCurServiceId = -1
    override var media: MediaModel? = null
    private val videoHoleReal = 258
    private var viewListener: ViewListener? = null
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
        return this.visibility == VISIBLE
    }

    override fun init() {
        initSatelliteVideoView()
        setSatelliteScreenWidthHeight()
    }

    override fun prepareView(media: MediaModel?, viewListener: ViewListener?) {
        super.prepareView(media, viewListener)
        try {
            val service  = Intent("com.amlogic.dvb.DVB_SERVICE")
            context.startService(service)
        } catch (e: Exception) {
            viewListener?.viewNotSupported(this)
            return
        }
        this.viewListener = viewListener
        mDvbClient = DVBClient(context, "com.amlogic.dvb.DVB_SERVICE")
        mDvbClient!!.setEventListener(object : DVBClient.OnEventListener {
            override fun onEvent(c: DVBClient, e: DVBEvent) {

                when (e.type) {
                    DVBEvent.EVENT_PLAY_FINSIH -> mCurServiceId = c.currentServiceID
                    DVBEvent.EVENT_NO_SIGNAL -> {
                        viewListener?.viewNotSupported(this@SatelliteView)
                    }

                    else -> {

                    }
                }
            }

            //when DVBClient connect to DVBService success,will call this function
            override fun onConnected(c: DVBClient) {

                c.setVideoWindow(0, 0, 0, 0)
                c.screenmode = 1

                // start play the current service
                c.playByServiceNumber(mServiceType, mServiceNumber, false)
                setSatelliteScreenWidthHeight()
            }
        })

        val index = media!!.path!!.toInt()
        val list = try {
            allTvList
        } catch (e: Exception) {
            viewListener!!.viewNotSupported(this)
            return
        }
        if (list.isNullOrEmpty() || index > list.size - 1 || index < 0) {
            viewListener!!.viewNotSupported(this)
            return
        }
        val serviceEntity = list.filter { it!!.chanNub == index }[0]
        if (serviceEntity == null) {
            viewListener!!.finished(this)
            return
        }
        mServiceType = serviceEntity.serviceType
        mServiceNumber = serviceEntity.chanNub

        viewListener?.viewIsReady(this)
    }


    private fun setSatelliteScreenWidthHeight() {
        if (mDvbClient != null) //  mDvbClient.setVideoWindow(0, 0, ScreenUtils.getScreenWidth(getActivity()), ScreenUtils.getScreenHeight(getActivity()));
            mDvbClient!!.setVideoWindow(0, 0, 0, 0)
    }

    private fun stopSatellite() {


        if (mDvbClient != null) {
            mDvbClient!!.stopPlaying() // disconnect from DVBService
            mDvbClient!!.disconnect()
        }
    }

    private fun initSatelliteVideoView() {
        holder.addCallback(this)
        holder.setFormat(videoHoleReal)
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, w: Int, h: Int) {}
    override fun surfaceCreated(holder: SurfaceHolder) {
        initSurface(holder)
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {

        stopSatellite()
    }

    private fun initSurface(h: SurfaceHolder) {
        var c: Canvas? = null
        try {
            c = h.lockCanvas()
        } finally {
            if (c != null) h.unlockCanvasAndPost(c)
        }
    }

    var allTvList: ArrayList<ServiceEntity?>? = null
        get() {
            if (field != null)
                return field
            var cur: Cursor? = null
            val mTVList = ArrayList<ServiceEntity?>()
            if (null != mDvbClient) {
                try {
                    cur = context.contentResolver.query(
                        DVBClient.TABLE_SERVICE, null,
                        "service_type = 1", null, "chan_order"
                    )
                    if (cur != null && cur.count > 0) {
                        cur.moveToFirst()
                        while (!cur.isAfterLast) {
                            val entity = ServiceEntity()
                            entity.serviceName = ContactsUtil.getStringValue(cur, ContactsUtil.NAME)
                            entity.serviceType =
                                ContactsUtil.getIntValue(cur, ContactsUtil.SERVICE_TYPE)
                            entity.chanNub = ContactsUtil.getIntValue(cur, ContactsUtil.CHAN_ORDER)
                            entity.service_id =
                                ContactsUtil.getIntValue(cur, ContactsUtil.SERVICE_ID)

                            mTVList.add(entity)
                            cur.moveToNext()
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                } finally {
                    // visibility = GONE
                    if (cur != null && !cur.isClosed) {
                        cur.close()
                    }
                    field = mTVList
                }
            } else {
                field = mTVList
            }
            return field


        }

    override fun pauseView() {
        mDvbClient?.pausePVR()
        if (mDvbClient != null) {
            mDvbClient!!.stopPlaying() // disconnect from DVBService
            mDvbClient!!.disconnect()
        }

    }

    override fun resumeView() {
        mDvbClient?.resumePVR()
        val index = media!!.path!!.toInt()
        val list = try {
            allTvList
        } catch (e: Exception) {
            return
        }

        val serviceEntity :ServiceEntity?= list?.filter { it!!.chanNub == index }?.get(0)
        if (serviceEntity == null) {
            viewListener!!.finished(this)
            return
        }
        mServiceType = serviceEntity.serviceType
        mServiceNumber = serviceEntity.chanNub
        if (mDvbClient != null)
            mDvbClient!!.connect()

    }

    override fun playView(media: MediaModel?) {

        if (mDvbClient != null) mDvbClient!!.connect()

    }


    override fun mute() {
    }



}
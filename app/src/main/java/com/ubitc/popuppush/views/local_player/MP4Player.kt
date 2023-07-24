package com.ubitc.popuppush.views.local_player

import android.content.Context
import android.graphics.SurfaceTexture
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.util.AttributeSet
import android.view.Surface
import android.view.TextureView
import com.ubitc.popuppush.models.MediaModel
import com.ubitc.popuppush.views.tools.MyView
import com.ubitc.popuppush.views.tools.ViewListener


class MP4Player : TextureView, MyView, TextureView.SurfaceTextureListener {
    private var param: ViewListener? = null
    private var surface: Surface? = null

    constructor(context: Context?) : super(context!!) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(
        context!!, attrs
    ) {
        init()
    }


    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context!!, attrs, defStyleAttr
    ) {
        init()
    }

    override var media: MediaModel? = null

    override fun init() {
        surfaceTextureListener = this

    }

    override fun isVisible(): Boolean {
        return this.visibility == VISIBLE
    }

    private var pos = 0
    override fun pauseView() {
        try {
            mediaPlayer?.pause()
            pos = mediaPlayer?.currentPosition ?: 0
            mediaPlayer?.release()
        } catch (_: Exception) {
        }
    }

    @Suppress("DEPRECATION")
    override fun resumeView() {
        mediaPlayer = MediaPlayer()
        mediaPlayer?.setDataSource(context, Uri.parse(media?.localUri))
        mediaPlayer?.setOnErrorListener { _, _, _ ->

            false
        }
        mediaPlayer?.setSurface(this.surface)
        mediaPlayer?.setOnCompletionListener {
            param?.finished(this)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mediaPlayer?.setAudioAttributes(
                AudioAttributes
                    .Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build()
            )
        } else {
            mediaPlayer?.setAudioStreamType(AudioManager.STREAM_MUSIC)

        }

        mediaPlayer?.setOnPreparedListener { mp ->
            mp.seekTo(pos)
            val sound = if (media?.isMuted == true) {
                0
            } else {
                1
            }
            mp.setVolume(sound.toFloat(), sound.toFloat())
            mp.start()
        }

        mediaPlayer?.prepareAsync()
    }

    private var mediaPlayer: MediaPlayer? = null
    override fun prepareView(media: MediaModel?, viewListener: ViewListener?) {
        super.prepareView(media, viewListener)
        this.media = media
        this.param = viewListener

        param?.viewIsReady(this@MP4Player)

    }

    override fun forceStop() {

        try {
            if (mediaPlayer?.isPlaying == true) {
                mediaPlayer?.stop()
            }
            mediaPlayer?.reset()
            mediaPlayer?.release()
            surface?.release()
        } catch (_: Exception) {

        } finally {

            surface = null
            surfaceTextureListener = null
            //  mediaPlayer?.setSurface(null)
            mediaPlayer = null
            super.forceStop()

        }
    }

    override fun playView(media: MediaModel?) {
        mediaPlayer?.start()


    }


    override fun mute() {

    }

    var errorCount = 0
    var errorOccurred = false

    @Suppress("DEPRECATION")
    override fun onSurfaceTextureAvailable(surface: SurfaceTexture, width: Int, height: Int) {
        if (mediaPlayer != null) {
            mediaPlayer?.stop()
            mediaPlayer?.reset()
            mediaPlayer?.release()
            surfaceTextureListener = null
            try {
                mediaPlayer?.setSurface(null)

            } catch (_: Exception) {

            }
            mediaPlayer = null
        }
        mediaPlayer = MediaPlayer()

        mediaPlayer?.setDataSource(context, Uri.parse(media?.localUri))
        mediaPlayer?.setOnErrorListener { mp, what, extra ->
            errorCount += 1
            errorOccurred = true
            if (errorCount > 3) {
                param?.finished(this)
                false
            } else {
                pauseView()
                resumeView()
                false
            }
        }

        this.surface = Surface(surface)

        mediaPlayer?.setSurface(this.surface)




        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mediaPlayer?.setAudioAttributes(
                AudioAttributes
                    .Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build()
            )
        } else {
            mediaPlayer?.setAudioStreamType(AudioManager.STREAM_MUSIC)

        }

        mediaPlayer?.setOnPreparedListener { mp ->
            mp?.setOnCompletionListener {
                if (errorOccurred) {
                    errorOccurred = false
                    return@setOnCompletionListener
                }

                param?.finished(this)

            }
            mp.seekTo(pos)
            val sound = if (media?.isMuted == true) {
                0
            } else {
                1
            }
            mp.setVolume(sound.toFloat(), sound.toFloat())
            mp.start()
        }

        mediaPlayer?.prepareAsync()
        //   mediaPlayer?.prepareAsync()

    }

    override fun onSurfaceTextureSizeChanged(surface: SurfaceTexture, width: Int, height: Int) {

    }

    override fun onSurfaceTextureDestroyed(surface: SurfaceTexture): Boolean {
        // forceStop()
//        mediaPlayer?.stop()

        try {
            mediaPlayer?.reset()
            mediaPlayer?.release()
            surface.release()
            this.surface?.release()
        } catch (_: Exception) {

        } finally {
            this.surface = null
            surfaceTextureListener = null
            //  mediaPlayer?.setSurface(null)
            mediaPlayer = null
        }

        return false
    }

    override fun onSurfaceTextureUpdated(surface: SurfaceTexture) {
    }


}
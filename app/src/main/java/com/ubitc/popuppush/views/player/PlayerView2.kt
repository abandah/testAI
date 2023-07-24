package com.ubitc.popuppush.views.player

import android.content.Context
import android.net.Uri
import android.util.AttributeSet
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.DefaultRenderersFactory.EXTENSION_RENDERER_MODE_PREFER
import com.google.android.exoplayer2.mediacodec.MediaCodecInfo
import com.google.android.exoplayer2.mediacodec.MediaCodecSelector
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.hls.HlsMediaSource
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.ui.StyledPlayerView
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource
import com.google.android.exoplayer2.upstream.FileDataSource
import com.google.android.exoplayer2.util.MimeTypes
import com.ubitc.popuppush.models.MediaModel
import com.ubitc.popuppush.views.tools.MyView
import com.ubitc.popuppush.views.tools.ViewListener
import java.util.*


class PlayerView2 : StyledPlayerView, Player.Listener, MyView {
    private var param: ViewListener? = null
    override var media: MediaModel? = null

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

    override fun init() {
        useController = false
        visibility = INVISIBLE
    }

    private fun createPlayer(link: MediaItem) {
        val rf = DefaultRenderersFactory(context)
            .setExtensionRendererMode(EXTENSION_RENDERER_MODE_PREFER)
            .setMediaCodecSelector { mimeType, requiresSecureDecoder, requiresTunnelingDecoder ->
                var decoderInfo: MutableList<MediaCodecInfo> = MediaCodecSelector.DEFAULT
                    .getDecoderInfos(mimeType, requiresSecureDecoder, requiresTunnelingDecoder)
                if (MimeTypes.VIDEO_H264 == mimeType) {
                    // copy the list because MediaCodecSelector.DEFAULT returns an unmodifiable list
                    decoderInfo = ArrayList(decoderInfo)
                    decoderInfo.reverse()
                }
                decoderInfo
            }
//
//        val dataSourceFactory: DataSource.Factory = DefaultHttpDataSource.Factory()
//            // Create a HLS media source pointing to a playlist uri.
//        val hlsMediaSource: HlsMediaSource = HlsMediaSource.Factory(dataSourceFactory)
//            .createMediaSource(link)

        val dts = DefaultTrackSelector(context)
        val parameters = dts.parameters
        val newParameters =
            parameters.buildUpon().setMaxVideoBitrate(dts.parameters.maxVideoBitrate * 2)
                .build() // 2 Mbps
        newParameters.buildUpon().setMaxVideoFrameRate(dts.parameters.maxVideoFrameRate * 2)
            .build() // 2 Mbps
        dts.parameters = newParameters
        //  dts.parameters.maxVideoBitrate
        player = ExoPlayer.Builder(context, rf)
            .setTrackSelector(DefaultTrackSelector(context))
            .build().apply {
                playWhenReady = false
                addListener(this@PlayerView2)
                if (media?.isMuted == true)
                    volume = 0f
                setMediaItem(link)
                prepare()
            }
    }
    private fun createPlayer(link: MediaSource) {
        val rf = DefaultRenderersFactory(context)
            .setExtensionRendererMode(EXTENSION_RENDERER_MODE_PREFER)
            .setMediaCodecSelector { mimeType, requiresSecureDecoder, requiresTunnelingDecoder ->
                var decoderInfo: MutableList<MediaCodecInfo> = MediaCodecSelector.DEFAULT
                    .getDecoderInfos(mimeType, requiresSecureDecoder, requiresTunnelingDecoder)
                if (MimeTypes.VIDEO_H264 == mimeType) {
                    // copy the list because MediaCodecSelector.DEFAULT returns an unmodifiable list
                    decoderInfo = ArrayList(decoderInfo)
                    decoderInfo.reverse()
                }
                decoderInfo
            }
//
//        val dataSourceFactory: DataSource.Factory = DefaultHttpDataSource.Factory()
//            // Create a HLS media source pointing to a playlist uri.
//        val hlsMediaSource: HlsMediaSource = HlsMediaSource.Factory(dataSourceFactory)
//            .createMediaSource(link)

        val dts = DefaultTrackSelector(context)
        val parameters = dts.parameters
        val newParameters =
            parameters.buildUpon().setMaxVideoBitrate(dts.parameters.maxVideoBitrate * 2)
                .build() // 2 Mbps
        newParameters.buildUpon().setMaxVideoFrameRate(dts.parameters.maxVideoFrameRate * 2)
            .build() // 2 Mbps
        dts.parameters = newParameters
        //  dts.parameters.maxVideoBitrate
        player = ExoPlayer.Builder(context, rf)
            .setTrackSelector(DefaultTrackSelector(context))
            .build().apply {
                playWhenReady = false
                addListener(this@PlayerView2)
                if (media?.isMuted == true)
                    volume = 0f
                addMediaSource(0,link)
                prepare()
            }
    }


    override fun prepareView(media: MediaModel?, viewListener: ViewListener?) {
        super.prepareView(media, viewListener)
        val link = media?.path
        if (player != null) {
            return
        }
        this.param = viewListener
        createPlayer(buildMediaSource(link!!))
    }


    fun playSound(soundRes: Int, param: ViewListener?) {
        if (player != null) {
            return
        }
        this.param = param
        val uri = Uri.parse("android.resource://" + context.packageName + "/" + soundRes)
        val firstItem = MediaItem.fromUri(uri)
        createPlayer(firstItem)

    }

//    fun playUrl(soundRes: String, param: ViewListener?) {
//        if (player != null) {
//            return
//        }
//        this.param = param
//        createPlayer(createMediaItem(soundRes))
//
//    }

    //    private fun createMediaItem(link: String): MediaItem {
//        val uri = Uri.parse(link)
//        return MediaItem.fromUri(uri)
//    }
    fun buildMediaSource(link: String): MediaSource {
        val fdataSourceFactory: DataSource.Factory = FileDataSource.Factory()
        val ddataSourceFactory: DataSource.Factory = DefaultHttpDataSource.Factory()
        val uri = Uri.parse(link)
        val mediaItem = MediaItem.fromUri(uri)
        if (link.startsWith("http")) {
            // Create a HLS media source pointing to a playlist uri.
            val hlsMediaSource: MediaSource = HlsMediaSource.Factory(ddataSourceFactory)
                .setAllowChunklessPreparation(false)
                .createMediaSource(mediaItem)
            return hlsMediaSource
        } else {
            // Create a HLS media source pointing to a playlist uri.
            val hlsMediaSource: MediaSource = HlsMediaSource.Factory(fdataSourceFactory)
                .setAllowChunklessPreparation(false)
                .createMediaSource(mediaItem)
            return hlsMediaSource
        }
    }

    override fun forceStop() {
        if (player != null) {
            player!!.release()
            player = null
        }
        super.forceStop()
    }

    override fun isVisible(): Boolean {
        return this.visibility == VISIBLE
    }

    private var pos: Long = 0
    override fun pauseView() {
        player?.playWhenReady = false
        pos = player?.currentPosition ?: 0
        player?.pause()
        player?.release()
        player?.removeListener(this)

    }

    override fun resumeView() {

//        player?.playWhenReady = true
        val rf = DefaultRenderersFactory(context)
            .setExtensionRendererMode(EXTENSION_RENDERER_MODE_PREFER)
            .setMediaCodecSelector { mimeType, requiresSecureDecoder, requiresTunnelingDecoder ->
                var decoderInfo: MutableList<MediaCodecInfo> = MediaCodecSelector.DEFAULT
                    .getDecoderInfos(mimeType, requiresSecureDecoder, requiresTunnelingDecoder)
                if (MimeTypes.VIDEO_H264 == mimeType) {
                    // copy the list because MediaCodecSelector.DEFAULT returns an unmodifiable list
                    decoderInfo = ArrayList(decoderInfo)
                    decoderInfo.reverse()
                }
                decoderInfo
            }
//
//        val dataSourceFactory: DataSource.Factory = DefaultHttpDataSource.Factory()
//            // Create a HLS media source pointing to a playlist uri.
//        val hlsMediaSource: HlsMediaSource = HlsMediaSource.Factory(dataSourceFactory)
//            .createMediaSource(link)

        val l = object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                super.onIsPlayingChanged(isPlaying)
                if (isPlaying) {
                    player?.removeListener(this)
                    player?.addListener(this@PlayerView2)
                }
            }
        }
        player = ExoPlayer.Builder(context, rf)
            .setTrackSelector(DefaultTrackSelector(context))
            .build().apply {
                playWhenReady = true

                if (media?.isMuted == true)
                    volume = 0f
                addListener(l)
                addMediaSource(0, buildMediaSource(media?.path!!))
                prepare()
            }

        if (media?.isMuted == true)
            player?.volume = 0f
        else
            player?.volume = 1f


    }

    //    fun onPlayerError(eventTime: EventTime?, e: ExoPlaybackException) {
//        if (e.rendererException is DecoderInitializationException) {
//            val decoderInitializationException =
//                e.rendererException as DecoderInitializationException
//            val codecInfo = decoderInitializationException.codecInfo
//            Log.d("EXO_V2", "Decoder failed to initialize: " + codecInfo!!.name)
//            Log.d("EXO_V2", "Hardware? " + codecInfo.hardwareAccelerated)
//            Log.d("EXO_V2", "Mime type? " + codecInfo.mimeType)
//            Log.d("EXO_V2", "Is H264? " + (MimeTypes.VIDEO_H264 == codecInfo.mimeType))
//        }
//    }
    override fun playView(media: MediaModel?) {
        var f = media?.fileName
        player?.removeListener(this)
        player?.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(state: Int) {
                if (state == Player.STATE_ENDED || state == Player.STATE_IDLE) {
                    param?.finished(this@PlayerView2)
                }
            }

            override fun onPlayerErrorChanged(error: PlaybackException?) {
                super.onPlayerErrorChanged(error)
                if (retryCount > 3) {
                    param?.finished(this@PlayerView2)
                    retryCount = 0
                    return
                }
                retryCount++
                //(player as ExoPlayer).release()
                (player as ExoPlayer).prepare()

            }
        })
        player?.playWhenReady = true
        player?.play()
    }

    @Deprecated("Deprecated in Java") //TODO: remove in 2.0.0
    override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
        if (playbackState == Player.STATE_READY) {
            param?.viewIsReady(this@PlayerView2)
        } else if (playbackState == Player.STATE_ENDED) {
            param?.finished(this@PlayerView2)
        }

    }


    var retryCount = 0
    override fun onPlayerErrorChanged(error: PlaybackException?) {
        if (error != null) {
            when (error.errorCode) {
                PlaybackException.ERROR_CODE_IO_NETWORK_CONNECTION_FAILED, PlaybackException.ERROR_CODE_IO_BAD_HTTP_STATUS, PlaybackException.ERROR_CODE_PARSING_CONTAINER_UNSUPPORTED -> {
                    param?.viewIsReady(this@PlayerView2)
                    param?.finished(this@PlayerView2)
                    return
                }

                PlaybackException.ERROR_CODE_DECODER_INIT_FAILED -> {
                    if (retryCount > 3) {
                        param?.finished(this@PlayerView2)
                        retryCount = 0
                        return
                    }
                    retryCount++
                    //(player as ExoPlayer).release()
                    (player as ExoPlayer).prepare()
                    return
                }

                else -> {
                    param?.finished(this@PlayerView2)
                    return
                }
            }
        }
    }

    override fun mute() {
        player?.volume = if (player?.volume == 0f) 1f else 0f

    }

}
package com.ubitc.popuppush.providers.downloadx

import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.VolleyError
import com.bumptech.glide.Glide
import com.ubitc.popuppush.App
import com.ubitc.popuppush.R
import com.ubitc.popuppush.models.MediaModel
import com.ubitc.popuppush.models.UriReqModel
import com.ubitc.popuppush.providers.downloadx.core.DownloadTask
import com.ubitc.popuppush.providers.downloadx.helper.Default
import com.ubitc.popuppush.service.apis.ApiService
import com.ubitc.popuppush.service.downloader.DownloadMainFunction
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class DownloaderAdapter : RecyclerView.Adapter<DownloaderAdapter.ItemViewHolder>() {

    private var downloadMainFunction: DownloadMainFunction? = null
    private var items = ArrayList<MediaModel>()
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): DownloaderAdapter.ItemViewHolder {
        val viewHolder: RecyclerView.ViewHolder?
        val inflater = LayoutInflater.from(parent.context)
        // val viewItem: View? =currentActivity?.layoutInflater?.inflate(R.layout.layout_progress_button, null)
        val viewItem: View = inflater.inflate(R.layout.item_downloadx_media, parent, false)
        viewHolder = ItemViewHolder(viewItem)
        return viewHolder
    }

    override fun onBindViewHolder(holder: DownloaderAdapter.ItemViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int {
        return items.size
    }

    private fun addItem(item: MediaModel) {
        this.items.add(item)
        notifyItemInserted(items.size - 1)


    }

    fun removeItem(mediaModel: MediaModel) {

        ApiService.getInstance()
            .updateUri(UriReqModel(mediaModel.localUri!!, mediaModel.mediaId!!), onSuccess = {
                removeItemAndAskForNext(mediaModel)
            }, onError2 = { _: Int, _: String?, _: VolleyError ->
                removeItemAndAskForNext(mediaModel)
            })

    }

    private fun removeItemAndAskForNext(mediaModel: MediaModel) {
        val itemIndex = items.indexOf(mediaModel)
        if (itemIndex == -1) {
            return
        }
        items.removeAt(itemIndex)
        //  itemView.animate().translationX(-1500f).setDuration(500).withEndAction {
        notifyItemRemoved(itemIndex)
        if (items.isEmpty())
            downloadMainFunction?.downloadFinished(mediaModel)
        //}.start()
    }

    fun addDownloadListener(downloadMainFunction: DownloadMainFunction) {
        this.downloadMainFunction = downloadMainFunction
    }

    fun addItems(array: List<MediaModel>) {
        startAddingItems(array, 0)

    }

    private fun startAddingItems(array: List<MediaModel>, i: Int) {
        if (i >= array.size) return
        Handler(Looper.getMainLooper()).postDelayed({
            addItem(array[i])
            startAddingItems(array, i + 1)

        }, 500)
    }


    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private var pic: ImageView? = null
        private var button: TextView? = null
        private var downloadTask: DownloadTask? = null

        init {
            pic = itemView.findViewById(R.id.pic) as ImageView
            button = itemView.findViewById(R.id.button)
        }

        fun bind(mediaModel: MediaModel) {
            Glide.with(itemView).load(mediaModel.thumb).placeholder(R.drawable.mediaicon)
                .override(100, 100).into(pic!!)
            downloadTask =
                GlobalScope.download(url = mediaModel.path!!, saveName = mediaModel.fileName!!)
            downloadTask!!.state()
                .onEach {
                    Handler(Looper.getMainLooper()).post {
                        handleState(it, mediaModel)
                    }
                }
                .launchIn(GlobalScope)
            downloadTask!!.start()


        }

        private fun handleState(state: State, mediaModel: MediaModel) {
            when (state) {
                is State.None -> {
                    button?.text = App.getString(R.string.NotStarted)

                }
                is State.Waiting -> {
                    button?.text = App.getString(R.string.waiting)
                }
                is State.Downloading -> {
                    // progressBar?.progress = state.progress.percent().toInt()
                    button?.text = state.progress.percentStr()
                }
                is State.Failed -> {
                    button?.text = App.getString(R.string.failed)
                    downloadTask!!.start()
                    //  removeItem(mediaModel)

                }
                is State.Stopped -> {
                    button?.text = App.getString(R.string.stopped)

                }
                is State.Succeed -> {
                    mediaModel.localUri = Default.DEFAULT_SAVE_PATH + "/" + mediaModel.fileName
                    button?.text = App.getString(R.string.succeed)
                    removeItem(mediaModel)

                }
            }
        }


    }

}

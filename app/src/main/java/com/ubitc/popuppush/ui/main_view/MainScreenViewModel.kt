package com.ubitc.popuppush.ui.main_view

import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.ubitc.popuppush.models.MAIN
import com.ubitc.popuppush.models.MediaModel
import com.ubitc.popuppush.ui.BaseViewModel
import com.ubitc.popuppush.ui.dialog.CampaignsAdapter
class MainScreenViewModel: BaseViewModel() {
    var availableRam: MutableLiveData<String> = MutableLiveData("0")
    var heapInfo: MutableLiveData<String> = MutableLiveData("0")
    var mainTicks: MutableLiveData<String> = MutableLiveData("0")
    var medias: MutableLiveData<String> = MutableLiveData("")
    var mediasL1: MutableLiveData<String> = MutableLiveData("")
    var bugs: MutableLiveData<String> = MutableLiveData("")
    var mediasL2: MutableLiveData<String> = MutableLiveData("")
    var campaignsAdapters: MutableLiveData<Adapter<CampaignsAdapter.CampaignsViewHolder>>? = MutableLiveData()

    var list = ArrayList<String>()

    fun mainTicks(l: Long) {
        mainTicks.value = l.toString()
    }
    fun getDenaturation():Int{
        return mainTicks.value.toString().toInt()
    }

    fun printList(mains: List<MAIN>?, index: Int) {
        if(mains == null) return
        var value = ""
       // Thread{
            for ((i , media) in mains.withIndex()){
                value = if(i == index) {
                    " $value \n---> ${media.mediaName}  ${media.show_layer_one} ${media.show_layer_two}  ${(media.customDuration!! / 1000)} ${media.isCashed()}"
                }else{
                    " $value \n     ${media.mediaName}  ${media.show_layer_one} ${media.show_layer_two}  ${(media.customDuration!! / 1000)} ${media.isCashed()}"
                }

            }
            medias.postValue(value)
     //   }.start()


    }

    fun printL1List(mains: List<MediaModel>?, index: Int) {
        if(mains == null) return
        var value = ""
      //  Thread{
            for ((i , media) in mains.withIndex()){
                value = if(i == index) {
                    " $value \n---> ${media.mediaName}${(media.customDuration!! / 1000)} ${media.isCashed()}"
                }else{
                    " $value \n     ${media.mediaName} ${(media.customDuration!! / 1000)} ${media.isCashed()}"
                }

            }
            mediasL1.postValue(value)


    }

    fun printL2List(mains: List<MediaModel>?, index: Int) {
        if(mains == null) return

        var value = ""

            for ((i , media) in mains.withIndex()){
                value = if(i == index) {
                    " $value \n---> ${media.mediaName}${(media.customDuration!! / 1000)} ${media.isCashed()}"
                }else{
                    " $value \n     ${media.mediaName}${(media.customDuration!! / 1000)} ${media.isCashed()}"
                }

            }
            mediasL2.postValue(value)


    }

    fun bugDetected(s: String) {
        bugs.value = bugs.value + "\n" + s
    }


}
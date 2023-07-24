package com.ubitc.popuppush.service.db

import com.ubitc.popuppush.api_send_model.CurrentState
import com.ubitc.popuppush.models.Campaign
import com.ubitc.popuppush.models.MAIN
import com.ubitc.popuppush.models.MediaModel

abstract class DbMainFunctions {

    abstract fun saveMediaToDB(campaign: Campaign?, onSuccess: () -> Unit)
    abstract fun getMediasToPlayFromMainLayer(onSuccess: ( List<MAIN>?) -> Unit)
    abstract fun getMediasToPlayFromPLAYGROUND(mediaModel: MediaModel,onSuccess: ( List<MediaModel>?) -> Unit)
    abstract fun getMediasToPlayFromCORNER(mediaModel: MediaModel,onSuccess: ( List<MediaModel>?) -> Unit)
    abstract fun getMediasToPlayFromThirdLayer(mediaModel: MediaModel,onSuccess: ( List<MediaModel>?) -> Unit)
    abstract fun updateCashItem(cashItem: MediaModel)
    abstract fun getCampaign(): Campaign?

    abstract fun saveValue(key: String, value: String?)
    abstract fun getValue(key: String): String?
    abstract fun removeValue(key: String)
    abstract fun saveCurrentState(currentState: CurrentState?)
    abstract fun getCurrentState() : CurrentState?
    abstract fun clearDb()

}
package com.ubitc.popuppush.service.db

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.ubitc.popuppush.App
import com.ubitc.popuppush.api_send_model.CurrentState
import com.ubitc.popuppush.models.*
import com.ubitc.popuppush.service.updator.LocalMediaService
import java.util.*

class DbService(private val dbProvider: DbMainFunctions) : DbMainFunctions() {

    companion object {
        @Volatile
        var providerInstance: DbService? = null
        fun getInstance(): DbService {
            if (providerInstance == null) {
                providerInstance = DbService(DpSharedPrefProvider.getInstance())
            }
            return providerInstance!!
        }
    }


    override fun saveMediaToDB(campaign: Campaign?, onSuccess: () -> Unit) {
        dbProvider.saveMediaToDB(campaign, onSuccess)
    }

    override fun getMediasToPlayFromMainLayer(onSuccess: (List<MAIN>?) -> Unit) {
        return dbProvider.getMediasToPlayFromMainLayer(onSuccess)
    }

    override fun getMediasToPlayFromPLAYGROUND(
        mediaModel: MediaModel,
        onSuccess: (List<MediaModel>?) -> Unit
    ) {
        return dbProvider.getMediasToPlayFromPLAYGROUND(mediaModel, onSuccess)
    }

    override fun getMediasToPlayFromCORNER(
        mediaModel: MediaModel,
        onSuccess: (List<MediaModel>?) -> Unit
    ) {
        return dbProvider.getMediasToPlayFromCORNER(mediaModel, onSuccess)
    }

    override fun getMediasToPlayFromThirdLayer(
        mediaModel: MediaModel,
        onSuccess: (List<MediaModel>?) -> Unit
    ) {
        return dbProvider.getMediasToPlayFromThirdLayer(mediaModel, onSuccess)
    }

    override fun updateCashItem(cashItem: MediaModel) {
        dbProvider.updateCashItem(cashItem)
    }

    override fun getCampaign(): Campaign? {
        return dbProvider.getCampaign()
    }

//    override fun saveDeviceInfo(deviceInfo: DeviceInfo?) {
//        dbProvider.saveDeviceInfo(deviceInfo)
//    }
//
//    override fun getDeviceInfo(): DeviceInfo? {
//        return dbProvider.getDeviceInfo()
//    }
//
//    override fun removeDeviceInfo() {
//        dbProvider.removeDeviceInfo()
//    }

    override fun saveValue(key: String, value: String?) {
        dbProvider.saveValue(key, value)
    }

    override fun getValue(key: String): String? {
        return dbProvider.getValue(key)
    }

    override fun removeValue(key: String) {
        dbProvider.removeValue(key)
    }

    override fun saveCurrentState(currentState: CurrentState?) {
        dbProvider.saveCurrentState(currentState)
    }

    override fun getCurrentState(): CurrentState? {
        return dbProvider.getCurrentState()
    }

    override fun clearDb() {
        dbProvider.clearDb()
    }

    private class DpSharedPrefProvider(val preferences: SharedPreferences?) : DbMainFunctions() {

        var editor: SharedPreferences.Editor? = null
        val gson: Gson = Gson()

        companion object {
            var INSTANCE: DpSharedPrefProvider? = null
            fun getInstance(): DpSharedPrefProvider {
                if (INSTANCE == null) {
                    INSTANCE = DpSharedPrefProvider(
                         App.activity?.get()?.getSharedPreferences(
                            DbConstants.DBName, Context.MODE_PRIVATE
                        )
                    )
                }
                return INSTANCE!!
            }
        }

        init {
            editor = preferences?.edit()
        }


        override fun saveMediaToDB(campaign: Campaign?, onSuccess: () -> Unit) {
            if (campaign == null) {
                editor?.remove(DbConstants.main_playlist_Key)
                editor?.remove(DbConstants.media_playground_Key)
                editor?.remove(DbConstants.media_corner_Key)
                editor?.remove(DbConstants.media_Campaign)
                editor?.commit()
                onSuccess()
                return
            }
            val mainMedia = campaign.MAIN
            val playgroundMedia = campaign.PLAYGROUND
            val cornerMedia = campaign.CORNER

            val mainMediaList: LinkedList<MAIN> = getMediasFromMains(mainMedia) ?: return

//            val playListPlayGround: HashMap<String, kotlin.collections.ArrayList<PLAYGROUND>> =
//                HashMap<String, kotlin.collections.ArrayList<PLAYGROUND>>()
//
////            mainMedia?.let {
//                for (main in mainMedia){
//                    if(!main.PLAYGROUND.isNullOrEmpty()){
//                        val id = main.PLAYGROUND!![0].playlist_id
//                        id?.let {
//                            playListPlayGround.put(id, main.PLAYGROUND!!)
//                        }
//                    }
//                }
//            }
//
//            editor?.remove(DbConstants.playListPlayGround)

            //  editor?.remove(DbConstants.media_main_Key)
            editor?.remove(DbConstants.main_playlist_Key)
            editor?.remove(DbConstants.media_playground_Key)
            editor?.remove(DbConstants.media_corner_Key)
            editor?.remove(DbConstants.media_Campaign)

            //   editor?.putString(DbConstants.media_main_Key, gson.toJson(mainMedia))
           // editor?.putString(DbConstants.playListPlayGround, gson.toJson(playListPlayGround))
            editor?.putString(DbConstants.main_playlist_Key, gson.toJson(mainMediaList))
            editor?.putString(DbConstants.media_playground_Key, gson.toJson(playgroundMedia))
            editor?.putString(DbConstants.media_corner_Key, gson.toJson(cornerMedia))
            editor?.putString(DbConstants.media_Campaign, gson.toJson(campaign))
            editor?.commit()
            onSuccess.invoke()


        }

        override fun getMediasToPlayFromMainLayer(onSuccess: (List<MAIN>?) -> Unit) {
            val media = preferences?.getString(DbConstants.main_playlist_Key, null)
            val json = gson.fromJson(media, Array<MAIN>::class.java)
            if (json.isNullOrEmpty()) {
                onSuccess(null)
                return
            }
            val list = json.toCollection(ArrayList())
            if (list.size > 0) {
                onSuccess(list)
                return
            }
            onSuccess(null)
//
        }

        override fun getMediasToPlayFromPLAYGROUND(
            mediaModel: MediaModel,
            onSuccess: (List<MediaModel>?) -> Unit
        ) {
            val m = mediaModel as MAIN
            val list: List<MediaModel>? = if (m.PLAYGROUND.isNullOrEmpty()) {
                getListByKey(DbConstants.media_playground_Key)
            } else {
                m.PLAYGROUND
            }

            if (list.isNullOrEmpty()) {
                onSuccess(null)
                return
            }

            onSuccess(list)
        }

        override fun getMediasToPlayFromCORNER(
            mediaModel: MediaModel,
            onSuccess: (List<MediaModel>?) -> Unit
        ) {
            val m = mediaModel as MAIN
            val list: List<MediaModel>? = if (m.CORNER.isNullOrEmpty()) {
                getListByKey(DbConstants.media_corner_Key)
            } else {
                m.CORNER
            }

            if (list.isNullOrEmpty()) {
                onSuccess(null)
                return
            }

            onSuccess(list)
        }

        override fun getMediasToPlayFromThirdLayer(
            mediaModel: MediaModel,
            onSuccess: (List<MediaModel>?) -> Unit
        ) {
            onSuccess(null)
        }

        override fun updateCashItem(cashItem: MediaModel) {
            getListByKey(DbConstants.main_playlist_Key)?.let {
                val list = it as ArrayList<MediaModel>
                list.iterator().forEach { main ->
                    if (main is MAIN && main.MAIN.isNullOrEmpty().not()) {
                        for (item in main.MAIN!!) {
                            if (item == cashItem) {
                                item.localUri = cashItem.localUri
                            }
                        }
                    }
                    if (main == cashItem) {
                        main.localUri = cashItem.localUri
                    }
                }
                editor?.remove(DbConstants.main_playlist_Key)
                editor?.putString(DbConstants.main_playlist_Key, gson.toJson(list))
                editor?.apply()

            }
            getListByKey(DbConstants.media_playground_Key)?.let {
                val list = it as ArrayList<MediaModel>
                list.iterator().forEach { main ->
                    if (main == cashItem) {
                        main.localUri = cashItem.localUri
                    }
                }
                editor?.remove(DbConstants.media_playground_Key)
                editor?.putString(DbConstants.media_playground_Key, gson.toJson(list))
                editor?.apply()
            }
            getListByKey(DbConstants.media_corner_Key)?.let {
                val list = it as ArrayList<MediaModel>
                list.iterator().forEach { main ->
                    if (main == cashItem) {
                        main.localUri = cashItem.localUri
                    }
                }
                editor?.remove(DbConstants.media_corner_Key)
                editor?.putString(DbConstants.media_corner_Key, gson.toJson(list))
                editor?.apply()
            }

            LocalMediaService.getInstance().updateMain { _, _ ->

            }

        }

        override fun getCampaign(): Campaign? {
            val media = preferences?.getString(DbConstants.media_Campaign, null)
            if (media.isNullOrEmpty()) return null
            return gson.fromJson(media, Campaign::class.java)
        }

//        override fun saveDeviceInfo(deviceInfo: DeviceInfo?) {
//            editor?.putString(DbConstants.DeviceInfo, gson.toJson(deviceInfo))
//            editor?.commit()
//        }
//
//        override fun getDeviceInfo(): DeviceInfo? {
//            val t = preferences?.getString(DbConstants.DeviceInfo, null)
//            return gson.fromJson(t, DeviceInfo::class.java)
//        }
//
//        override fun removeDeviceInfo() {
//            editor?.remove(DbConstants.DeviceInfo)
//            editor?.commit()
//        }

        override fun saveValue(key: String, value: String?) {
            editor?.putString(key, value)
            editor?.commit()
        }

        override fun getValue(key: String): String? {
            return preferences?.getString(key, null)
        }

        override fun removeValue(key: String) {
            editor?.remove(key)
            editor?.commit()
        }

        override fun saveCurrentState(currentState: CurrentState?) {
            if (currentState == null) {
                editor?.remove(DbConstants.currentState)
                editor?.commit()
                return
            }
            val current = gson.toJson(currentState)
            editor?.putString(DbConstants.currentState, current)
            editor?.commit()
        }

        override fun getCurrentState(): CurrentState? {
            val currentState = preferences?.getString(DbConstants.currentState, null)
            if (currentState.isNullOrEmpty()) return null
            val currentStateOBJ = gson.fromJson(currentState, CurrentState::class.java)
            saveCurrentState(null)
            return currentStateOBJ
        }

        override fun clearDb() {
            editor?.clear()
            editor?.commit()
        }

        private fun getMediasFromMains(mainMedia: ArrayList<MAIN>? , playground: ArrayList<PLAYGROUND>? = null,corner : ArrayList<CORNER>? = null): LinkedList<MAIN>? {
            val mainMediaList: LinkedList<MAIN> = LinkedList()
            if (mainMedia == null) return null
            for (media in mainMedia) {

                if (!media.path.isNullOrEmpty()) {
                    media.PLAYGROUND = playground
                    media.CORNER = corner
                    mainMediaList.add(media)
                } else {
                    mainMediaList.addAll(getMediasFromMains(media.MAIN,media.PLAYGROUND,media.CORNER)!!)
                }
            }
            return mainMediaList
        }


        private fun getListByKey(key: String): List<MediaModel>? {
            val media = preferences?.getString(key, null)
            if (media != null && !media.equals("null", true)) {
                val list =
                    gson.fromJson(media, Array<MediaModel>::class.java).toCollection(ArrayList())
                if (list.size > 0) {
                    return list
                }
            }
            return null

        }

    }
}
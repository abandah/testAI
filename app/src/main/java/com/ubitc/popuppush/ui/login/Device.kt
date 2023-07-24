package com.ubitc.popuppush.ui.login

import com.google.gson.Gson
import com.ubitc.popuppush.AppMainFeatureConstants
import com.ubitc.popuppush.models.DeviceInfo
import com.ubitc.popuppush.providers.BackgroundProcess
import com.ubitc.popuppush.service.db.DbService

class Device {
    companion object {
        private const val loginStepsEnabled = AppMainFeatureConstants.loginStepsEnabled
        fun deviceDetached(onSuccess: () -> Unit) {
            reset {
                onSuccess.invoke()
            }
        }

        fun deviceFlushed(onSuccess: () -> Unit) {
            reset {
                onSuccess.invoke()
            }
        }

        fun deviceDeleted(onSuccess: () -> Unit) {
            reset {
                onSuccess.invoke()
            }

        }

        fun campaignAssigned(onSuccess: () -> Unit) {
            DbService.getInstance().saveMediaToDB(null) {
                needToGetNewInfo = true
                isCampaignAdded = false
                onSuccess.invoke()
            }


        }

        var deviceInfo: DeviceInfo? = null
            get() {
                if (field == null) {
                    val stringValue = DbService.getInstance().getValue("deviceInfo")
                    field = Gson().fromJson(stringValue, DeviceInfo::class.java)
                }
                return field
            }
            set(value) {
                field = value
                DbService.getInstance().saveValue("deviceInfo", Gson().toJson(value))
            }
        var needToGetNewInfo: Boolean? = null // if true, we need to get new info from server
            get() {
                if (field == null) {
                    field =
                        DbService.getInstance().getValue("needToGetNewInfo")?.toBoolean() ?: true
                }
                return if (loginStepsEnabled) return field as Boolean else true
            }
            set(value) {
                field = value
                DbService.getInstance().saveValue("needToGetNewInfo", value.toString())
            }

        var is_prayer_enable: Boolean? = null // if true, we need to get new info from server
            get() {
                if (field == null) {
                    field =
                        DbService.getInstance().getValue("is_prayer_enable")?.toBoolean() ?: false
                }
                return field
            }
            set(value) {
                field = value
                DbService.getInstance().saveValue("is_prayer_enable", value.toString())
                BackgroundProcess.getInstance().startPrayingTimeChecker()

            }
        var country_name: String? = null // if true, we need to get new info from server
            get() {
                if (field == null) {
                    field =
                        DbService.getInstance().getValue("country_name") ?: null
                }
                return field
            }
            set(value) {
                field = value
                DbService.getInstance().saveValue("country_name", value)
            }
        var checkYoutubeDone: Boolean? = null
            get() {
                if (field == null) {
                    field =
                        DbService.getInstance().getValue("checkYoutubeDone")?.toBoolean() ?: false
                }
                return field as Boolean && loginStepsEnabled
            }
            set(value) {
                field = value
                DbService.getInstance().saveValue("checkYoutubeDone", value.toString())
            }
        var checkChannelsDone: Boolean? = null
            get() {
                if (field == null) {
                    field =
                        DbService.getInstance().getValue("checkChannelsDone")?.toBoolean() ?: false
                }
                return field as Boolean && loginStepsEnabled
            }
            set(value) {
                field = value
                DbService.getInstance().saveValue("checkChannelsDone", value.toString())
            }
        var checkHDMIDone: Boolean? = null
            get() {
                if (field == null) {
                    field = DbService.getInstance().getValue("checkHDMIDone")?.toBoolean() ?: false
                }
                return field as Boolean && loginStepsEnabled
            }
            set(value) {
                field = value
                DbService.getInstance().saveValue("checkHDMIDone", value.toString())
            }
        var locationDone: Boolean? = null
            get() {
                if (field == null) {
                    field = DbService.getInstance().getValue("locationDone")?.toBoolean() ?: false
                }
                return field as Boolean && loginStepsEnabled
            }
            set(value) {
                field = value
                DbService.getInstance().saveValue("locationDone", value.toString())
            }
        var deviceId: String? = null
            get() {
                if (field == null) {
                    field = DbService.getInstance().getValue("deviceId")
                }
                return field
            }
            set(value) {
                field = value
                DbService.getInstance().saveValue("deviceId", value)
            }
        var deviceDetailsUpdated: Boolean? = null
            get() {
                if (field == null) {
                    field = DbService.getInstance().getValue("deviceDetailsUpdated")?.toBoolean()
                        ?: false
                }
                return field as Boolean && loginStepsEnabled
            }
            set(value) {
                field = value
                DbService.getInstance().saveValue("deviceDetailsUpdated", value.toString())
            }

        var installServiceProviderIfNeededDone: Boolean? = null
            get() {
                if (field == null) {
                    field = DbService.getInstance().getValue("installServiceProviderIfNeededDone")
                        ?.toBoolean() ?: false
                }
                return field as Boolean && loginStepsEnabled
            }
            set(value) {
                field = value
                DbService.getInstance()
                    .saveValue("installServiceProviderIfNeededDone", value.toString())
            }

        var permissionStepDone: Boolean? = null
            get() {
                if (field == null) {
                    field = DbService.getInstance().getValue("permissionStepDone")?.toBoolean()
                        ?: false
                }
                return field as Boolean && loginStepsEnabled
            }
            set(value) {
                field = value
                DbService.getInstance().saveValue("permissionStepDone", value.toString())
            }

        var isCampaignAdded: Boolean? = null
            get() {
                if (field == null) {
                    field =
                        DbService.getInstance().getValue("isCampaignAdded")?.toBoolean() ?: false
                }
                return field as Boolean && loginStepsEnabled
            }
            set(value) {
                field = value
                DbService.getInstance().saveValue("isCampaignAdded", value.toString())
            }

        var isAddedToCompany: Boolean? = null
            get() {
                if (field == null) {
                    field =
                        DbService.getInstance().getValue("isAddedToCompany")?.toBoolean() ?: false
                }
                return field as Boolean && loginStepsEnabled
            }
            set(value) {
                field = value
                DbService.getInstance().saveValue("isAddedToCompany", value.toString())
            }

        var isActive: Boolean? = null
            get() {
                if (field == null) {
                    field = DbService.getInstance().getValue("isActive")?.toBoolean() ?: false
                }
                return field as Boolean && loginStepsEnabled
            }
            set(value) {
                field = value
                DbService.getInstance().saveValue("isActive", value.toString())
            }

        var Token: String? = null
            get() {
                if (field == null) {
                    field = DbService.getInstance().getValue("Token")
                }
                return field
            }
            set(value) {
                field = value
                DbService.getInstance().saveValue("Token", value)
            }


        fun reset(onSuccess: () -> Unit) {
            deviceInfo = null
            needToGetNewInfo = null
            checkYoutubeDone = null
            checkChannelsDone = null
            checkHDMIDone = null
            locationDone = null
            deviceId = null
            deviceDetailsUpdated = null
            installServiceProviderIfNeededDone = null
            permissionStepDone = null
            isCampaignAdded = null
            isAddedToCompany = null
            isActive = null
            Token = null
            is_prayer_enable = null
            DbService.getInstance().saveMediaToDB(null) {
                onSuccess.invoke()
            }
        }

        fun isNeedAnyUpdate(): Boolean {
            return needToGetNewInfo == true ||
                    checkYoutubeDone == false ||
                    checkChannelsDone == false ||
                    checkHDMIDone == false ||
                    locationDone == false ||
                    deviceDetailsUpdated == false ||
                    installServiceProviderIfNeededDone == false ||
                    permissionStepDone == false ||
                    isCampaignAdded == false ||
                    isAddedToCompany == false ||
                    isActive == false ||
                    is_prayer_enable == null
        }
    }
}
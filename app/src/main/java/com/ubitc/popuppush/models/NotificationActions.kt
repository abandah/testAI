package com.ubitc.popuppush.models

enum class NotificationActions(private val stringValue: String) {
    Restart("restart_hardware"),
    Detach("detach_hardware"),
    Flush("flush_screen_hardware"),
    Assigned("assigned_campaign"),
    AddDevice("added_device"),
    StatusUpdated("status_updated"),
    DeletedDevice("device_deleted"),
    Play("play"),
    Pause("pause"),
    UpdateOrder("updated"),
    GetChannels("get_channels"),
    Rotate("Rotate"),
    DELETE_SCHEDULED("delete_scheduled"),
    START_SCHEDULE("start_schedule");



    companion object {
        fun getAction(action: String): NotificationActions? {
            for (value in values()) {
                if (value.stringValue.equals(action, true)) {
                    return value
                }
            }
            return null
        }
    }
}
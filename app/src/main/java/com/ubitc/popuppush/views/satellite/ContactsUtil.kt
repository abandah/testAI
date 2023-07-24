package com.ubitc.popuppush.views.satellite

import android.database.Cursor

object ContactsUtil {
    const val NAME = "name"
    const val SERVICE_TYPE = "service_type"
    const val CHAN_ORDER = "chan_order"
    const val SERVICE_ID = "service_id"


    fun getIntValue(cur: Cursor ,value: String?): Int {
        val columnIndex1 = cur.getColumnIndex(value)
        return cur.getInt(columnIndex1)
    }
    fun getStringValue(cur: Cursor ,value: String?): String? {
        val columnIndex1 = cur.getColumnIndex(value)
        return DVBPlayerUtil.getMultilingual(cur.getBlob(columnIndex1))
    }
}
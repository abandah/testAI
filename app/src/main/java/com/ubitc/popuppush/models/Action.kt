@file:Suppress("PropertyName")
package com.ubitc.popuppush.models

import java.io.Serializable

class Action :Serializable {
    var action: String? = null
    var id: String? = null
    var data_id :String? = null
    override fun toString(): String {
        return "Action(action=$action, id=$id)"
    }
    // var created_at: Long? = null
}
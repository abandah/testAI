package com.ubitc.popuppush.providers.info.impl

class NullHandle<T>(
    private val data: T?,
    private val defaultResult: T
) {

    fun result(): T = data ?: defaultResult

}

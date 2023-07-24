package com.ubitc.popuppush.views.satellite

import java.util.Arrays
import java.util.Locale

/**
 * Created by elements on 8/23/16.
 */
object DVBPlayerUtil {
    private val aChi = byteArrayOf('c'.code.toByte(), 'h'.code.toByte(), 'i'.code.toByte())
    private val aZho = byteArrayOf('z'.code.toByte(), 'h'.code.toByte(), 'o'.code.toByte())
    private val aTTT = byteArrayOf('t'.code.toByte(), 't'.code.toByte(), 't'.code.toByte())
    private val uiLanguage: ByteArray?
        get() {
            try {
                return Locale.getDefault().isO3Language.toByteArray(charset("UTF-8"))
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return null
        }

    
    fun getMultilingual(aMultiLingual: ByteArray?): String? {
        val language = uiLanguage
        var str: String? = null
        var strTTT: String? = null
        var i =0
        var plainLen = 0
        var k: Int
        if (aMultiLingual == null) {
            return null
        }
        while (i < aMultiLingual.size) {
            if (aMultiLingual[i].toInt() == 0) {
                plainLen = i
                ++i
                break
            }
            i++
        }
        if (plainLen == 0) {
            plainLen = i
        }
        try {
            val bCheckChinese = Arrays.equals(language, aChi) || Arrays.equals(language, aZho)
            while (i < aMultiLingual.size) {
                k = i
                while (i < aMultiLingual.size) {
                    if (aMultiLingual[i].toInt() == 0) {
                        ++i
                        break
                    }
                    i++
                }
                if (i - k > 3) {
                    val aISO = Arrays.copyOfRange(aMultiLingual, k, k + 3)
                    if (bCheckChinese) {
                        if (Arrays.equals(aISO, aChi)
                            || Arrays.equals(aISO, aZho)
                        ) {
                            str = String(aMultiLingual, k + 3, i - k - 4)
                            break
                        }
                    } else if (Arrays.equals(aISO, language)) {
                        str = String(aMultiLingual, k + 3, i - k - 4)
                        break
                    }
                    if (Arrays.equals(aISO, aTTT)) {
                        strTTT = String(aMultiLingual, k + 3, i - k - 4)
                    }
                }
            }
            if (str == null) {
                str = strTTT ?: String(aMultiLingual, 0, plainLen)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return str
    }
}
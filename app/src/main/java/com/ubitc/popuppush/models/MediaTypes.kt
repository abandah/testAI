package com.ubitc.popuppush.models

enum class MediaTypes(private val stringValue: String, val intValue: Int, vararg ext: String) {
    VIDEO("VIDEO", 1, ".m3u8", ".mp4")
    , AUDIO("AUDIO", 2, ".mp3")
    , CORNER(
        "CORNER",
        3,
        ".gif",
        ".png"
    ),
    IMAGE("IMAGE", 4, ".jpg",".jpeg")
    , HDMI("HDMI", 5, "HDMI")
    , CHANNEL(
        "CHANNEL",
        6,
        ""
    ),
    WEBVIEW("WEBVIEW", 7, ".com");

    private val extension: Array<out String>

    init {
        extension = ext
    }

    override fun toString(): String {
        return intValue.toString() + ""
    }

    companion object {

        fun getIntValue(stringValue: String): Int? {
            for (mediaTypes in values()) {
                if (mediaTypes.stringValue.equals(stringValue, ignoreCase = true)) {
                    return mediaTypes.intValue
                }
            }
            return null
        }

    }
}
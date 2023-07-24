package com.ubitc.popuppush.models

class LocationReqModel (lat : Double, long : Double){

    private var location: Location? = null
    init {
        location = Location()
        location?.lat = lat
        location?.long = long
    }


    class Location {
        var lat: Double? = null
        var long: Double? = null
    }
}
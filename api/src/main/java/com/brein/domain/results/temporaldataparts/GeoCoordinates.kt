package com.brein.domain.results.temporaldataparts

class GeoCoordinates(private val latitude: Double, private val longitude: Double) {
    override fun toString(): String {
        return "$latitude $longitude"
    }
}
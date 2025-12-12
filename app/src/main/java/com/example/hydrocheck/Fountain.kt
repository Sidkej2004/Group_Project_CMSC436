package com.example.hydrocheck

data class Fountain(
    val lat: Double = 0.0,
    val lng: Double = 0.0,
    var rating: Float = 0f
) {
    constructor() : this(0.0, 0.0, 0f)
}
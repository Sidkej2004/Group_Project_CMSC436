package com.example.hydrocheck

class HydroModel {
    var currentWater: Int = 0
    var maxWater: Int = 2000
    var isDarkMode: Boolean = false
    val fountains: MutableList<Fountain> = mutableListOf()

    fun addWater(amount: Int) {
        currentWater += amount
        if (currentWater > maxWater) {
            currentWater = maxWater
        }
    }

    fun resetWater() {
        currentWater = 0
    }

    fun addFountain(fountain: Fountain) {
        fountains.add(fountain)
    }
}
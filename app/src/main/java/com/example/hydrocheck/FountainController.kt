package com.example.hydrocheck

object FountainController {
    val fountains = mutableListOf<Fountain>()

    fun addFountain(f: Fountain) {
        fountains.add(f)
    }
}

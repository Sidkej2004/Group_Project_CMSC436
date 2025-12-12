package com.example.hydrocheck

import android.content.Context
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

object HydroController {
    private val model = HydroModel()
    private val database = FirebaseDatabase.getInstance()
    private val fountainsRef = database.getReference("fountains")

    fun loadFromPrefs(context: Context) {
        model.currentWater = Prefs.getCurrentWater(context)
        model.maxWater = Prefs.getMaxWater(context)
        model.isDarkMode = Prefs.isDark(context)
    }

    fun saveToPrefs(context: Context) {
        Prefs.setCurrentWater(context, model.currentWater)
        Prefs.setMaxWater(context, model.maxWater)
        Prefs.setDark(context, model.isDarkMode)
    }

    fun getCurrentWater(): Int {
        return model.currentWater
    }

    fun getMaxWater(): Int {
        return model.maxWater
    }

    fun isDarkMode(): Boolean {
        return model.isDarkMode
    }

    fun addWater(context: Context, amount: Int) {
        model.addWater(amount)
        saveToPrefs(context)
    }

    fun setMaxWater(context: Context, value: Int) {
        var safeValue = value
        if (safeValue < 500) safeValue = 500
        if (safeValue > 4000) safeValue = 4000

        model.maxWater = safeValue

        if (model.currentWater > safeValue) {
            model.currentWater = safeValue
        }

        saveToPrefs(context)
    }

    fun setDarkMode(context: Context, isDark: Boolean) {
        model.isDarkMode = isDark
        saveToPrefs(context)
    }

    fun resetWater(context: Context) {
        model.resetWater()
        saveToPrefs(context)
    }

    fun addFountainToFirebase(fountain: Fountain) {
        model.addFountain(fountain)
        fountainsRef.push().setValue(fountain)
    }

    fun updateFountainRating(fountainKey: String, rating: Float) {
        fountainsRef.child(fountainKey).child("rating").setValue(rating)
    }

    fun loadFountainsFromFirebase(callback: (List<Fountain>) -> Unit) {
        fountainsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val fountainsList = mutableListOf<Fountain>()
                for (childSnapshot in snapshot.children) {
                    val fountain = childSnapshot.getValue(Fountain::class.java)
                    if (fountain != null) {
                        fountainsList.add(fountain)
                    }
                }
                model.fountains.clear()
                model.fountains.addAll(fountainsList)
                callback(fountainsList)
            }

            override fun onCancelled(error: DatabaseError) {
                callback(emptyList())
            }
        })
    }

    fun getFountains(): List<Fountain> {
        return model.fountains
    }
}
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
        var val1 = value
        if (val1 < 500) val1 = 500
        if (val1 > 4000) val1 = 4000

        model.maxWater = val1

        if (model.currentWater > val1) {
            model.currentWater = val1
        }

        saveToPrefs(context)
    }

    fun setDarkMode(context: Context, dark: Boolean) {
        model.isDarkMode = dark
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

    fun updateFountainRating(key: String, rating: Float) {
        fountainsRef.child(key).child("rating").setValue(rating)
    }

    fun loadFountainsFromFirebase(callback: (List<Fountain>) -> Unit) {
        fountainsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = mutableListOf<Fountain>()
                for (child in snapshot.children) {
                    val f = child.getValue(Fountain::class.java)
                    if (f != null) {
                        list.add(f)
                    }
                }
                model.fountains.clear()
                model.fountains.addAll(list)
                callback(list)
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
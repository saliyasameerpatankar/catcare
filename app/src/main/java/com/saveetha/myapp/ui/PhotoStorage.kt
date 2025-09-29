package com.saveetha.myapp.ui

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.saveetha.myapp.models.Photo

object PhotoStorage {
    private val gson = Gson()

    fun getPhotosForCat(context: Context, catId: Int): List<Photo> {
        val prefs = context.getSharedPreferences("PhotoBooth", Context.MODE_PRIVATE)
        val json = prefs.getString("photos_cat_$catId", "[]")
        val type = object : TypeToken<List<Photo>>() {}.type
        return gson.fromJson(json, type)
    }

    fun savePhotosForCat(context: Context, catId: Int, photos: List<Photo>) {
        val prefs = context.getSharedPreferences("PhotoBooth", Context.MODE_PRIVATE).edit()
        prefs.putString("photos_cat_$catId", gson.toJson(photos))
        prefs.apply()
    }
}

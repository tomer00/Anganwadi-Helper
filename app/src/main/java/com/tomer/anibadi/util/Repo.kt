package com.tomer.anibadi.util

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.tomer.anibadi.modal.Mother

class Repo(private val con: Context) {
    private val prefAll by lazy { con.getSharedPreferences("mothers", Context.MODE_PRIVATE) }
    private val gson by lazy { Gson() }

    fun getALLMother(): List<Mother> {
        val type = object : TypeToken<Mother>() {}.type
        val retKi = mutableListOf<Mother>()

        for (entry: MutableMap.MutableEntry<String, out Any?> in prefAll.all.entries)
            retKi.add(gson.fromJson(entry.value.toString(), type))

        return retKi
    }

    fun getMother(id: String): Mother {
        return gson.fromJson(prefAll.getString(id, ""), Mother::class.java)
    }

    fun saveMother(mod: Mother) {
        prefAll.edit()
            .putString(mod.ID, gson.toJson(mod))
            .apply()
    }

}
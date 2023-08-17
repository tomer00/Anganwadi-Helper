package com.tomer.anibadi.util

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.tomer.anibadi.modal.Mother

class Repo(private val con: Context) {
    private val prefAll by lazy { con.getSharedPreferences("mothers", Context.MODE_PRIVATE) }
    private val gson by lazy { Gson() }

//    fun getALLMother(): List<Mother> {
//        val mutableList = mutableListOf<Mother>()
//        val random = Random
//        for (i in 0..12) mutableList.add(
//            Mother(i.toString(), (Char(100 + i)) + "Name $i", "02/02/1855", "Hname", "8554658454", "2518481984194", "02/02/2021", "20/12/2021", random.nextBoolean(),
//                random.nextBoolean(), listOf()
//            )
//        )
//        return mutableList
//    }

    fun getALLMother(): List<Mother> {
        val type = object : TypeToken<Mother>() {}.type
        val retKi = mutableListOf<Mother>()

        for (entry: MutableMap.MutableEntry<String, out Any?> in prefAll.all.entries)
            retKi.add(gson.fromJson(entry.value.toString(), type))

        return retKi
    }

    fun removeAll() {
        prefAll.edit().clear().apply()
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
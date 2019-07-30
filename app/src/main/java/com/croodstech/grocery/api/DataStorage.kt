package com.croodstech.grocery.api

import android.content.Context
import android.content.SharedPreferences

class DataStorage(private val SharedPreferenceName: String, private val ctx: Context) {
    private val pref: SharedPreferences
    private val writer: SharedPreferences.Editor

    init {
        pref = ctx.getSharedPreferences(this.SharedPreferenceName, Context.MODE_PRIVATE)
        writer = pref.edit()
    }

    fun write(key: String, value: Int) {
        writer.putInt(key, value)
        writer.commit()
    }

    fun write(key: String, value: Float) {
        writer.putFloat(key, value)
        writer.commit()
    }

    fun write(key: String, value: String) {
        writer.putString(key, value)
        writer.commit()
    }

    fun write(key: String, value: Boolean) {
        writer.putBoolean(key, value)
        writer.commit()
    }

    fun write(key: String, value: Long) {
        writer.putLong(key, value)
        writer.commit()
    }

    fun read(key: String, datatype: Int): Any? {
        var value: Any? = null
        if (datatype == INTEGER)
            value = pref.getInt(key, 0)
        else if (datatype == FLOAT)
            value = pref.getFloat(key, 0.0f)
        else if (datatype == STRING)
            value = pref.getString(key, "")
        else if (datatype == BOOLEAN)
            value = pref.getBoolean(key, false)
        else if (datatype == LONG)
            value = pref.getLong(key, 0)

        return value
    }

    companion object {
        val INTEGER = 1
        val FLOAT = 2
        val STRING = 3
        val BOOLEAN = 4
        val LONG = 5
    }
}

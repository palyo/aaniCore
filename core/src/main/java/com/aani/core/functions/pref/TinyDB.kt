package com.aani.core.functions.pref

import android.content.Context
import android.content.SharedPreferences

class TinyDB(context: Context?) {

    var instance: TinyDB? = null
    var mShare: SharedPreferences? = null

    init {
        instance = TinyDB(context)
        mShare = context!!.applicationContext.getSharedPreferences("honted_house_pref", 0)
    }

    fun putBoolean(key: String?, value: Boolean) {
        mShare!!.edit().putBoolean(key, value).apply()
    }

    fun putInt(key: String?, value: Int) {
        mShare!!.edit().putInt(key, value).apply()
    }

    fun putLong(key: String?, value: Long) {
        mShare!!.edit().putLong(key, value).apply()
    }

    fun getBoolean(key: String?, b: Boolean): Boolean {
        return mShare!!.getBoolean(key, b)
    }

    fun getInt(key: String?, value: Int): Int {
        return mShare!!.getInt(key, value)
    }

    fun getLong(key: String?): Long {
        return mShare!!.getLong(key, 0)
    }

    fun putString(key: String?, value: String?) {
        mShare!!.edit().putString(key, value).apply()
    }

    fun getString(key: String?): String? {
        return mShare!!.getString(key, "")
    }
}
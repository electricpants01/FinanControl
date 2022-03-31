package com.locotoDevTeam.financontrol.ui

import android.content.Context

class SharePreference(val context: Context) {

    private val preferences = context.getSharedPreferences(SHARE_PREFERENCE_NAME, Context.MODE_PRIVATE)

    companion object {
        const val SHARE_PREFERENCE_NAME = "FinanControlPreference"
        const val CURRENCY = "Currency"
    }

    fun saveCurrency(currency: String) {
        val editor = preferences.edit()
        editor.putString(CURRENCY, currency)
        editor.commit()
    }

    fun getCurrencyPreference(): String? {
        return preferences.getString(CURRENCY, "$")
    }


}
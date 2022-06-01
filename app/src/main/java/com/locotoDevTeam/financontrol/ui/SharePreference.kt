package com.locotoDevTeam.financontrol.ui

import android.content.Context

class SharePreference(val context: Context) {

    private val preferences = context.getSharedPreferences(SHARE_PREFERENCE_NAME, Context.MODE_PRIVATE)

    companion object {
        const val SHARE_PREFERENCE_NAME = "FinanControlPreference"
        const val CURRENCY = "Currency"
        const val SHOW_OVERVIEW_TOTAL = "ShowOverviewTotal"
    }

    fun saveCurrency(currency: String) {
        val editor = preferences.edit()
        editor.putString(CURRENCY, currency)
        editor.commit()
    }

    fun getCurrencyPreference(): String? {
        return preferences.getString(CURRENCY, "$")
    }

    // show overview total
    fun saveShowOverviewTotal(show: Boolean) {
        val editor = preferences.edit()
        editor.putBoolean(SHOW_OVERVIEW_TOTAL, show)
        editor.commit()
    }

    fun getShowOverviewTotal(): Boolean {
        return preferences.getBoolean(SHOW_OVERVIEW_TOTAL, true)
    }


}
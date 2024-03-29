/*
 * Copyright (c) 2021 Indra Azimi. All rights reserved.
 *
 * Dibuat untuk kelas Pemrograman untuk Perangkat Bergerak 2.
 * Dilarang melakukan penggandaan dan atau komersialisasi,
 * sebagian atau seluruh bagian, baik cetak maupun elektronik
 * terhadap project ini tanpa izin pemilik hak cipta.
 */

package com.indraazimi.covid19id.widget

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.SharedPreferences
import com.indraazimi.covid19id.model.Harian

object WidgetUtils {

    const val KEY_DATE = "date"
    const val KEY_DATA = "data"

    fun saveData(sharedPref: SharedPreferences, data: Harian) {
        with(sharedPref.edit()) {
            putLong(KEY_DATE, data.key)
            putInt(KEY_DATA, data.jumlahPositif.value)
            apply()
        }
    }

    fun updateUI(context: Context) {
        val appContext = context.applicationContext
        val widgetManager = AppWidgetManager.getInstance(appContext)
        val widgetIds = widgetManager.getAppWidgetIds(
            ComponentName(appContext, CovidWidgetProvider::class.java)
        )
        CovidWidgetProvider.updateAllWidget(appContext, widgetManager, widgetIds)
    }
}
package com.victorprata.contagem.data

import android.content.Context
import androidx.core.content.edit
import java.time.LocalDate
import kotlin.math.max
import kotlin.math.min

/** Guarda a contagem aplicada e alguns dados de controle em SharedPreferences. */
class SettingsRepository(context: Context) {

    private val appContext = context.applicationContext
    private val prefs = appContext.getSharedPreferences("contagem", Context.MODE_PRIVATE)

    fun load(): CountdownSettings? {
        if (!prefs.contains(KEY_TARGET)) return null
        return CountdownSettings(
            targetDate = LocalDate.ofEpochDay(prefs.getLong(KEY_TARGET, 0)),
            eventName = prefs.getString(KEY_EVENT, null).orEmpty(),
            presetIndex = prefs.getInt(KEY_PRESET, 0),
            photoFileName = prefs.getString(KEY_PHOTO, null),
            verticalPosition = prefs.getFloat(KEY_POSITION, 0.62f),
            showDate = prefs.getBoolean(KEY_SHOW_DATE, true),
        )
    }

    fun save(settings: CountdownSettings) = prefs.edit {
        putLong(KEY_TARGET, settings.targetDate.toEpochDay())
        putString(KEY_EVENT, settings.eventName)
        putInt(KEY_PRESET, settings.presetIndex)
        putString(KEY_PHOTO, settings.photoFileName)
        putFloat(KEY_POSITION, settings.verticalPosition)
        putBoolean(KEY_SHOW_DATE, settings.showDate)
    }

    fun isAutoUpdateOn(): Boolean = prefs.getBoolean(KEY_AUTO_UPDATE, false)

    fun setAutoUpdate(on: Boolean) = prefs.edit { putBoolean(KEY_AUTO_UPDATE, on) }

    fun lastAppliedDay(): LocalDate? =
        prefs.getLong(KEY_LAST_APPLIED, Long.MIN_VALUE)
            .takeIf { it != Long.MIN_VALUE }
            ?.let(LocalDate::ofEpochDay)

    fun saveLastAppliedDay(day: LocalDate) = prefs.edit { putLong(KEY_LAST_APPLIED, day.toEpochDay()) }

    /** Tamanho real da tela em retrato (largura menor que altura). */
    fun screenSize(): ScreenSize {
        val w = prefs.getInt(KEY_SCREEN_W, 0)
        val h = prefs.getInt(KEY_SCREEN_H, 0)
        if (w > 0 && h > 0) return ScreenSize(w, h)
        val metrics = appContext.resources.displayMetrics
        return ScreenSize.portrait(metrics.widthPixels, metrics.heightPixels)
    }

    fun saveScreenSize(size: ScreenSize) = prefs.edit {
        putInt(KEY_SCREEN_W, size.width)
        putInt(KEY_SCREEN_H, size.height)
    }

    private companion object {
        const val KEY_TARGET = "target_epoch_day"
        const val KEY_EVENT = "event_name"
        const val KEY_PRESET = "preset_index"
        const val KEY_PHOTO = "photo_file"
        const val KEY_POSITION = "vertical_position"
        const val KEY_SHOW_DATE = "show_date"
        const val KEY_AUTO_UPDATE = "auto_update"
        const val KEY_LAST_APPLIED = "last_applied_day"
        const val KEY_SCREEN_W = "screen_w"
        const val KEY_SCREEN_H = "screen_h"
    }
}

data class ScreenSize(val width: Int, val height: Int) {
    companion object {
        fun portrait(a: Int, b: Int) = ScreenSize(min(a, b), max(a, b))
    }
}

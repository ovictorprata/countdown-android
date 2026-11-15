package com.victorprata.contagem.wallpaper

import android.app.WallpaperManager
import android.content.Context
import android.graphics.Bitmap
import com.victorprata.contagem.data.PhotoStore
import com.victorprata.contagem.data.SettingsRepository
import com.victorprata.contagem.render.WallpaperRenderer
import java.time.LocalDate

/** Gera a imagem do dia e aplica só na tela de bloqueio (a tela inicial não muda). */
class WallpaperUpdater(context: Context) {

    private val appContext = context.applicationContext
    private val repository = SettingsRepository(appContext)
    private val photoStore = PhotoStore(appContext)
    private val renderer = WallpaperRenderer()

    fun updateNow(): Boolean {
        val settings = repository.load() ?: return false
        val size = repository.screenSize()
        val today = LocalDate.now()
        val photo = photoStore.load(settings.photoFileName)
        val bitmap = renderer.render(settings, size.width, size.height, photo, today)
        val applied = setLockScreen(bitmap)
        bitmap.recycle()
        photo?.recycle()
        if (applied) repository.saveLastAppliedDay(today)
        return applied
    }

    /** Usado ao abrir o app: corrige o número se a atualização da meia-noite atrasou. */
    fun updateIfStale(): Boolean =
        if (repository.lastAppliedDay() == LocalDate.now()) true else updateNow()

    private fun setLockScreen(bitmap: Bitmap): Boolean = runCatching {
        WallpaperManager.getInstance(appContext)
            .setBitmap(bitmap, null, true, WallpaperManager.FLAG_LOCK)
        true
    }.getOrDefault(false)
}

package com.victorprata.contagem.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.victorprata.contagem.data.SettingsRepository
import com.victorprata.contagem.wallpaper.WallpaperUpdater

/** Roda logo depois da meia-noite, troca o número e agenda a próxima meia-noite. */
class DailyUpdateWorker(
    context: Context,
    params: WorkerParameters,
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        if (!SettingsRepository(applicationContext).isAutoUpdateOn()) return Result.success()

        val applied = WallpaperUpdater(applicationContext).updateNow()
        if (!applied && runAttemptCount < MAX_RETRIES) return Result.retry()

        UpdateScheduler.scheduleNextFromWorker(applicationContext)
        return Result.success()
    }

    private companion object {
        const val MAX_RETRIES = 3
    }
}

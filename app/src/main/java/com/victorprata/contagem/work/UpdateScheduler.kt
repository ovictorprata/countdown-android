package com.victorprata.contagem.work

import android.content.Context
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import java.time.Duration
import java.time.LocalDateTime
import java.util.concurrent.TimeUnit

/**
 * Agenda uma atualização para 00:01 do dia seguinte.
 * O WorkManager mantém o agendamento mesmo se o celular reiniciar.
 */
object UpdateScheduler {

    private const val WORK_NAME = "atualizacao-diaria"

    /** Chamado pelo app: substitui qualquer agendamento anterior. */
    fun scheduleFromApp(context: Context) = enqueue(context, ExistingWorkPolicy.REPLACE)

    /**
     * Chamado pelo próprio worker. APPEND_OR_REPLACE encadeia o próximo
     * depois do atual, em vez de cancelar o que está rodando.
     */
    fun scheduleNextFromWorker(context: Context) = enqueue(context, ExistingWorkPolicy.APPEND_OR_REPLACE)

    /** Ao abrir o app: garante que existe um agendamento (ex.: após restaurar backup), sem mexer no atual. */
    fun ensureScheduled(context: Context) = enqueue(context, ExistingWorkPolicy.KEEP)

    fun cancel(context: Context) {
        WorkManager.getInstance(context).cancelUniqueWork(WORK_NAME)
    }

    private fun enqueue(context: Context, policy: ExistingWorkPolicy) {
        val now = LocalDateTime.now()
        val nextRun = now.toLocalDate().plusDays(1).atStartOfDay().plusMinutes(1)
        val delay = Duration.between(now, nextRun).toMillis()

        val request = OneTimeWorkRequestBuilder<DailyUpdateWorker>()
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .build()

        WorkManager.getInstance(context).enqueueUniqueWork(WORK_NAME, policy, request)
    }
}

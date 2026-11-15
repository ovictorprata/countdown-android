package com.victorprata.contagem.domain

import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Locale

object Formats {
    private val ptBr: Locale = Locale.forLanguageTag("pt-BR")
    private val longDate = DateTimeFormatter.ofPattern("d 'de' MMMM 'de' yyyy", ptBr)

    fun longDate(date: LocalDate): String = longDate.format(date)

    /** Resumo exibido no app, ex.: "Faltam 42 dias". */
    fun remaining(days: Long): String = when {
        days == 0L -> "É hoje!"
        days == 1L -> "Falta 1 dia"
        days > 1 -> "Faltam $days dias"
        days == -1L -> "Foi ontem"
        else -> "Foi há ${-days} dias"
    }
}

/** O DatePicker do Material trabalha com milissegundos em UTC. */
fun LocalDate.toUtcMillis(): Long = atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()

fun Long.utcMillisToLocalDate(): LocalDate = Instant.ofEpochMilli(this).atZone(ZoneOffset.UTC).toLocalDate()

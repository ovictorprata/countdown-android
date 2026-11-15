package com.victorprata.contagem.domain

import java.time.LocalDate
import java.time.temporal.ChronoUnit
import kotlin.math.abs

/**
 * Os textos que aparecem na tela de bloqueio.
 * Ex.: headline "42", label "DIAS PARA", eventName "as férias".
 */
data class CountdownText(
    val headline: String,
    val label: String,
    val eventName: String,
) {
    companion object {
        fun from(target: LocalDate, eventName: String, today: LocalDate = LocalDate.now()): CountdownText {
            val days = daysUntil(target, today)
            return when {
                days == 0L -> CountdownText("É hoje", "", eventName)
                days > 0 -> CountdownText(days.toString(), if (days == 1L) "dia para" else "dias para", eventName)
                else -> {
                    val past = abs(days)
                    CountdownText(past.toString(), if (past == 1L) "dia desde" else "dias desde", eventName)
                }
            }
        }

        fun daysUntil(target: LocalDate, today: LocalDate = LocalDate.now()): Long =
            ChronoUnit.DAYS.between(today, target)
    }
}

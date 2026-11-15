package com.victorprata.contagem.data

import java.time.LocalDate

/** Tudo o que define como a contagem aparece na tela de bloqueio. */
data class CountdownSettings(
    val targetDate: LocalDate,
    val eventName: String = "o grande dia",
    val presetIndex: Int = 0,
    /** Nome do arquivo da foto de fundo em filesDir; null = usa o degradê escolhido. */
    val photoFileName: String? = null,
    /** Altura do bloco de texto: 0 = topo da tela, 1 = rodapé. */
    val verticalPosition: Float = 0.62f,
    val showDate: Boolean = true,
) {
    companion object {
        fun default() = CountdownSettings(targetDate = LocalDate.now().plusDays(30))
    }
}

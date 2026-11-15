package com.victorprata.contagem.render

/** Degradês de fundo prontos (cor do topo → cor da base), em ARGB. */
data class BackgroundPreset(val name: String, val top: Int, val bottom: Int)

object BackgroundPresets {
    val all = listOf(
        BackgroundPreset("Noite", 0xFF0F172A.toInt(), 0xFF1E3A8A.toInt()),
        BackgroundPreset("Ameixa", 0xFF2E1065.toInt(), 0xFF9D174D.toInt()),
        BackgroundPreset("Floresta", 0xFF052E2B.toInt(), 0xFF166534.toInt()),
        BackgroundPreset("Pôr do sol", 0xFF7C2D12.toInt(), 0xFFDB2777.toInt()),
        BackgroundPreset("Grafite", 0xFF111111.toInt(), 0xFF3F3F46.toInt()),
    )

    fun get(index: Int): BackgroundPreset = all.getOrElse(index) { all.first() }
}

package com.victorprata.contagem.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/** Limites que evitam o relógio do sistema (topo) e a área de desbloqueio (rodapé). */
private val POSITION_RANGE = 0.35f..0.85f

@Composable
fun PositionSection(position: Float, onPositionChange: (Float) -> Unit) {
    SectionCard(title = "Posição na tela") {
        Slider(
            value = position.coerceIn(POSITION_RANGE),
            onValueChange = onPositionChange,
            valueRange = POSITION_RANGE,
        )
        Row {
            Label("Mais acima")
            Spacer(Modifier.weight(1f))
            Label("Mais abaixo")
        }
    }
}

@Composable
private fun Label(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
}

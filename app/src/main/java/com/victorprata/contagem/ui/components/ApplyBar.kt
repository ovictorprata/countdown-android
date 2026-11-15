package com.victorprata.contagem.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/** Barra fixa no rodapé: o botão principal sempre à vista, com o estado atual da contagem. */
@Composable
fun ApplyBar(
    isActive: Boolean,
    hasPendingChanges: Boolean,
    isApplying: Boolean,
    onApply: () -> Unit,
    onStop: () -> Unit,
) {
    val label = when {
        isApplying -> "Aplicando…"
        !isActive -> "Aplicar na tela de bloqueio"
        hasPendingChanges -> "Aplicar alterações"
        else -> "Aplicado ✓"
    }

    Surface(tonalElevation = 3.dp) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 20.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Button(
                onClick = onApply,
                enabled = !isApplying && (!isActive || hasPendingChanges),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
            ) { Text(label) }

            if (isActive) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Hint("Atualiza sozinho toda meia-noite", Modifier.weight(1f))
                    TextButton(onClick = onStop) { Text("Desligar") }
                }
            } else {
                Hint("Troca só o fundo da tela de bloqueio. A tela inicial continua igual.")
            }
        }
    }
}

@Composable
private fun Hint(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        modifier = modifier,
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
}

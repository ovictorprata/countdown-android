package com.victorprata.contagem.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.victorprata.contagem.render.BackgroundPreset
import com.victorprata.contagem.render.BackgroundPresets

@Composable
fun BackgroundSection(
    presetIndex: Int,
    hasPhoto: Boolean,
    onPresetChosen: (Int) -> Unit,
    onPickPhoto: () -> Unit,
    onRemovePhoto: () -> Unit,
) {
    SectionCard(title = "Fundo") {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            BackgroundPresets.all.forEachIndexed { index, preset ->
                PresetSwatch(
                    preset = preset,
                    selected = !hasPhoto && index == presetIndex,
                    onClick = { onPresetChosen(index) },
                )
            }
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            OutlinedButton(onClick = onPickPhoto) {
                Text(if (hasPhoto) "Trocar foto" else "Usar uma foto")
            }
            if (hasPhoto) {
                TextButton(onClick = onRemovePhoto) { Text("Remover foto") }
            }
        }
    }
}

@Composable
private fun PresetSwatch(preset: BackgroundPreset, selected: Boolean, onClick: () -> Unit) {
    val ringColor = if (selected) MaterialTheme.colorScheme.primary else Color.Transparent
    Box(
        modifier = Modifier
            .size(48.dp)
            .border(3.dp, ringColor, CircleShape)
            .clip(CircleShape)
            .clickable(role = Role.RadioButton, onClick = onClick)
            .semantics {
                contentDescription = "Fundo ${preset.name}"
                this.selected = selected
            },
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(CircleShape)
                .background(Brush.verticalGradient(listOf(Color(preset.top), Color(preset.bottom)))),
        )
    }
}

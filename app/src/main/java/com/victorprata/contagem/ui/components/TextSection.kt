package com.victorprata.contagem.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.victorprata.contagem.domain.CountdownText
import java.time.LocalDate

@Composable
fun TextSection(
    targetDate: LocalDate,
    eventName: String,
    showDate: Boolean,
    onEventNameChange: (String) -> Unit,
    onShowDateChange: (Boolean) -> Unit,
) {
    val sample = CountdownText.from(targetDate, eventName.ifBlank { "…" })

    SectionCard(title = "Texto") {
        OutlinedTextField(
            value = eventName,
            onValueChange = onEventNameChange,
            label = { Text("Nome do evento") },
            placeholder = { Text("Ex.: as férias") },
            supportingText = { Text("Vai aparecer: ${sample.headline} ${sample.label} ${sample.eventName}".replace("  ", " ")) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "Mostrar a data",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f),
            )
            Switch(checked = showDate, onCheckedChange = onShowDateChange)
        }
    }
}

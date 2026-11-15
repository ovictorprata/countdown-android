@file:OptIn(ExperimentalMaterial3Api::class)

package com.victorprata.contagem.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import com.victorprata.contagem.domain.CountdownText
import com.victorprata.contagem.domain.Formats
import com.victorprata.contagem.domain.toUtcMillis
import com.victorprata.contagem.domain.utcMillisToLocalDate
import java.time.LocalDate

private const val MODE_DATE = 0
private const val MODE_DAYS = 1

/** Duas formas de definir o alvo: escolher a data no calendário ou digitar quantos dias faltam. */
@Composable
fun TargetDateSection(
    date: LocalDate,
    onDateChosen: (LocalDate) -> Unit,
    onDaysTyped: (Int) -> Unit,
) {
    var mode by rememberSaveable { mutableIntStateOf(MODE_DATE) }
    var showPicker by remember { mutableStateOf(false) }

    SectionCard(title = "Quando é?") {
        SingleChoiceSegmentedButtonRow(Modifier.fillMaxWidth()) {
            listOf("Escolher data", "Digitar dias").forEachIndexed { index, label ->
                SegmentedButton(
                    selected = mode == index,
                    onClick = { mode = index },
                    shape = SegmentedButtonDefaults.itemShape(index = index, count = 2),
                ) { Text(label) }
            }
        }

        if (mode == MODE_DATE) {
            OutlinedButton(onClick = { showPicker = true }, modifier = Modifier.fillMaxWidth()) {
                Text(Formats.longDate(date))
            }
        } else {
            DaysField(date = date, onDaysTyped = onDaysTyped)
        }

        Text(
            text = Formats.remaining(CountdownText.daysUntil(date)),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.primary,
        )
    }

    if (showPicker) {
        TargetDatePickerDialog(
            initial = date,
            onConfirm = {
                onDateChosen(it)
                showPicker = false
            },
            onDismiss = { showPicker = false },
        )
    }
}

@Composable
private fun DaysField(date: LocalDate, onDaysTyped: (Int) -> Unit) {
    var text by rememberSaveable {
        mutableStateOf(CountdownText.daysUntil(date).coerceAtLeast(0).toString())
    }
    OutlinedTextField(
        value = text,
        onValueChange = { input ->
            val digits = input.filter(Char::isDigit).take(5)
            text = digits
            digits.toIntOrNull()?.let(onDaysTyped)
        },
        label = { Text("Faltam quantos dias?") },
        supportingText = { Text("Cai em ${Formats.longDate(date)}") },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        singleLine = true,
        modifier = Modifier.fillMaxWidth(),
    )
}

@Composable
private fun TargetDatePickerDialog(
    initial: LocalDate,
    onConfirm: (LocalDate) -> Unit,
    onDismiss: () -> Unit,
) {
    val todayMillis = remember { LocalDate.now().toUtcMillis() }
    val onlyFuture = remember {
        object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long) = utcTimeMillis >= todayMillis
        }
    }
    val state = rememberDatePickerState(
        initialSelectedDateMillis = initial.toUtcMillis(),
        selectableDates = onlyFuture,
    )

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = { state.selectedDateMillis?.let { onConfirm(it.utcMillisToLocalDate()) } ?: onDismiss() },
            ) { Text("OK") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } },
    ) {
        DatePicker(state = state)
    }
}

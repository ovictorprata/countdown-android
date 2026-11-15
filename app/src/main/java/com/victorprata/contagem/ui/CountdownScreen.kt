package com.victorprata.contagem.ui

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.victorprata.contagem.ui.components.ApplyBar
import com.victorprata.contagem.ui.components.BackgroundSection
import com.victorprata.contagem.ui.components.LockScreenPreview
import com.victorprata.contagem.ui.components.PositionSection
import com.victorprata.contagem.ui.components.TargetDateSection
import com.victorprata.contagem.ui.components.TextSection

/** Liga o ViewModel à tela. A tela em si só recebe estado e callbacks. */
@Composable
fun CountdownRoute(viewModel: CountdownViewModel = viewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbar = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.messages.collect { snackbar.showSnackbar(it) }
    }

    // Seletor de fotos do sistema: não pede permissão de galeria.
    val photoPicker = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) viewModel.onPhotoPicked(uri)
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbar) },
        bottomBar = {
            ApplyBar(
                isActive = state.isActive,
                hasPendingChanges = state.hasPendingChanges,
                isApplying = state.isApplying,
                onApply = viewModel::apply,
                onStop = viewModel::stopAutoUpdate,
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(
                text = "Contagem na tela de bloqueio",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(top = 16.dp),
            )

            LockScreenPreview(
                preview = state.preview,
                modifier = Modifier.align(Alignment.CenterHorizontally),
            )

            TargetDateSection(
                date = state.draft.targetDate,
                onDateChosen = viewModel::onDateChosen,
                onDaysTyped = viewModel::onDaysTyped,
            )

            TextSection(
                targetDate = state.draft.targetDate,
                eventName = state.draft.eventName,
                showDate = state.draft.showDate,
                onEventNameChange = viewModel::onEventNameChange,
                onShowDateChange = viewModel::onShowDateChange,
            )

            BackgroundSection(
                presetIndex = state.draft.presetIndex,
                hasPhoto = state.draft.photoFileName != null,
                onPresetChosen = viewModel::onPresetChosen,
                onPickPhoto = {
                    photoPicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                },
                onRemovePhoto = viewModel::onRemovePhoto,
            )

            PositionSection(
                position = state.draft.verticalPosition,
                onPositionChange = viewModel::onPositionChange,
            )

            Spacer(Modifier.height(8.dp))
        }
    }
}

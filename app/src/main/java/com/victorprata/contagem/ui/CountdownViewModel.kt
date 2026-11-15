package com.victorprata.contagem.ui

import android.app.Application
import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.victorprata.contagem.data.CountdownSettings
import com.victorprata.contagem.data.PhotoStore
import com.victorprata.contagem.data.SettingsRepository
import com.victorprata.contagem.render.WallpaperRenderer
import com.victorprata.contagem.wallpaper.WallpaperUpdater
import com.victorprata.contagem.work.UpdateScheduler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import kotlin.math.roundToInt

data class CountdownUiState(
    /** O que está sendo editado na tela. */
    val draft: CountdownSettings,
    /** O que está na tela de bloqueio agora; null se a contagem não está ativa. */
    val applied: CountdownSettings?,
    val preview: ImageBitmap? = null,
    val isApplying: Boolean = false,
) {
    val isActive get() = applied != null
    val hasPendingChanges get() = draft != applied
}

class CountdownViewModel(app: Application) : AndroidViewModel(app) {

    private val repository = SettingsRepository(app)
    private val photoStore = PhotoStore(app)
    private val renderer = WallpaperRenderer()
    private val updater = WallpaperUpdater(app)

    private val _state = MutableStateFlow(initialState())
    val state: StateFlow<CountdownUiState> = _state.asStateFlow()

    private val _messages = Channel<String>(Channel.BUFFERED)
    val messages = _messages.receiveAsFlow()

    private var previewJob: Job? = null
    private var photoCache: Pair<String, Bitmap>? = null

    init {
        refreshPreview()
        if (repository.isAutoUpdateOn()) {
            UpdateScheduler.ensureScheduled(app)
            viewModelScope.launch(Dispatchers.IO) { updater.updateIfStale() }
        }
    }

    private fun initialState(): CountdownUiState {
        val saved = repository.load()
        return CountdownUiState(
            draft = saved ?: CountdownSettings.default(),
            applied = saved?.takeIf { repository.isAutoUpdateOn() },
        )
    }

    // ---- Edição ----

    fun onDateChosen(date: LocalDate) = editDraft { it.copy(targetDate = date) }

    fun onDaysTyped(days: Int) = editDraft { it.copy(targetDate = LocalDate.now().plusDays(days.toLong())) }

    fun onEventNameChange(name: String) = editDraft { it.copy(eventName = name.take(MAX_EVENT_LENGTH)) }

    fun onShowDateChange(show: Boolean) = editDraft { it.copy(showDate = show) }

    fun onPositionChange(position: Float) = editDraft { it.copy(verticalPosition = position) }

    fun onPresetChosen(index: Int) = editDraft { it.copy(presetIndex = index, photoFileName = null) }

    fun onRemovePhoto() = editDraft { it.copy(photoFileName = null) }

    fun onPhotoPicked(uri: Uri) {
        viewModelScope.launch {
            val shortSide = repository.screenSize().width
            val name = withContext(Dispatchers.IO) { photoStore.importPhoto(uri, shortSide) }
            if (name == null) {
                _messages.send("Não consegui abrir essa imagem. Tente outra.")
            } else {
                editDraft { it.copy(photoFileName = name) }
            }
        }
    }

    // ---- Ações ----

    fun apply() {
        val draft = _state.value.draft
        _state.update { it.copy(isApplying = true) }
        viewModelScope.launch {
            val ok = withContext(Dispatchers.IO) {
                repository.save(draft)
                repository.setAutoUpdate(true)
                updater.updateNow()
            }
            if (ok) {
                UpdateScheduler.scheduleFromApp(getApplication())
                withContext(Dispatchers.IO) { photoStore.deleteAllExcept(draft.photoFileName) }
                _state.update { it.copy(applied = draft, isApplying = false) }
                _messages.send("Pronto! Bloqueie o celular para ver.")
            } else {
                // Volta ao estado anterior para a meia-noite não aplicar algo que falhou agora.
                val previous = _state.value.applied
                withContext(Dispatchers.IO) {
                    if (previous != null) repository.save(previous) else repository.setAutoUpdate(false)
                }
                _state.update { it.copy(isApplying = false) }
                _messages.send("O celular não permitiu trocar o papel de parede.")
            }
        }
    }

    fun stopAutoUpdate() {
        repository.setAutoUpdate(false)
        UpdateScheduler.cancel(getApplication())
        _state.update { it.copy(applied = null) }
        viewModelScope.launch {
            _messages.send("Atualização desligada. A imagem atual fica até você trocar o papel de parede.")
        }
    }

    // ---- Prévia ----

    private fun editDraft(transform: (CountdownSettings) -> CountdownSettings) {
        _state.update { it.copy(draft = transform(it.draft)) }
        refreshPreview()
    }

    private fun refreshPreview() {
        previewJob?.cancel()
        val draft = _state.value.draft
        previewJob = viewModelScope.launch {
            val image = withContext(Dispatchers.Default) {
                val screen = repository.screenSize()
                val height = (PREVIEW_WIDTH * screen.height / screen.width.toFloat()).roundToInt()
                renderer.render(draft, PREVIEW_WIDTH, height, previewPhoto(draft.photoFileName)).asImageBitmap()
            }
            _state.update { it.copy(preview = image) }
        }
    }

    /** Guarda a foto já decodificada para a prévia não reabrir o arquivo a cada tecla. */
    private fun previewPhoto(name: String?): Bitmap? {
        if (name == null) return null
        photoCache?.let { (cachedName, bitmap) -> if (cachedName == name) return bitmap }
        return photoStore.load(name, sampleSize = 2)?.also { photoCache = name to it }
    }

    private companion object {
        const val PREVIEW_WIDTH = 540
        const val MAX_EVENT_LENGTH = 40
    }
}

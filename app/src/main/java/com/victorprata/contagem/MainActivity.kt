package com.victorprata.contagem

import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.victorprata.contagem.data.ScreenSize
import com.victorprata.contagem.data.SettingsRepository
import com.victorprata.contagem.ui.CountdownRoute
import com.victorprata.contagem.ui.theme.ContagemTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        // A atualização da meia-noite não tem tela aberta; por isso o tamanho fica salvo.
        SettingsRepository(this).saveScreenSize(realScreenSize())
        setContent {
            ContagemTheme {
                CountdownRoute()
            }
        }
    }

    private fun realScreenSize(): ScreenSize {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val bounds = windowManager.maximumWindowMetrics.bounds
            return ScreenSize.portrait(bounds.width(), bounds.height())
        }
        val metrics = DisplayMetrics()
        @Suppress("DEPRECATION")
        windowManager.defaultDisplay.getRealMetrics(metrics)
        return ScreenSize.portrait(metrics.widthPixels, metrics.heightPixels)
    }
}

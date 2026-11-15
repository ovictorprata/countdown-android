package com.victorprata.contagem.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.LocalTime
import java.time.format.DateTimeFormatter

/**
 * Celular em miniatura com a imagem que será aplicada.
 * O relógio é ilustrativo: mostra que o sistema desenha por cima do papel de parede.
 */
@Composable
fun LockScreenPreview(preview: ImageBitmap?, modifier: Modifier = Modifier) {
    val shape = RoundedCornerShape(24.dp)
    val aspect = preview?.let { it.width / it.height.toFloat() } ?: (9f / 19.5f)

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Box(
            modifier = Modifier
                .width(190.dp)
                .aspectRatio(aspect)
                .clip(shape)
                .background(Color.Black)
                .border(1.dp, MaterialTheme.colorScheme.outlineVariant, shape),
        ) {
            if (preview != null) {
                Image(
                    bitmap = preview,
                    contentDescription = "Prévia da tela de bloqueio",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize(),
                )
            }
            IllustrativeClock(Modifier.align(Alignment.TopCenter).padding(top = 26.dp))
        }
        Text(
            text = "Relógio e notificações do celular aparecem por cima",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun IllustrativeClock(modifier: Modifier = Modifier) {
    val time = remember { LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm")) }
    Text(
        text = time,
        modifier = modifier,
        color = Color.White.copy(alpha = 0.55f),
        fontSize = 36.sp,
        fontWeight = FontWeight.Light,
    )
}

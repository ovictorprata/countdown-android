package com.victorprata.contagem.render

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Shader
import android.graphics.Typeface
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import com.victorprata.contagem.data.CountdownSettings
import com.victorprata.contagem.domain.CountdownText
import com.victorprata.contagem.domain.Formats
import java.time.LocalDate
import kotlin.math.max

/**
 * Desenha o papel de parede: fundo (foto ou degradê) + bloco de texto da contagem.
 * É a mesma função para a prévia do app e para a imagem real, então o que se vê é o que se aplica.
 */
class WallpaperRenderer {

    fun render(
        settings: CountdownSettings,
        width: Int,
        height: Int,
        photo: Bitmap?,
        today: LocalDate = LocalDate.now(),
    ): Bitmap {
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        if (photo != null) drawPhoto(canvas, photo) else drawGradient(canvas, BackgroundPresets.get(settings.presetIndex))
        drawCountdown(canvas, settings, today)
        return bitmap
    }

    /** Foto em "center crop" + véu escuro para o texto branco continuar legível. */
    private fun drawPhoto(canvas: Canvas, photo: Bitmap) {
        val scale = max(canvas.width / photo.width.toFloat(), canvas.height / photo.height.toFloat())
        val w = photo.width * scale
        val h = photo.height * scale
        val left = (canvas.width - w) / 2f
        val top = (canvas.height - h) / 2f
        canvas.drawBitmap(photo, null, RectF(left, top, left + w, top + h), Paint(Paint.FILTER_BITMAP_FLAG))
        canvas.drawColor(0x59000000)
    }

    private fun drawGradient(canvas: Canvas, preset: BackgroundPreset) {
        val paint = Paint().apply {
            shader = LinearGradient(0f, 0f, 0f, canvas.height.toFloat(), preset.top, preset.bottom, Shader.TileMode.CLAMP)
        }
        canvas.drawRect(0f, 0f, canvas.width.toFloat(), canvas.height.toFloat(), paint)
    }

    private fun drawCountdown(canvas: Canvas, settings: CountdownSettings, today: LocalDate) {
        val text = CountdownText.from(settings.targetDate, settings.eventName, today)
        val w = canvas.width.toFloat()
        val boxWidth = (w * 0.84f).toInt()

        val blocks = buildList {
            add(layout(text.headline, textPaint(w * 0.30f, "sans-serif-light"), boxWidth))
            if (text.label.isNotEmpty()) {
                val labelPaint = textPaint(w * 0.055f, "sans-serif-medium").apply { letterSpacing = 0.18f }
                add(layout(text.label.uppercase(), labelPaint, boxWidth))
            }
            if (text.eventName.isNotBlank()) {
                add(layout(text.eventName.trim(), textPaint(w * 0.068f, "sans-serif"), boxWidth))
            }
            if (settings.showDate) {
                val datePaint = textPaint(w * 0.040f, "sans-serif").apply { alpha = 190 }
                add(layout(Formats.longDate(settings.targetDate), datePaint, boxWidth))
            }
        }

        val gap = w * 0.02f
        val totalHeight = blocks.sumOf { it.height } + gap * (blocks.size - 1)
        var y = canvas.height * settings.verticalPosition - totalHeight / 2f
        val x = (w - boxWidth) / 2f

        for (block in blocks) {
            canvas.save()
            canvas.translate(x, y)
            block.draw(canvas)
            canvas.restore()
            y += block.height + gap
        }
    }

    private fun textPaint(size: Float, family: String) = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
        color = 0xFFFFFFFF.toInt()
        textSize = size
        typeface = Typeface.create(family, Typeface.NORMAL)
        setShadowLayer(size * 0.08f, 0f, size * 0.03f, 0x66000000)
    }

    private fun layout(text: String, paint: TextPaint, width: Int): StaticLayout =
        StaticLayout.Builder.obtain(text, 0, text.length, paint, width)
            .setAlignment(Layout.Alignment.ALIGN_CENTER)
            .setIncludePad(false)
            .build()
}

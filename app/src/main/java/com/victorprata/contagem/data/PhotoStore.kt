package com.victorprata.contagem.data

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import java.io.File
import kotlin.math.max
import kotlin.math.min

/**
 * Copia a foto escolhida para dentro do app. Assim a atualização da meia-noite
 * não depende de permissão para ler a galeria.
 */
class PhotoStore(context: Context) {

    private val appContext = context.applicationContext
    private val dir = File(appContext.filesDir, "fotos").apply { mkdirs() }

    /** Retorna o nome do arquivo salvo, ou null se a imagem não puder ser lida. */
    fun importPhoto(uri: Uri, screenShortSide: Int): String? = runCatching {
        val bitmap = decode(uri, screenShortSide) ?: return null
        val name = "fundo_${System.currentTimeMillis()}.jpg"
        File(dir, name).outputStream().use { bitmap.compress(Bitmap.CompressFormat.JPEG, 92, it) }
        bitmap.recycle()
        name
    }.getOrNull()

    fun load(name: String?, sampleSize: Int = 1): Bitmap? {
        val file = name?.let { File(dir, it) }?.takeIf(File::exists) ?: return null
        val options = BitmapFactory.Options().apply { inSampleSize = sampleSize }
        return BitmapFactory.decodeFile(file.absolutePath, options)
    }

    /** Apaga fotos antigas que nenhuma contagem usa mais. */
    fun deleteAllExcept(keep: String?) {
        dir.listFiles()?.filter { it.name != keep }?.forEach(File::delete)
    }

    private fun decode(uri: Uri, targetShortSide: Int): Bitmap? {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val source = ImageDecoder.createSource(appContext.contentResolver, uri)
            return ImageDecoder.decodeBitmap(source) { decoder, info, _ ->
                decoder.setTargetSampleSize(sampleSizeFor(info.size.width, info.size.height, targetShortSide))
                // Bitmap de software: o de hardware não pode ser desenhado num Canvas comum.
                decoder.allocator = ImageDecoder.ALLOCATOR_SOFTWARE
            }
        }
        val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        appContext.contentResolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it, null, bounds) }
        val options = BitmapFactory.Options().apply {
            inSampleSize = sampleSizeFor(bounds.outWidth, bounds.outHeight, targetShortSide)
        }
        return appContext.contentResolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it, null, options) }
    }

    /** Reduz a foto em potências de 2 sem ficar menor que a tela. */
    private fun sampleSizeFor(width: Int, height: Int, targetShortSide: Int): Int {
        var sample = 1
        val shortSide = min(width, height)
        while (shortSide / (sample * 2) >= max(targetShortSide, 1)) sample *= 2
        return sample
    }
}

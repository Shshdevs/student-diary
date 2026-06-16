package com.praktika.studentdiary.data.source

import android.content.Context
import android.net.Uri
import com.tom_roush.pdfbox.android.PDFBoxResourceLoader
import com.tom_roush.pdfbox.pdmodel.PDDocument
import com.tom_roush.pdfbox.text.PDFTextStripper
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.InputStream
import javax.inject.Inject

class PdfParserSourceImpl @Inject constructor(
    @ApplicationContext private val context: Context,
) : PdfParserSource {

    init {
        PDFBoxResourceLoader.init(context)
    }

    override suspend fun extractTextFromPdf(uri: Uri): String = withContext(Dispatchers.IO) {
        var document: PDDocument? = null
        try {
            val inputStream: InputStream = context.contentResolver.openInputStream(uri)
                ?: throw Exception("Не удалось открыть поток для файла")

            document = PDDocument.load(inputStream)

            val stripper = PDFTextStripper()

            val text = stripper.getText(document)

            return@withContext text
        } catch (e: Exception) {
            e.printStackTrace()
            throw Exception("Ошибка при парсинге PDF: ${e.message}")
        } finally {
            document?.close()
        }
    }
}
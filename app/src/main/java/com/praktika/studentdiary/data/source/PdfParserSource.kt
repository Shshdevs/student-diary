package com.praktika.studentdiary.data.source

import android.net.Uri

interface PdfParserSource {
    suspend fun extractTextFromPdf(uri: Uri): String
}
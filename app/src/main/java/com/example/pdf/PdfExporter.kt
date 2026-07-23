package com.example.pdf

import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.util.Log
import androidx.core.content.FileProvider
import com.example.data.model.CompanyLetterhead
import com.example.data.model.Meeting
import java.io.File
import java.io.FileOutputStream

class PdfExporter(private val context: Context) {

    fun generateMeetingMinutesPdf(meeting: Meeting, letterhead: CompanyLetterhead): File? {
        val pdfDocument = PdfDocument()
        val pageWidth = 595 // A4 standard width in points
        val pageHeight = 842 // A4 standard height in points
        val margin = 40f

        val pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 1).create()
        val page = pdfDocument.startPage(pageInfo)
        val canvas: Canvas = page.canvas

        // Paints
        val titlePaint = Paint().apply {
            color = Color.parseColor("#1E1B4B") // Deep Indigo
            textSize = 16f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
        }

        val subtitlePaint = Paint().apply {
            color = Color.parseColor("#4F46E5")
            textSize = 10f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
            isAntiAlias = true
        }

        val headerLabelPaint = Paint().apply {
            color = Color.parseColor("#334155")
            textSize = 9f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
        }

        val bodyPaint = Paint().apply {
            color = Color.parseColor("#0F172A")
            textSize = 9.5f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
            isAntiAlias = true
        }

        val sectionPaint = Paint().apply {
            color = Color.parseColor("#312E81")
            textSize = 11f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
        }

        val linePaint = Paint().apply {
            color = Color.parseColor("#CBD5E1")
            strokeWidth = 1f
            isAntiAlias = true
        }

        val cardBgPaint = Paint().apply {
            color = Color.parseColor("#F1F5F9")
            style = Paint.Style.FILL
            isAntiAlias = true
        }

        var y = margin

        // 1. LETTERHEAD HEADER
        canvas.drawText(letterhead.companyName.uppercase(), margin, y + 14f, titlePaint)
        y += 20f
        canvas.drawText(letterhead.tagline, margin, y + 10f, subtitlePaint)
        y += 14f
        canvas.drawText("${letterhead.address} | Tel: ${letterhead.phone} | E-mel: ${letterhead.email}", margin, y + 10f, headerLabelPaint)
        y += 18f

        // Header Divider Line
        linePaint.strokeWidth = 2f
        linePaint.color = Color.parseColor("#312E81")
        canvas.drawLine(margin, y, pageWidth - margin, y, linePaint)
        y += 15f

        // Document Title
        titlePaint.textSize = 14f
        titlePaint.color = Color.parseColor("#1E1B4B")
        val docTitle = "MINIT MESYUARAT RASMI"
        canvas.drawText(docTitle, margin, y + 12f, titlePaint)

        val refText = "No. Rujukan: ${if (meeting.refNumber.isNotBlank()) meeting.refNumber else letterhead.refNoPrefix + "/" + meeting.id}"
        val refWidth = bodyPaint.measureText(refText)
        canvas.drawText(refText, pageWidth - margin - refWidth, y + 12f, headerLabelPaint)
        y += 24f

        // 2. MEETING DETAILS CARD (TABLE BOX)
        val boxRect = RectF(margin, y, pageWidth - margin, y + 60f)
        canvas.drawRoundRect(boxRect, 6f, 6f, cardBgPaint)

        var cardY = y + 14f
        canvas.drawText("TAJUK:", margin + 12f, cardY, headerLabelPaint)
        canvas.drawText(meeting.title, margin + 75f, cardY, bodyPaint)

        cardY += 15f
        canvas.drawText("TARIKH/MASA:", margin + 12f, cardY, headerLabelPaint)
        canvas.drawText("${meeting.date} (${meeting.time})", margin + 75f, cardY, bodyPaint)

        cardY += 15f
        canvas.drawText("LOKASI:", margin + 12f, cardY, headerLabelPaint)
        canvas.drawText(meeting.location, margin + 75f, cardY, bodyPaint)

        y += 72f

        // 3. PESERTA MESYUARAT
        canvas.drawText("1. KEHADIRAN PESERTA", margin, y + 10f, sectionPaint)
        y += 18f
        val participantsText = if (meeting.participants.isNotBlank()) meeting.participants else "Ahli Mesyuarat Rasmi"
        y = drawWrappedText(canvas, participantsText, margin + 10f, y, pageWidth - (margin * 2) - 10f, bodyPaint)
        y += 10f

        // 4. RINGKASAN EKSEKUTIF (AI GENERATED)
        canvas.drawText("2. RINGKASAN EKSEKUTIF (MINITAURA AI)", margin, y + 10f, sectionPaint)
        y += 18f
        val summaryText = if (meeting.executiveSummary.isNotBlank()) meeting.executiveSummary else meeting.refinedText
        y = drawWrappedText(canvas, summaryText, margin + 10f, y, pageWidth - (margin * 2) - 10f, bodyPaint)
        y += 10f

        // 5. SENARAI KEPUTUSAN
        canvas.drawText("3. KEPUTUSAN MESYUARAT", margin, y + 10f, sectionPaint)
        y += 18f
        val decisionsText = if (meeting.decisions.isNotBlank()) meeting.decisions else "1. Dipersetujui dan disahkan mengikut draf minit."
        y = drawWrappedText(canvas, decisionsText, margin + 10f, y, pageWidth - (margin * 2) - 10f, bodyPaint)
        y += 10f

        // 6. TINDAKAN / TUGASAN
        canvas.drawText("4. SENARAI TINDAKAN & TUGASAN", margin, y + 10f, sectionPaint)
        y += 18f
        val actionsText = if (meeting.actionItems.isNotBlank()) meeting.actionItems else "• Tiada tindakan khas ditetapkan."
        y = drawWrappedText(canvas, actionsText, margin + 10f, y, pageWidth - (margin * 2) - 10f, bodyPaint)
        y += 24f

        // 7. SIGNATURE BLOCK
        linePaint.strokeWidth = 1f
        linePaint.color = Color.parseColor("#E2E8F0")
        canvas.drawLine(margin, y, pageWidth - margin, y, linePaint)
        y += 20f

        val sigColWidth = (pageWidth - (margin * 2) - 40f) / 2f

        // Left signature - Pencatat Minit
        var sigY = y
        canvas.drawText("Disediakan Oleh (Pencatat Minit):", margin, sigY, headerLabelPaint)
        sigY += 35f
        canvas.drawLine(margin, sigY, margin + 160f, sigY, linePaint)
        sigY += 12f
        canvas.drawText("Nama: ${if (meeting.secretaryName.isNotBlank()) meeting.secretaryName else letterhead.defaultSecretary}", margin, sigY, bodyPaint)
        sigY += 12f
        canvas.drawText("Jawatan: Setiausaha / Urusetia", margin, sigY, bodyPaint)

        // Right signature - Pengerusi
        sigY = y
        val rightX = margin + sigColWidth + 40f
        canvas.drawText("Disahkan Oleh (Pengerusi):", rightX, sigY, headerLabelPaint)
        sigY += 35f
        canvas.drawLine(rightX, sigY, rightX + 160f, sigY, linePaint)
        sigY += 12f
        canvas.drawText("Nama: ${if (meeting.chairmanName.isNotBlank()) meeting.chairmanName else letterhead.defaultChairman}", rightX, sigY, bodyPaint)
        sigY += 12f
        canvas.drawText("Jawatan: Pengerusi Mesyuarat", rightX, sigY, bodyPaint)

        pdfDocument.finishPage(page)

        // Save PDF File to cache
        return try {
            val pdfDir = File(context.cacheDir, "pdf_exports")
            if (!pdfDir.exists()) pdfDir.mkdirs()
            val pdfFile = File(pdfDir, "MinitAura_${meeting.id}_${System.currentTimeMillis()}.pdf")
            val fileOutputStream = FileOutputStream(pdfFile)
            pdfDocument.writeTo(fileOutputStream)
            fileOutputStream.close()
            pdfDocument.close()
            pdfFile
        } catch (e: Exception) {
            Log.e("PdfExporter", "Error writing PDF file", e)
            pdfDocument.close()
            null
        }
    }

    private fun drawWrappedText(
        canvas: Canvas,
        text: String,
        x: Float,
        startY: Float,
        maxWidth: Float,
        paint: Paint
    ): Float {
        var currentY = startY
        val lines = text.split("\n")

        for (line in lines) {
            val words = line.split(" ")
            var currentLine = ""

            for (word in words) {
                val testLine = if (currentLine.isEmpty()) word else "$currentLine $word"
                val textWidth = paint.measureText(testLine)

                if (textWidth > maxWidth) {
                    canvas.drawText(currentLine, x, currentY, paint)
                    currentY += paint.textSize + 4f
                    currentLine = word
                } else {
                    currentLine = testLine
                }
            }

            if (currentLine.isNotEmpty()) {
                canvas.drawText(currentLine, x, currentY, paint)
                currentY += paint.textSize + 4f
            }
        }

        return currentY
    }

    fun sharePdf(pdfFile: File) {
        try {
            val contentUri: Uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                pdfFile
            )

            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "application/pdf"
                putExtra(Intent.EXTRA_STREAM, contentUri)
                putExtra(Intent.EXTRA_SUBJECT, "Minit Mesyuarat Rasmi - ${pdfFile.name}")
                putExtra(Intent.EXTRA_TEXT, "Dihasilkan melalui aplikasi MinitAura AI.")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            val chooser = Intent.createChooser(shareIntent, "Kongsi Minit Mesyuarat (PDF)")
            chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(chooser)
        } catch (e: Exception) {
            Log.e("PdfExporter", "Error sharing PDF", e)
        }
    }
}

package com.tomer.anibadi.util

import android.graphics.Bitmap
import android.graphics.Rect
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.view.PreviewView
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions

class AadhaarAnalyser(
    private val onNo: (Rect, String, Boolean) -> Unit,
    private val view: PreviewView
) : ImageAnalysis.Analyzer {


    private var isText = true
    private var rect = Rect()
    private var validRect = Rect(0, 0, 100, 100)

    //    private val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
    private val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)


    private fun readFromImage(image: InputImage) {
        try {
            recognizer.process(image)
                .addOnSuccessListener { visionText ->
                    isText = true
                    processTextFromRecognizer(visionText)
                }
                .addOnFailureListener { err ->
                    err.printStackTrace()
                    isText = true
                }

        } catch (e: Exception) {
            e.printStackTrace()
            isText = true
        }
    }

    private fun processTextFromRecognizer(visionText: Text) {
        val bul = java.lang.StringBuilder()
        var aano = ""
        var dob = ""
        var found = false
        for (block in visionText.textBlocks) {
            for (line in block.lines) {
                val text = line.text.trim()
                if (isAAno(text) && text.length > 12) {
                    rect = line.boundingBox!!
                    found = true
                    aano = text
                } else {
                    if (text.contains("DOB") && text.length > 6) {
                        dob = text.subSequence(text.indexOf("DOB") + 5, text.length).toString()
                    } else {
                        if (text.lowercase().contains("male") ||
                            text.lowercase().contains("female") ||
                            text.lowercase().contains("india")) {
                        } else {
                            bul.append(text)
                            bul.append(",,,")
                        }

                    }
                }
            }
        }
        if (found && dob.isNotEmpty()) {
            onNo(rect, "$aano,,,$dob,,,${bul}", true)
        } else {
            rect.set(-100, 0, -80, 0)
            onNo(rect, bul.toString(), false)
        }
    }

    private fun process(image: Bitmap) {
        try {
            readFromImage(InputImage.fromBitmap(image, 0))
        } catch (e: Exception) {
            e.printStackTrace()
            isText = true
        }
    }


    private fun isAAno(text: String): Boolean {
        return text.matches("(\\s*[0-9]+)+".toRegex())
    }

    fun setValidRect(rect: Rect) {
        validRect.set(rect)
    }

    @androidx.annotation.OptIn(androidx.camera.core.ExperimentalGetImage::class)
    override fun analyze(image: ImageProxy) {
        image.close()
        if (isText) {

            val bmp = view.bitmap
            isText = false
            try {
                if (bmp != null) {
                    process(Bitmap.createBitmap(bmp, validRect.left, validRect.top, validRect.width(), validRect.height()))
                } else isText = true
            } catch (_: Exception) {
                isText = true
            }
        }
    }
}
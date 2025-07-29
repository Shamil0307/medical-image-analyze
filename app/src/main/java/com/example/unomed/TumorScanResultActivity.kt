package com.example.unomed

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.makeramen.roundedimageview.RoundedImageView
import java.io.IOException

class TumorScanResultActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tumor_scan_result)

        val textViewTitle: TextView = findViewById(R.id.textViewTitle)
        val imageViewScanResult: RoundedImageView = findViewById(R.id.imageViewScanResult)
        val textViewScanResult: TextView = findViewById(R.id.textViewScanResult)

        val scanResult = intent.getStringExtra("SCAN_RESULT")
        val imageUriString = intent.getStringExtra("SCAN_IMAGE_URI")
        val imageUri = imageUriString?.let { Uri.parse(it) }

        textViewScanResult.text = scanResult ?: "No result"

        imageUri?.let {
            try {
                val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, it)
                imageViewScanResult.setImageBitmap(bitmap)
            } catch (e: IOException) {
                e.printStackTrace()
                textViewScanResult.text = "Error loading image"
            }
        }

        textViewScanResult.text = when (scanResult) {
            "Brain Tumor Detected" -> "Tumor detected. Please consult a doctor immediately. This diagnosis may not be precise."
            "No Brain Tumor Detected" -> "No tumor detected. However, if you have symptoms, please consult a doctor. This diagnosis may not be precise."
            else -> "No result"
        }
    }
}

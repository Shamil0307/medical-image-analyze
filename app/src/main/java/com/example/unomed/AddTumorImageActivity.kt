package com.example.unomed

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.unomed.ml.Model
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.io.ByteArrayOutputStream
import java.io.IOException

class AddTumorImageActivity : AppCompatActivity() {

    companion object {
        private const val PICK_IMAGE_REQUEST = 1
    }

    private lateinit var imageViewTumor: ImageView
    private var selectedImage: Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_tumor_image)

        imageViewTumor = findViewById(R.id.imageViewTumor)
        val buttonUploadImage: Button = findViewById(R.id.buttonUploadImage)
        val buttonScan: Button = findViewById(R.id.buttonScan)

        buttonUploadImage.setOnClickListener {
            openFilePicker()
        }

        buttonScan.setOnClickListener {
            selectedImage?.let { scanImage(it) } ?: Toast.makeText(this, "Please select an image first", Toast.LENGTH_SHORT).show()
        }
    }

    private fun openFilePicker() {
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "image/*"
            addCategory(Intent.CATEGORY_OPENABLE)
        }
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK) {
            data?.data?.let { uri ->
                handleImageSelection(uri)
            }
        }
    }

    private fun handleImageSelection(uri: Uri) {
        try {
            selectedImage = MediaStore.Images.Media.getBitmap(contentResolver, uri)
            imageViewTumor.setImageURI(uri)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun scanImage(bitmap: Bitmap) {
        val resizedBitmap = Bitmap.createScaledBitmap(bitmap, 64, 64, true)
        try {
            val model = Model.newInstance(applicationContext)

            val tensorImage = TensorImage(DataType.FLOAT32).apply {
                load(resizedBitmap)
            }
            val byteBuffer = tensorImage.buffer

            val inputFeature0 = TensorBuffer.createFixedSize(intArrayOf(1, 64, 64, 3), DataType.FLOAT32).apply {
                loadBuffer(byteBuffer)
            }

            val outputs = model.process(inputFeature0)
            val outputFeature0 = outputs.outputFeature0AsTensorBuffer

            val result = if (outputFeature0.floatArray[0] == 1.0f) {
                "Brain Tumor Detected"
            } else {
                "No Brain Tumor Detected"
            }

            model.close()

            val stream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
            val byteArray = stream.toByteArray()

            val intent = Intent(this, TumorScanResultActivity::class.java).apply {
                putExtra("SCAN_RESULT", result)
                putExtra("SCAN_IMAGE", byteArray)
            }
            startActivity(intent)

        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}

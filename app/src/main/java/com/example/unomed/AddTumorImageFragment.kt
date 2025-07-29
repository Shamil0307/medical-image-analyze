package com.example.unomed

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.example.unomed.ml.Model
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class AddTumorImageFragment : Fragment() {

    companion object {
        private const val ARGUMENT_TAG = "ARGUMENT_TAG"

        fun newInstance(tag: String): AddTumorImageFragment {
            return AddTumorImageFragment().apply {
                arguments = Bundle().apply {
                    putString(ARGUMENT_TAG, tag)
                }
            }
        }
    }

    private lateinit var imageViewTumor: ImageView
    private var selectedImage: Bitmap? = null
    private lateinit var pickImageLauncher: ActivityResultLauncher<String>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_add_tumor_image, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        imageViewTumor = view.findViewById(R.id.imageViewTumor)
        val buttonUploadImage: Button = view.findViewById(R.id.buttonUploadImage)
        val buttonScan: Button = view.findViewById(R.id.buttonScan)

        // Register the activity result launcher for image picking
        pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uri?.let { handleImageSelection(it) }
        }

        buttonUploadImage.setOnClickListener {
            openFilePicker()
        }

        buttonScan.setOnClickListener {
            selectedImage?.let { image ->
                scanImage(image)
            } ?: Toast.makeText(context, "Please select an image first", Toast.LENGTH_SHORT).show()
        }
    }

    private fun openFilePicker() {
        pickImageLauncher.launch("image/*")
    }

    private fun handleImageSelection(uri: Uri) {
        try {
            selectedImage = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                val source = ImageDecoder.createSource(requireActivity().contentResolver, uri)
                ImageDecoder.decodeBitmap(source)
            } else {
                MediaStore.Images.Media.getBitmap(requireActivity().contentResolver, uri)
            }
            imageViewTumor.setImageBitmap(selectedImage)
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(context, "Error loading image", Toast.LENGTH_SHORT).show()
        }
    }

    private fun convertBitmapToARGB8888(bitmap: Bitmap): Bitmap {
        if (bitmap.config != Bitmap.Config.ARGB_8888) {
            return bitmap.copy(Bitmap.Config.ARGB_8888, true)
        }
        return bitmap
    }

    private fun saveBitmapToFile(bitmap: Bitmap): Uri? {
        val file = File(requireContext().cacheDir, "temp_image.png")
        return try {
            FileOutputStream(file).use { out ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
                Uri.fromFile(file)
            }
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

    private fun scanImage(bitmap: Bitmap) {
        val argbBitmap = convertBitmapToARGB8888(bitmap)
        val resizedBitmap = Bitmap.createScaledBitmap(argbBitmap, 64, 64, true)
        try {
            val model = Model.newInstance(requireContext())
            Log.d("TensorFlow", "Model loaded successfully")

            val tensorImage = TensorImage(DataType.FLOAT32).apply {
                load(resizedBitmap)
            }
            val byteBuffer = tensorImage.buffer
            Log.d("TensorFlow", "TensorImage loaded successfully")

            val inputFeature0 = TensorBuffer.createFixedSize(intArrayOf(1, 64, 64, 3), DataType.FLOAT32).apply {
                loadBuffer(byteBuffer)
            }

            Log.d("TensorFlow", "InputFeature0 prepared")

            val outputs = model.process(inputFeature0)
            val outputFeature0 = outputs.outputFeature0AsTensorBuffer

            Log.d("TensorFlow", "Model inference completed")

            val result = if (outputFeature0.floatArray[0] == 1.0f) {
                "Brain Tumor Detected"
            } else {
                "No Brain Tumor Detected"
            }

            model.close()

            val fileUri = saveBitmapToFile(bitmap)
            fileUri?.let {
                val intent = Intent(context, TumorScanResultActivity::class.java).apply {
                    putExtra("SCAN_RESULT", result)
                    putExtra("SCAN_IMAGE_URI", it.toString())
                }
                startActivity(intent)
            } ?: Toast.makeText(context, "Error saving image", Toast.LENGTH_SHORT).show()

        } catch (e: Exception) {
            Log.e("TensorFlow", "Error processing image: ${e.message}", e)
            Toast.makeText(context, "Error processing image", Toast.LENGTH_SHORT).show()
        }
    }
}

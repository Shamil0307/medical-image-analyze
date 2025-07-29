package com.example.unomed

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.makeramen.roundedimageview.RoundedImageView
import android.widget.TextView

class AboutTumorFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_about_tumor, container, false)

        val aboutTitle = view.findViewById<TextView>(R.id.about_title)
        val aboutImage = view.findViewById<RoundedImageView>(R.id.about_image)
        val aboutDescription = view.findViewById<TextView>(R.id.about_description)
        val treatmentTitle = view.findViewById<TextView>(R.id.treatment_title)
        val treatmentDescription = view.findViewById<TextView>(R.id.treatment_description)

        aboutTitle.text = "About Brain Tumor"
        aboutImage.setImageResource(R.drawable.about_tumor) // Ensure you have a drawable resource named about_tumor
        aboutDescription.text = "Brain tumors are abnormal growths of cells in the brain. There are many types of brain tumors, some benign and some malignant."
        treatmentTitle.text = "Preferred Treatments"
        treatmentDescription.text = """
            • Surgery: The removal of the tumor through an operation.
            • Radiation therapy: The use of high-energy radiation to kill or shrink tumor cells.
            • Chemotherapy: The use of drugs to kill cancer cells.
            • Targeted therapy: The use of drugs that target specific molecules involved in the growth and spread of cancer cells.
        """.trimIndent()

        return view
    }
}

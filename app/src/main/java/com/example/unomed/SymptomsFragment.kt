package com.example.unomed

import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.BulletSpan
import android.text.style.StyleSpan
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment

class SymptomsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_symptoms, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val symptomsTextView: TextView = view.findViewById(R.id.symptoms_text_view)
        val tumorImageView: ImageView = view.findViewById(R.id.tumor_image_view)

        // Format the symptoms text
        val symptoms = listOf(
            "Persistent headaches",
            "Seizures",
            "Nausea or vomiting",
            "Blurred vision or double vision",
            "Loss of sensation or movement in an arm or a leg",
            "Difficulty with balance",
            "Speech difficulties",
            "Confusion in everyday matters",
            "Personality or behavior changes",
            "Hearing problems"
        )

        val formattedSymptoms = symptoms.joinToString(separator = "\n") { "\u2022 $it" }
        val spannableString = SpannableString(formattedSymptoms)

        // Apply bullet span and bold style to the entire text
        spannableString.setSpan(BulletSpan(20), 0, spannableString.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        spannableString.setSpan(StyleSpan(Typeface.BOLD), 0, spannableString.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        symptomsTextView.text = spannableString
        tumorImageView.setImageResource(R.drawable.brain)
    }
}

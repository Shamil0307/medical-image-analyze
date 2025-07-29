package com.example.unomed

import android.os.Bundle
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var pickImageLauncher: ActivityResultLauncher<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bottomNavigationView = findViewById(R.id.bottom_navigation)

        // Set default fragment if this is the first launch
        if (savedInstanceState == null) {
            openFragment(AddTumorImageFragment.newInstance("UPLOAD"))
        }

        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_upload -> {
                    openFragment(AddTumorImageFragment.newInstance("UPLOAD"))
                    true
                }
                R.id.navigation_symptoms -> {
                    openFragment(SymptomsFragment(), "SYMPTOMS")
                    true
                }
                R.id.navigation_about -> {
                    openFragment(AboutTumorFragment(), "ABOUT")
                    true
                }
                else -> false
            }
        }
    }

    private fun openFragment(fragment: Fragment, tag: String? = null) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, fragment, tag)
            .commit()
    }
}

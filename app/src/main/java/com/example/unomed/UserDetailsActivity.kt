package com.example.unomed

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import android.util.Patterns

class UserDetailsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_details)

        val buttonNext: Button = findViewById(R.id.buttonNext)
        val editTextFullName: EditText = findViewById(R.id.editTextFullName)
        val editTextEmail: EditText = findViewById(R.id.editTextEmail)
        val editTextAge: EditText = findViewById(R.id.editTextAge)

        buttonNext.setOnClickListener {
            val fullName = editTextFullName.text.toString().trim()
            val email = editTextEmail.text.toString().trim()
            val age = editTextAge.text.toString().trim()

            if (validateInput(fullName, email, age)) {
                // Start MainActivity after user details
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish() // Finish UserDetailsActivity so it's not in the back stack
            }
        }
    }

    private fun validateInput(fullName: String, email: String, age: String): Boolean {
        if (fullName.isEmpty()) {
            showToast("Please enter your full name")
            return false
        }

        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            showToast("Please enter a valid email address")
            return false
        }

        if (age.isEmpty() || !age.matches(Regex("\\d+")) || age.toInt() <= 0) {
            showToast("Please enter a valid age")
            return false
        }

        return true
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}

package com.example.piratv.ui.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import com.example.piratv.MainActivity
import com.example.piratv.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        binding.register.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
            finish()
        }

        binding.forgetPassword.setOnClickListener {
            val email = binding.emailInput.text.toString().trim()
            if (!email.equals("")) {
                auth.sendPasswordResetEmail(email)
                    .addOnSuccessListener {
                        Toast.makeText(
                            this@LoginActivity,
                            "Password reset email sent to $email \ncheck the spam dir",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(
                            this@LoginActivity,
                            "Failed to send reset email: ${e.message}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
            }else{
                Toast.makeText(
                    this@LoginActivity,
                    "the email field is empty",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        binding.login.setOnClickListener {
            val email = binding.emailInput.text.toString().trim()
            val password = binding.password.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {

                Toast.makeText(
                    this,
                    "Please enter email and password",
                    Toast.LENGTH_LONG
                ).show()

            } else {
                auth.signInWithEmailAndPassword(email, password)
                    .addOnSuccessListener {
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    }
                    .addOnFailureListener {

                        Toast.makeText(
                            this,
                            "Check the inserted email and password",
                            Toast.LENGTH_LONG
                        ).show()

                    }
            }
        }
    }
}

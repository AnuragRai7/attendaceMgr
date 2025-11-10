package com.example.attendancemanager.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.example.attendancemanager.data.AppDatabase
import com.example.attendancemanager.data.User
import com.example.attendancemanager.databinding.ActivityLoginBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private var isLoginMode = true // true = Login, false = Sign Up

    private val db by lazy { AppDatabase.getDatabase(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnLogin.setOnClickListener {
            if (isLoginMode) performLogin() else performSignUp()
        }

        binding.tvToggleRegister.setOnClickListener {
            if (isLoginMode) {
                isLoginMode = false
                binding.tvLoginTitle.text = "Create Account"
                binding.tvLoginSubtitle.text = "Sign up to get started."
                binding.btnLogin.text = "Sign Up"
                binding.tvToggleRegister.text = "Already have an account? Login"
            } else {
                isLoginMode = true
                binding.tvLoginTitle.text = "Welcome Back"
                binding.tvLoginSubtitle.text = "Sign in to manage your classes."
                binding.btnLogin.text = "Login"
                binding.tvToggleRegister.text = "Don't have an account? Sign Up"
            }
        }
    }

    private fun performSignUp() {
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Email and password cannot be empty", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            val existingUser = withContext(Dispatchers.IO) {
                db.attendanceDao().getUserByEmail(email)
            }

            if (existingUser != null) {
                Toast.makeText(this@LoginActivity, "This email is already registered", Toast.LENGTH_SHORT).show()
            } else {
                withContext(Dispatchers.IO) {
                    val newUser = User(email = email, passwordHash = password)
                    db.attendanceDao().insertUser(newUser)
                }
                Toast.makeText(this@LoginActivity, "Account created! Logging in...", Toast.LENGTH_SHORT).show()
                goToMainActivity()
            }
        }
    }

    private fun performLogin() {
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            val user = withContext(Dispatchers.IO) {
                db.attendanceDao().getUserByEmail(email)
            }

            when {
                user == null -> {
                    Toast.makeText(this@LoginActivity, "No account found with this email", Toast.LENGTH_SHORT).show()
                }
                user.passwordHash != password -> {
                    Toast.makeText(this@LoginActivity, "Incorrect password", Toast.LENGTH_SHORT).show()
                }
                else -> {
                    Toast.makeText(this@LoginActivity, "Login Successful!", Toast.LENGTH_SHORT).show()
                    goToMainActivity()
                }
            }
        }
    }

    private fun goToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}

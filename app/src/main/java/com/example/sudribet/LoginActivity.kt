package com.example.sudribet

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Auto-login : si l'utilisateur est déjà connecté, aller directement à l'accueil
        val prefs = getSharedPreferences("SudriPrefs", MODE_PRIVATE)
        if (prefs.getString("email", null) != null) {
            ActivityTransitions.navigateAndClear(this, Intent(this, HomeActivity::class.java))
            return
        }

        setContentView(R.layout.activity_login)

        val etEmail = findViewById<EditText>(R.id.etEmail)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val btnLogin = findViewById<Button>(R.id.btnLogin)

        btnLogin.setOnClickListener {
            val email = etEmail.text.toString()
            val password = etPassword.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                getSharedPreferences("SudriPrefs", MODE_PRIVATE)
                    .edit().putString("email", email).apply()
                Toast.makeText(this, "Connexion réussie !", Toast.LENGTH_SHORT).show()
                ActivityTransitions.navigateAndClear(this, Intent(this, HomeActivity::class.java))
            } else {
                Toast.makeText(this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
package com.example.sudribet

import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat

class EditProfilActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profil)

        val etUsername = findViewById<EditText>(R.id.etUsername)
        val etEmail = findViewById<EditText>(R.id.etEmail)
        val switchNotifications = findViewById<SwitchCompat>(R.id.switchNotifications)
        val switchNewsletter = findViewById<SwitchCompat>(R.id.switchNewsletter)
        val btnSave = findViewById<Button>(R.id.btnSaveProfil)
        val btnBack = findViewById<ImageView>(R.id.ivBack)
        val btnDelete = findViewById<TextView>(R.id.btnDeleteAccount)

        // Charger les données actuelles
        val sharedPref = getSharedPreferences("SudriPrefs", Context.MODE_PRIVATE)
        etUsername.setText(sharedPref.getString("username", "Utilisateur Sudri"))
        etEmail.setText(sharedPref.getString("email", "contact@sudri.fr"))
        switchNotifications.isChecked = sharedPref.getBoolean("notifications", true)
        switchNewsletter.isChecked = sharedPref.getBoolean("newsletter", false)

        btnBack.setOnClickListener { finish() }

        btnSave.setOnClickListener {
            val newName = etUsername.text.toString()
            val newEmail = etEmail.text.toString()
            
            if (newName.isNotEmpty() && newEmail.isNotEmpty()) {
                with(sharedPref.edit()) {
                    putString("username", newName)
                    putString("email", newEmail)
                    putBoolean("notifications", switchNotifications.isChecked)
                    putBoolean("newsletter", switchNewsletter.isChecked)
                    apply()
                }
                Toast.makeText(this, "Profil mis à jour avec succès !", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show()
            }
        }

        btnDelete.setOnClickListener {
            Toast.makeText(this, "Fonctionnalité de suppression bientôt disponible", Toast.LENGTH_SHORT).show()
        }
    }
}
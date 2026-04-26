package com.example.sudribet

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.imageview.ShapeableImageView
import java.io.File

class EditProfilActivity : AppCompatActivity() {

    private lateinit var ivEditAvatar: ShapeableImageView

    private val pickImageLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { saveAndDisplayAvatar(it) }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profil)

        ivEditAvatar = findViewById(R.id.ivEditAvatar)
        val etUsername = findViewById<EditText>(R.id.etUsername)
        val etEmail = findViewById<EditText>(R.id.etEmail)
        val switchNotifications = findViewById<SwitchCompat>(R.id.switchNotifications)
        val switchNewsletter = findViewById<SwitchCompat>(R.id.switchNewsletter)
        val btnSave = findViewById<Button>(R.id.btnSaveProfil)
        val btnBack = findViewById<ImageView>(R.id.ivBack)
        val btnDelete = findViewById<TextView>(R.id.btnDeleteAccount)
        val fabChangeAvatar = findViewById<FloatingActionButton>(R.id.fabChangeAvatar)

        val sharedPref = getSharedPreferences("SudriPrefs", Context.MODE_PRIVATE)
        etUsername.setText(sharedPref.getString("username", "Utilisateur Sudri"))
        etEmail.setText(sharedPref.getString("email", "contact@sudri.fr"))
        switchNotifications.isChecked = sharedPref.getBoolean("notifications", true)
        switchNewsletter.isChecked = sharedPref.getBoolean("newsletter", false)

        // Charger l'avatar existant
        sharedPref.getString("avatar_path", null)?.let { path ->
            val file = File(path)
            if (file.exists()) ivEditAvatar.setImageURI(Uri.fromFile(file))
        }

        fabChangeAvatar.setOnClickListener { pickImageLauncher.launch("image/*") }
        ivEditAvatar.setOnClickListener { pickImageLauncher.launch("image/*") }

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

    private fun saveAndDisplayAvatar(uri: Uri) {
        try {
            val inputStream = contentResolver.openInputStream(uri) ?: return
            val file = File(filesDir, "avatar.jpg")
            inputStream.use { input ->
                file.outputStream().use { output -> input.copyTo(output) }
            }
            getSharedPreferences("SudriPrefs", Context.MODE_PRIVATE)
                .edit().putString("avatar_path", file.absolutePath).apply()
            ivEditAvatar.setImageURI(Uri.fromFile(file))
        } catch (e: Exception) {
            Toast.makeText(this, "Impossible de charger l'image", Toast.LENGTH_SHORT).show()
        }
    }
}

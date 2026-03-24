package com.example.sudribet

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView

class SupportActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_support)

        val btnBack = findViewById<ImageView>(R.id.ivBack)
        val btnChat = findViewById<Button>(R.id.btnChat)
        val cardEmail = findViewById<CardView>(R.id.cardEmail)

        btnBack.setOnClickListener { finish() }

        btnChat.setOnClickListener {
            startActivity(Intent(this, ChatActivity::class.java))
        }

        // Configuration de l'envoi d'email via API Intent
        cardEmail.setOnClickListener {
            val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:")
                putExtra(Intent.EXTRA_EMAIL, arrayOf("sacha.lathuilliere@esme.fr"))
                putExtra(Intent.EXTRA_SUBJECT, "Support SudriBet - Demande d'assistance")
                putExtra(Intent.EXTRA_TEXT, "Bonjour l'équipe Support,\n\nJ'ai besoin d'aide concernant...")
            }
            
            try {
                startActivity(Intent.createChooser(emailIntent, "Envoyer un email avec..."))
            } catch (e: Exception) {
                android.widget.Toast.makeText(this, "Aucune application de messagerie trouvée", android.widget.Toast.LENGTH_SHORT).show()
            }
        }
    }
}
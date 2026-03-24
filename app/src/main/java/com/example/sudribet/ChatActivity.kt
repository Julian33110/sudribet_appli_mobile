package com.example.sudribet

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.*
import androidx.lifecycle.lifecycleScope
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.launch

class ChatActivity : AppCompatActivity() {

    private lateinit var adapter: ChatAdapter
    private val messages = mutableListOf<Message>()
    private lateinit var rvChat: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        rvChat = findViewById(R.id.rvChat)
        val et = findViewById<EditText>(R.id.etMessage)
        val btnSend = findViewById<View>(R.id.btnSend)
        val btnBack = findViewById<ImageView>(R.id.ivBack)

        adapter = ChatAdapter(messages)
        rvChat.layoutManager = LinearLayoutManager(this)
        rvChat.adapter = adapter

        // Message de bienvenue amélioré
        addBotMessage("Bienvenue chez SudriBet ! 🛡️ Je suis votre assistant virtuel intelligent. Je peux vous aider pour vos paris, votre solde ou la sécurité de votre compte. Que souhaitez-vous savoir ?")

        btnSend.setOnClickListener {
            val text = et.text.toString().trim()
            if (text.isNotEmpty()) {
                addUserMessage(text)
                et.text.clear()
                generateBotResponse(text)
            }
        }

        btnBack.setOnClickListener { finish() }
    }

    private fun addUserMessage(text: String) {
        val time = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
        messages.add(Message(text, true, time))
        adapter.notifyItemInserted(messages.size - 1)
        rvChat.scrollToPosition(messages.size - 1)
    }

    private fun addBotMessage(text: String) {
        val time = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
        messages.add(Message(text, false, time))
        adapter.notifyItemInserted(messages.size - 1)
        rvChat.scrollToPosition(messages.size - 1)
    }

    private fun generateBotResponse(userText: String) {
        lifecycleScope.launch {
            try {
                // Initialisation du modèle Gemini
                val generativeModel = GenerativeModel(
                    modelName = "gemini-2.5-flash",
                    apiKey = BuildConfig.GEMINI_API_KEY,
                    systemInstruction = content { 
                        text("Tu es SudriBot, l'assistant IA de l'application SudriBet. Tu réponds de manière courte et concise, avec un ton amical et tu ajoutes parfois des emojis. L'application permet de parier sur des matchs, retirer ses gains (minimum 10€), obtenir un bonus quotidien de 5€, et contacter le support.") 
                    }
                )
                
                // Appel asynchrone à l'API Gemini
                val response = generativeModel.generateContent(userText)
                
                response.text?.let { 
                    addBotMessage(it) 
                } ?: run {
                    addBotMessage("Je n'ai pas pu générer de réponse. Réessayez.")
                }
                
            } catch (e: Exception) {
                e.printStackTrace()
                addBotMessage("Détail de l'Erreur : ${e.localizedMessage}")
            }
        }
    }
}
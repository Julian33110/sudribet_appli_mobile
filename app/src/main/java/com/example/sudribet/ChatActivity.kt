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
import org.json.JSONObject

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

    private fun addBotMessage(text: String, betDesc: String? = null, betCote: Double? = null) {
        val time = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
        messages.add(Message(text, false, time, betDesc, betCote))
        adapter.notifyItemInserted(messages.size - 1)
        rvChat.scrollToPosition(messages.size - 1)
    }

    private fun generateBotResponse(userText: String) {
        lifecycleScope.launch {
            try {
                // Initialisation du modèle Gemini
                val generativeModel = GenerativeModel(
                    modelName = "gemini-1.5-flash",
                    apiKey = BuildConfig.GEMINI_API_KEY,
                    systemInstruction = content { 
                        text("Tu es SudriBot, l'assistant IA de l'application SudriBet. Tu réponds de manière courte et concise. Si l'utilisateur te demande un pari ou un conseil de mise, tu peux proposer un pari spécifique en incluant à la fin de ta réponse un bloc JSON de ce format : [[BET_JSON:{\"desc\": \"Victoire ESME\", \"cote\": 2.10}]]. Les écoles possibles sont ESME, EPITA, IPSA, Epitech, HEC, Centrale. Ne parle que de sports universitaires.") 
                    }
                )
                
                // Appel asynchrone à l'API Gemini
                val response = generativeModel.generateContent(userText)
                
                response.text?.let { rawText ->
                    var cleanText = rawText
                    var betDesc: String? = null
                    var betCote: Double? = null

                    // Extraction du JSON si présent
                    if (rawText.contains("[[BET_JSON:")) {
                        try {
                            val start = rawText.indexOf("[[BET_JSON:") + 11
                            val end = rawText.indexOf("]]", start)
                            val jsonStr = rawText.substring(start, end)
                            val json = JSONObject(jsonStr)
                            betDesc = json.optString("desc")
                            betCote = json.optDouble("cote")
                            cleanText = rawText.replace(Regex("\\[\\[BET_JSON:.*?\\]\\]"), "").trim()
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                    
                    addBotMessage(cleanText, betDesc, betCote) 
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
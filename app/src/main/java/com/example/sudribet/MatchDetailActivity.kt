package com.example.sudribet

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class MatchDetailActivity : AppCompatActivity() {

    private lateinit var tvIAAdvice: TextView

    // Pari sélectionné : "A", "B" ou "Nul"
    private var equipeChoisie: String = ""
    private var coteChoisie: Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_match_detail)

        val matchId   = intent.getStringExtra("matchId") ?: ""
        val nameA     = intent.getStringExtra("nameA") ?: "Equipe A"
        val nameB     = intent.getStringExtra("nameB") ?: "Equipe B"
        val scoreA    = intent.getIntExtra("scoreA", 0)
        val scoreB    = intent.getIntExtra("scoreB", 0)
        val isLive    = intent.getBooleanExtra("isLive", false)
        val isUpcoming = intent.getBooleanExtra("isUpcoming", false)
        val cat       = intent.getStringExtra("cat") ?: "FOOTBALL"
        val heure     = intent.getStringExtra("heure") ?: "15:00"
        val date      = intent.getStringExtra("date") ?: ""
        val coteA     = intent.getDoubleExtra("coteA", 1.5)
        val coteB     = intent.getDoubleExtra("coteB", 2.5)
        val coteNul   = intent.getDoubleExtra("coteNul", 0.0)

        findViewById<TextView>(R.id.tvDetailNameA).text = nameA
        findViewById<TextView>(R.id.tvDetailNameB).text = nameB
        findViewById<TextView>(R.id.tvDetailCat).text = cat.uppercase()
        findViewById<TextView>(R.id.tvDetailHeure).text = when {
            isLive -> "EN DIRECT"
            date.isNotEmpty() -> formatDate(date) + " · " + heure
            else -> heure
        }

        val tvScore = findViewById<TextView>(R.id.tvDetailScore)
        tvScore.text = if (isLive) "$scoreA - $scoreB" else "VS"

        // ─── Boutons de sélection d'équipe ───────────────────────────────────
        val btnBetA   = findViewById<Button>(R.id.btnBetA)
        val btnBetNul = findViewById<Button>(R.id.btnBetNul)
        val btnBetB   = findViewById<Button>(R.id.btnBetB)
        val tvCote    = findViewById<TextView>(R.id.tvDynamicCote)
        val etMise    = findViewById<EditText>(R.id.etMise)
        val tvGains   = findViewById<TextView>(R.id.tvGainsPotentiels)
        val btnConfirm = findViewById<Button>(R.id.btnBetExactScore)

        btnBetA.text   = "${nameA.take(12)}\n${String.format(Locale.US, "%.2f", coteA)}"
        btnBetB.text   = "${nameB.take(12)}\n${String.format(Locale.US, "%.2f", coteB)}"

        if (coteNul > 0.0) {
            btnBetNul.visibility = View.VISIBLE
            btnBetNul.text = "Nul\n${String.format(Locale.US, "%.2f", coteNul)}"
        } else {
            btnBetNul.visibility = View.GONE
        }

        // Sélection équipe → met à jour la cote affichée et recalcule le gain
        fun selectEquipe(choix: String, cote: Double) {
            equipeChoisie = choix
            coteChoisie = cote
            tvCote.text = String.format(Locale.US, "%.2f", cote)
            recalcGains(etMise, tvGains, cote)
            btnBetA.isSelected   = choix == "A"
            btnBetNul.isSelected = choix == "Nul"
            btnBetB.isSelected   = choix == "B"
        }

        btnBetA.setOnClickListener   { selectEquipe("A",   coteA) }
        btnBetNul.setOnClickListener { selectEquipe("Nul", coteNul) }
        btnBetB.setOnClickListener   { selectEquipe("B",   coteB) }

        etMise.addTextChangedListener(object : android.text.TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                recalcGains(etMise, tvGains, coteChoisie)
            }
            override fun afterTextChanged(s: android.text.Editable?) {}
        })

        // ─── Confirmer le pari ───────────────────────────────────────────────
        btnConfirm.text = "Confirmer le pari"
        btnConfirm.setOnClickListener {
            if (matchId.isEmpty()) {
                Toast.makeText(this, "Match non disponible pour le pari", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (equipeChoisie.isEmpty()) {
                Toast.makeText(this, "Choisissez une équipe d'abord", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val mise = etMise.text.toString().toDoubleOrNull()
            if (mise == null || mise <= 0) {
                Toast.makeText(this, "Entrez une mise valide", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val prefs = getSharedPreferences("SudriPrefs", Context.MODE_PRIVATE)
            val balance = prefs.getFloat("balance", 150f)
            if (mise > balance) {
                Toast.makeText(this, "Solde insuffisant (${balance.toInt()} SC)", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val gains = mise * coteChoisie
            val equipeLabel = when (equipeChoisie) {
                "A"   -> nameA
                "B"   -> nameB
                else  -> "Nul"
            }
            val bet = Bet(
                id              = UUID.randomUUID().toString(),
                description     = "$equipeLabel ($cat)",
                totalCote       = coteChoisie,
                mise            = mise,
                gainsPotentiels = gains,
                date            = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.FRANCE).format(Date()),
                status          = "En cours",
                matchId         = matchId,
                equipeChoisie   = equipeChoisie
            )

            // Sauvegarder le pari et débiter la mise
            val betsJson = prefs.getString("my_bets", "[]")
            val type = object : TypeToken<MutableList<Bet>>() {}.type
            val bets: MutableList<Bet> = Gson().fromJson(betsJson, type)
            bets.add(bet)
            prefs.edit()
                .putString("my_bets", Gson().toJson(bets))
                .putFloat("balance", (balance - mise).toFloat())
                .apply()

            Toast.makeText(this, "✅ Pari placé — ${mise.toInt()} SC sur $equipeLabel", Toast.LENGTH_LONG).show()
            finish()
        }

        // ─── IA Advisor ──────────────────────────────────────────────────────
        tvIAAdvice = findViewById(R.id.tvIAAdvice)
        findViewById<View>(R.id.btnIAAdvisor).setOnClickListener {
            getIAAdvice(nameA, nameB, cat, coteA, coteB)
        }

        // ─── Bouton retour ───────────────────────────────────────────────────
        findViewById<ImageView>(R.id.ivBack).setOnClickListener { finish() }
    }

    private fun recalcGains(etMise: EditText, tvGains: TextView, cote: Double) {
        val mise = etMise.text.toString().toDoubleOrNull() ?: 0.0
        if (mise > 0 && cote > 0) {
            tvGains.text = "Gains potentiels : ${String.format(Locale.US, "%.0f", mise * cote)} SC"
            tvGains.visibility = View.VISIBLE
        } else {
            tvGains.visibility = View.INVISIBLE
        }
    }

    private fun formatDate(iso: String): String {
        return try {
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.FRANCE)
            val date = sdf.parse(iso.take(10)) ?: return iso
            SimpleDateFormat("dd MMM", Locale.FRANCE).format(date)
        } catch (e: Exception) { iso.take(10) }
    }

    private fun getIAAdvice(nameA: String, nameB: String, cat: String, coteA: Double, coteB: Double) {
        tvIAAdvice.visibility = View.VISIBLE
        tvIAAdvice.text = "L'IA analyse le match..."

        lifecycleScope.launch {
            try {
                val generativeModel = GenerativeModel(
                    modelName = "gemini-2.0-flash",
                    apiKey = BuildConfig.GEMINI_API_KEY,
                    systemInstruction = content {
                        text("Tu es un expert en paris sportifs universitaires. Analyse les cotes et donne un conseil court (2-3 phrases).")
                    }
                )
                val prompt = "Match de $cat : $nameA (cote $coteA) vs $nameB (cote $coteB). Qui a l'avantage ? Conseil de pari."
                val response = generativeModel.generateContent(prompt)
                tvIAAdvice.text = response.text ?: "L'IA n'a pas pu générer de conseil."
            } catch (e: Exception) {
                tvIAAdvice.text = "Conseil IA indisponible"
            }
        }
    }
}

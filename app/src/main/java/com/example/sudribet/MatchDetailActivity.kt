package com.example.sudribet

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.util.*

class MatchDetailActivity : AppCompatActivity() {

    private lateinit var etScoreA: EditText
    private lateinit var etScoreB: EditText
    private lateinit var tvCote: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_match_detail)

        etScoreA = findViewById(R.id.etScoreA)
        etScoreB = findViewById(R.id.etScoreB)
        tvCote = findViewById(R.id.tvDynamicCote)

        val nameA = intent.getStringExtra("nameA") ?: "Equipe A"
        val nameB = intent.getStringExtra("nameB") ?: "Equipe B"
        val scoreA = intent.getIntExtra("scoreA", 0)
        val scoreB = intent.getIntExtra("scoreB", 0)
        val isLive = intent.getBooleanExtra("isLive", false)
        val cat = intent.getStringExtra("cat") ?: "FOOTBALL"
        val heure = intent.getStringExtra("heure") ?: "15:00"
        val coteA = intent.getDoubleExtra("coteA", 1.5)
        val coteB = intent.getDoubleExtra("coteB", 2.5)

        findViewById<TextView>(R.id.tvDetailNameA).text = nameA
        findViewById<TextView>(R.id.tvDetailNameB).text = nameB
        findViewById<TextView>(R.id.tvDetailCat).text = cat.uppercase()
        findViewById<TextView>(R.id.tvDetailHeure).text = if (isLive) "EN DIRECT" else heure
        
        val tvScore = findViewById<TextView>(R.id.tvDetailScore)
        tvScore.text = if (isLive) "$scoreA - $scoreB" else "VS"

        findViewById<ImageView>(R.id.ivBack).setOnClickListener { finish() }

        // Écouter les changements de score pour mettre à jour la cote
        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                calculateDynamicCote(coteA, coteB)
            }
            override fun afterTextChanged(s: Editable?) {}
        }

        etScoreA.addTextChangedListener(textWatcher)
        etScoreB.addTextChangedListener(textWatcher)

        // Calcul initial
        calculateDynamicCote(coteA, coteB)

        findViewById<Button>(R.id.btnBetExactScore).setOnClickListener {
            val sA = etScoreA.text.toString()
            val sB = etScoreB.text.toString()
            if (sA.isNotEmpty() && sB.isNotEmpty()) {
                Toast.makeText(this, "Pari placé sur le score $sA - $sB (Cote: ${tvCote.text})", Toast.LENGTH_LONG).show()
                finish()
            }
        }
    }

    private fun calculateDynamicCote(coteA: Double, coteB: Double) {
        val sA = etScoreA.text.toString().toIntOrNull() ?: 0
        val sB = etScoreB.text.toString().toIntOrNull() ?: 0
        
        // Algorithme Winamax/Betclic Style : Basé sur les probabilités réelles
        // Plus une équipe est favorite (cote basse), plus ses scores victorieux ont une cote basse.
        
        val totalGoals = sA + sB
        val diff = sA - sB
        
        var baseProb: Double
        
        when {
            sA == 0 && sB == 0 -> baseProb = 0.10 // 0-0
            sA == 1 && sB == 1 -> baseProb = 0.12 // 1-1
            sA == 1 && sB == 0 -> baseProb = 0.15 * (1 / coteA) // Avantage favori A
            sA == 0 && sB == 1 -> baseProb = 0.15 * (1 / coteB) // Avantage favori B
            sA == 2 && sB == 0 -> baseProb = 0.08 * (1 / coteA)
            sA == 0 && sB == 2 -> baseProb = 0.08 * (1 / coteB)
            sA == 2 && sB == 1 -> baseProb = 0.09 * (1 / coteA)
            sA == 1 && sB == 2 -> baseProb = 0.09 * (1 / coteB)
            else -> {
                // Scores plus rares (3+ buts)
                baseProb = 0.05 / (totalGoals + Math.abs(diff) + 1)
                if (diff > 0) baseProb *= (1 / coteA) else if (diff < 0) baseProb *= (1 / coteB)
            }
        }

        // Conversion Probabilité -> Cote (Cote = 1/Probabilité)
        // On ajoute une marge pour le bookmaker (0.85 au lieu de 1.0)
        var finalCote = 0.85 / baseProb
        
        // Limites réalistes
        if (finalCote < 4.0) finalCote = 4.0 + (Random().nextDouble())
        if (finalCote > 100.0) finalCote = 100.0

        tvCote.text = String.format(Locale.US, "%.2f", finalCote)
    }
}
package com.example.sudribet

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.*
import java.text.SimpleDateFormat
import androidx.activity.viewModels
import androidx.compose.ui.platform.ComposeView
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

class MainActivity : AppCompatActivity() {

    private val viewModel: MatchViewModel by viewModels()
    private var stakeAmount: Double = 10.0
    private val handler = Handler(Looper.getMainLooper())
    private val selectedMatches = mutableMapOf<String, Pair<Match, Int>>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val composeView = findViewById<ComposeView>(R.id.composeViewMatchs)
        composeView.setContent {
            val matches by viewModel.matches.collectAsState()
            var selectedSport by remember { mutableStateOf("Tous") }

            Column {
                // Filtre de sports en Compose
                val sports = listOf("Tous", "Football", "Rugby", "Basket", "Handball", "Volley")
                LazyRow(
                    modifier = Modifier.padding(vertical = 12.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp)
                ) {
                    items(sports) { sport ->
                        CategoryChip(
                            text = sport,
                            isSelected = selectedSport == sport,
                            onClick = { 
                                selectedSport = sport 
                                viewModel.updateSportFilter(sport)
                            }
                        )
                    }
                }

                // Liste de matchs en Compose
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 100.dp)
                ) {
                    items(matches) { match ->
                        MatchItemCompose(
                            match = match,
                            onMatchClick = { 
                                val intent = Intent(this@MainActivity, MatchDetailActivity::class.java).apply {
                                    putExtra("nameA", match.equipeA)
                                    putExtra("nameB", match.equipeB)
                                    putExtra("scoreA", match.scoreA)
                                    putExtra("scoreB", match.scoreB)
                                    putExtra("isLive", match.isLive)
                                    putExtra("cat", match.categorie)
                                    putExtra("heure", match.heure)
                                    putExtra("coteA", match.coteA)
                                    putExtra("coteB", match.coteB)
                                }
                                ActivityTransitions.navigate(this@MainActivity, intent)
                            },
                            onBetClick = { m, selection -> 
                                handleSelection(m, selection, true) 
                            }
                        )
                    }
                }
            }
        }

        val etSearch = findViewById<EditText>(R.id.etSearch)
        etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.updateSearchQuery(s.toString())
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        findViewById<ImageView>(R.id.btnBack).setOnClickListener {
            ActivityTransitions.navigateBack(this)
        }
        findViewById<Button>(R.id.btnValidateBet).setOnClickListener { saveBet() }

        val etStake = findViewById<EditText>(R.id.etStake)
        etStake.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                stakeAmount = s.toString().toDoubleOrNull() ?: 0.0
                updateBetSlip()
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        // Bottom Navigation
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav)
        bottomNav.selectedItemId = R.id.nav_bet
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    ActivityTransitions.navigateTab(this, Intent(this, HomeActivity::class.java))
                    true
                }
                R.id.nav_bet -> true // déjà ici
                R.id.nav_history -> {
                    ActivityTransitions.navigateTab(this, Intent(this, HistoryActivity::class.java))
                    true
                }
                R.id.nav_profil -> {
                    ActivityTransitions.navigateTab(this, Intent(this, ProfilActivity::class.java))
                    true
                }
                else -> false
            }
        }
    }

    private fun handleSelection(match: Match, selection: Int, isAdded: Boolean) {
        if (isAdded) selectedMatches[match.id] = Pair(match, selection)
        else selectedMatches.remove(match.id)
        updateBetSlip()
    }

    private fun updateBetSlip() {
        val cardBetSlip = findViewById<CardView>(R.id.cardBetSlip)
        val tvCount = findViewById<TextView>(R.id.tvSelectionCount)
        val tvCote = findViewById<TextView>(R.id.tvTotalCote)

        if (selectedMatches.isEmpty()) {
            if (cardBetSlip.visibility == View.VISIBLE) {
                cardBetSlip.startAnimation(AnimationUtils.loadAnimation(this, android.R.anim.fade_out))
                cardBetSlip.visibility = View.GONE
            }
        } else {
            if (cardBetSlip.visibility == View.GONE) {
                cardBetSlip.visibility = View.VISIBLE
                cardBetSlip.startAnimation(AnimationUtils.loadAnimation(this, android.R.anim.slide_in_left))
            }
            val count = selectedMatches.size
            tvCount.text = if (count > 1) "$count SÉLECTIONS (COMBINÉ)" else "1 SÉLECTION"
            var totalCote = 1.0
            selectedMatches.values.forEach { (match, sel) ->
                totalCote *= when (sel) {
                    1 -> match.coteA
                    2 -> match.coteB
                    3 -> match.coteNul ?: match.coteB
                    else -> match.coteB
                }
            }
            tvCote.text = "Cote Totale: ${String.format(Locale.US, "%.2f", totalCote)}"

            val tvPotentialWinnings = findViewById<TextView>(R.id.tvPotentialWinnings)
            val winnings = totalCote * stakeAmount
            tvPotentialWinnings.text = "${winnings.toInt()} SC"
        }
    }

    private fun saveBet() {
        val sharedPref = getSharedPreferences("SudriPrefs", Context.MODE_PRIVATE)
        var balance = sharedPref.getFloat("balance", 150.0f)
        val userEmail = sharedPref.getString("email", "sacha.lathuilliere@esme.fr") ?: "sacha.lathuilliere@esme.fr"

        val etStake = findViewById<EditText>(R.id.etStake)
        val betMise = etStake.text.toString().toDoubleOrNull() ?: 10.0

        if (betMise <= 0.0) {
            Toast.makeText(this, "Mise invalide !", Toast.LENGTH_SHORT).show()
            return
        }

        if (balance < betMise) {
            Toast.makeText(this, "Solde insuffisant !", Toast.LENGTH_SHORT).show()
            return
        }
        balance -= betMise.toFloat()
        sharedPref.edit().putFloat("balance", balance).apply()

        val betsJson = sharedPref.getString("my_bets", "[]")
        val type = object : TypeToken<MutableList<Bet>>() {}.type
        val betsList: MutableList<Bet> = Gson().fromJson(betsJson, type)

        var desc = ""
        var totalCote = 1.0
        selectedMatches.values.forEach { (match, sel) ->
            val selLabel = when (sel) { 1 -> "1"; 3 -> "X"; else -> "2" }
            desc += "${match.equipeA} vs ${match.equipeB} ($selLabel), "
            totalCote *= when (sel) {
                1 -> match.coteA
                2 -> match.coteB
                3 -> match.coteNul ?: match.coteB
                else -> match.coteB
            }
        }

        val description = desc.removeSuffix(", ")
        val betCote = totalCote

        val newBet = Bet(UUID.randomUUID().toString(), description, betCote, betMise, betMise * betCote,
            SimpleDateFormat("dd/MM HH:mm", Locale.US).format(Date()), "En cours")

        betsList.add(0, newBet)
        sharedPref.edit().putString("my_bets", Gson().toJson(betsList)).apply()

        DailyManager.incrementBetsToday(this)
        EmailService.sendBetConfirmation(userEmail, description, betCote, betMise)

        Toast.makeText(this, "Pari validé ! +${betMise.toInt()} SC misés", Toast.LENGTH_SHORT).show()
        ActivityTransitions.navigateBack(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
    }
}

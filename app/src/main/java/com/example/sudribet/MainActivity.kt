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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.chip.ChipGroup
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var adapter: MatchAdapter
    private val handler = Handler(Looper.getMainLooper())
    private var stakeAmount: Double = 10.0

    private val tousLesMatchs = mutableListOf(
        Match("1", "PSG", "Marseille", 1.85, 3.40, "21:00", "Football", isLive = true, scoreA = 1, scoreB = 0),
        Match("2", "Toulouse", "La Rochelle", 1.5, 2.5, "15:00", "Rugby"),
        Match("3", "Lakers", "Warriors", 1.8, 1.9, "21:00", "Basket", isLive = true, scoreA = 88, scoreB = 92),
        Match("4", "Montpellier", "Paris", 1.4, 2.8, "18:00", "Handball"),
        Match("5", "Cannes", "Nantes", 1.6, 2.2, "20:00", "Volley"),
        Match("6", "Real Madrid", "Barça", 2.1, 2.0, "21:00", "Football"),
        Match("7", "Lyon", "Monaco", 2.30, 2.45, "17:00", "Football"),
        Match("8", "Stade Français", "Bordeaux", 1.70, 2.10, "19:00", "Rugby")
    )

    private val selectedMatches = mutableMapOf<String, Pair<Match, Int>>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val rv = findViewById<RecyclerView>(R.id.rvMatchs)
        rv.layoutManager = LinearLayoutManager(this)

        setupAdapter(tousLesMatchs)

        val etSearch = findViewById<EditText>(R.id.etSearch)
        etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterBySearch(s.toString())
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        val chipGroup = findViewById<ChipGroup>(R.id.chipGroupSports)
        chipGroup.setOnCheckedChangeListener { _, checkedId ->
            val sport = when (checkedId) {
                R.id.chipFoot -> "Football"
                R.id.chipRugby -> "Rugby"
                R.id.chipBasket -> "Basket"
                R.id.chipVolley -> "Volley"
                R.id.chipHand -> "Handball"
                else -> "Tous"
            }
            filterBySport(sport)
        }

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

        startLiveUpdates()
    }

    private fun setupAdapter(list: List<Match>) {
        adapter = MatchAdapter(list) { match, selection, isAdded ->
            handleSelection(match, selection, isAdded)
        }
        findViewById<RecyclerView>(R.id.rvMatchs).adapter = adapter
    }

    private fun filterBySearch(query: String) {
        val filtered = tousLesMatchs.filter {
            it.equipeA.contains(query, ignoreCase = true) || it.equipeB.contains(query, ignoreCase = true)
        }
        setupAdapter(filtered)
    }

    private fun filterBySport(sport: String) {
        val filtered = if (sport == "Tous") tousLesMatchs else tousLesMatchs.filter { it.categorie == sport }
        setupAdapter(filtered)
    }

    private fun startLiveUpdates() {
        handler.postDelayed(object : Runnable {
            override fun run() {
                tousLesMatchs.forEachIndexed { index, match ->
                    if (match.isLive) {
                        val newScoreA = match.scoreA + if (Random().nextInt(100) > 97) 1 else 0
                        val newScoreB = match.scoreB + if (Random().nextInt(100) > 97) 1 else 0
                        tousLesMatchs[index] = match.copy(scoreA = newScoreA, scoreB = newScoreB)
                        adapter.notifyItemChanged(index)
                    }
                }
                handler.postDelayed(this, 3000)
            }
        }, 3000)
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
            selectedMatches.values.forEach { totalCote *= if (it.second == 1) it.first.coteA else it.first.coteB }
            tvCote.text = "Cote Totale: ${String.format(Locale.US, "%.2f", totalCote)}"

            val tvPotentialWinnings = findViewById<TextView>(R.id.tvPotentialWinnings)
            val winnings = totalCote * stakeAmount
            tvPotentialWinnings.text = "${String.format(Locale.US, "%.2f", winnings)} €"
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
        selectedMatches.values.forEach {
            desc += "${it.first.equipeA} vs ${it.first.equipeB}, "
            totalCote *= if (it.second == 1) it.first.coteA else it.first.coteB
        }

        val description = desc.removeSuffix(", ")
        val betCote = totalCote

        val newBet = Bet(UUID.randomUUID().toString(), description, betCote, betMise, betMise * betCote,
            SimpleDateFormat("dd/MM HH:mm", Locale.US).format(Date()), "En cours")

        betsList.add(0, newBet)
        sharedPref.edit().putString("my_bets", Gson().toJson(betsList)).apply()

        EmailService.sendBetConfirmation(userEmail, description, betCote, betMise)

        Toast.makeText(this, "Pari validé ✅", Toast.LENGTH_SHORT).show()
        ActivityTransitions.navigateBack(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
    }
}
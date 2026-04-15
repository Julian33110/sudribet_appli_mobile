package com.example.sudribet

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import java.util.*

class MatchAdapter(
    private val listMatchs: List<Match>,
    private val onSelectionChanged: (Match, Int, Boolean) -> Unit
) : RecyclerView.Adapter<MatchAdapter.ViewHolder>() {

    private val selectedBets = mutableMapOf<String, Int>()

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtA: TextView = view.findViewById(R.id.nameA)
        val txtB: TextView = view.findViewById(R.id.nameB)
        val txtCat: TextView = view.findViewById(R.id.tvCategorie)
        val txtHeure: TextView = view.findViewById(R.id.tvHeure)
        val btnCoteA: Button = view.findViewById(R.id.btnCoteA)
        val btnCoteB: Button = view.findViewById(R.id.btnCoteB)
        val liveIndicator: View = view.findViewById(R.id.liveIndicator)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_match, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val match = listMatchs[position]
        
        holder.itemView.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, MatchDetailActivity::class.java)
            intent.putExtra("matchId", match.id)
            intent.putExtra("nameA", match.equipeA)
            intent.putExtra("nameB", match.equipeB)
            intent.putExtra("scoreA", match.scoreA)
            intent.putExtra("scoreB", match.scoreB)
            intent.putExtra("isLive", match.isLive)
            intent.putExtra("isUpcoming", match.isUpcoming)
            intent.putExtra("cat", match.categorie)
            intent.putExtra("heure", match.heure)
            intent.putExtra("date", match.date)
            intent.putExtra("coteA", match.coteA)
            intent.putExtra("coteB", match.coteB)
            intent.putExtra("coteNul", match.coteNul ?: 0.0)
            context.startActivity(intent)
        }

        if (match.isLive) {
            holder.txtA.text = "${match.equipeA} (${match.scoreA})"
            holder.txtB.text = "(${match.scoreB}) ${match.equipeB}"
            holder.liveIndicator.visibility = View.VISIBLE
            holder.txtHeure.text = "EN DIRECT"
            holder.txtHeure.setTextColor(ContextCompat.getColor(holder.itemView.context, android.R.color.holo_red_dark))
        } else {
            holder.txtA.text = match.equipeA
            holder.txtB.text = match.equipeB
            holder.liveIndicator.visibility = View.GONE
            holder.txtHeure.text = match.heure
            holder.txtHeure.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.primary))
        }

        holder.txtCat.text = match.categorie.uppercase()
        holder.btnCoteA.text = "1 | ${String.format(Locale.US, "%.2f", match.coteA)}"
        holder.btnCoteB.text = "2 | ${String.format(Locale.US, "%.2f", match.coteB)}"

        // Mise à jour visuelle PRO
        updateButtonStyle(holder.btnCoteA, selectedBets[match.id] == 1)
        updateButtonStyle(holder.btnCoteB, selectedBets[match.id] == 2)

        holder.btnCoteA.setOnClickListener {
            it.startAnimation(AnimationUtils.loadAnimation(it.context, android.R.anim.fade_in))
            toggleSelection(match, 1)
            notifyItemChanged(position)
        }

        holder.btnCoteB.setOnClickListener {
            it.startAnimation(AnimationUtils.loadAnimation(it.context, android.R.anim.fade_in))
            toggleSelection(match, 2)
            notifyItemChanged(position)
        }
    }

    private fun toggleSelection(match: Match, selection: Int) {
        if (selectedBets[match.id] == selection) {
            selectedBets.remove(match.id)
            onSelectionChanged(match, selection, false)
        } else {
            selectedBets[match.id] = selection
            onSelectionChanged(match, selection, true)
        }
    }

    private fun updateButtonStyle(button: Button, isSelected: Boolean) {
        val context = button.context
        if (isSelected) {
            // Style sélectionné : Fond Orange, Texte Blanc (Style Betclic)
            button.backgroundTintList = ContextCompat.getColorStateList(context, R.color.primary)
            button.setTextColor(ContextCompat.getColor(context, R.color.white))
        } else {
            // Style normal : Transparent avec bordures (Style Winamax)
            button.backgroundTintList = ContextCompat.getColorStateList(context, android.R.color.transparent)
            button.setTextColor(ContextCompat.getColor(context, R.color.background_blue))
        }
    }

    override fun getItemCount(): Int = listMatchs.size
}
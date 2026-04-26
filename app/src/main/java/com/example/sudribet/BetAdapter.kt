package com.example.sudribet

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView

class BetAdapter(private val bets: List<Bet>) : RecyclerView.Adapter<BetAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvDate: TextView = view.findViewById(R.id.tvBetDate)
        val tvStatus: TextView = view.findViewById(R.id.tvBetStatus)
        val tvDesc: TextView = view.findViewById(R.id.tvBetDesc)
        val tvMise: TextView = view.findViewById(R.id.tvBetMise)
        val tvCote: TextView = view.findViewById(R.id.tvBetCote)
        val tvGains: TextView = view.findViewById(R.id.tvBetGains)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_bet, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val bet = bets[position]
        holder.tvDate.text = bet.date
        holder.tvStatus.text = bet.status.uppercase()
        holder.tvDesc.text = bet.description
        holder.tvMise.text = String.format("%.0f SC", bet.mise)
        holder.tvCote.text = String.format("%.2f", bet.totalCote)
        holder.tvGains.text = String.format("%.0f SC", bet.gainsPotentiels)

        if (bet.status == "Gagné") {
            holder.tvStatus.setTextColor(ContextCompat.getColor(holder.itemView.context, android.R.color.holo_green_dark))
        } else if (bet.status == "Perdu") {
            holder.tvStatus.setTextColor(ContextCompat.getColor(holder.itemView.context, android.R.color.holo_red_dark))
        } else {
            holder.tvStatus.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.primary))
        }
    }

    override fun getItemCount(): Int = bets.size
}
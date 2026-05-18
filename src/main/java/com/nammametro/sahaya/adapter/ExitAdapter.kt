package com.nammametro.sahaya.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.nammametro.sahaya.R

class ExitAdapter(
    private val exits: List<String>,
    private val onExitClick: ((gateName: String, gateDestination: String) -> Unit)? = null
) : RecyclerView.Adapter<ExitAdapter.ExitViewHolder>() {

    class ExitViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvExit: TextView = itemView.findViewById(R.id.tvExitItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExitViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_exit, parent, false)
        return ExitViewHolder(view)
    }

    override fun onBindViewHolder(holder: ExitViewHolder, position: Int) {
        val exitText = exits[position]
        holder.tvExit.text = "🚪 $exitText"

        holder.itemView.setOnClickListener {
            // Parse "Gate 1: KSRTC Bus Stand" → gateName="Gate 1", destination="KSRTC Bus Stand"
            val cleanText = exitText
                .removePrefix("✅ ")
                .removeSuffix(" ← YOU WANT THIS")
                .trim()
            val colonIndex = cleanText.indexOf(":")
            if (colonIndex != -1) {
                val gateName = cleanText.substring(0, colonIndex).trim()
                val destination = cleanText.substring(colonIndex + 1).trim()
                onExitClick?.invoke(gateName, destination)
            }
        }
    }

    override fun getItemCount() = exits.size
}
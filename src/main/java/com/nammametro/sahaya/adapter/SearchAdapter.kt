package com.nammametro.sahaya.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.nammametro.sahaya.R
import com.nammametro.sahaya.SearchResult

class SearchAdapter(
    private val results: List<SearchResult>,
    private val onClick: (SearchResult) -> Unit
) : RecyclerView.Adapter<SearchAdapter.SearchViewHolder>() {

    fun getResults(): List<SearchResult> = results

    class SearchViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvIcon: TextView = itemView.findViewById(R.id.tvSearchIcon)
        val tvTitle: TextView = itemView.findViewById(R.id.tvSearchTitle)
        val tvSubtitle: TextView = itemView.findViewById(R.id.tvSearchSubtitle)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_search_result, parent, false)
        return SearchViewHolder(view)
    }

    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
        val result = results[position]
        holder.tvIcon.text = result.icon
        holder.tvTitle.text = result.title
        holder.tvSubtitle.text = result.subtitle

        // Highlight AI results
        if (result.icon == "✨") {
            holder.itemView.setBackgroundColor(
                holder.itemView.context.getColor(android.R.color.transparent)
            )
            holder.tvTitle.setTextColor(
                android.graphics.Color.parseColor("#00ACC1")
            )
        }

        holder.itemView.setOnClickListener { onClick(result) }
    }

    override fun getItemCount() = results.size
}
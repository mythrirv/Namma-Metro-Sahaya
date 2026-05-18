package com.nammametro.sahaya.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.nammametro.sahaya.R
import com.nammametro.sahaya.data.VisualStep

class StepAdapter(
    private val steps: List<VisualStep>,
    private val showKannada: Boolean = true,
    private val apiKey: String = "",
    private val onStepClick: ((step: VisualStep, stepType: String) -> Unit)? = null
) : RecyclerView.Adapter<StepAdapter.StepViewHolder>() {

    companion object {
        const val STEP_TYPE_TOKEN = "token"
        const val STEP_TYPE_DETAIL = "detail"
    }

    class StepViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvStepNumber: TextView = itemView.findViewById(R.id.tvStepNumber)
        val tvIcon: TextView = itemView.findViewById(R.id.tvIcon)
        val tvTitle: TextView = itemView.findViewById(R.id.tvTitle)
        val tvDescription: TextView = itemView.findViewById(R.id.tvDescription)
        val tvKannada: TextView = itemView.findViewById(R.id.tvKannada)
        val cardStep: CardView = itemView.findViewById(R.id.cardStep)
        val viewLineColor: View = itemView.findViewById(R.id.viewLineColor)
        val tvStepImage: TextView = itemView.findViewById(R.id.tvStepImage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StepViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_visual_step, parent, false)
        return StepViewHolder(view)
    }

    override fun onBindViewHolder(holder: StepViewHolder, position: Int) {
        val step = steps[position]
        val isDark = (holder.itemView.context.resources.configuration.uiMode
                and android.content.res.Configuration.UI_MODE_NIGHT_MASK) ==
                android.content.res.Configuration.UI_MODE_NIGHT_YES

        holder.tvStepNumber.text = "STEP ${step.stepNumber} OF $itemCount"
        holder.tvIcon.text = step.icon
        holder.tvTitle.text = step.title
        holder.tvDescription.text = step.description

        if (showKannada) {
            holder.tvKannada.visibility = View.VISIBLE
            holder.tvKannada.text = "🇮🇳 ಕನ್ನಡ: ${step.kannadaDescription}"
        } else {
            holder.tvKannada.visibility = View.GONE
        }

        val cardBg = if (isDark) Color.parseColor("#1e1e1e") else Color.WHITE
        val primaryText = if (isDark) Color.WHITE else Color.parseColor("#111111")
        val secondaryText = if (isDark) Color.parseColor("#cccccc") else Color.parseColor("#444444")
        val kannadaBg = if (isDark) Color.parseColor("#1a237e") else Color.parseColor("#e8eaf6")
        val kannadaText = if (isDark) Color.WHITE else Color.parseColor("#1a237e")

        holder.cardStep.setCardBackgroundColor(cardBg)
        holder.tvTitle.setTextColor(primaryText)
        holder.tvDescription.setTextColor(secondaryText)
        holder.tvStepNumber.setTextColor(secondaryText)
        holder.tvKannada.setTextColor(kannadaText)
        holder.tvKannada.setBackgroundColor(kannadaBg)
        holder.tvStepImage.setTextColor(Color.WHITE)

        when {
            step.stepNumber == 1 -> {
                holder.viewLineColor.setBackgroundColor(Color.parseColor("#1a237e"))
                holder.tvStepImage.setBackgroundColor(Color.parseColor("#1a237e"))
                holder.tvStepImage.text =
                    "🏛️  ENTER METRO STATION\n" +
                            "━━━━━━━━━━━━━━━━━━━━\n" +
                            "🎫 Token Counter → Buy Token\n" +
                            "💳 OR Use Metro Smart Card\n" +
                            "🚧 Tap card/token at Entry Gate"

                // Step 1 is clickable — token/pass info
                holder.cardStep.setOnClickListener {
                    onStepClick?.invoke(step, STEP_TYPE_TOKEN)
                }

                // Show clickable hint
                holder.tvDescription.text = step.description + "\n\n👆 Tap for fare info & pass options"
            }
            step.isInterchange -> {
                holder.viewLineColor.setBackgroundColor(Color.parseColor("#F57C00"))
                holder.tvStepImage.setBackgroundColor(Color.parseColor("#E65100"))
                holder.tvStepImage.text =
                    "⚡ INTERCHANGE — MAJESTIC\n" +
                            "━━━━━━━━━━━━━━━━━━━━\n" +
                            "1️⃣  Get OFF the train\n" +
                            "2️⃣  Follow INTERCHANGE signs 🪧\n" +
                            "3️⃣  Walk to other platform\n" +
                            "4️⃣  Board new train"

                holder.cardStep.setOnClickListener {
                    onStepClick?.invoke(step, STEP_TYPE_DETAIL)
                }
            }
            step.icon.contains("🟣") -> {
                holder.viewLineColor.setBackgroundColor(Color.parseColor("#7B1FA2"))
                holder.tvStepImage.setBackgroundColor(Color.parseColor("#4A148C"))
                holder.tvStepImage.text =
                    "🟣 PURPLE LINE PLATFORM\n" +
                            "━━━━━━━━━━━━━━━━━━━━\n" +
                            "🚇 Wait for Purple Line train\n" +
                            "📍 Check direction board\n" +
                            "✅ Board when doors open"

                holder.cardStep.setOnClickListener {
                    onStepClick?.invoke(step, STEP_TYPE_DETAIL)
                }
            }
            step.icon.contains("🟢") -> {
                holder.viewLineColor.setBackgroundColor(Color.parseColor("#2E7D32"))
                holder.tvStepImage.setBackgroundColor(Color.parseColor("#1B5E20"))
                holder.tvStepImage.text =
                    "🟢 GREEN LINE PLATFORM\n" +
                            "━━━━━━━━━━━━━━━━━━━━\n" +
                            "🚇 Wait for Green Line train\n" +
                            "📍 Check direction board\n" +
                            "✅ Board when doors open"

                holder.cardStep.setOnClickListener {
                    onStepClick?.invoke(step, STEP_TYPE_DETAIL)
                }
            }
            step.icon.contains("🏁") -> {
                holder.viewLineColor.setBackgroundColor(Color.parseColor("#1565C0"))
                holder.tvStepImage.setBackgroundColor(Color.parseColor("#0D47A1"))
                holder.tvStepImage.text =
                    "🏁 YOU HAVE ARRIVED!\n" +
                            "━━━━━━━━━━━━━━━━━━━━\n" +
                            "🚪 Exit through correct gate\n" +
                            "🎫 Tap token/card at Exit Gate\n" +
                            "📍 Check exits listed below ↓"

                holder.cardStep.setOnClickListener {
                    onStepClick?.invoke(step, STEP_TYPE_DETAIL)
                }
            }
            else -> {
                holder.viewLineColor.setBackgroundColor(Color.parseColor("#1a237e"))
                holder.tvStepImage.setBackgroundColor(Color.parseColor("#1a237e"))
                holder.tvStepImage.text = "🚇 Metro Step"
                holder.cardStep.setOnClickListener {
                    onStepClick?.invoke(step, STEP_TYPE_DETAIL)
                }
            }
        }
    }

    override fun getItemCount() = steps.size
}
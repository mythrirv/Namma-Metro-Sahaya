package com.nammametro.sahaya

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class TokenInfoBottomSheet : BottomSheetDialogFragment() {

    companion object {
        fun newInstance(
            stationName: String,
            toStationName: String,
            apiKey: String
        ): TokenInfoBottomSheet {
            return TokenInfoBottomSheet().apply {
                arguments = Bundle().apply {
                    putString("station", stationName)
                    putString("toStation", toStationName)
                    putString("apiKey", apiKey)
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Read all args at the TOP — before any lambdas
        val stationName = arguments?.getString("station") ?: ""
        val toStationName = arguments?.getString("toStation") ?: ""
        val apiKey = arguments?.getString("apiKey") ?: ""

        val root = LinearLayout(requireContext())
        root.orientation = LinearLayout.VERTICAL
        root.setPadding(48, 48, 48, 48)

        // Handle bar
        val handle = View(requireContext())
        handle.setBackgroundColor(android.graphics.Color.parseColor("#CCCCCC"))
        val handleParams = LinearLayout.LayoutParams(120, 8)
        handleParams.gravity = android.view.Gravity.CENTER_HORIZONTAL
        handleParams.bottomMargin = 32
        handle.layoutParams = handleParams
        root.addView(handle)

        // Title
        val tvTitle = TextView(requireContext())
        tvTitle.text = "🎫 Ticket & Pass Options"
        tvTitle.textSize = 20f
        tvTitle.setTypeface(null, android.graphics.Typeface.BOLD)
        tvTitle.setTextColor(resources.getColor(R.color.text_primary, null))
        tvTitle.setPadding(0, 0, 0, 8)
        root.addView(tvTitle)

        val tvSubtitle = TextView(requireContext())
        tvSubtitle.text = "Choose what you need:"
        tvSubtitle.textSize = 14f
        tvSubtitle.setTextColor(android.graphics.Color.GRAY)
        tvSubtitle.setPadding(0, 0, 0, 32)
        root.addView(tvSubtitle)

        // Option 1: Check Fare — pre-fills FROM and TO
        root.addView(makeOptionCard(
            emoji = "💰",
            title = "Check Fare",
            subtitle = "See how much your journey costs",
            bgColor = "#1a237e"
        ) {
            dismiss()
            startActivity(Intent(requireContext(), FareActivity::class.java).apply {
                putExtra("FROM_STATION", stationName)
                putExtra("TO_STATION", toStationName)
            })
        })

        addSpace(root, 16)

        // Option 2: Get Metro Pass
        root.addView(makeOptionCard(
            emoji = "🪪",
            title = "Get Metro Pass",
            subtitle = "Day pass, Monthly pass, Smart Card & more",
            bgColor = "#2E7D32"
        ) {
            dismiss()
            val sheet = MetroPassBottomSheet.newInstance(stationName, apiKey)
            sheet.show(parentFragmentManager, "MetroPass")
        })

        addSpace(root, 32)

        return root
    }

    private fun makeOptionCard(
        emoji: String,
        title: String,
        subtitle: String,
        bgColor: String,
        onClick: () -> Unit
    ): com.google.android.material.card.MaterialCardView {
        val card = com.google.android.material.card.MaterialCardView(requireContext())
        card.radius = 16f
        card.cardElevation = 4f
        card.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )

        val inner = LinearLayout(requireContext())
        inner.orientation = LinearLayout.HORIZONTAL
        inner.setPadding(32, 28, 32, 28)
        inner.gravity = android.view.Gravity.CENTER_VERTICAL
        inner.setBackgroundColor(android.graphics.Color.parseColor(bgColor))

        val tvEmoji = TextView(requireContext())
        tvEmoji.text = emoji
        tvEmoji.textSize = 36f
        val emojiParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        emojiParams.marginEnd = 24
        tvEmoji.layoutParams = emojiParams

        val textBlock = LinearLayout(requireContext())
        textBlock.orientation = LinearLayout.VERTICAL

        val tvTitle = TextView(requireContext())
        tvTitle.text = title
        tvTitle.textSize = 17f
        tvTitle.setTypeface(null, android.graphics.Typeface.BOLD)
        tvTitle.setTextColor(android.graphics.Color.WHITE)

        val tvSub = TextView(requireContext())
        tvSub.text = subtitle
        tvSub.textSize = 13f
        tvSub.setTextColor(android.graphics.Color.parseColor("#CCCCCC"))
        tvSub.setPadding(0, 4, 0, 0)

        textBlock.addView(tvTitle)
        textBlock.addView(tvSub)
        inner.addView(tvEmoji)
        inner.addView(textBlock)
        card.addView(inner)
        card.setOnClickListener { onClick() }
        return card
    }

    private fun addSpace(parent: LinearLayout, dp: Int) {
        val space = View(requireContext())
        space.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, dp
        )
        parent.addView(space)
    }
}
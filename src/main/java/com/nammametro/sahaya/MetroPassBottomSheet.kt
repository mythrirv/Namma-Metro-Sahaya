package com.nammametro.sahaya

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.*
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

class MetroPassBottomSheet : BottomSheetDialogFragment() {

    companion object {
        fun newInstance(stationName: String, apiKey: String): MetroPassBottomSheet {
            return MetroPassBottomSheet().apply {
                arguments = Bundle().apply {
                    putString("station", stationName)
                    putString("apiKey", apiKey)
                }
            }
        }
    }

    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    private val passes = listOf(
        Triple("🌅", "Day Pass", "#E65100"),
        Triple("📅", "Monthly Pass", "#1565C0"),
        Triple("📆", "Yearly Pass", "#2E7D32"),
        Triple("💳", "Smart Card", "#6A1B9A"),
        Triple("👴", "Senior Citizen Pass", "#4E342E"),
        Triple("🎓", "Student Pass", "#00695C")
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val apiKey = arguments?.getString("apiKey") ?: ""

        val scroll = ScrollView(requireContext())
        val root = LinearLayout(requireContext())
        root.orientation = LinearLayout.VERTICAL
        root.setPadding(48, 48, 48, 48)
        scroll.addView(root)

        // Handle
        val handle = View(requireContext())
        handle.setBackgroundColor(Color.parseColor("#CCCCCC"))
        val handleParams = LinearLayout.LayoutParams(120, 8)
        handleParams.gravity = android.view.Gravity.CENTER_HORIZONTAL
        handleParams.bottomMargin = 32
        handle.layoutParams = handleParams
        root.addView(handle)

        // Title
        val tvTitle = TextView(requireContext())
        tvTitle.text = "🪪 Metro Pass Options"
        tvTitle.textSize = 20f
        tvTitle.setTypeface(null, Typeface.BOLD)
        tvTitle.setTextColor(resources.getColor(R.color.text_primary, null))
        tvTitle.setPadding(0, 0, 0, 8)
        root.addView(tvTitle)

        val tvSub = TextView(requireContext())
        tvSub.text = "Select a pass to see how to get it:"
        tvSub.textSize = 14f
        tvSub.setTextColor(Color.GRAY)
        tvSub.setPadding(0, 0, 0, 24)
        root.addView(tvSub)

        // Content area for instructions
        val instructionContainer = FrameLayout(requireContext())
        val instructionParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        instructionParams.topMargin = 24
        instructionContainer.layoutParams = instructionParams

        // Pass option cards
        passes.forEach { (emoji, passName, color) ->
            val card = makePassCard(emoji, passName, color) {
                showPassInstructions(instructionContainer, passName, apiKey)
                // Scroll to instructions
                scroll.post { scroll.fullScroll(View.FOCUS_DOWN) }
            }
            root.addView(card)
            addSpace(root, 12)
        }

        root.addView(instructionContainer)
        addSpace(root, 32)

        return scroll
    }

    private fun showPassInstructions(container: FrameLayout, passName: String, apiKey: String) {
        container.removeAllViews()

        val progressBar = ProgressBar(requireContext())
        progressBar.layoutParams = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.WRAP_CONTENT,
            FrameLayout.LayoutParams.WRAP_CONTENT,
            android.view.Gravity.CENTER
        )
        container.addView(progressBar)

        val prompt = "Give step-by-step instructions on how to get a $passName for Namma Metro Bengaluru. Include: eligibility, documents needed, where to apply, cost/fees, and how long it takes. Format as numbered steps. Keep it simple and helpful for a first-time user."

        scope.launch {
            try {
                val result = withContext(Dispatchers.IO) { callNvidiaAI(apiKey, prompt) }
                container.removeAllViews()

                val card = com.google.android.material.card.MaterialCardView(requireContext())
                card.radius = 16f
                card.cardElevation = 4f

                val inner = LinearLayout(requireContext())
                inner.orientation = LinearLayout.VERTICAL
                inner.setPadding(32, 32, 32, 32)

                val tvHeader = TextView(requireContext())
                tvHeader.text = "📋 How to get $passName"
                tvHeader.textSize = 16f
                tvHeader.setTypeface(null, Typeface.BOLD)
                tvHeader.setTextColor(resources.getColor(R.color.text_primary, null))
                tvHeader.setPadding(0, 0, 0, 16)

                val tvContent = TextView(requireContext())
                tvContent.text = result.trim()
                tvContent.textSize = 14f
                tvContent.setTextColor(resources.getColor(R.color.text_primary, null))
                tvContent.setLineSpacing(6f, 1f)

                inner.addView(tvHeader)
                inner.addView(tvContent)
                card.addView(inner)
                container.addView(card)

            } catch (e: Exception) {
                container.removeAllViews()
                val tv = TextView(requireContext())
                tv.text = "Could not load instructions. Please check your internet connection."
                tv.setPadding(0, 16, 0, 0)
                tv.setTextColor(Color.RED)
                container.addView(tv)
            }
        }
    }

    private fun callNvidiaAI(apiKey: String, prompt: String): String {
        val url = URL("https://integrate.api.nvidia.com/v1/chat/completions")
        val body = JSONObject()
        body.put("model", "meta/llama-3.1-8b-instruct")
        val messages = JSONArray()
        val sys = JSONObject()
        sys.put("role", "system")
        sys.put("content", "You are a helpful Namma Metro Bengaluru assistant. Give clear, simple instructions.")
        val usr = JSONObject()
        usr.put("role", "user")
        usr.put("content", prompt)
        messages.put(sys)
        messages.put(usr)
        body.put("messages", messages)
        body.put("temperature", 0.3)
        body.put("max_tokens", 600)

        val conn = url.openConnection() as HttpURLConnection
        conn.requestMethod = "POST"
        conn.setRequestProperty("Content-Type", "application/json")
        conn.setRequestProperty("Authorization", "Bearer $apiKey")
        conn.doOutput = true
        conn.connectTimeout = 30000
        conn.readTimeout = 30000
        conn.outputStream.write(body.toString().toByteArray())

        if (conn.responseCode != HttpURLConnection.HTTP_OK) {
            throw Exception("HTTP ${conn.responseCode}")
        }
        val json = JSONObject(conn.inputStream.bufferedReader().readText())
        return json.getJSONArray("choices").getJSONObject(0)
            .getJSONObject("message").getString("content")
    }

    private fun makePassCard(
        emoji: String,
        passName: String,
        bgColor: String,
        onClick: () -> Unit
    ): com.google.android.material.card.MaterialCardView {
        val card = com.google.android.material.card.MaterialCardView(requireContext())
        card.radius = 12f
        card.cardElevation = 3f
        card.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )

        val inner = LinearLayout(requireContext())
        inner.orientation = LinearLayout.HORIZONTAL
        inner.setPadding(24, 20, 24, 20)
        inner.gravity = android.view.Gravity.CENTER_VERTICAL
        inner.setBackgroundColor(Color.parseColor(bgColor))

        val tvEmoji = TextView(requireContext())
        tvEmoji.text = emoji
        tvEmoji.textSize = 28f
        val ep = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        ep.marginEnd = 20
        tvEmoji.layoutParams = ep

        val tvName = TextView(requireContext())
        tvName.text = passName
        tvName.textSize = 16f
        tvName.setTypeface(null, Typeface.BOLD)
        tvName.setTextColor(Color.WHITE)
        tvName.layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)

        val tvArrow = TextView(requireContext())
        tvArrow.text = "→"
        tvArrow.textSize = 18f
        tvArrow.setTextColor(Color.WHITE)

        inner.addView(tvEmoji)
        inner.addView(tvName)
        inner.addView(tvArrow)
        card.addView(inner)
        card.setOnClickListener { onClick() }
        return card
    }

    private fun addSpace(parent: LinearLayout, dp: Int) {
        val space = View(requireContext())
        space.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, dp)
        parent.addView(space)
    }

    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
    }
}
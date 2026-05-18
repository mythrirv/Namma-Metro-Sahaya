package com.nammametro.sahaya

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.ScrollView
import android.widget.TextView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.tabs.TabLayout
import kotlinx.coroutines.*
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

class StepDetailBottomSheet : BottomSheetDialogFragment() {

    companion object {
        fun newInstance(
            stepTitle: String,
            stepDescription: String,
            lineColor: String,
            stationName: String,
            apiKey: String
        ): StepDetailBottomSheet {
            return StepDetailBottomSheet().apply {
                arguments = Bundle().apply {
                    putString("title", stepTitle)
                    putString("desc", stepDescription)
                    putString("lineColor", lineColor)
                    putString("station", stationName)
                    putString("apiKey", apiKey)
                }
            }
        }
    }

    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private var savedLineColor = "#1a237e"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val root = inflater.inflate(R.layout.bottom_sheet_gate_directions, container, false)

        val stepTitle = arguments?.getString("title") ?: ""
        val stepDesc = arguments?.getString("desc") ?: ""
        savedLineColor = arguments?.getString("lineColor") ?: "#1a237e"
        val stationName = arguments?.getString("station") ?: ""
        val apiKey = arguments?.getString("apiKey") ?: ""

        val tvTitle = root.findViewById<TextView>(R.id.tvGateTitle)
        val tvSubtitle = root.findViewById<TextView>(R.id.tvGateSubtitle)
        val tabLayout = root.findViewById<TabLayout>(R.id.tabDirections)
        val contentContainer = root.findViewById<FrameLayout>(R.id.frameContent)
        val progressBar = root.findViewById<ProgressBar>(R.id.progressLoading)

        tvTitle.text = stepTitle
        tvSubtitle.text = stationName
        tvTitle.setTextColor(Color.parseColor(savedLineColor))

        tabLayout.addTab(tabLayout.newTab().setText("🗺️ Line Map"))
        tabLayout.addTab(tabLayout.newTab().setText("👣 Visual Steps"))
        tabLayout.addTab(tabLayout.newTab().setText("📋 Instructions"))
        tabLayout.addTab(tabLayout.newTab().setText("▶ Walk Through"))

        fun loadTab(position: Int) {
            contentContainer.removeAllViews()
            when (position) {
                0 -> {
                    val scrollView = ScrollView(requireContext())
                    val mapView = LineMapView(
                        requireContext(), stationName, stepTitle, savedLineColor, apiKey
                    )
                    scrollView.addView(
                        mapView,
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    )
                    contentContainer.addView(
                        scrollView,
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                }
                1 -> loadAiContent(contentContainer, progressBar, stationName, stepTitle, stepDesc, apiKey, "visual")
                2 -> loadAiContent(contentContainer, progressBar, stationName, stepTitle, stepDesc, apiKey, "guide")
                3 -> loadAiContent(contentContainer, progressBar, stationName, stepTitle, stepDesc, apiKey, "walkthrough")
            }
        }

        loadTab(0)

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) = loadTab(tab.position)
            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })

        return root
    }

    private fun loadAiContent(
        container: FrameLayout,
        progressBar: ProgressBar,
        stationName: String,
        stepTitle: String,
        stepDesc: String,
        apiKey: String,
        mode: String
    ) {
        progressBar.visibility = View.VISIBLE
        container.removeAllViews()

        val prompt = when (mode) {
            "visual" -> "A user at $stationName metro station in Bengaluru needs to: $stepTitle. $stepDesc. Give exactly 5 step-by-step visual directions. Format: ICON|STEP_TITLE|STEP_DETAIL. Use icons: 🚶 🔄 ⬆️ ⬇️ 🚪 ➡️ ⬅️ 🎯 🎫 💳. Return only 5 lines, no extra text."
            "guide" -> "A user at $stationName metro station in Bengaluru needs to: $stepTitle. Give detailed bullet point instructions. Start each with •. Include all important details. Return 6-8 bullet points, no intro text."
            "walkthrough" -> "A user at $stationName metro station in Bengaluru needs to: $stepTitle. Create exactly 6 walkthrough steps. Format: STEP_NUMBER|STEP_TITLE|STEP_DETAIL. Return only 6 lines, no extra text."
            else -> ""
        }

        scope.launch {
            try {
                val result = withContext(Dispatchers.IO) { callNvidiaAI(apiKey, prompt) }
                progressBar.visibility = View.GONE
                when (mode) {
                    "visual" -> showVisualSteps(container, result)
                    "guide" -> showGuideSteps(container, result)
                    "walkthrough" -> showWalkthrough(container, result)
                }
            } catch (e: Exception) {
                progressBar.visibility = View.GONE
                val tv = TextView(requireContext())
                tv.text = "Error: ${e.message}"
                tv.setPadding(32, 32, 32, 32)
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
        sys.put("content", "You are a helpful Namma Metro Bengaluru guide. Give clear, simple directions.")
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
            val errorBody = conn.errorStream?.bufferedReader()?.readText() ?: "Unknown error"
            throw Exception("HTTP ${conn.responseCode}: $errorBody")
        }
        val json = JSONObject(conn.inputStream.bufferedReader().readText())
        return json.getJSONArray("choices").getJSONObject(0)
            .getJSONObject("message").getString("content")
    }

    private fun showVisualSteps(container: FrameLayout, raw: String) {
        val scroll = ScrollView(requireContext())
        val layout = LinearLayout(requireContext())
        layout.orientation = LinearLayout.VERTICAL
        layout.setPadding(16, 16, 16, 16)
        val steps = raw.trim().lines().filter { it.contains("|") }
        steps.forEachIndexed { index, line ->
            val parts = line.split("|")
            if (parts.size >= 3) {
                val card = layoutInflater.inflate(R.layout.item_step_card, layout, false)
                card.findViewById<TextView>(R.id.tvStepNumber).text = "${index + 1}"
                card.findViewById<TextView>(R.id.tvStepIcon).text = parts[0].trim()
                card.findViewById<TextView>(R.id.tvStepTitle).text = parts[1].trim()
                card.findViewById<TextView>(R.id.tvStepDetail).text = parts[2].trim()
                layout.addView(card)
            }
        }
        scroll.addView(layout)
        container.addView(scroll)
    }

    private fun showGuideSteps(container: FrameLayout, raw: String) {
        val scroll = ScrollView(requireContext())
        val tv = TextView(requireContext())
        tv.text = raw.trim()
        tv.textSize = 15f
        tv.setPadding(32, 24, 32, 24)
        tv.setTextColor(resources.getColor(R.color.text_primary, null))
        tv.setLineSpacing(8f, 1f)
        scroll.addView(tv)
        container.addView(scroll)
    }

    private fun showWalkthrough(container: FrameLayout, raw: String) {
        val steps = raw.trim().lines().filter { it.contains("|") }
        if (steps.isEmpty()) { showGuideSteps(container, raw); return }

        var currentStep = 0
        val color = savedLineColor
        val layout = LinearLayout(requireContext())
        layout.orientation = LinearLayout.VERTICAL
        layout.setPadding(24, 24, 24, 24)

        val pb = ProgressBar(requireContext(), null, android.R.attr.progressBarStyleHorizontal)
        pb.max = steps.size
        pb.progress = 1
        val pbp = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        pbp.bottomMargin = 24
        pb.layoutParams = pbp

        val tvProgress = TextView(requireContext())
        tvProgress.textSize = 13f
        tvProgress.setPadding(0, 0, 0, 16)
        tvProgress.setTextColor(Color.GRAY)

        val tvIcon = TextView(requireContext())
        tvIcon.textSize = 48f
        tvIcon.gravity = android.view.Gravity.CENTER
        val ip = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        ip.bottomMargin = 16
        tvIcon.layoutParams = ip

        val tvTitle = TextView(requireContext())
        tvTitle.textSize = 20f
        tvTitle.textAlignment = View.TEXT_ALIGNMENT_CENTER
        tvTitle.setTypeface(null, Typeface.BOLD)
        tvTitle.setTextColor(resources.getColor(R.color.text_primary, null))
        val tp = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        tp.bottomMargin = 12
        tvTitle.layoutParams = tp

        val tvDetail = TextView(requireContext())
        tvDetail.textSize = 15f
        tvDetail.textAlignment = View.TEXT_ALIGNMENT_CENTER
        tvDetail.setTextColor(resources.getColor(R.color.text_primary, null))
        tvDetail.setLineSpacing(6f, 1f)
        val dp2 = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        dp2.bottomMargin = 32
        tvDetail.layoutParams = dp2

        val btnNext = Button(requireContext())
        btnNext.setBackgroundColor(Color.parseColor(color))
        btnNext.setTextColor(Color.WHITE)

        fun updateStep() {
            val parts = steps[currentStep].split("|")
            tvProgress.text = "Step ${currentStep + 1} of ${steps.size}"
            pb.progress = currentStep + 1
            tvIcon.text = "👣"
            tvTitle.text = if (parts.size > 1) parts[1].trim() else ""
            tvDetail.text = if (parts.size > 2) parts[2].trim() else ""
            if (currentStep == steps.size - 1) {
                btnNext.text = "🎉 You have arrived!"
                btnNext.setOnClickListener { dismiss() }
            } else {
                btnNext.text = "✅ Done, Next Step →"
                btnNext.setOnClickListener { currentStep++; updateStep() }
            }
        }

        updateStep()
        layout.addView(pb)
        layout.addView(tvProgress)
        layout.addView(tvIcon)
        layout.addView(tvTitle)
        layout.addView(tvDetail)
        layout.addView(btnNext)
        container.addView(layout)
    }

    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
    }
}
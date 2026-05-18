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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

class GateDirectionsBottomSheet : BottomSheetDialogFragment() {

    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private var savedLineColor: String = "#1a237e"

    companion object {
        fun newInstance(
            stationName: String,
            gateName: String,
            gateDestination: String,
            lineColor: String,
            geminiApiKey: String
        ): GateDirectionsBottomSheet {
            return GateDirectionsBottomSheet().apply {
                arguments = Bundle().apply {
                    putString("station", stationName)
                    putString("gate", gateName)
                    putString("destination", gateDestination)
                    putString("lineColor", lineColor)
                    putString("apiKey", geminiApiKey)
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val root = inflater.inflate(R.layout.bottom_sheet_gate_directions, container, false)

        val stationName = arguments?.getString("station") ?: ""
        val gateName = arguments?.getString("gate") ?: ""
        val gateDestination = arguments?.getString("destination") ?: ""
        savedLineColor = arguments?.getString("lineColor") ?: "#1a237e"
        val apiKey = arguments?.getString("apiKey") ?: ""

        val tvTitle = root.findViewById<TextView>(R.id.tvGateTitle)
        val tvSubtitle = root.findViewById<TextView>(R.id.tvGateSubtitle)
        val tabLayout = root.findViewById<TabLayout>(R.id.tabDirections)
        val contentContainer = root.findViewById<FrameLayout>(R.id.frameContent)
        val progressBar = root.findViewById<ProgressBar>(R.id.progressLoading)

        tvTitle.text = "$gateName → $gateDestination"
        tvSubtitle.text = stationName
        tvTitle.setTextColor(Color.parseColor(savedLineColor))

        tabLayout.addTab(tabLayout.newTab().setText("Line Map"))
        tabLayout.addTab(tabLayout.newTab().setText("Visual Steps"))
        tabLayout.addTab(tabLayout.newTab().setText("Instructions"))
        tabLayout.addTab(tabLayout.newTab().setText("Walk Through"))

        fun loadTab(position: Int) {
            contentContainer.removeAllViews()
            when (position) {
                0 -> showLineMap(contentContainer, stationName, gateName)
                1 -> loadAiContent(contentContainer, progressBar, stationName, gateName, gateDestination, apiKey, "visual")
                2 -> loadAiContent(contentContainer, progressBar, stationName, gateName, gateDestination, apiKey, "guide")
                3 -> loadAiContent(contentContainer, progressBar, stationName, gateName, gateDestination, apiKey, "walkthrough")
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

    private fun showLineMap(container: FrameLayout, stationName: String, gateName: String) {
        val scrollView = ScrollView(requireContext())
        val mapView = LineMapView(requireContext(), stationName, gateName, savedLineColor)
        scrollView.addView(
            mapView,
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        container.addView(
            scrollView,
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
    }

    private fun loadAiContent(
        container: FrameLayout,
        progressBar: ProgressBar,
        stationName: String,
        gateName: String,
        gateDestination: String,
        apiKey: String,
        mode: String
    ) {
        progressBar.visibility = View.VISIBLE
        container.removeAllViews()

        val prompt = when (mode) {
            "visual" -> "A user is at $stationName metro station in Bengaluru and wants to reach $gateName which leads to $gateDestination. Give exactly 5 step-by-step visual directions from the platform to the gate exit. Format each step as: ICON|STEP_TITLE|STEP_DETAIL. Use these icons: 🚶 🔄 ⬆️ ⬇️ 🚪 ➡️ ⬅️ 🎯. Return only the 5 steps, one per line, no extra text."
            "guide" -> "A user is at $stationName metro station in Bengaluru and wants to reach $gateName which leads to $gateDestination. Give clear bullet point instructions from the platform to the exit gate. Start each line with • and be specific. Return 6-8 bullet points only, no intro or outro text."
            "walkthrough" -> "A user is at $stationName metro station in Bengaluru and wants to reach $gateName which leads to $gateDestination. Create a step-by-step walkthrough. Exactly 6 steps. Format each step as: STEP_NUMBER|STEP_TITLE|STEP_DETAIL. Return only the 6 steps, one per line, no extra text."
            else -> ""
        }

        scope.launch {
            try {
                val result = withContext(Dispatchers.IO) {
                    callNvidiaAI(apiKey, prompt)
                }
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
        val systemMsg = JSONObject()
        systemMsg.put("role", "system")
        systemMsg.put("content", "You are a helpful Namma Metro Bengaluru assistant.")
        messages.put(systemMsg)
        val userMsg = JSONObject()
        userMsg.put("role", "user")
        userMsg.put("content", prompt)
        messages.put(userMsg)
        body.put("messages", messages)
        body.put("temperature", 0.5)
        body.put("max_tokens", 600)

        val conn = url.openConnection() as HttpURLConnection
        conn.requestMethod = "POST"
        conn.setRequestProperty("Content-Type", "application/json")
        conn.setRequestProperty("Authorization", "Bearer $apiKey")
        conn.doOutput = true
        conn.connectTimeout = 30000
        conn.readTimeout = 30000
        conn.outputStream.write(body.toString().toByteArray())

        val responseCode = conn.responseCode
        if (responseCode != HttpURLConnection.HTTP_OK) {
            val errorBody = conn.errorStream?.bufferedReader()?.readText() ?: "Unknown error"
            throw Exception("HTTP $responseCode: $errorBody")
        }

        val response = conn.inputStream.bufferedReader().readText()
        val json = JSONObject(response)
        return json.getJSONArray("choices")
            .getJSONObject(0)
            .getJSONObject("message")
            .getString("content")
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
        if (steps.isEmpty()) {
            showGuideSteps(container, raw)
            return
        }

        var currentStep = 0
        val color = savedLineColor

        val layout = LinearLayout(requireContext())
        layout.orientation = LinearLayout.VERTICAL
        layout.setPadding(24, 24, 24, 24)

        val progressBar = ProgressBar(requireContext(), null, android.R.attr.progressBarStyleHorizontal)
        progressBar.max = steps.size
        progressBar.progress = 1
        val pbParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        pbParams.bottomMargin = 24
        progressBar.layoutParams = pbParams

        val tvProgress = TextView(requireContext())
        tvProgress.textSize = 13f
        tvProgress.setPadding(0, 0, 0, 16)
        tvProgress.setTextColor(Color.GRAY)

        val tvStepIcon = TextView(requireContext())
        tvStepIcon.textSize = 48f
        tvStepIcon.gravity = android.view.Gravity.CENTER
        val iconParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        iconParams.bottomMargin = 16
        tvStepIcon.layoutParams = iconParams

        val tvStepTitle = TextView(requireContext())
        tvStepTitle.textSize = 20f
        tvStepTitle.textAlignment = View.TEXT_ALIGNMENT_CENTER
        tvStepTitle.setTypeface(null, Typeface.BOLD)
        tvStepTitle.setTextColor(resources.getColor(R.color.text_primary, null))
        val titleParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        titleParams.bottomMargin = 12
        tvStepTitle.layoutParams = titleParams

        val tvStepDetail = TextView(requireContext())
        tvStepDetail.textSize = 15f
        tvStepDetail.textAlignment = View.TEXT_ALIGNMENT_CENTER
        tvStepDetail.setTextColor(resources.getColor(R.color.text_primary, null))
        tvStepDetail.setLineSpacing(6f, 1f)
        val detailParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        detailParams.bottomMargin = 32
        tvStepDetail.layoutParams = detailParams

        val btnNext = Button(requireContext())
        btnNext.setBackgroundColor(Color.parseColor(color))
        btnNext.setTextColor(Color.WHITE)

        fun updateStep() {
            val parts = steps[currentStep].split("|")
            val title = if (parts.size > 1) parts[1].trim() else ""
            val detail = if (parts.size > 2) parts[2].trim() else ""
            progressBar.progress = currentStep + 1
            tvProgress.text = "Step ${currentStep + 1} of ${steps.size}"
            tvStepIcon.text = "👣"
            tvStepTitle.text = title
            tvStepDetail.text = detail
            if (currentStep == steps.size - 1) {
                btnNext.text = "You have arrived!"
                btnNext.setOnClickListener { dismiss() }
            } else {
                btnNext.text = "Done, Next Step"
                btnNext.setOnClickListener {
                    currentStep++
                    updateStep()
                }
            }
        }

        updateStep()

        layout.addView(progressBar)
        layout.addView(tvProgress)
        layout.addView(tvStepIcon)
        layout.addView(tvStepTitle)
        layout.addView(tvStepDetail)
        layout.addView(btnNext)
        container.addView(layout)
    }

    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
    }
}
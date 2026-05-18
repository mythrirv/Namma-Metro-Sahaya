package com.nammametro.sahaya

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.nammametro.sahaya.adapter.StepAdapter
import com.nammametro.sahaya.data.MetroData
import com.nammametro.sahaya.data.MetroLine
import com.nammametro.sahaya.databinding.ActivityVisualGuideBinding

class VisualGuideActivity : AppCompatActivity() {

    private lateinit var binding: ActivityVisualGuideBinding
    private val NVIDIA_API_KEY = "ADD_YOUR_API_KEY_HERE"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVisualGuideBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Visual Guide"

        val fromId = intent.getStringExtra("FROM_ID") ?: return
        val toId = intent.getStringExtra("TO_ID") ?: return
        val totalMinutes = intent.getIntExtra("TOTAL_MINUTES", 0)
        val totalStations = intent.getIntExtra("TOTAL_STATIONS", 0)
        val fare = intent.getIntExtra("FARE", 0)
        val hasInterchange = intent.getBooleanExtra("HAS_INTERCHANGE", false)
        val interchangeStation = intent.getStringExtra("INTERCHANGE_STATION")
        val pathNames = intent.getStringArrayListExtra("PATH_NAMES") ?: arrayListOf()

        val fromStation = MetroData.getStationById(fromId)
        val toStation = MetroData.getStationById(toId)

        if (fromStation == null || toStation == null) return

        binding.tvFrom.text = fromStation.name
        binding.tvTo.text = toStation.name
        binding.tvDuration.text = "$totalMinutes mins"
        binding.tvStations.text = "$totalStations stations"
        binding.tvFare.text = "₹$fare"

        // Interchange warning — color based on line
        if (hasInterchange && interchangeStation != null) {
            binding.tvInterchange.visibility = View.VISIBLE
            binding.tvInterchange.text = "⚡ Change trains at: $interchangeStation"
            // Interchange is always at Majestic — orange color
            binding.tvInterchange.setTextColor(Color.parseColor("#FF9800"))
        } else {
            binding.tvInterchange.visibility = View.GONE
        }

        // Line color for from station
        val lineColor = when (fromStation.line) {
            MetroLine.PURPLE -> "#7B1FA2"
            MetroLine.GREEN -> "#4CAF50"
        }

        binding.tvPath.text = pathNames.joinToString(" → ")

        val steps = MetroData.getVisualGuideSteps(
            fromStation, toStation,
            MetroData.allStations.map { it.id }
        )

        binding.rvSteps.layoutManager = LinearLayoutManager(this)
        val prefs = getSharedPreferences("MetroSettings", MODE_PRIVATE)
        val showKannada = prefs.getBoolean("kannada_mode", true)

        binding.rvSteps.adapter = StepAdapter(
            steps = steps,
            showKannada = showKannada,
            apiKey = NVIDIA_API_KEY,
            onStepClick = { step, stepType ->
                when (stepType) {

// NEW:
                    StepAdapter.STEP_TYPE_TOKEN -> {
                        val sheet = TokenInfoBottomSheet.newInstance(
                            stationName = fromStation.name,
                            toStationName = toStation.name,
                            apiKey = NVIDIA_API_KEY
                        )
                        sheet.show(supportFragmentManager, "TokenInfo")
                    }
                    StepAdapter.STEP_TYPE_DETAIL -> {
                        val sheet = StepDetailBottomSheet.newInstance(
                            stepTitle = step.title,
                            stepDescription = step.description,
                            lineColor = lineColor,
                            stationName = fromStation.name,
                            apiKey = NVIDIA_API_KEY
                        )
                        sheet.show(supportFragmentManager, "StepDetail")
                    }
                }
            }
        )
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}
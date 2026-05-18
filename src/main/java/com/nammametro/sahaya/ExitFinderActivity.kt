package com.nammametro.sahaya

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.nammametro.sahaya.adapter.ExitAdapter
import com.nammametro.sahaya.data.MetroData
import com.nammametro.sahaya.data.MetroLine
import com.nammametro.sahaya.data.MetroStation
import com.nammametro.sahaya.databinding.ActivityExitFinderBinding

class ExitFinderActivity : AppCompatActivity() {

    private lateinit var binding: ActivityExitFinderBinding
    private val stationNames = MetroData.allStations.map { it.name }

    // Same key as AIAssistantActivity — replace with your actual key
    private val GEMINI_API_KEY = "ADD_Your_API_KEY_HERE"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityExitFinderBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Exit Finder"

        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, stationNames)
        (binding.etStation as? AutoCompleteTextView)?.setAdapter(adapter)

        val prefilledStation = intent.getStringExtra("STATION_NAME")
        val lookingFor = intent.getStringExtra("LOOKING_FOR")

        if (!prefilledStation.isNullOrEmpty()) {
            binding.etStation.setText(prefilledStation)
            showStationExits(prefilledStation, lookingFor)
        }

        binding.btnFindExit.setOnClickListener {
            val stationName = binding.etStation.text.toString().trim()
            if (stationName.isEmpty()) {
                Toast.makeText(this, "Please enter a station name", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            showStationExits(stationName, lookingFor)
        }
    }

    private fun showStationExits(stationName: String, lookingFor: String? = null) {
        val station = MetroData.findStationByName(stationName)
            ?: MetroData.allStations.find {
                it.name.lowercase().contains(stationName.lowercase())
            }

        if (station == null) {
            Toast.makeText(this, "Station not found", Toast.LENGTH_SHORT).show()
            return
        }

        // Show cards and header
        binding.cardStationInfo.visibility = View.VISIBLE
        binding.tvExitsHeader.visibility = View.VISIBLE

        binding.tvStationName.text = station.name
        binding.tvKannadaName.text = station.kannadaName

        // Line color
        val lineColor = when (station.line) {
            MetroLine.GREEN -> "#4CAF50"
            MetroLine.PURPLE -> "#7B1FA2"
        }

        binding.viewLineColor.background.setTint(
            android.graphics.Color.parseColor(lineColor)
        )
        binding.tvLine.text = station.line.displayName

        // Station type with line color
        if (station.isInterchange) {
            binding.tvIsInterchange.text = "⚡ Interchange Station (Purple ↔ Green)"
            binding.tvIsInterchange.setTextColor(
                android.graphics.Color.parseColor("#e65100")
            )
        } else {
            binding.tvIsInterchange.text = "● Regular Station"
            binding.tvIsInterchange.setTextColor(
                android.graphics.Color.parseColor(lineColor)
            )
        }

        // Build exit list
        val exitList = station.exits.entries.map { (gate, destination) ->
            if (lookingFor != null &&
                destination.lowercase().contains(lookingFor.lowercase())) {
                "✅ $gate: $destination ← YOU WANT THIS"
            } else {
                "$gate: $destination"
            }
        }

        binding.rvExits.layoutManager = LinearLayoutManager(this)
        binding.rvExits.adapter = ExitAdapter(exitList) { gateName, gateDestination ->
            showGateDirections(station, gateName, gateDestination, lineColor)
        }
    }

    private fun showGateDirections(
        station: MetroStation,
        gateName: String,
        gateDestination: String,
        lineColor: String
    ) {
        val bottomSheet = GateDirectionsBottomSheet.newInstance(
            stationName = station.name,
            gateName = gateName,
            gateDestination = gateDestination,
            lineColor = lineColor,
            geminiApiKey = GEMINI_API_KEY
        )
        bottomSheet.show(supportFragmentManager, "GateDirections")
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}
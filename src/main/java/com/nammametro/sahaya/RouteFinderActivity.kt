package com.nammametro.sahaya

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.nammametro.sahaya.data.MetroData
import com.nammametro.sahaya.data.MetroGraph
import com.nammametro.sahaya.databinding.ActivityRouteFinderBinding

class RouteFinderActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRouteFinderBinding
    private val stationNames = MetroData.allStations.map { it.name }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRouteFinderBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Setup toolbar with back arrow
        val toolbar = binding.toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(true)
        supportActionBar?.title = "Route Finder"

        // Setup autocomplete
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, stationNames)
        (binding.etFrom as? AutoCompleteTextView)?.setAdapter(adapter)
        (binding.etTo as? AutoCompleteTextView)?.setAdapter(adapter)

        binding.btnFindRoute.setOnClickListener {
            val fromName = binding.etFrom.text.toString().trim()
            val toName = binding.etTo.text.toString().trim()

            if (fromName.isEmpty() || toName.isEmpty()) {
                Toast.makeText(this, "Please select both stations", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (fromName == toName) {
                Toast.makeText(this, "Source and destination cannot be the same", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val fromStation = MetroData.findStationByName(fromName)
            val toStation = MetroData.findStationByName(toName)

            if (fromStation == null || toStation == null) {
                Toast.makeText(this, "Invalid station name. Please select from the list", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val result = MetroGraph.findRoute(fromStation.id, toStation.id)

            if (result == null) {
                Toast.makeText(this, "No route found", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val intent = Intent(this, VisualGuideActivity::class.java).apply {
                putExtra("FROM_ID", fromStation.id)
                putExtra("TO_ID", toStation.id)
                putExtra("TOTAL_MINUTES", result.totalMinutes)
                putExtra("TOTAL_STATIONS", result.totalStations)
                putExtra("FARE", result.fareRupees)
                putExtra("HAS_INTERCHANGE", result.hasInterchange)
                putExtra("INTERCHANGE_STATION", result.interchangeStation)
                putStringArrayListExtra("PATH_NAMES", ArrayList(result.stationNames))
            }
            startActivity(intent)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}
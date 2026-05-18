package com.nammametro.sahaya

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.nammametro.sahaya.data.MetroData
import com.nammametro.sahaya.databinding.ActivityFareBinding
import kotlin.math.abs

class FareActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFareBinding
    private val stationNames = MetroData.allStations.map { it.name }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFareBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = "Fare & Time"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Auto-fill if coming from search
        val preFrom = intent.getStringExtra("FROM_STATION")
        val preTo = intent.getStringExtra("TO_STATION")
        if (!preFrom.isNullOrEmpty()) binding.etFrom.setText(preFrom)
        if (!preTo.isNullOrEmpty()) binding.etTo.setText(preTo)
        if (!preFrom.isNullOrEmpty() && !preTo.isNullOrEmpty()) {
            binding.btnCalculate.performClick()
        }

        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_dropdown_item_1line,
            stationNames
        )
        (binding.etFrom as? AutoCompleteTextView)?.setAdapter(adapter)
        (binding.etTo as? AutoCompleteTextView)?.setAdapter(adapter)

        binding.btnCalculate.setOnClickListener {
            val fromName = binding.etFrom.text.toString().trim()
            val toName = binding.etTo.text.toString().trim()

            if (fromName.isEmpty() || toName.isEmpty()) {
                Toast.makeText(this, "Please select both stations", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (fromName == toName) {
                Toast.makeText(this, "Source and destination cannot be same", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val fromStation = MetroData.findStationByName(fromName)
            val toStation = MetroData.findStationByName(toName)

            if (fromStation == null || toStation == null) {
                Toast.makeText(this, "Invalid station. Select from the list", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Calculate distance
            val distance = abs(
                fromStation.distanceFromStartKm - toStation.distanceFromStartKm
            )

            // Add extra distance if interchange is needed
            val actualDistance = if (fromStation.line != toStation.line) {
                distance + 2.0
            } else {
                distance
            }

            val fare = MetroData.calculateFare(actualDistance)

            // Approximate time: 2 min per station + 5 min for interchange
            val stationCount = (actualDistance / 1.2).toInt().coerceAtLeast(1)
            val interchangeTime = if (fromStation.line != toStation.line) 5 else 0
            val totalTime = (stationCount * 2) + interchangeTime

            // Show results card now that we have data
            binding.cardResults.visibility = View.VISIBLE

            // Display results
            binding.tvFareResult.text = "₹$fare"
            binding.tvTimeResult.text = "$totalTime mins"
            binding.tvDistanceResult.text = String.format("%.1f km", actualDistance)
            binding.tvStationsResult.text = "$stationCount stations"

            if (fromStation.line != toStation.line) {
                binding.tvInterchangeNote.text =
                    "⚡ You need to change trains at Majestic (KSR Bengaluru City)"
            } else {
                binding.tvInterchangeNote.text = "✅ Direct route — no interchange needed"
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}
package com.nammametro.sahaya

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.View
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.nammametro.sahaya.adapter.SearchAdapter
import com.nammametro.sahaya.data.MetroData
import com.nammametro.sahaya.data.MetroGraph
import com.nammametro.sahaya.databinding.ActivityHomeBinding
import kotlinx.coroutines.launch
import org.json.JSONObject

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private lateinit var auth: FirebaseAuth
    private var searchJob: kotlinx.coroutines.Job? = null

    // ⚠️ PASTE YOUR GEMINI API KEY HERE
    private val GEMINI_API_KEY = "YOUR_GEMINI_API_KEY_HERE"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        loadUserInfo()
        setupHamburger()
        setupCards()
        setupCardHover()
        setupSearch()
    }

    override fun onResume() {
        super.onResume()
        loadUserInfo()
    }

    private fun loadUserInfo() {
        auth.currentUser?.reload()?.addOnCompleteListener {
            val userEmail = auth.currentUser?.email ?: ""
            val userName = auth.currentUser?.displayName
            val displayName = if (userName.isNullOrEmpty()) {
                userEmail.substringBefore("@").replaceFirstChar { it.uppercase() }
            } else userName
            binding.tvWelcome.text = "Welcome, $displayName"
            binding.tvEmail.text = userEmail
        }
    }

    private fun setupHamburger() {
        binding.btnHamburger.setOnClickListener { view ->
            val popup = PopupMenu(this, view, Gravity.END)
            popup.menu.add(0, 1, 0, "👤  Profile")
            popup.menu.add(0, 2, 1, "⚙️  Settings")
            popup.menu.add(0, 3, 2, "ℹ️  About")
            popup.menu.add(0, 4, 3, "🚪  Logout")
            popup.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    1 -> startActivity(Intent(this, ProfileActivity::class.java))
                    2 -> startActivity(Intent(this, SettingsActivity::class.java))
                    3 -> startActivity(Intent(this, AboutActivity::class.java))
                    4 -> {
                        auth.signOut()
                        Toast.makeText(this, "Logged out", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this, LoginActivity::class.java))
                        finish()
                    }
                }
                true
            }
            popup.show()
        }
    }

    private fun setupCards() {
        binding.cardRouteFinder.setOnClickListener {
            startActivity(Intent(this, RouteFinderActivity::class.java))
        }
        binding.cardVisualGuide.setOnClickListener {
            android.app.AlertDialog.Builder(this)
                .setTitle("Visual Guide")
                .setMessage("Use Route Finder to get step-by-step visual guide for your journey.")
                .setPositiveButton("Open Route Finder") { _, _ ->
                    startActivity(Intent(this, RouteFinderActivity::class.java))
                }
                .setNegativeButton("Cancel", null)
                .show()
        }
        binding.cardExitFinder.setOnClickListener {
            startActivity(Intent(this, ExitFinderActivity::class.java))
        }
        binding.cardFare.setOnClickListener {
            startActivity(Intent(this, FareActivity::class.java))
        }
        binding.cardAiAssistant.setOnClickListener {
            startActivity(Intent(this, AIAssistantActivity::class.java))
        }
    }

    private fun setupCardHover() {
        listOf(
            binding.cardRouteFinder,
            binding.cardVisualGuide,
            binding.cardExitFinder,
            binding.cardFare
        ).forEach { card ->
            card.setOnTouchListener { v, event ->
                when (event.action) {
                    android.view.MotionEvent.ACTION_DOWN -> {
                        v.animate().scaleX(0.96f).scaleY(0.96f).setDuration(100).start()
                        (v as com.google.android.material.card.MaterialCardView)
                            .cardElevation = 12f
                    }
                    android.view.MotionEvent.ACTION_UP,
                    android.view.MotionEvent.ACTION_CANCEL -> {
                        v.animate().scaleX(1f).scaleY(1f).setDuration(100).start()
                        (v as com.google.android.material.card.MaterialCardView)
                            .cardElevation = 4f
                        v.performClick()
                    }
                }
                true
            }
        }

        binding.cardAiAssistant.setOnTouchListener { v, event ->
            when (event.action) {
                android.view.MotionEvent.ACTION_DOWN ->
                    v.animate().scaleX(0.9f).scaleY(0.9f).setDuration(100).start()
                android.view.MotionEvent.ACTION_UP,
                android.view.MotionEvent.ACTION_CANCEL -> {
                    v.animate().scaleX(1f).scaleY(1f).setDuration(100).start()
                    v.performClick()
                }
            }
            true
        }
    }

    private fun setupSearch() {
        binding.rvSearchResults.layoutManager = LinearLayoutManager(this)

        binding.etGlobalSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, st: Int, c: Int, a: Int) {}
            override fun onTextChanged(s: CharSequence?, st: Int, c: Int, a: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val query = s.toString().trim()
                searchJob?.cancel()
                if (query.length < 2) {
                    binding.layoutSearchResults.visibility = View.GONE
                    binding.btnClearSearch.visibility = View.GONE
                    return
                }
                binding.btnClearSearch.visibility = View.VISIBLE

                // Show instant local results first
                showLocalResults(query)

                // Then enhance with AI after short delay
                searchJob = lifecycleScope.launch {
                    kotlinx.coroutines.delay(600)
                    if (query.length >= 3) {
                        runAiSearch(query)
                    }
                }
            }
        })

        binding.btnClearSearch.setOnClickListener {
            binding.etGlobalSearch.setText("")
            binding.layoutSearchResults.visibility = View.GONE
            binding.btnClearSearch.visibility = View.GONE
        }

        // Search on keyboard "Search" button
        binding.etGlobalSearch.setOnEditorActionListener { _, _, _ ->
            val query = binding.etGlobalSearch.text.toString().trim()
            if (query.isNotEmpty()) runAiSearch(query)
            true
        }
    }

    // ─── INSTANT LOCAL SEARCH ─────────────────────────────────────────────────
    private fun showLocalResults(query: String) {
        val results = mutableListOf<SearchResult>()
        val q = query.lowercase()

        // Detect "X to Y" pattern for routes
        val toPattern = Regex("(.+?)\\s+to\\s+(.+)", RegexOption.IGNORE_CASE)
        val toMatch = toPattern.find(query)
        if (toMatch != null) {
            val fromPart = toMatch.groupValues[1].trim()
            val toPart = toMatch.groupValues[2].trim()
            val fromStation = MetroData.allStations.find {
                it.name.lowercase().contains(fromPart.lowercase())
            }
            val toStation = MetroData.allStations.find {
                it.name.lowercase().contains(toPart.lowercase())
            }
            if (fromStation != null && toStation != null) {
                val route = MetroGraph.findRoute(fromStation.id, toStation.id)
                if (route != null) {
                    results.add(SearchResult(
                        title = "${fromStation.name} → ${toStation.name}",
                        subtitle = "🕐 ${route.totalMinutes} mins  •  🚉 ${route.totalStations} stops  •  💰 ₹${route.fareRupees}",
                        icon = "🗺️",
                        action = "route_result",
                        fromStation = fromStation.name,
                        toStation = toStation.name,
                        routeData = route
                    ))
                }
            } else {
                results.add(SearchResult(
                    title = "Route: $fromPart → $toPart",
                    subtitle = "Tap to find this route",
                    icon = "🗺️",
                    action = "route_open",
                    fromStation = fromPart,
                    toStation = toPart
                ))
            }
        }

        // Fare pattern "fare X to Y"
        val farePattern = Regex("fare\\s+(.+?)\\s+to\\s+(.+)", RegexOption.IGNORE_CASE)
        val fareMatch = farePattern.find(query)
        if (fareMatch != null) {
            val fromPart = fareMatch.groupValues[1].trim()
            val toPart = fareMatch.groupValues[2].trim()
            val fromStation = MetroData.allStations.find {
                it.name.lowercase().contains(fromPart.lowercase())
            }
            val toStation = MetroData.allStations.find {
                it.name.lowercase().contains(toPart.lowercase())
            }
            if (fromStation != null && toStation != null) {
                val dist = Math.abs(
                    fromStation.distanceFromStartKm - toStation.distanceFromStartKm
                )
                val fare = MetroData.calculateFare(dist)
                results.add(0, SearchResult(
                    title = "Fare: ${fromStation.name} → ${toStation.name}",
                    subtitle = "💰 ₹$fare  •  Tap to see full details",
                    icon = "💰",
                    action = "fare_result",
                    fromStation = fromStation.name,
                    toStation = toStation.name
                ))
            }
        }

        // Station name search
        MetroData.allStations.filter {
            it.name.lowercase().contains(q) || it.kannadaName.contains(q)
        }.take(4).forEach { station ->
            results.add(SearchResult(
                title = station.name,
                subtitle = "${station.line.displayName}${if (station.isInterchange) " • ⚡ Interchange" else ""}",
                icon = if (station.line.displayName.contains("Purple")) "🟣" else "🟢",
                action = "station",
                fromStation = station.name
            ))
        }

        // Exit/destination search
        MetroData.allStations.forEach { station ->
            station.exits.forEach { (gate, destination) ->
                if (destination.lowercase().contains(q)) {
                    results.add(SearchResult(
                        title = destination,
                        subtitle = "${station.name} • $gate",
                        icon = "🚪",
                        action = "exit",
                        fromStation = station.name,
                        extraData = destination
                    ))
                }
            }
        }

        // Feature shortcuts
        if (q.contains("route") || q.contains("path") || q.contains("go to"))
            results.add(0, SearchResult("Route Finder", "Find metro route", "🗺️", "feature_route"))
        if (q.contains("fare") || q.contains("ticket") || q.contains("price") || q.contains("cost"))
            results.add(0, SearchResult("Fare Calculator", "Check ticket price", "💰", "feature_fare"))
        if (q.contains("exit") || q.contains("gate") || q.contains("outside"))
            results.add(0, SearchResult("Exit Finder", "Find station exits", "🚪", "feature_exit"))
        if (q.contains("ai") || q.contains("help") || q.contains("ask") || q.contains("sahaya"))
            results.add(0, SearchResult("Ask Sahaya AI", "Get AI help", "🤖", "feature_ai"))

        displayResults(results, query)
    }

    // ─── AI-POWERED SEARCH ────────────────────────────────────────────────────
    private fun runAiSearch(query: String) {
        if (GEMINI_API_KEY == "YOUR_GEMINI_API_KEY_HERE") {
            return // Skip AI if key not set
        }

        lifecycleScope.launch {
            try {
                val stationList = MetroData.allStations
                    .take(20).joinToString(", ") { it.name }

                val prompt = """
                    You are a search assistant for Namma Metro Bengaluru app.
                    User searched: "$query"
                    
                    Available stations include: $stationList (and more)
                    
                    Understand the user's intent and return ONLY a JSON object.
                    No explanation, no markdown, just raw JSON.
                    
                    Possible actions:
                    - route: user wants to go from one place to another
                    - fare: user wants to know ticket price
                    - exit: user wants to know which gate/exit to use
                    - station_info: user wants info about a station
                    - feature: user wants to open a feature
                    
                    JSON format:
                    {"action":"route","from":"exact station name","to":"exact station name","confidence":0.9}
                    {"action":"fare","from":"exact station name","to":"exact station name","confidence":0.9}
                    {"action":"exit","station":"exact station name","looking_for":"what they want outside","confidence":0.8}
                    {"action":"station_info","station":"exact station name","confidence":0.9}
                    {"action":"feature","feature":"route/fare/exit/ai","confidence":0.7}
                    {"action":"unknown","confidence":0.1}
                    
                    Match station names to the closest real station name from the list.
                    NIMHANS is near Yelachenahalli. Airport direction is Nagasandra.
                """.trimIndent()

                val response = com.nammametro.sahaya.api.GeminiApiService.askGemini(
                    apiKey = GEMINI_API_KEY,
                    userMessage = prompt,
                    systemContext = "Return only valid JSON. No other text."
                )

                // Parse AI response
                val cleanResponse = response.trim()
                    .removePrefix("```json").removePrefix("```")
                    .removeSuffix("```").trim()

                val json = JSONObject(cleanResponse)
                val action = json.optString("action", "unknown")
                val confidence = json.optDouble("confidence", 0.0)

                if (confidence < 0.5) return@launch

                val aiResults = mutableListOf<SearchResult>()

                when (action) {
                    "route" -> {
                        val from = json.optString("from")
                        val to = json.optString("to")
                        val fromStation = MetroData.findStationByName(from)
                            ?: MetroData.allStations.find {
                                it.name.lowercase().contains(from.lowercase())
                            }
                        val toStation = MetroData.findStationByName(to)
                            ?: MetroData.allStations.find {
                                it.name.lowercase().contains(to.lowercase())
                            }

                        if (fromStation != null && toStation != null) {
                            val route = MetroGraph.findRoute(fromStation.id, toStation.id)
                            if (route != null) {
                                aiResults.add(SearchResult(
                                    title = "🤖 AI: ${fromStation.name} → ${toStation.name}",
                                    subtitle = "🕐 ${route.totalMinutes} mins  •  🚉 ${route.totalStations} stops  •  💰 ₹${route.fareRupees}",
                                    icon = "✨",
                                    action = "route_result",
                                    fromStation = fromStation.name,
                                    toStation = toStation.name,
                                    routeData = route
                                ))
                            }
                        }
                    }
                    "fare" -> {
                        val from = json.optString("from")
                        val to = json.optString("to")
                        val fromStation = MetroData.allStations.find {
                            it.name.lowercase().contains(from.lowercase())
                        }
                        val toStation = MetroData.allStations.find {
                            it.name.lowercase().contains(to.lowercase())
                        }
                        if (fromStation != null && toStation != null) {
                            val dist = Math.abs(
                                fromStation.distanceFromStartKm - toStation.distanceFromStartKm
                            )
                            val fare = MetroData.calculateFare(dist)
                            aiResults.add(SearchResult(
                                title = "🤖 Fare: ${fromStation.name} → ${toStation.name}",
                                subtitle = "💰 ₹$fare  •  Tap for full details",
                                icon = "✨",
                                action = "fare_result",
                                fromStation = fromStation.name,
                                toStation = toStation.name
                            ))
                        }
                    }
                    "exit" -> {
                        val station = json.optString("station")
                        val lookingFor = json.optString("looking_for")
                        aiResults.add(SearchResult(
                            title = "🤖 Exits at $station",
                            subtitle = "Looking for: $lookingFor",
                            icon = "✨",
                            action = "exit",
                            fromStation = station,
                            extraData = lookingFor
                        ))
                    }
                    "station_info" -> {
                        val stationName = json.optString("station")
                        val station = MetroData.allStations.find {
                            it.name.lowercase().contains(stationName.lowercase())
                        }
                        if (station != null) {
                            aiResults.add(SearchResult(
                                title = "🤖 ${station.name}",
                                subtitle = "${station.line.displayName}${if (station.isInterchange) " • ⚡ Interchange" else ""}  •  ${station.exits.size} exits",
                                icon = "✨",
                                action = "station",
                                fromStation = station.name
                            ))
                        }
                    }
                }

                if (aiResults.isNotEmpty()) {
                    // Prepend AI results to existing results
                    val currentAdapter = binding.rvSearchResults.adapter as? SearchAdapter
                    val existingResults = currentAdapter?.getResults() ?: emptyList()
                    val combined = aiResults + existingResults
                    displayResults(combined, query)
                }

            } catch (e: Exception) {
                // AI failed silently — local results already shown
            }
        }
    }

    private fun displayResults(results: List<SearchResult>, query: String) {
        if (results.isEmpty()) {
            val noResult = listOf(SearchResult(
                "No results for \"$query\"",
                "Try: 'MG Road to Whitefield' or 'KSRTC exit'",
                "🔍", "none"
            ))
            binding.rvSearchResults.adapter = SearchAdapter(noResult) {}
        } else {
            binding.rvSearchResults.adapter = SearchAdapter(results) { result ->
                handleSearchResultClick(result)
            }
        }
        binding.layoutSearchResults.visibility = View.VISIBLE
    }

    private fun handleSearchResultClick(result: SearchResult) {
        binding.etGlobalSearch.setText("")
        binding.layoutSearchResults.visibility = View.GONE

        when (result.action) {
            "route_result" -> {
                // Go directly to Visual Guide with route data
                val route = result.routeData ?: return
                val intent = Intent(this, VisualGuideActivity::class.java).apply {
                    putExtra("FROM_ID", MetroData.findStationByName(result.fromStation)?.id)
                    putExtra("TO_ID", MetroData.findStationByName(result.toStation)?.id)
                    putExtra("TOTAL_MINUTES", route.totalMinutes)
                    putExtra("TOTAL_STATIONS", route.totalStations)
                    putExtra("FARE", route.fareRupees)
                    putExtra("HAS_INTERCHANGE", route.hasInterchange)
                    putExtra("INTERCHANGE_STATION", route.interchangeStation)
                    putStringArrayListExtra("PATH_NAMES", ArrayList(route.stationNames))
                }
                startActivity(intent)
            }
            "route_open" -> {
                startActivity(Intent(this, RouteFinderActivity::class.java))
            }
            "fare_result" -> {
                val intent = Intent(this, FareActivity::class.java).apply {
                    putExtra("FROM_STATION", result.fromStation)
                    putExtra("TO_STATION", result.toStation)
                }
                startActivity(intent)
            }
            "station", "exit" -> {
                val intent = Intent(this, ExitFinderActivity::class.java).apply {
                    putExtra("STATION_NAME", result.fromStation)
                    if (result.extraData.isNotEmpty()) {
                        putExtra("LOOKING_FOR", result.extraData)
                    }
                }
                startActivity(intent)
            }
            "feature_route" -> startActivity(Intent(this, RouteFinderActivity::class.java))
            "feature_fare"  -> startActivity(Intent(this, FareActivity::class.java))
            "feature_exit"  -> startActivity(Intent(this, ExitFinderActivity::class.java))
            "feature_ai"    -> startActivity(Intent(this, AIAssistantActivity::class.java))
        }
    }
}

data class SearchResult(
    val title: String,
    val subtitle: String,
    val icon: String,
    val action: String,
    val fromStation: String = "",
    val toStation: String = "",
    val extraData: String = "",
    val routeData: com.nammametro.sahaya.data.RouteResult? = null
)
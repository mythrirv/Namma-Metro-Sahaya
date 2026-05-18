package com.nammametro.sahaya.data

enum class MetroLine(val displayName: String) {
    PURPLE("Purple Line"),
    GREEN("Green Line")
}

data class MetroStation(
    val id: String,
    val name: String,
    val kannadaName: String,
    val line: MetroLine,
    val isInterchange: Boolean = false,
    val distanceFromStartKm: Double = 0.0,
    val exits: Map<String, String> = emptyMap()
)

data class RouteResult(
    val path: List<String>,
    val stationNames: List<String>,
    val totalMinutes: Int,
    val totalStations: Int,
    val fareRupees: Int,
    val hasInterchange: Boolean,
    val interchangeStation: String? = null
)

data class VisualStep(
    val stepNumber: Int,
    val title: String,
    val description: String,
    val kannadaDescription: String,
    val icon: String,
    val isInterchange: Boolean
)
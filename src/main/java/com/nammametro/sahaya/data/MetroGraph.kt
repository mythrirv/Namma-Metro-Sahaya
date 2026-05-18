package com.nammametro.sahaya.data

object MetroGraph {

    // BFS to find shortest path between two stations
    fun findRoute(fromId: String, toId: String): RouteResult? {
        val fromStation = MetroData.getStationById(fromId) ?: return null
        val toStation = MetroData.getStationById(toId) ?: return null

        // Build adjacency list
        val graph = buildGraph()

        // BFS
        val visited = mutableSetOf<String>()
        val queue = ArrayDeque<List<String>>()
        queue.add(listOf(fromId))
        visited.add(fromId)

        while (queue.isNotEmpty()) {
            val path = queue.removeFirst()
            val currentId = path.last()

            if (currentId == toId) {
                // Found the path
                val stationNames = path.map {
                    MetroData.getStationById(it)?.name ?: it
                }

                val totalStations = path.size - 1
                val totalMinutes = (totalStations * 2) +
                        if (needsInterchange(path)) 5 else 0

                val distance = calculatePathDistance(path)
                val fare = MetroData.calculateFare(distance)

                val interchange = findInterchangeStation(path)

                return RouteResult(
                    path = path,
                    stationNames = stationNames,
                    totalMinutes = totalMinutes,
                    totalStations = totalStations,
                    fareRupees = fare,
                    hasInterchange = interchange != null,
                    interchangeStation = interchange
                )
            }

            // Add neighbours
            val neighbours = graph[currentId] ?: emptyList()
            for (neighbour in neighbours) {
                if (neighbour !in visited) {
                    visited.add(neighbour)
                    queue.add(path + neighbour)
                }
            }
        }

        return null // No path found
    }

    private fun buildGraph(): Map<String, List<String>> {
        val graph = mutableMapOf<String, MutableList<String>>()

        // Connect purple line stations sequentially
        val purpleIds = MetroData.purpleLineStations.map { it.id }
        for (i in 0 until purpleIds.size - 1) {
            graph.getOrPut(purpleIds[i]) { mutableListOf() }.add(purpleIds[i + 1])
            graph.getOrPut(purpleIds[i + 1]) { mutableListOf() }.add(purpleIds[i])
        }

        // Connect green line stations sequentially
        val greenIds = MetroData.greenLineStations.map { it.id }
        for (i in 0 until greenIds.size - 1) {
            graph.getOrPut(greenIds[i]) { mutableListOf() }.add(greenIds[i + 1])
            graph.getOrPut(greenIds[i + 1]) { mutableListOf() }.add(greenIds[i])
        }

        // Connect interchange: P16 (Majestic Purple) ↔ G14 (Majestic Green)
        graph.getOrPut("P16") { mutableListOf() }.add("G14")
        graph.getOrPut("G14") { mutableListOf() }.add("P16")

        return graph
    }

    private fun needsInterchange(path: List<String>): Boolean {
        return path.contains("P16") && path.contains("G14")
    }

    private fun findInterchangeStation(path: List<String>): String? {
        if (needsInterchange(path)) {
            return "KSR Bengaluru City (Majestic)"
        }
        return null
    }

    private fun calculatePathDistance(path: List<String>): Double {
        if (path.size < 2) return 0.0
        val first = MetroData.getStationById(path.first()) ?: return 0.0
        val last = MetroData.getStationById(path.last()) ?: return 0.0
        return Math.abs(first.distanceFromStartKm - last.distanceFromStartKm)
    }
}
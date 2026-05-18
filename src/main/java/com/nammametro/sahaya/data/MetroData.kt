package com.nammametro.sahaya.data

object MetroData {

    val purpleLineStations = listOf(
        MetroStation("P1", "Challaghatta", "ಚಳ್ಳಘಟ್ಟ", MetroLine.PURPLE,
            distanceFromStartKm = 0.0,
            exits = mapOf("Gate 1" to "Challaghatta Village", "Gate 2" to "Bus Stand")),
        MetroStation("P2", "Kengeri", "ಕೆಂಗೇರಿ", MetroLine.PURPLE,
            distanceFromStartKm = 2.1,
            exits = mapOf("Gate 1" to "Kengeri Bus Stand", "Gate 2" to "Market Area")),
        MetroStation("P3", "Kengeri Bus Terminal", "ಕೆಂಗೇರಿ ಬಸ್ ನಿಲ್ದಾಣ", MetroLine.PURPLE,
            distanceFromStartKm = 3.4,
            exits = mapOf("Gate 1" to "BMTC Bus Terminal", "Gate 2" to "Main Road")),
        MetroStation("P4", "Pattanagere", "ಪಟ್ಟಣಗೆರೆ", MetroLine.PURPLE,
            distanceFromStartKm = 5.0,
            exits = mapOf("Gate 1" to "Pattanagere Main Road")),
        MetroStation("P5", "Jnanabharathi", "ಜ್ಞಾನಭಾರತಿ", MetroLine.PURPLE,
            distanceFromStartKm = 6.3,
            exits = mapOf("Gate 1" to "Bangalore University", "Gate 2" to "Mysuru Road")),
        MetroStation("P6", "Rajarajeshwari Nagar", "ರಾಜರಾಜೇಶ್ವರಿ ನಗರ", MetroLine.PURPLE,
            distanceFromStartKm = 7.6,
            exits = mapOf("Gate 1" to "RR Nagar Bus Stop", "Gate 2" to "BBMP Office")),
        MetroStation("P7", "Nayandahalli", "ನಾಯಂಡಹಳ್ಳಿ", MetroLine.PURPLE,
            distanceFromStartKm = 8.9,
            exits = mapOf("Gate 1" to "Nayandahalli Road", "Gate 2" to "Mysuru Road")),
        MetroStation("P8", "Pantarapalya", "ಪಂತರಪಾಳ್ಯ", MetroLine.PURPLE,
            distanceFromStartKm = 9.8,
            exits = mapOf("Gate 1" to "Pantarapalya Junction")),
        MetroStation("P9", "Mysuru Road", "ಮೈಸೂರು ರಸ್ತೆ", MetroLine.PURPLE,
            distanceFromStartKm = 11.0,
            exits = mapOf("Gate 1" to "Mysuru Road Main", "Gate 2" to "Magadi Road Junction")),
        MetroStation("P10", "Deepanjali Nagar", "ದೀಪಾಂಜಲಿ ನಗರ", MetroLine.PURPLE,
            distanceFromStartKm = 12.1,
            exits = mapOf("Gate 1" to "Deepanjali Nagar Bus Stop")),
        MetroStation("P11", "Attiguppe", "ಅತ್ತಿಗುಪ್ಪೆ", MetroLine.PURPLE,
            distanceFromStartKm = 13.2,
            exits = mapOf("Gate 1" to "Attiguppe Bus Stand", "Gate 2" to "Hosur Road")),
        MetroStation("P12", "Vijayanagar", "ವಿಜಯನಗರ", MetroLine.PURPLE,
            distanceFromStartKm = 14.4,
            exits = mapOf("Gate 1" to "Vijayanagar Circle", "Gate 2" to "8th Cross")),
        MetroStation("P13", "Hosahalli", "ಹೊಸಹಳ್ಳಿ", MetroLine.PURPLE,
            distanceFromStartKm = 15.5,
            exits = mapOf("Gate 1" to "Hosahalli Bus Stop")),
        MetroStation("P14", "Magadi Road", "ಮಾಗಡಿ ರಸ್ತೆ", MetroLine.PURPLE,
            distanceFromStartKm = 16.7,
            exits = mapOf("Gate 1" to "Magadi Road Bus Stand", "Gate 2" to "City Market")),
        MetroStation("P15", "City Railway Station", "ನಗರ ರೈಲ್ವೆ ನಿಲ್ದಾಣ", MetroLine.PURPLE,
            distanceFromStartKm = 17.8,
            exits = mapOf("Gate 1" to "KSR Railway Station", "Gate 2" to "Kempe Gowda Road")),
        MetroStation("P16", "KSR Bengaluru City (Majestic)", "ಕೆ.ಎಸ್.ಆರ್. ಬೆಂಗಳೂರು ನಗರ",
            MetroLine.PURPLE, isInterchange = true, distanceFromStartKm = 18.9,
            exits = mapOf(
                "Gate 1" to "KSRTC Central Bus Stand",
                "Gate 2" to "City Railway Station",
                "Gate 3" to "Kempe Gowda Road",
                "Gate 4" to "Majestic Circle"
            )),
        MetroStation("P17", "Sir M. Visvesvaraya", "ಸರ್ ಎಂ. ವಿಶ್ವೇಶ್ವರಯ್ಯ", MetroLine.PURPLE,
            distanceFromStartKm = 20.1,
            exits = mapOf("Gate 1" to "Ambedkar Veedhi", "Gate 2" to "Central College")),
        MetroStation("P18", "Vidhana Soudha", "ವಿಧಾನ ಸೌಧ", MetroLine.PURPLE,
            distanceFromStartKm = 21.0,
            exits = mapOf("Gate 1" to "Vidhana Soudha Complex", "Gate 2" to "High Court")),
        MetroStation("P19", "Cubbon Park", "ಕಬ್ಬನ್ ಪಾರ್ಕ್", MetroLine.PURPLE,
            distanceFromStartKm = 21.9,
            exits = mapOf("Gate 1" to "Cubbon Park Entrance", "Gate 2" to "Raj Bhavan Road")),
        MetroStation("P20", "MG Road", "ಎಂ.ಜಿ. ರಸ್ತೆ", MetroLine.PURPLE,
            distanceFromStartKm = 22.8,
            exits = mapOf("Gate 1" to "Brigade Road", "Gate 2" to "MG Road East")),
        MetroStation("P21", "Trinity", "ಟ್ರಿನಿಟಿ", MetroLine.PURPLE,
            distanceFromStartKm = 23.7,
            exits = mapOf("Gate 1" to "Trinity Circle", "Gate 2" to "Airport Road")),
        MetroStation("P22", "Halasuru", "ಹಲಸೂರು", MetroLine.PURPLE,
            distanceFromStartKm = 24.6,
            exits = mapOf("Gate 1" to "Halasuru Temple", "Gate 2" to "Ulsoor Road")),
        MetroStation("P23", "Indiranagar", "ಇಂದಿರಾ ನಗರ", MetroLine.PURPLE,
            distanceFromStartKm = 25.5,
            exits = mapOf("Gate 1" to "100 Feet Road", "Gate 2" to "CMH Road")),
        MetroStation("P24", "Swami Vivekananda Road", "ಸ್ವಾಮಿ ವಿವೇಕಾನಂದ ರಸ್ತೆ", MetroLine.PURPLE,
            distanceFromStartKm = 26.4,
            exits = mapOf("Gate 1" to "Old Madras Road", "Gate 2" to "HAL Area")),
        MetroStation("P25", "Baiyappanahalli", "ಬೈಯಪ್ಪನಹಳ್ಳಿ", MetroLine.PURPLE,
            distanceFromStartKm = 27.4,
            exits = mapOf("Gate 1" to "Baiyappanahalli Bus Stop", "Gate 2" to "TTMC")),
        MetroStation("P26", "Benniganahalli", "ಬೆನ್ನಿಗಾನಹಳ್ಳಿ", MetroLine.PURPLE,
            distanceFromStartKm = 28.8,
            exits = mapOf("Gate 1" to "Benniganahalli Road")),
        MetroStation("P27", "KR Puram", "ಕೆ.ಆರ್. ಪುರ", MetroLine.PURPLE,
            distanceFromStartKm = 30.2,
            exits = mapOf("Gate 1" to "KR Puram Bus Stand", "Gate 2" to "Old Madras Road")),
        MetroStation("P28", "Hoodi Junction", "ಹೂಡಿ ಜಂಕ್ಷನ್", MetroLine.PURPLE,
            distanceFromStartKm = 31.5,
            exits = mapOf("Gate 1" to "Hoodi Circle", "Gate 2" to "Outer Ring Road")),
        MetroStation("P29", "Garudacharpalya", "ಗರುಡಾಚಾರ್ ಪಾಳ್ಯ", MetroLine.PURPLE,
            distanceFromStartKm = 32.8,
            exits = mapOf("Gate 1" to "Garudacharpalya Main Road")),
        MetroStation("P30", "Mahadevapura", "ಮಹಾದೇವಪುರ", MetroLine.PURPLE,
            distanceFromStartKm = 34.1,
            exits = mapOf("Gate 1" to "Mahadevapura BBMP Office", "Gate 2" to "ORR")),
        MetroStation("P31", "ITPL", "ಐ.ಟಿ.ಪಿ.ಎಲ್.", MetroLine.PURPLE,
            distanceFromStartKm = 35.4,
            exits = mapOf("Gate 1" to "ITPL Tech Park Main Gate", "Gate 2" to "Whitefield Road")),
        MetroStation("P32", "Channasandra", "ಚನ್ನಸಂದ್ರ", MetroLine.PURPLE,
            distanceFromStartKm = 36.6,
            exits = mapOf("Gate 1" to "Channasandra Main Road")),
        MetroStation("P33", "Pattandur Agrahara", "ಪಟ್ಟಂದೂರು ಅಗ್ರಹಾರ", MetroLine.PURPLE,
            distanceFromStartKm = 37.8,
            exits = mapOf("Gate 1" to "Pattandur Road")),
        MetroStation("P34", "Whitefield (Kadugodi)", "ವೈಟ್‌ಫೀಲ್ಡ್", MetroLine.PURPLE,
            distanceFromStartKm = 39.0,
            exits = mapOf("Gate 1" to "Whitefield Main Road", "Gate 2" to "Kadugodi Bus Stand"))
    )

    val greenLineStations = listOf(
        MetroStation("G1", "Nagasandra", "ನಾಗಸಂದ್ರ", MetroLine.GREEN,
            distanceFromStartKm = 0.0,
            exits = mapOf("Gate 1" to "Nagasandra Bus Stand", "Gate 2" to "Peenya Direction")),
        MetroStation("G2", "Dasarahalli", "ದಸರಹಳ್ಳಿ", MetroLine.GREEN,
            distanceFromStartKm = 1.4,
            exits = mapOf("Gate 1" to "Dasarahalli Main Road")),
        MetroStation("G3", "Jalahalli", "ಜಾಲಹಳ್ಳಿ", MetroLine.GREEN,
            distanceFromStartKm = 2.8,
            exits = mapOf("Gate 1" to "Jalahalli Cross", "Gate 2" to "Air Force Station")),
        MetroStation("G4", "Peenya Industry", "ಪೀಣ್ಯ ಉದ್ಯಮ", MetroLine.GREEN,
            distanceFromStartKm = 4.2,
            exits = mapOf("Gate 1" to "KIADB Industrial Area", "Gate 2" to "Peenya 2nd Stage")),
        MetroStation("G5", "Peenya", "ಪೀಣ್ಯ", MetroLine.GREEN,
            distanceFromStartKm = 5.5,
            exits = mapOf("Gate 1" to "Peenya Industrial Area", "Gate 2" to "Tumkur Road")),
        MetroStation("G6", "Goraguntepalya", "ಗೊರಗುಂಟೆ ಪಾಳ್ಯ", MetroLine.GREEN,
            distanceFromStartKm = 6.9,
            exits = mapOf("Gate 1" to "Goraguntepalya Bus Stop", "Gate 2" to "Tumkur Road")),
        MetroStation("G7", "Yeshwanthpur", "ಯಶವಂತಪುರ", MetroLine.GREEN,
            distanceFromStartKm = 8.3,
            exits = mapOf("Gate 1" to "Yeshwanthpur Railway Station", "Gate 2" to "APMC Yard")),
        MetroStation("G8", "Sandal Soap Factory", "ಸ್ಯಾಂಡಲ್ ಸೋಪ್ ಫ್ಯಾಕ್ಟರಿ", MetroLine.GREEN,
            distanceFromStartKm = 9.6,
            exits = mapOf("Gate 1" to "Sandal Soap Factory", "Gate 2" to "Rajajinagar Direction")),
        MetroStation("G9", "Mahalakshmi", "ಮಹಾಲಕ್ಷ್ಮಿ", MetroLine.GREEN,
            distanceFromStartKm = 10.8,
            exits = mapOf("Gate 1" to "Mahalakshmi Layout", "Gate 2" to "Palace Guttahalli")),
        MetroStation("G10", "Rajajinagar", "ರಾಜಾಜಿನಗರ", MetroLine.GREEN,
            distanceFromStartKm = 12.1,
            exits = mapOf("Gate 1" to "Rajajinagar Bus Stand", "Gate 2" to "8th Block")),
        MetroStation("G11", "Kuvempu Road", "ಕುವೆಂಪು ರಸ್ತೆ", MetroLine.GREEN,
            distanceFromStartKm = 13.3,
            exits = mapOf("Gate 1" to "Kuvempu Road", "Gate 2" to "Sadashivanagar")),
        MetroStation("G12", "Srirampura", "ಶ್ರೀರಾಮಪುರ", MetroLine.GREEN,
            distanceFromStartKm = 14.4,
            exits = mapOf("Gate 1" to "Srirampura Bus Stop", "Gate 2" to "Mathikere")),
        MetroStation("G13", "Shivajinagar", "ಶಿವಾಜಿ ನಗರ", MetroLine.GREEN,
            distanceFromStartKm = 15.6,
            exits = mapOf("Gate 1" to "Shivajinagar Bus Stand", "Gate 2" to "Cubbon Road")),
        MetroStation("G14", "KSR Bengaluru City (Majestic)", "ಕೆ.ಎಸ್.ಆರ್. ಬೆಂಗಳೂರು ನಗರ",
            MetroLine.GREEN, isInterchange = true, distanceFromStartKm = 17.0,
            exits = mapOf(
                "Gate 1" to "KSRTC Central Bus Stand",
                "Gate 2" to "City Railway Station",
                "Gate 3" to "Kempe Gowda Road",
                "Gate 4" to "Majestic Circle"
            )),
        MetroStation("G15", "National College", "ನ್ಯಾಷನಲ್ ಕಾಲೇಜ್", MetroLine.GREEN,
            distanceFromStartKm = 18.1,
            exits = mapOf("Gate 1" to "National College Basavanagudi", "Gate 2" to "KH Road")),
        MetroStation("G16", "Lalbagh", "ಲಾಲ್ ಬಾಗ್", MetroLine.GREEN,
            distanceFromStartKm = 19.3,
            exits = mapOf("Gate 1" to "Lalbagh West Gate", "Gate 2" to "Lalbagh Road")),
        MetroStation("G17", "South End Circle", "ಸೌತ್ ಎಂಡ್ ಸರ್ಕಲ್", MetroLine.GREEN,
            distanceFromStartKm = 20.5,
            exits = mapOf("Gate 1" to "South End Circle", "Gate 2" to "Basavanagudi")),
        MetroStation("G18", "Jayanagar", "ಜಯನಗರ", MetroLine.GREEN,
            distanceFromStartKm = 21.7,
            exits = mapOf("Gate 1" to "4th Block Jayanagar", "Gate 2" to "Shopping Complex")),
        MetroStation("G19", "Rashtriya Vidyalaya Road", "ರಾಷ್ಟ್ರೀಯ ವಿದ್ಯಾಲಯ ರಸ್ತೆ", MetroLine.GREEN,
            distanceFromStartKm = 22.9,
            exits = mapOf("Gate 1" to "RV College Area", "Gate 2" to "Mysuru Road")),
        MetroStation("G20", "Banashankari", "ಬನಶಂಕರಿ", MetroLine.GREEN,
            distanceFromStartKm = 24.1,
            exits = mapOf("Gate 1" to "Banashankari Temple", "Gate 2" to "BSK Bus Stand")),
        MetroStation("G21", "Jaya Prakash Nagar", "ಜಯ ಪ್ರಕಾಶ್ ನಗರ", MetroLine.GREEN,
            distanceFromStartKm = 25.4,
            exits = mapOf("Gate 1" to "JP Nagar Main Road", "Gate 2" to "BMTC Depot")),
        MetroStation("G22", "Yelachenahalli", "ಯಲಚೇನಹಳ್ಳಿ", MetroLine.GREEN,
            distanceFromStartKm = 26.7,
            exits = mapOf("Gate 1" to "Yelachenahalli Bus Stand")),
        MetroStation("G23", "Konanakunte Cross", "ಕೊನನಕುಂಟೆ ಕ್ರಾಸ್", MetroLine.GREEN,
            distanceFromStartKm = 27.9,
            exits = mapOf("Gate 1" to "Konanakunte Main Road")),
        MetroStation("G24", "Doddakallasandra", "ದೊಡ್ಡಕಲ್ಲಸಂದ್ರ", MetroLine.GREEN,
            distanceFromStartKm = 29.2,
            exits = mapOf("Gate 1" to "Doddakallasandra Bus Stop")),
        MetroStation("G25", "Vajarahalli", "ವಜರಹಳ್ಳಿ", MetroLine.GREEN,
            distanceFromStartKm = 30.5,
            exits = mapOf("Gate 1" to "Vajarahalli Road")),
        MetroStation("G26", "Thalaghattapura", "ತಳಘಟ್ಟಪುರ", MetroLine.GREEN,
            distanceFromStartKm = 31.8,
            exits = mapOf("Gate 1" to "Thalaghattapura Bus Stop")),
        MetroStation("G27", "Silk Institute", "ರೇಷ್ಮೆ ಸಂಸ್ಥೆ", MetroLine.GREEN,
            distanceFromStartKm = 33.1,
            exits = mapOf("Gate 1" to "Central Silk Board", "Gate 2" to "NICE Road Junction"))
    )

    val allStations: List<MetroStation> get() = purpleLineStations + greenLineStations

    fun getStationById(id: String) = allStations.find { it.id == id }

    fun findStationByName(name: String) = allStations.find { it.name == name }

    fun calculateFare(distanceKm: Double): Int {
        return when {
            distanceKm <= 2  -> 10
            distanceKm <= 4  -> 15
            distanceKm <= 6  -> 20
            distanceKm <= 8  -> 25
            distanceKm <= 12 -> 30
            distanceKm <= 18 -> 35
            distanceKm <= 24 -> 40
            distanceKm <= 30 -> 45
            else             -> 50
        }
    }

    fun getVisualGuideSteps(
        fromStation: MetroStation,
        toStation: MetroStation,
        path: List<String>
    ): List<VisualStep> {
        val steps = mutableListOf<VisualStep>()
        val sameLine = fromStation.line == toStation.line

        steps.add(VisualStep(1, "Start at ${fromStation.name}",
            "Enter the station. Buy a token or use Metro Card at the ticket counter.",
            "${fromStation.kannadaName} ನಿಲ್ದಾಣದಲ್ಲಿ ಪ್ರವೇಶಿಸಿ.",
            "🚉", false))

        if (sameLine) {
            steps.add(VisualStep(2, "Board ${fromStation.line.displayName}",
                "Go to platform. Check direction board — train heading to ${toStation.name}.",
                "${fromStation.line.displayName} ಪ್ಲಾಟ್‌ಫಾರ್ಮ್‌ಗೆ ಹೋಗಿ.",
                if (fromStation.line == MetroLine.PURPLE) "🟣" else "🟢", false))
        } else {
            steps.add(VisualStep(2, "Board ${fromStation.line.displayName}",
                "Go to ${fromStation.line.displayName} platform. You will change trains at Majestic.",
                "${fromStation.line.displayName} ಪ್ಲಾಟ್‌ಫಾರ್ಮ್‌ಗೆ ಹೋಗಿ.",
                if (fromStation.line == MetroLine.PURPLE) "🟣" else "🟢", false))

            steps.add(VisualStep(3, "⚡ Interchange at Majestic",
                "Get off at KSR Bengaluru City (Majestic). Follow interchange signs. Walk to other platform.",
                "ಮೆಜೆಸ್ಟಿಕ್‌ನಲ್ಲಿ ಇಳಿದು, ಎಕ್ಸ್‌ಚೇಂಜ್ ಬೋರ್ಡ್ ಅನ್ನು ಅನುಸರಿಸಿ.",
                "🔄", true))
        }

        val exitInfo = toStation.exits.entries.joinToString("\n") { "• ${it.key}: ${it.value}" }
        steps.add(VisualStep(steps.size + 1, "Arrive at ${toStation.name}",
            "You have arrived! Use the correct exit:\n$exitInfo",
            "${toStation.kannadaName} ನಿಮ್ಮ ತಲುಪಿ.",
            "🏁", false))

        return steps
    }
}
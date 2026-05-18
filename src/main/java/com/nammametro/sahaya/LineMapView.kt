package com.nammametro.sahaya

import android.content.Context
import android.graphics.*
import android.view.View
import kotlinx.coroutines.*
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

class LineMapView(
    context: Context,
    private val stationName: String,
    private val gateName: String,
    private val lineColorHex: String,
    private val apiKey: String = ""
) : View(context) {

    data class PathStep(val direction: String, val label: String)
    data class Pt(val x: Float, val y: Float)

    private var pathSteps: List<PathStep> = emptyList()
    private var isLoading = true

    private val linePaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val dotFillPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val dotWhitePaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val labelPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val boldPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val loadingPaint = Paint(Paint.ANTI_ALIAS_FLAG)

    companion object {
        private val pathCache = mutableMapOf<String, List<PathStep>>()
    }

    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    // Enforce a minimum height of 600dp so the map never gets clipped
    private val minHeightPx: Int by lazy {
        (600 * resources.displayMetrics.density).toInt()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        // Always force 600dp regardless of what parent says — never let parent shrink this view
        val h = (600 * resources.displayMetrics.density).toInt()
        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), h)
    }

    init {
        linePaint.strokeWidth = 12f
        linePaint.style = Paint.Style.STROKE
        linePaint.strokeCap = Paint.Cap.ROUND
        linePaint.strokeJoin = Paint.Join.ROUND

        dotFillPaint.style = Paint.Style.FILL
        dotWhitePaint.style = Paint.Style.FILL
        dotWhitePaint.color = Color.WHITE

        labelPaint.textSize = 30f
        labelPaint.color = Color.LTGRAY

        boldPaint.textSize = 32f
        boldPaint.typeface = Typeface.DEFAULT_BOLD

        loadingPaint.textSize = 40f
        loadingPaint.color = Color.GRAY
        loadingPaint.textAlign = Paint.Align.CENTER

        val cacheKey = "$stationName|$gateName"
        if (pathCache.containsKey(cacheKey)) {
            pathSteps = pathCache[cacheKey]!!
            isLoading = false
        } else if (apiKey.isNotEmpty()) {
            fetchPathFromAI()
        } else {
            pathSteps = defaultPath()
            isLoading = false
        }
    }

    private fun defaultPath() = listOf(
        PathStep("S", "Exit Train"),
        PathStep("S", "Take Stairs"),
        PathStep("R", "Turn Right"),
        PathStep("S", "Walk Straight"),
        PathStep("S", "Reach Gate")
    )

    private fun fetchPathFromAI() {
        scope.launch {
            try {
                val prompt = "Metro navigation: $stationName station, $gateName, Bengaluru. Give exactly 5 steps from platform to gate. Format: DIRECTION|LABEL (one per line). DIRECTION: S=straight/stairs, R=turn right, L=turn left. LABEL: max 2 words only. No numbering, no extra text."
                val result = withContext(Dispatchers.IO) { callNvidiaAI(apiKey, prompt) }
                val steps = result.trim().lines()
                    .filter { it.contains("|") }
                    .take(5)
                    .mapNotNull { line ->
                        val parts = line.trim().removePrefix("-").trim().split("|")
                        if (parts.size >= 2) {
                            val dir = parts[0].trim().uppercase().firstOrNull()?.toString() ?: "S"
                            PathStep(if (dir in listOf("S", "R", "L")) dir else "S", parts[1].trim())
                        } else null
                    }
                pathSteps = if (steps.isNotEmpty()) steps else defaultPath()
                pathCache["$stationName|$gateName"] = pathSteps
            } catch (e: Exception) {
                pathSteps = defaultPath()
            }
            isLoading = false
            invalidate()
        }
    }

    private fun callNvidiaAI(apiKey: String, prompt: String): String {
        val url = URL("https://integrate.api.nvidia.com/v1/chat/completions")
        val body = JSONObject()
        body.put("model", "meta/llama-3.1-8b-instruct")
        val messages = JSONArray()
        val sys = JSONObject()
        sys.put("role", "system")
        sys.put("content", "Return only the exact format requested. No extra text.")
        val usr = JSONObject()
        usr.put("role", "user")
        usr.put("content", prompt)
        messages.put(sys)
        messages.put(usr)
        body.put("messages", messages)
        body.put("temperature", 0.1)
        body.put("max_tokens", 80)

        val conn = url.openConnection() as HttpURLConnection
        conn.requestMethod = "POST"
        conn.setRequestProperty("Content-Type", "application/json")
        conn.setRequestProperty("Authorization", "Bearer $apiKey")
        conn.doOutput = true
        conn.connectTimeout = 15000
        conn.readTimeout = 15000
        conn.outputStream.write(body.toString().toByteArray())

        if (conn.responseCode != HttpURLConnection.HTTP_OK) {
            throw Exception("HTTP ${conn.responseCode}")
        }
        val json = JSONObject(conn.inputStream.bufferedReader().readText())
        return json.getJSONArray("choices").getJSONObject(0)
            .getJSONObject("message").getString("content")
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val lineColor = try { Color.parseColor(lineColorHex) } catch (e: Exception) { Color.GREEN }
        linePaint.color = lineColor
        dotFillPaint.color = lineColor
        boldPaint.color = lineColor

        val w = width.toFloat()
        val h = height.toFloat()

        if (isLoading) {
            canvas.drawText("Generating path...", w / 2f, h / 2f, loadingPaint)
            return
        }

        if (pathSteps.isEmpty()) {
            canvas.drawText("No path", w / 2f, h / 2f, loadingPaint)
            return
        }

        val padTop = 120f
        val padBottom = 120f
        val padLeft = w * 0.1f
        val padRight = w * 0.1f
        val drawW = w - padLeft - padRight
        val drawH = h - padTop - padBottom

        // Start from bottom-center, draw upward
        val startX = w / 2f
        val startY = h - padBottom
        val segH = drawH / (pathSteps.size + 1).toFloat()
        val turnW = drawW * 0.28f

        // Build waypoints
        val waypoints = mutableListOf<Pt>()
        waypoints.add(Pt(startX, startY))
        var cx = startX
        var cy = startY

        pathSteps.forEach { step ->
            when (step.direction) {
                "S" -> {
                    cy -= segH
                    waypoints.add(Pt(cx, cy))
                }
                "R" -> {
                    val cornerY = cy - segH * 0.5f
                    val newX = (cx + turnW).coerceAtMost(padLeft + drawW - 20f)
                    waypoints.add(Pt(cx, cornerY))
                    waypoints.add(Pt(newX, cornerY))
                    cx = newX
                    cy = cornerY
                }
                "L" -> {
                    val cornerY = cy - segH * 0.5f
                    val newX = (cx - turnW).coerceAtLeast(padLeft + 20f)
                    waypoints.add(Pt(cx, cornerY))
                    waypoints.add(Pt(newX, cornerY))
                    cx = newX
                    cy = cornerY
                }
            }
        }

        // Draw path line
        val path = Path()
        path.moveTo(waypoints[0].x, waypoints[0].y)
        waypoints.drop(1).forEach { path.lineTo(it.x, it.y) }
        canvas.drawPath(path, linePaint)

        // PLATFORM label at bottom
        drawDot(canvas, waypoints[0], true)
        canvas.drawText("▲ PLATFORM", waypoints[0].x + 24f, waypoints[0].y - 20f, boldPaint)

        // EXIT label at top
        val exitPt = waypoints.last()
        drawDot(canvas, exitPt, true)
        val exitText = "🚪 EXIT"
        val exitTextW = boldPaint.measureText(exitText)
        val exitTextX = (exitPt.x - exitTextW / 2f).coerceIn(padLeft, w - padRight - exitTextW)
        canvas.drawText(exitText, exitTextX, exitPt.y - 44f, boldPaint)

        // Draw step labels along the path
        var wIdx = 1
        pathSteps.forEachIndexed { i, step ->
            val pt = waypoints.getOrNull(wIdx) ?: return@forEachIndexed
            val isLastStep = i == pathSteps.size - 1

            if (!isLastStep) {
                drawDot(canvas, pt, false)
            }

            val arrow = when (step.direction) { "R" -> "→ "; "L" -> "← "; else -> "↑ " }
            val labelText = "$arrow${step.label}"
            val labelW = labelPaint.measureText(labelText)

            if (isLastStep) {
                val labelX = (exitPt.x - labelW / 2f).coerceIn(padLeft, w - padRight - labelW)
                canvas.drawText(labelText, labelX, exitPt.y + 44f, labelPaint)
            } else {
                val labelX = when (step.direction) {
                    "R" -> (pt.x - labelW - 24f).coerceAtLeast(padLeft)
                    "L" -> (pt.x + 24f).coerceAtMost(w - padRight - labelW)
                    else -> if (i % 2 == 0) {
                        (pt.x + 24f).coerceAtMost(w - padRight - labelW)
                    } else {
                        (pt.x - labelW - 24f).coerceAtLeast(padLeft)
                    }
                }
                canvas.drawText(labelText, labelX, pt.y + 10f, labelPaint)
            }

            wIdx += when (step.direction) { "S" -> 1; else -> 2 }
        }
    }

    private fun drawDot(canvas: Canvas, pt: Pt, large: Boolean) {
        val outer = if (large) 18f else 12f
        val inner = if (large) 9f else 6f
        canvas.drawCircle(pt.x, pt.y, outer, dotFillPaint)
        canvas.drawCircle(pt.x, pt.y, inner, dotWhitePaint)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        scope.cancel()
    }
}
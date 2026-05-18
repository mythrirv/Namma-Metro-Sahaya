package com.nammametro.sahaya

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.nammametro.sahaya.api.GeminiApiService
import com.nammametro.sahaya.databinding.ActivityAiAssistantBinding
import kotlinx.coroutines.launch

class AIAssistantActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAiAssistantBinding
    private val conversationHistory = StringBuilder()

    // NVIDIA API Key
    private val GEMINI_API_KEY = "ADD_Your_API_KEY_HERE"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAiAssistantBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = "Metro AI Assistant"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        appendMessage("Sahaya", "Namaskara! 🙏 I am Sahaya, your Namma Metro guide. " +
                "Ask me anything about routes, fares, exits, or how to use the metro!")

        binding.btnSend.setOnClickListener {
            val userMessage = binding.etMessage.text.toString().trim()
            if (userMessage.isEmpty()) {
                Toast.makeText(this, "Please type a message", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            appendMessage("You", userMessage)
            binding.etMessage.setText("")
            binding.progressBar.visibility = View.VISIBLE
            binding.btnSend.isEnabled = false

            lifecycleScope.launch {
                try {
                    val response = GeminiApiService.askGemini(
                        apiKey = GEMINI_API_KEY,
                        userMessage = userMessage,
                        systemContext = getMetroContext()
                    )
                    appendMessage("Sahaya", response)
                } catch (e: Exception) {
                    appendMessage("Sahaya", "Sorry, I couldn't connect right now. Error: ${e.message}")
                } finally {
                    binding.progressBar.visibility = View.GONE
                    binding.btnSend.isEnabled = true
                }
            }
        }

        binding.btnBack.setOnClickListener { finish() }
    }

    private fun appendMessage(sender: String, message: String) {
        conversationHistory.append("\n$sender: $message\n")
        binding.tvConversation.text = conversationHistory.toString()
        binding.scrollView.post {
            binding.scrollView.fullScroll(View.FOCUS_DOWN)
        }
    }

    private fun getMetroContext(): String {
        return """
            You are Sahaya, an AI assistant for Namma Metro Bengaluru.
            You help first-time metro users, especially people from rural areas.
            Keep answers simple, friendly and short.
            Key facts:
            - Two lines: Purple Line (Challaghatta to Whitefield) and Green Line (Nagasandra to Silk Institute)
            - Only interchange station: KSR Bengaluru City (Majestic)
            - Fare ranges from Rs 10 to Rs 50
            - Tokens available at ticket counters, Metro Cards also accepted
            - Reply in English. If user writes in Kannada, reply in Kannada.
        """.trimIndent()
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}
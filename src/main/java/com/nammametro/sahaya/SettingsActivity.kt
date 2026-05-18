package com.nammametro.sahaya

import android.content.SharedPreferences
import android.os.Bundle
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.nammametro.sahaya.databinding.ActivitySettingsBinding

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding
    private lateinit var prefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        prefs = getSharedPreferences("MetroSettings", MODE_PRIVATE)

        binding.switchDarkMode.isChecked = prefs.getBoolean("dark_mode", false)
        binding.switchKannada.isChecked = prefs.getBoolean("kannada_mode", true)

        binding.switchDarkMode.setOnCheckedChangeListener { _, isChecked ->
            prefs.edit().putBoolean("dark_mode", isChecked).apply()
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }

        binding.switchKannada.setOnCheckedChangeListener { _, isChecked ->
            prefs.edit().putBoolean("kannada_mode", isChecked).apply()
            Toast.makeText(
                this,
                if (isChecked) "Kannada translations enabled" else "Kannada translations disabled",
                Toast.LENGTH_SHORT
            ).show()
        }

        binding.btnBack.setOnClickListener { finish() }
    }
}
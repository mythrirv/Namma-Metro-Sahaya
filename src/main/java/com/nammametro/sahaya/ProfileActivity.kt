package com.nammametro.sahaya

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.nammametro.sahaya.databinding.ActivityProfileBinding

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        val user = auth.currentUser

        val userEmail = user?.email ?: ""
        val userName = user?.displayName
        val displayName = if (userName.isNullOrEmpty()) {
            userEmail.substringBefore("@").replaceFirstChar { it.uppercase() }
        } else userName

        binding.tvProfileEmail.text = userEmail
        binding.etProfileName.setText(displayName)
        binding.tvInitial.text = displayName.firstOrNull()?.uppercase() ?: "U"

        // Field is locked by default
        // EDIT button — unlocks the field
        binding.btnEditName.setOnClickListener {
            binding.etProfileName.isEnabled = true
            binding.etProfileName.isFocusableInTouchMode = true
            binding.etProfileName.requestFocus()
            binding.btnSaveName.isEnabled = true
            Toast.makeText(this, "You can now edit your name", Toast.LENGTH_SHORT).show()
        }

        // SAVE button — saves and locks field again
        binding.btnSaveName.setOnClickListener {
            val newName = binding.etProfileName.text.toString().trim()
            if (newName.isEmpty()) {
                Toast.makeText(this, "Name cannot be empty", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val profileUpdates = UserProfileChangeRequest.Builder()
                .setDisplayName(newName)
                .build()

            user?.updateProfile(profileUpdates)?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Lock field again
                    binding.etProfileName.isEnabled = false
                    binding.etProfileName.isFocusable = false
                    binding.btnSaveName.isEnabled = false
                    binding.tvInitial.text = newName.firstOrNull()?.uppercase() ?: "U"
                    Toast.makeText(
                        this,
                        "Name updated! Changes will show on Home screen.",
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    Toast.makeText(this, "Update failed. Try again.", Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.btnChangePassword.setOnClickListener {
            val email = user?.email
            if (email != null) {
                auth.sendPasswordResetEmail(email).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(
                            this,
                            "Reset email sent to $email",
                            Toast.LENGTH_LONG
                        ).show()
                    } else {
                        Toast.makeText(this, "Failed to send reset email", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        binding.btnBack.setOnClickListener { finish() }
    }
}
package com.example.piratv.ui.profile

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.piratv.databinding.FragmentProfileBinding
import com.example.piratv.ui.auth.LoginActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.*

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth

    private val IMAGE_PICK_CODE = 101

    private val PREFS = "profile_prefs"
    private val KEY_SAFE_SEARCH = "safe_search"
    private val KEY_PROFILE_IMAGE = "profile_image_uri"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()
        val user = auth.currentUser

        if (user == null) {
            startActivity(Intent(requireContext(), LoginActivity::class.java))
            requireActivity().finish()
            return
        }

        val prefs = requireContext().getSharedPreferences(PREFS, Context.MODE_PRIVATE)

        // Load profile image if exists
        val savedImageUri = prefs.getString(KEY_PROFILE_IMAGE, null)
        if (!savedImageUri.isNullOrEmpty()) {
            Glide.with(this)
                .load(Uri.parse(savedImageUri))
                .fitCenter()
                .into(binding.profilePicture)
        }

        binding.profilePicture.setOnClickListener {
            pickImageFromGallery()
        }

        binding.emailText.text = "Email: ${user.email}"

        val createdAt = user.metadata?.creationTimestamp
        if (createdAt != null) {
            val date = Date(createdAt)
            val fmt = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
            binding.memberSinceText.text = "Member since: ${fmt.format(date)}"
        } else {
            binding.memberSinceText.text = "Member since: —"
        }

        binding.switchSafeSearch.isChecked = prefs.getBoolean(KEY_SAFE_SEARCH, true)

        binding.switchSafeSearch.setOnCheckedChangeListener { _, isOn ->
            prefs.edit()
                .putBoolean(KEY_SAFE_SEARCH, isOn)
                .apply()
        }

        binding.changePasswordButton.setOnClickListener {
            val email = user.email!!
            auth.sendPasswordResetEmail(email)
                .addOnSuccessListener {
                    Toast.makeText(requireContext(), "Password reset email sent to $email \ncheck the spam dir", Toast.LENGTH_LONG).show()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(requireContext(), "Failed to send reset email: ${e.message}", Toast.LENGTH_LONG).show()
                }
        }

        binding.deleteAccountButton.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Delete account")
                .setMessage("Are you sure you want to delete your account? This cannot be undone.")
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Delete") { _, _ ->
                    user.delete()
                        .addOnSuccessListener {
                            Toast.makeText(requireContext(), "Account deleted", Toast.LENGTH_SHORT).show()
                            startActivity(Intent(requireContext(), LoginActivity::class.java))
                            requireActivity().finish()
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(requireContext(), "Failed to delete: ${e.message}", Toast.LENGTH_LONG).show()
                        }
                }
                .show()
        }

        binding.logoutButton.setOnClickListener {
            auth.signOut()
            startActivity(Intent(requireContext(), LoginActivity::class.java))
            requireActivity().finish()
        }
    }

    private fun pickImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, IMAGE_PICK_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == IMAGE_PICK_CODE && resultCode == Activity.RESULT_OK && data?.data != null) {
            val imageUri = data.data!!

            Glide.with(this)
                .load(imageUri)
                .into(binding.profilePicture)

            val prefs = requireContext().getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            prefs.edit().putString(KEY_PROFILE_IMAGE, imageUri.toString()).apply()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

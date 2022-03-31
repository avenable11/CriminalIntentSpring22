package edu.ivytech.criminalintentspring22

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import edu.ivytech.criminalintentspring22.databinding.ActivityUserProfileBinding
import edu.ivytech.criminalintentspring22.firestore.CrimeUser
import edu.ivytech.criminalintentspring22.firestore.FirestoreUtil

class UserProfileActivity : AppCompatActivity() {
    private lateinit var binding:ActivityUserProfileBinding
    private var user : CrimeUser? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        FirestoreUtil.loadUserData().addOnSuccessListener {
            document ->
            user = document.toObject(CrimeUser::class.java)
            updateUI()
        }

        binding.saveProfileBtn.setOnClickListener {
            user!!.name = binding.nameEditText.text.toString()
            FirestoreUtil.saveUserData(user!!).addOnSuccessListener {
                startActivity(Intent(this, MainActivity::class.java))
            }
        }
    }

    private fun updateUI() {
        binding.emailEditText.setText(user!!.email)
        binding.nameEditText.setText(user!!.name)
    }
}
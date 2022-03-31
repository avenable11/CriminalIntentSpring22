package edu.ivytech.criminalintentspring22

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import edu.ivytech.criminalintentspring22.firestore.CrimeUser
import edu.ivytech.criminalintentspring22.firestore.FirestoreUtil

class FirebaseAuthActivity : AppCompatActivity() {
    private val signInLauncher = registerForActivityResult(FirebaseAuthUIActivityResultContract()) {
        res -> this.onSignInResult(res)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_firebase_auth)
        createSignInIntent()
    }

    private fun createSignInIntent() {
        val providers = arrayListOf(
            AuthUI.IdpConfig.GoogleBuilder().build(),
            AuthUI.IdpConfig.AnonymousBuilder().build()
        )
        val signInIntent = AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setTheme(R.style.Theme_CriminalIntentSpring22)
            .setAvailableProviders(providers)
            .build()
        signInLauncher.launch(signInIntent)
    }

    private fun onSignInResult(res: FirebaseAuthUIAuthenticationResult) {
        val response = res.idpResponse
        if(res.resultCode == RESULT_OK) {
           val user = FirestoreUtil.getCurrentUser()
           val email = user!!.email!!
           if(response!!.isNewUser) {
               val newUser = CrimeUser(user.uid, "", email)
               FirestoreUtil.registerUser(newUser).addOnSuccessListener {
                   Toast.makeText(this,
                       "Welcome ${user.email},you have successfully registered.",
                       Toast.LENGTH_LONG
                   ).show()
                   startActivity(Intent(this, UserProfileActivity::class.java))
                   finish()
               }
           } else {
               startActivity(Intent(this, MainActivity::class.java))
               finish()
           }
        } else {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    override fun onStop() {
        super.onStop()

    }
}
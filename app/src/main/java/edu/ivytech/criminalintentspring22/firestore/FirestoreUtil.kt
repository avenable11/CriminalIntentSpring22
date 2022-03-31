package edu.ivytech.criminalintentspring22.firestore

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import edu.ivytech.criminalintentspring22.database.Crime

object FirestoreUtil {
    val userCollection = "users"

    fun getCurrentUser() : FirebaseUser? {
        return FirebaseAuth.getInstance().currentUser
    }

    fun registerUser(userInfo : CrimeUser) : Task<Void> {
        val firestore = FirebaseFirestore.getInstance()
        return firestore.collection(userCollection)
            .document(getCurrentUser()!!.uid)
            .set(userInfo, SetOptions.merge())

    }

    fun saveUserData(userInfo: CrimeUser) : Task<Void> {
        val data = HashMap<String, Any>()
        data["name"] = userInfo.name
        return FirebaseFirestore.getInstance()
            .collection(userCollection)
            .document(userInfo.id)
            .update(data)
    }

    fun loadUserData(): Task<DocumentSnapshot>{
        var userID = ""
        if(getCurrentUser() != null)
        {
            userID = getCurrentUser()!!.uid
        }
        return FirebaseFirestore.getInstance()
            .collection(userCollection)
            .document(userID)
            .get()
    }
    fun saveUserCrimes(list : List<Crime>, userInfo : CrimeUser) {
        val firebase = FirebaseFirestore.getInstance()
        for(crime in list) {
            val firebaseCrime = FirestoreCrime(crime.id.toString(),
                crime.title, crime.date, crime.isSolved, userInfo.id, userInfo.name)
            firebase.collection("crimes").document(crime.id.toString()).set(firebaseCrime, SetOptions.merge())
        }
    }

    fun getAllCrimes() : Task<QuerySnapshot> {
        return FirebaseFirestore.getInstance()
            .collection("crimes").get()
    }
}
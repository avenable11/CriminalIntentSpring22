package edu.ivytech.criminalintentspring22

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.room.Room
import edu.ivytech.criminalintentspring22.database.Crime
import edu.ivytech.criminalintentspring22.database.CrimeDatabase
import edu.ivytech.criminalintentspring22.database.migrations_1_2
import edu.ivytech.criminalintentspring22.database.migrations_2_3
import edu.ivytech.criminalintentspring22.firestore.CrimeUser
import edu.ivytech.criminalintentspring22.firestore.FirestoreCrime
import edu.ivytech.criminalintentspring22.firestore.FirestoreUtil
import java.io.File
import java.util.*
import java.util.concurrent.Executors
import kotlin.collections.HashMap

private const val DATABASE_NAME = "crime_database.db"
class CrimeRepository private constructor(context : Context) {
    companion object {
        private var INSTANCE: CrimeRepository? = null
        fun initialize(context: Context) {
            INSTANCE = CrimeRepository(context)
        }
        fun get(): CrimeRepository {
            return INSTANCE?: throw IllegalStateException("Crime repository must be initialized")
        }
    }
    private val database : CrimeDatabase = Room.databaseBuilder(context,
        CrimeDatabase::class.java, DATABASE_NAME)
        .addMigrations(migrations_1_2)
        .addMigrations(migrations_2_3)
        .build()
    private val crimeDao = database.crimeDao()
    private val executor = Executors.newSingleThreadExecutor()
    private val filesDir = context.applicationContext.filesDir
    fun getAllCrimes() : LiveData<List<Crime>> = crimeDao.getAllCrimes()
    fun getCrime(id : UUID) : LiveData<Crime> = crimeDao.getCrime(id)
    fun addCrime(crime : Crime) {
        executor.execute{crimeDao.addCrime(crime)}
    }
    fun updateCrime(crime : Crime) {
        executor.execute { crimeDao.updateCrime(crime) }
    }

    fun syncCrimes(userInfo : CrimeUser) {
        executor.execute {
            val crimes = crimeDao.getCrimesForFirebase()
            val crimeMap : MutableMap<String, Crime> = HashMap()
            for(c in crimes){
                crimeMap[c.id.toString()] = c
            }
            FirestoreUtil.saveUserCrimes(crimes, userInfo)
            FirestoreUtil.getAllCrimes().addOnSuccessListener {
                documents ->
                for(document in documents) {
                    val fsCrime = document.toObject(FirestoreCrime::class.java)
                    if(!crimeMap.containsKey(fsCrime.id)) {
                        val crime = Crime(UUID.fromString(fsCrime.id), fsCrime.title, fsCrime.date, fsCrime.solved, fsCrime.userID == FirestoreUtil.getCurrentUser()!!.uid)
                        executor.execute {
                            crimeDao.addCrime(crime)
                        }
                        Log.i("Crime Repo Sync", "Added Crime From Firestore ${crime.title}")
                    }
                }

            }

        }
    }
    fun getPhotoFile(crime:Crime): File = File(filesDir, crime.photoFileName)

}
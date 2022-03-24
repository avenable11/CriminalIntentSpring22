package edu.ivytech.criminalintentspring22

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.Room
import edu.ivytech.criminalintentspring22.database.Crime
import edu.ivytech.criminalintentspring22.database.CrimeDatabase
import java.util.*
import java.util.concurrent.Executors

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
        CrimeDatabase::class.java, DATABASE_NAME).build()
    private val crimeDao = database.crimeDao()
    private val executor = Executors.newSingleThreadExecutor()
    fun getAllCrimes() : LiveData<List<Crime>> = crimeDao.getAllCrimes()
    fun getCrime(id : UUID) : LiveData<Crime> = crimeDao.getCrime(id)
    fun addCrime(crime : Crime) {
        executor.execute{crimeDao.addCrime(crime)}
    }
    fun updateCrime(crime : Crime) {
        executor.execute { crimeDao.updateCrime(crime) }
    }


}
package edu.ivytech.criminalintentspring22

import android.content.Context
import androidx.room.Room
import edu.ivytech.criminalintentspring22.database.Crime
import edu.ivytech.criminalintentspring22.database.CrimeDatabase
import java.util.*

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

    fun getAllCrimes() : List<Crime> = crimeDao.getAllCrimes()
    fun getCrime(id : UUID) : Crime = crimeDao.getCrime(id)
    fun addCrime(crime : Crime) = crimeDao.addCrime(crime)
    fun updateCrime(crime : Crime) = crimeDao.updateCrime(crime)


}
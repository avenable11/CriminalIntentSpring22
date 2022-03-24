package edu.ivytech.criminalintentspring22.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import java.util.*

@Dao
interface CrimeDao {

    @Query("select * from crime")
    fun getAllCrimes() : LiveData<List<Crime>>

    //select * from crime where id = idOfCrimeWeWant
    @Query("select * from crime where id = (:crimeId)")
    fun getCrime(crimeId: UUID) : LiveData<Crime>

    @Insert
    fun addCrime(crime : Crime)

    @Update
    fun updateCrime(crime : Crime)
}
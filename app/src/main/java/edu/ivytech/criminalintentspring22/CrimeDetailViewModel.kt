package edu.ivytech.criminalintentspring22

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import edu.ivytech.criminalintentspring22.database.Crime
import java.util.*

class CrimeDetailViewModel : ViewModel() {
    private val crimeRepo = CrimeRepository.get()
    private val crimeIdLiveData = MutableLiveData<UUID>()
    var crimeLiveData : LiveData<Crime> = Transformations.switchMap(crimeIdLiveData) {
        crimeId -> crimeRepo.getCrime(crimeId)
    }

    fun loadCrime(id : UUID) {
        crimeIdLiveData.value = id
    }

    fun addCrime(crime:Crime) {
        crimeRepo.addCrime(crime)
    }

    fun saveCrime(crime: Crime) {
        crimeRepo.updateCrime(crime)
    }

}
package edu.ivytech.criminalintentspring22

import androidx.lifecycle.ViewModel

class CrimeListViewModel : ViewModel() {
    private val crimeRepo = CrimeRepository.get()
    val crimesLiveData = crimeRepo.getAllCrimes()
}
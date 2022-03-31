package edu.ivytech.criminalintentspring22.firestore

import java.util.*

data class FirestoreCrime(val id: String = "",
                          var title:String = "",
                          var date: Date = Date(),
                          var solved: Boolean = false,
                          val userID: String = "",
                          val creatorName: String = "")


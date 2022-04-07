package edu.ivytech.criminalintentspring22.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity
data class Crime(@PrimaryKey val id: UUID = UUID.randomUUID(),
                 var title:String = "",
                 var date:Date = Date(),
                 var isSolved: Boolean = false,
                 var canEdit: Boolean = true,
                 var suspect: String = "") {
    val photoFileName
        get() = "IMG_$id.jpg"
}



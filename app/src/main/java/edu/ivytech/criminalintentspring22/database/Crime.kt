package edu.ivytech.criminalintentspring22.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

object CrimeList {
    val ITEMS: MutableList<Crime> = ArrayList()
    val ITEM_MAP: MutableMap<UUID, Crime> = HashMap()
    private const val COUNT = 10
    init {
        for(i in 1 .. COUNT)
        {
            addItem(createCrime(i))
        }
    }

    private fun addItem(createCrime: Crime) {
        ITEMS.add(createCrime)
        ITEM_MAP[createCrime.id] = createCrime
    }

    private fun createCrime(i: Int): Crime {
        val crime = Crime()
        crime.title = "Crime $i"
        crime.isSolved = i % 2 == 0
        return crime
    }

}

@Entity
data class Crime(@PrimaryKey val id: UUID = UUID.randomUUID(),
                 var title:String = "",
                 var date:Date = Date(),
                 var isSolved: Boolean = false)


package edu.ivytech.criminalintentspring22.database

import androidx.room.TypeConverter
import java.util.*

class CrimeTypeConverters {

    @TypeConverter
    fun fromDate(date: Date?) : Long? {
        return date?.time
    }

    @TypeConverter
    fun toDate(millisSinceEpoch : Long?) : Date? {
        return millisSinceEpoch?.let { Date(it) }
    }

    @TypeConverter
    fun fromUUID(id : UUID) : String {
        return id.toString()
    }

    @TypeConverter
    fun toUUID(id : String?) : UUID? {
        return UUID.fromString(id)
    }



}
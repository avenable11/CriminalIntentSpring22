package edu.ivytech.criminalintentspring22.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [Crime::class], version = 3)
@TypeConverters(CrimeTypeConverters::class)
abstract class CrimeDatabase : RoomDatabase() {
    abstract fun crimeDao() : CrimeDao
}

val migrations_1_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("alter table crime add column canEdit integer not null default 1")
    }
}
val migrations_2_3 = object:Migration(2, 3) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("alter table crime add column suspect text not null default ''")
    }
}
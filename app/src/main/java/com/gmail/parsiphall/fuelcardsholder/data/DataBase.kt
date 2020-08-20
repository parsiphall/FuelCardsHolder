package com.gmail.parsiphall.fuelcardsholder.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Card::class, Note::class], version = 1)
abstract class DataBase : RoomDatabase() {
    abstract fun getDao(): Dao
}
package com.gmail.parsiphall.fuelcardsholder

import android.app.Application
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.gmail.parsiphall.fuelcardsholder.data.DataBase

const val DB_NAME = "FCHolder_DB"
const val DB_SHM = "FCHolder_DB-shm"
const val DB_WAL = "FCHolder_DB-wal"

val DB: DataBase by lazy {
    MainApp.mDataBase!!
}

class MainApp : Application() {

    companion object {
        var mDataBase: DataBase? = null
    }

    override fun onCreate() {
        super.onCreate()

        val mig1to2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE Card ADD COLUMN fuelType INTEGER DEFAULT 0 NOT NULL")
            }
        }

        mDataBase = Room
            .databaseBuilder(applicationContext, DataBase::class.java, DB_NAME)
            .addMigrations(mig1to2)
            .build()
    }
}
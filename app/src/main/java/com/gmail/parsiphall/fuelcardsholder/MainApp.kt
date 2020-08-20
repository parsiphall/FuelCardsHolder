package com.gmail.parsiphall.fuelcardsholder

import android.app.Application
import androidx.room.Room
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
        mDataBase = Room
            .databaseBuilder(applicationContext, DataBase::class.java, DB_NAME)
            .build()
    }
}
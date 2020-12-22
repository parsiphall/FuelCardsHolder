package com.gmail.parsiphall.importexportdb

import android.content.Context
import android.os.Environment
import android.widget.Toast
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.nio.channels.FileChannel

class ImportDB {
    companion object {
        fun launch(c: Context, name: String) {
            val sd =
                File(
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
                        .toString() + "/${name}/Export"
                )
            var source: FileChannel? = null
            var destination: FileChannel? = null
            val newDB = File(sd, "/backupDB")
            val newSHM = File(sd, "/backupSHM")
            val newWAL = File(sd, "/backupWAL")
            val oldDB = File(c.getDatabasePath(name).toString())
            val oldSHM = File(c.getDatabasePath("${name}-shm").toString())
            val oldWAL = File(c.getDatabasePath("${name}-wal").toString())
            try {
                source = FileInputStream(newDB).channel
                destination = FileOutputStream(oldDB).channel
                destination!!.transferFrom(source, 0, source!!.size())
                MainScope().launch {
                    Toast.makeText(c, "DB Imported!", Toast.LENGTH_LONG).show()
                }
                source = FileInputStream(newSHM).channel
                destination = FileOutputStream(oldSHM).channel
                destination!!.transferFrom(source, 0, source!!.size())
                MainScope().launch {
                    Toast.makeText(c, "SHM Imported!", Toast.LENGTH_LONG).show()
                }
                source = FileInputStream(newWAL).channel
                destination = FileOutputStream(oldWAL).channel
                destination!!.transferFrom(source, 0, source!!.size())
                MainScope().launch {
                    Toast.makeText(c, "WAL Imported!", Toast.LENGTH_LONG).show()
                }
            } catch (e: IOException) {
                e.printStackTrace()
                MainScope().launch {
                    Toast.makeText(c, "Error!", Toast.LENGTH_LONG).show()
                }
            } finally {
                source?.close()
                destination?.close()
            }
        }
    }
}

class ExportDB {
    companion object {
        fun launch(c: Context, name: String) {
            val sd =
                File(
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
                        .toString() + "/${name}/Export"
                )
            var source: FileChannel? = null
            var destination: FileChannel? = null
            val currentDB = File(c.getDatabasePath(name).toString())
            val currentSHM = File(c.getDatabasePath("${name}-shm").toString())
            val currentWAL = File(c.getDatabasePath("${name}-wal").toString())
            val backupDB = File(sd.absolutePath + "/backupDB")
            val backupSHM = File(sd.absolutePath + "/backupSHM")
            val backupWAL = File(sd.absolutePath + "/backupWAL")
            if (!sd.exists()) {
                sd.mkdirs()
            }
            if (!backupDB.exists()) {
                backupDB.createNewFile()
                backupSHM.createNewFile()
                backupWAL.createNewFile()
            }
            try {
                source = FileInputStream(currentDB).channel
                destination = FileOutputStream(backupDB).channel
                destination!!.transferFrom(source, 0, source!!.size())
                MainScope().launch {
                    Toast.makeText(c, "DB Exported!", Toast.LENGTH_LONG).show()
                }
                source = FileInputStream(currentSHM).channel
                destination = FileOutputStream(backupSHM).channel
                destination!!.transferFrom(source, 0, source!!.size())
                MainScope().launch {
                    Toast.makeText(c, "SHM Exported!", Toast.LENGTH_LONG).show()
                }
                source = FileInputStream(currentWAL).channel
                destination = FileOutputStream(backupWAL).channel
                destination!!.transferFrom(source, 0, source!!.size())
                MainScope().launch {
                    Toast.makeText(c, "WAL Exported!", Toast.LENGTH_LONG).show()
                }
            } catch (e: IOException) {
                e.printStackTrace()
                MainScope().launch {
                    Toast.makeText(c, "Error!", Toast.LENGTH_LONG).show()
                }
            } finally {
                source?.close()
                destination?.close()
            }
        }
    }
}
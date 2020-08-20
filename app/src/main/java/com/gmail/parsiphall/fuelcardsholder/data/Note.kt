package com.gmail.parsiphall.fuelcardsholder.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity
data class Note(
    @PrimaryKey(autoGenerate = true) @ColumnInfo var id: Long = 0,
    @ColumnInfo var date: String = "",
    @ColumnInfo var cardId: Int = 0,
    @ColumnInfo var difference: Float = 0f
) : Serializable
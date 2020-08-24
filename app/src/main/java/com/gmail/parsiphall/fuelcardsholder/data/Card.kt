package com.gmail.parsiphall.fuelcardsholder.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity
data class Card(
    @PrimaryKey(autoGenerate = true) @ColumnInfo var id: Int = 0,
    @ColumnInfo var number: String = "",
    @ColumnInfo var name: String = "",
    @ColumnInfo var balance: Float = 0f,
    @ColumnInfo var fuelType: Int = 0
) : Serializable
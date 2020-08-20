package com.gmail.parsiphall.fuelcardsholder.data

import androidx.room.*
import androidx.room.Dao

@Dao
interface Dao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addCard(card: Card)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addNote(note: Note)

    @Query("SELECT * FROM Card")
    fun getAllCards(): List<Card>

    @Query("SELECT * FROM Note WHERE cardId LIKE :cardId")
    fun getNotesForCard(cardId: Int): List<Note>

    @Update
    fun updateCard(card: Card)

    @Delete
    fun deleteCard(card: Card)

    @Delete
    fun deleteNote(note: Note)
}
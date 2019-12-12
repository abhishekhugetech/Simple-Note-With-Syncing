package com.abhishek.notesyncing.data.dao

import androidx.paging.DataSource
import androidx.room.*
import com.abhishek.notesyncing.data.model.Note

@Dao
interface NoteDao {

    @Query("SELECT * FROM Note WHERE id = :noteId")
    fun getNote(noteId:Int): Note?

    @Query("SELECT * FROM Note")
    fun getAllNotesList(): List<Note>

    @Query("Select * from note where isDeleted = '0' order by id desc")
    fun getAllNotes() : DataSource.Factory<Int, Note>

    @Insert
    suspend fun insertNote(user: Note):Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllNote(note: List<Note>)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateNote(user: Note)

    @Query("DELETE from note")
    suspend fun deleteAllNote()

    @Delete
    suspend fun deleteNote(note: Note)

}
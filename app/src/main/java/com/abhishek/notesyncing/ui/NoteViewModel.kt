package com.abhishek.notesyncing.ui

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.abhishek.notesyncing.data.AppDatabase
import com.abhishek.notesyncing.data.dao.NoteDao
import com.abhishek.notesyncing.data.model.Note
import com.abhishek.notesyncing.worker.NotesSyncWorker
import com.abhishek.notesyncing.util.WorkersUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class NoteViewModel : ViewModel() {

    private val uiScope = CoroutineScope(Dispatchers.Default)
    private lateinit var context:Context

    private lateinit var noteDao: NoteDao

    fun setUpDb(ctx: Context){
        noteDao = AppDatabase.invoke(ctx).noteDao()
        context = ctx.applicationContext
    }

    override fun onCleared() {
        uiScope.coroutineContext.cancel()
        super.onCleared()
    }

    fun getAllNotes(): LiveData<PagedList<Note>> {
        val allNotes =  noteDao.getAllNotes()

        val pagedListBuilder: LivePagedListBuilder<Int, Note> =
            LivePagedListBuilder<Int, Note>(
                allNotes,
                10
            )
        return pagedListBuilder.build()
    }

    fun addNote(note: Note){
        uiScope.launch {
            note.id = null
            val id = noteDao.insertNote(note)
            WorkersUtil.syncNote(context ,id.toInt(), NotesSyncWorker.EVENT_INSERT_NOTE)
        }
    }

    fun updateNote(note: Note){
        uiScope.launch {
            note.isSynced = false
            noteDao.updateNote(note)
            WorkersUtil.syncNote(context ,note.id!!, NotesSyncWorker.EVENT_UPDATE_NOTE)
        }
    }

    fun deleteAllNote() {
        uiScope.launch { 
            noteDao.deleteAllNote()
        }
    }

    fun deleteNote(note: Note) {
        uiScope.launch {
            note.isDeleted = true
            noteDao.updateNote(note)
            WorkersUtil.syncNote(context ,note.id!!, NotesSyncWorker.EVENT_UPDATE_NOTE)
        }
    }


}
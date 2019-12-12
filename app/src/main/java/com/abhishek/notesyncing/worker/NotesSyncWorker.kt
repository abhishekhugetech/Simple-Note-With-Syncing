package com.abhishek.notesyncing.worker

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.abhishek.notesyncing.data.AppDatabase
import com.abhishek.notesyncing.data.api.Apifactory
import com.abhishek.notesyncing.data.api.MagtappService
import com.abhishek.notesyncing.data.model.Note
import com.abhishek.notesyncing.util.NotificationUtil
import com.abhishek.notesyncing.util.WorkersUtil
import kotlinx.coroutines.*

class NotesSyncWorker(
    val context: Context,
    params : WorkerParameters
) : Worker(context,params) {

    override fun doWork(): Result = runBlocking{
        val noteId = inputData.getInt(NOTE_ID , -1)
        val event = inputData.getInt(EVENT_SYNC_NOTES , -1 )
        if (noteId!=-1 && event!=-1){
            when(event){
                EVENT_INSERT_NOTE -> insertNote(noteId)
                EVENT_UPDATE_NOTE -> updateNote(noteId)
                EVENT_DELETE_NOTE -> deleteNote(noteId)
                EVENT_DELETE_ALL_NOTE -> deleteAllNote()
                EVENT_SYNC_FROM_CLOUD -> syncAllNoteFromCloud()
                else -> Result.failure()
            }
        }else {
            Result.failure()
        }
    }

    private fun syncAllNoteFromCloud(): Result = runBlocking {
        // Show Notification About syncing..
        NotificationUtil.showNotification(context)
        val userId = context.getSharedPreferences(context.packageName,Context.MODE_PRIVATE).getString("userId","abhishek")
        val service = Apifactory.retrofit().create(MagtappService::class.java).getAllNoteAsync(userId!!)
        val result = service.await()
        if (result.isSuccessful){
            // Insert All Note To DB.
            val notesResponse = result.body()?.data
            if (!notesResponse.isNullOrEmpty()){
                val notes = mutableListOf<Note>()
                notesResponse.forEach {
                    notes.add(Note(null,it.title,it.body,isSynced = true,serverId = it._id))
                }
                val isDataSynced = context.getSharedPreferences(context.packageName, Context.MODE_PRIVATE).getBoolean("isDataSynced",false)
                if (!isDataSynced) {
                    AppDatabase.invoke(context).noteDao().insertAllNote(notes)
                    context.getSharedPreferences(context.packageName, Context.MODE_PRIVATE).edit()
                        .putBoolean("isDataSynced", true).apply()
                }
            }
            // Show notification about syncing completed.
            NotificationUtil.showNotificationCompleted(context)
            Result.success()
        }else{
            Result.retry()
        }
    }

    private fun deleteNote(noteId: Int): Result = runBlocking{
        val note = AppDatabase.invoke(context).noteDao().getNote(noteId)
        // If Note is not null
        if (note!=null){
            if (note.serverId!=null){
                val userId = context.getSharedPreferences(context.packageName,Context.MODE_PRIVATE).getString("userId","abhishek")
                // Delete in Cloud
                val service = Apifactory.retrofit().create(MagtappService::class.java).deleteNoteAsync(userId!!,note.serverId!!)
                val result = service.await()
                if (result.isSuccessful){
                    AppDatabase.invoke(context).noteDao().deleteNote(note)
                    Result.success()
                }else{
                    Result.retry()
                }
            }else{
                // Delete in local db
                AppDatabase.invoke(context).noteDao().deleteNote(note)
            }

        }
        Result.failure()
    }

    private fun deleteAllNote(): Result = runBlocking{
        val userId = context.getSharedPreferences(context.packageName,Context.MODE_PRIVATE).getString("userId","abhishek")
        if (!userId.isNullOrBlank()){
            val noteDao = AppDatabase.invoke(context).noteDao()
            val allNotes = noteDao.getAllNotesList()
            allNotes.forEach {
                if (it.serverId!=null){
                    WorkersUtil.syncNote(context,it.id!!, EVENT_DELETE_NOTE)
                    it.isDeleted = true
                    noteDao.updateNote(it)
                }else{
                    noteDao.deleteNote(it)
                }
            }
            Result.success()
        }
        Result.failure()
    }

    private fun updateNote(noteId: Int): Result = runBlocking{
        val note = AppDatabase.invoke(context).noteDao().getNote(noteId)
        // If Note is not null
        if (note!=null){
            val noteJson = note.toJson()
            val userId = context.getSharedPreferences(context.packageName,Context.MODE_PRIVATE).getString("userId","abhishek")
            noteJson.addProperty("userId" , userId )

            if (note.serverId!=null){
                // Update in Cloud
                noteJson.addProperty("note_id" , note.serverId )
                val service = Apifactory.retrofit().create(MagtappService::class.java).updateNoteAsync(userId!!,noteJson)
                val result = service.await()
                if (result.isSuccessful){
                    // Set the Is Synced to False in database.
                    note.isSynced = true
                    AppDatabase.invoke(context).noteDao().updateNote(note)
                    Result.success()
                }else{
                    Result.retry()
                }
            }else{
                // Update in local db
                AppDatabase.invoke(context).noteDao().updateNote(note)
            }

        }
        Result.failure()
    }

    private suspend fun insertNote(noteId:Int): Result = runBlocking {
        val note = AppDatabase.invoke(context).noteDao().getNote(noteId)
        if (note!=null){
            val noteJson = note.toJson()
            val userId = context.getSharedPreferences(context.packageName,Context.MODE_PRIVATE).getString("userId","abhishek")
            noteJson.addProperty("userId" , userId )
            val service = Apifactory.retrofit().create(MagtappService::class.java).addNoteAsync(noteJson)
            val result = service.await()
            if (result.isSuccessful){
                // Set the Is Synced to False in database.
                note.serverId = result.body()?.data?._id
                note.isSynced = true
                AppDatabase.invoke(context).noteDao().updateNote(note)
                Result.success()
            }else{
                Result.retry()
            }
        }else{
            Result.failure()
        }
    }

    companion object{
        const val EVENT_UPDATE_NOTE = 101
        const val EVENT_INSERT_NOTE = 102
        const val EVENT_DELETE_NOTE = 103
        const val EVENT_SYNC_FROM_CLOUD = 104
        const val EVENT_DELETE_ALL_NOTE = 105

        const val NOTE_ID = "arg_note_id"
        const val EVENT_SYNC_NOTES = "note_sync_event"
        const val TAG = "NoteSyncWorker"
    }
}
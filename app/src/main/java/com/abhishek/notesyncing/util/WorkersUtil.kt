package com.abhishek.notesyncing.util

import android.content.Context
import androidx.work.*
import com.abhishek.notesyncing.worker.NotesSyncWorker

object WorkersUtil {
    fun syncNote(context: Context,noteId:Int,event:Int){
        val imageData = workDataOf(
            NotesSyncWorker.EVENT_SYNC_NOTES to event,
            NotesSyncWorker.NOTE_ID to noteId
        )

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .setRequiresCharging(true)
            .setRequiresBatteryNotLow(true)
            .build()

        val uploadWorkRequest = OneTimeWorkRequestBuilder<NotesSyncWorker>()
            .setInputData(imageData)
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(context).enqueue(uploadWorkRequest)
    }
}
package com.abhishek.notesyncing.data.response

data class NoteUpsertResponse(
    val success:Boolean,
    val message:String,
    val data:NoteResponse
)
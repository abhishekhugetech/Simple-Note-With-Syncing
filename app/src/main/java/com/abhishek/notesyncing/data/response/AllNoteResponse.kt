package com.abhishek.notesyncing.data.response

class AllNoteResponse(
    val success:Boolean,
    val message:String,
    val data:List<NoteResponse>
)
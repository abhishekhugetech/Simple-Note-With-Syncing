package com.abhishek.notesyncing.data.api

import com.abhishek.notesyncing.data.response.AllNoteResponse
import com.abhishek.notesyncing.data.response.GeneralNoteResponse
import com.abhishek.notesyncing.data.response.NoteUpsertResponse
import com.google.gson.JsonObject
import kotlinx.coroutines.Deferred
import retrofit2.Response
import retrofit2.http.*

interface MagtappService {

    @POST("note")
    fun addNoteAsync(@Body note:JsonObject): Deferred<Response<NoteUpsertResponse>>

    @PUT("note/{userId}")
    fun updateNoteAsync(
        @Path("userId") userId:String,
        @Body note:JsonObject
    ): Deferred<Response<NoteUpsertResponse>>

    @DELETE("note/{userId}")
    fun deleteNoteAsync(
        @Path("userId") userId:String,
        @Query("note_id") note_id:String
    ): Deferred<Response<GeneralNoteResponse>>

    @DELETE("note/{userId}/delete_all_note")
    fun deleteAllNoteAsync(
        @Path("userId") userId:String
    ): Deferred<Response<GeneralNoteResponse>>

    @GET("note/{userId}")
    fun getAllNoteAsync(
        @Path("userId") userId:String
    ): Deferred<Response<AllNoteResponse>>
}
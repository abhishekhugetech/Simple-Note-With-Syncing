package com.abhishek.notesyncing.data.model

import android.os.Parcel
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.JsonObject
import java.util.*

@Entity(tableName = "Note")
data class Note (
    @PrimaryKey(autoGenerate = true)
    var id:Int? = null,
    var title:String? = null,
    var body:String? = null,
    var createdOn:Date = Date(),
    var modifiedOn:Date = Date(),
    var isDeleted:Boolean? = false,
    var isSynced:Boolean? = false,
    var serverId:String? = null
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readString(),
        parcel.readString(),
        parcel.readSerializable() as Date,
        parcel.readSerializable() as Date,
        parcel.readValue(Boolean::class.java.classLoader) as? Boolean,
        parcel.readValue(Boolean::class.java.classLoader) as? Boolean,
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(id)
        parcel.writeString(title)
        parcel.writeString(body)
        parcel.writeSerializable(createdOn)
        parcel.writeSerializable(modifiedOn)
        parcel.writeValue(isDeleted)
        parcel.writeValue(isSynced)
        parcel.writeString(serverId)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Note> {
        override fun createFromParcel(parcel: Parcel): Note {
            return Note(parcel)
        }

        override fun newArray(size: Int): Array<Note?> {
            return arrayOfNulls(size)
        }
    }

    fun toJson() : JsonObject{
         val note = JsonObject()
         note.addProperty("title" , title )
         note.addProperty("body" , body )
        return note
    }
}
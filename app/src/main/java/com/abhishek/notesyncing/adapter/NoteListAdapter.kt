package com.abhishek.notesyncing.adapter

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.abhishek.notesyncing.R
import com.abhishek.notesyncing.data.model.Note
import kotlinx.android.synthetic.main.single_note_item.view.*

class NoteListAdapter (
    private var listener: MNoteListInterface
) :
    PagedListAdapter<Note, RecyclerView.ViewHolder>(diffCallback) {

    var context: Context? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MNoteViewHolder {
        context = parent.context
        return MNoteViewHolder(
            LayoutInflater.from(
                context
            ).inflate(R.layout.single_note_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val note = getItem(position)
        val binding = (holder as MNoteViewHolder).binding

        note?.let {
            it.title?.let { binding.noteTitle.text = it }
            it.body?.let { binding.noteBody.text = it }
            val image = if ( it.isSynced!=null && it.isSynced!! ){
                R.drawable.ic_tick_inside_circle_g
            }else{
                R.drawable.ic_tick_inside_circle
            }
            binding.isSynced.setImageResource(image)
        }

        binding.setOnLongClickListener {
            showDeleteDialog(context!!,note!!)
            true
        }
        binding.setOnClickListener {
            listener.openNote(note!!)
        }
    }

    private fun showDeleteDialog(context: Context, note: Note){
        AlertDialog.Builder(context)
            .setTitle("Would you like to delete this note.")
            .setNegativeButton("No") { dialog, which ->  }
            .setPositiveButton( "Delete") { dialog, which -> listener.deleteNote(note) }
            .create()
            .show()
    }

    class MNoteViewHolder(val binding:View) : RecyclerView.ViewHolder(binding)

    companion object {
        val diffCallback = object : DiffUtil.ItemCallback<Note>() {

            override fun areItemsTheSame(
                oldItem: Note,
                newItem: Note
            ): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(
                oldItem: Note,
                newItem: Note
            ): Boolean {
                return oldItem.title == newItem.title && oldItem.body == newItem.body
                        && oldItem.isSynced == newItem.isSynced
            }
        }
    }

    interface MNoteListInterface{
        fun openNote(note:Note)
        fun deleteNote(note: Note)
    }
}
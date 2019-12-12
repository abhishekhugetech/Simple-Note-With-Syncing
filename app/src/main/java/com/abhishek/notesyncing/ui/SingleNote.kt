package com.abhishek.notesyncing.ui

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.abhishek.notesyncing.R
import com.abhishek.notesyncing.data.model.Note

import kotlinx.android.synthetic.main.activity_single_note.*
import kotlinx.android.synthetic.main.content_single_note.*

class SingleNote : AppCompatActivity() {

    var note: Note? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_single_note)
        setSupportActionBar(toolbar)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_close_black_24dp)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        note = intent.getParcelableExtra(MainActivity.NOTE_EXTRA)
        if (note!=null){
            // There is note and We need to Update the Current Note.
            note?.title?.let { noteTitleACET.setText(it) }
            note?.body?.let { noteBodyACET.setText(it) }
            addNoteBtn.text = "Update Note"
            toolbarTitle.text = "Update Note"
        }


        addNoteBtn.setOnClickListener {
            if (isNoteChanged()) {
                saveNote()
            }
        }
    }

    private fun saveNote() {
        val title = noteTitleACET.text.toString()
        val body = noteBodyACET.text.toString()
        val intent = Intent()
        val noteToUpdate = if (note!=null){
            note?.title = title
            note?.body = body
            note
        }else{
            Note(null,title,body)
        }
        intent.putExtra(MainActivity.NOTE_EXTRA,noteToUpdate)
        setResult(Activity.RESULT_OK , intent )
        finish()
    }

    private fun isNoteChanged():Boolean{
        val title = noteTitleACET.text.toString()
        val body = noteBodyACET.text.toString()
        return if (note==null){
            title.isNotBlank() || body.isNotBlank()
        }else {
            note!!.title != title || note!!.body != body
        }
    }

    override fun onBackPressed() {
        if (isNoteChanged()){
            showSaveDialog()
        }else {
            super.onBackPressed()
        }
    }

    private fun showSaveDialog() {
        // Show Save Dialog
        AlertDialog.Builder(this)
            .setTitle("Would you like to save this note.")
            .setNegativeButton("No") { _, _ ->
                finish()
            }
            .setPositiveButton( "Yes") { _, _ ->
                saveNote()
                finish()
            }
            .create()
            .show()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.single_note, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            R.id.check_ -> {
                saveNote()
                finish()
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

}

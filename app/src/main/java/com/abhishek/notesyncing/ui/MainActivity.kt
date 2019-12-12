package com.abhishek.notesyncing.ui

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.abhishek.notesyncing.R
import com.abhishek.notesyncing.adapter.NoteListAdapter
import com.abhishek.notesyncing.data.model.Note
import com.abhishek.notesyncing.util.DialogUtil
import com.abhishek.notesyncing.worker.NotesSyncWorker
import com.abhishek.notesyncing.util.WorkersUtil
import com.abhishek.notesyncing.util.isNetworkAvailable
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*

class MainActivity : AppCompatActivity(), NoteListAdapter.MNoteListInterface {

    private lateinit var viewModel: NoteViewModel
    private lateinit var adapter: NoteListAdapter
    private lateinit var preferences:SharedPreferences

    private val spListener =
        SharedPreferences.OnSharedPreferenceChangeListener { sharedPreferences, key ->
            if (key == "isDataSynced"){
                val isDataSynced = sharedPreferences?.getBoolean("isDataSynced",false)
                if (isDataSynced!=null && isDataSynced){
                    hideDataSyncing()
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!isUserRegistered()){
            startActivity(Intent(this,RegisterActivity::class.java))
            finishAfterTransition()
            return
        }
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        setUpViewModel()
        setUpUi()
        setUpAdapter()

        // Sync Data from cloud
        val isDataSynced = getSharedPreferences(packageName, Context.MODE_PRIVATE).getBoolean("isDataSynced",false)
        if (!isDataSynced && this.isNetworkAvailable){
            WorkersUtil.syncNote( this , 1 , NotesSyncWorker.EVENT_SYNC_FROM_CLOUD )
            showDataSyncing()
        }

    }

    private fun hideDataSyncing() {
        DialogUtil.hideDialog()
    }

    private fun showDataSyncing() {
        DialogUtil.showProgressDialog(this,"Syncing Notes...")
    }

    private fun isUserRegistered(): Boolean {
        val isRegistered = !this.getSharedPreferences(packageName, Context.MODE_PRIVATE).getString("userId","").isNullOrBlank()
        if (!isRegistered){
            Toast.makeText( this , "Please Register to Continue." , Toast.LENGTH_SHORT ).show()
        }
        return isRegistered
    }

    private fun setUpAdapter() {
        rvNotes.layoutManager = LinearLayoutManager(this)
        // Set Up Adapter
        rvNotes.itemAnimator = DefaultItemAnimator()
        adapter = NoteListAdapter(this)
        rvNotes.adapter = adapter
    }

    private fun setUpViewModel() {
        viewModel = ViewModelProviders.of(this).get(NoteViewModel::class.java)
        viewModel.setUpDb(this)

        viewModel.getAllNotes().observe(this, Observer {
            adapter.submitList(it)
        })
    }

    private fun setUpUi() {
        fab.setOnClickListener { view ->
            startActivityForResult(Intent(this, SingleNote::class.java), OPEN_NOTE_SCREEN)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_delete_all_note -> {
                WorkersUtil.syncNote(this,1,NotesSyncWorker.EVENT_DELETE_ALL_NOTE)
                viewModel.deleteAllNote()
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onStart() {
        super.onStart()
        setUpPreferenceAndListener()
    }

    private fun setUpPreferenceAndListener() {
        if (!::preferences.isInitialized){
            preferences = getSharedPreferences(packageName, Context.MODE_PRIVATE)
        }
        preferences.registerOnSharedPreferenceChangeListener(spListener)
    }

    override fun onStop() {
        super.onStop()
        removePreferenceListener()
    }

    private fun removePreferenceListener() {
        if (::preferences.isInitialized){
            preferences.unregisterOnSharedPreferenceChangeListener(spListener)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == OPEN_NOTE_SCREEN && resultCode == Activity.RESULT_OK) {
            if (data != null && data.hasExtra(NOTE_EXTRA)) {
                val note = data.getParcelableExtra(NOTE_EXTRA) as Note
                if (note.id != null && note.id != -1) {
                    // Update Note.
                    viewModel.updateNote(note)
                } else {
                    // Insert Note.
                    viewModel.addNote(note)
                }
            }
        }
    }

    override fun openNote(note: Note) {
        val intent = Intent(this, SingleNote::class.java)
        intent.putExtra(NOTE_EXTRA, note)
        startActivityForResult(intent, OPEN_NOTE_SCREEN)
    }

    override fun deleteNote(note: Note) {
        viewModel.deleteNote(note)
    }

    private fun toast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    companion object {
        const val OPEN_NOTE_SCREEN = 1004
        const val NOTE_EXTRA = "note"
    }
}

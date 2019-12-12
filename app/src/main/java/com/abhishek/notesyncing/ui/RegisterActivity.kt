package com.abhishek.notesyncing.ui

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.abhishek.notesyncing.R
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        signIn.setOnClickListener {
            val userNameStr = userName.text.toString()
            if (userNameStr.isBlank()){
                Toast.makeText( this , "Please enter your Username" , Toast.LENGTH_SHORT ).show()
            }else{
                getSharedPreferences(packageName, Context.MODE_PRIVATE).edit().putString("userId" , userNameStr ).apply()
                startActivity(Intent(this,MainActivity::class.java))
                finishAfterTransition()
            }
        }
    }
}

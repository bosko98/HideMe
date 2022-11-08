package com.example.hideme

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button

class NotesActivity : AppCompatActivity() {

    private lateinit var btn_new: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notes)

        btn_new = findViewById<Button>(R.id.btn_new)
        btn_new.setOnClickListener(object : View.OnClickListener{
            override fun onClick(p0: View?) {
                finish()
            }
        })
    }
}
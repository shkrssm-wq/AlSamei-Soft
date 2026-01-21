package com.alsamei.soft.ui

import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import com.alsamei.soft.MainActivity
import com.alsamei.soft.R
import com.alsamei.soft.database.DatabaseHelper

class SetupActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setup) // تأكد من إنشاء ملف XML لهذا النشاط

        val btnStart = findViewById<Button>(R.id.btnSaveAdmin)
        val etUser = findViewById<EditText>(R.id.etAdminUser)
        val etPass = findViewById<EditText>(R.id.etAdminPass)

        btnStart.setOnClickListener {
            val db = DatabaseHelper(this).writableDatabase
            val values = ContentValues().apply {
                put("username", etUser.text.toString())
                put("password", etPass.text.toString())
                put("role", "ADMIN")
            }
            db.insert("users", null, values)
            
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }
}

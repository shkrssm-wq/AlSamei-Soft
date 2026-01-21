package com.alsamei.soft

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.alsamei.soft.database.DatabaseHelper

class MainActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // التحقق من وجود مدير مسجل
        val dbHelper = DatabaseHelper(this)
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM users WHERE role = 'ADMIN'", null)
        
        if (cursor.count == 0) {
            // إذا لم يوجد مدير، انتقل لشاشة الإعداد
            startActivity(Intent(this, SetupActivity::class.java))
            finish()
        } else {
            setContentView(R.layout.activity_main)
        }
        cursor.close()
    }
}

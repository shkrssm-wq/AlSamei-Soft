package com.alsamei.soft

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import com.alsamei.soft.database.DatabaseHelper
import com.alsamei.soft.ui.*

class MainActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // التحقق من وجود المدير
        if (!isAdminCreated()) {
            startActivity(Intent(this, SetupActivity::class.java))
            finish()
            return
        }

        setContentView(R.layout.activity_main)

        // ربط جميع الأزرار الأربعة بواجهاتها البرمجية
        findViewById<Button>(R.id.btnAccounts).setOnClickListener {
            startActivity(Intent(this, InventoryActivity::class.java)) // إدارة المخزن
        }

        findViewById<Button>(R.id.btnSales).setOnClickListener {
            startActivity(Intent(this, SalesActivity::class.java)) // المبيعات
        }

        findViewById<Button>(R.id.btnPurchase).setOnClickListener {
            startActivity(Intent(this, PurchasesActivity::class.java)) // المشتريات
        }

        findViewById<Button>(R.id.btnReports).setOnClickListener {
            startActivity(Intent(this, ReportsActivity::class.java)) // التقارير والأرباح
        }
    }

    private fun isAdminCreated(): Boolean {
        val dbHelper = DatabaseHelper(this)
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM users WHERE role = 'ADMIN'", null)
        val exists = cursor.count > 0
        cursor.close()
        return exists
    }
}

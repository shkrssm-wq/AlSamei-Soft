package com.alsamei.soft

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import com.alsamei.soft.database.DatabaseHelper
import com.alsamei.soft.ui.InventoryActivity
import com.alsamei.soft.ui.SetupActivity

class MainActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. فحص وجود مدير نظام مسجل في قاعدة البيانات
        if (!isAdminCreated()) {
            // إذا لم يوجد مدير، يتم التوجيه لشاشة الإعداد الأولي
            val intent = Intent(this, SetupActivity::class.java)
            startActivity(intent)
            finish() // إغلاق هذه الشاشة لكي لا يعود المستخدم إليها بالخلف
            return
        }

        // 2. إذا كان المدير موجوداً، نعرض الواجهة الرئيسية
        setContentView(R.layout.activity_main)

        // 3. تعريف الأزرار وربطها بالواجهة (XML)
        val btnInventory = findViewById<Button>(R.id.btnAccounts) // استخدمنا id الموجود في التصميم السابق
        val btnSales = findViewById<Button>(R.id.btnSales)
        val btnPurchase = findViewById<Button>(R.id.btnPurchase)
        val btnReports = findViewById<Button>(R.id.btnReports)

        // 4. تفعيل زر المخزون لفتح شاشة إدارة الأصناف
        btnInventory.setOnClickListener {
            val intent = Intent(this, InventoryActivity::class.java)
            startActivity(intent)
        }

        // 5. تفعيل زر المبيعات (سنقوم ببرمجتها في المرحلة القادمة)
        btnSales.setOnClickListener {
            // startActivity(Intent(this, SalesActivity::class.java))
        }

        // 6. تفعيل زر المشتريات
        btnPurchase.setOnClickListener {
             // startActivity(Intent(this, PurchasesActivity::class.java))
        }

        // 7. تفعيل زر التقارير
        btnReports.setOnClickListener {
             // startActivity(Intent(this, ReportsActivity::class.java))
        }
    }

    /**
     * دالة مساعدة للتحقق من وجود حساب المدير في جدول المستخدمين
     */
    private fun isAdminCreated(): Boolean {
        val dbHelper = DatabaseHelper(this)
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM users WHERE role = 'ADMIN'", null)
        val exists = cursor.count > 0
        cursor.close()
        return exists
    }
}

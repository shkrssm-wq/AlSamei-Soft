package com.alsamei.soft

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import com.alsamei.soft.database.DatabaseHelper
import com.alsamei.soft.ui.InventoryActivity
import com.alsamei.soft.ui.SalesActivity
import com.alsamei.soft.ui.SetupActivity

class MainActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. فحص وجود مدير نظام مسجل (الأمان أولاً)
        if (!isAdminCreated()) {
            val intent = Intent(this, SetupActivity::class.java)
            startActivity(intent)
            finish() 
            return
        }

        // 2. عرض الواجهة الرئيسية
        setContentView(R.layout.activity_main)

        // 3. تعريف الأزرار وربطها بالواجهة
        val btnInventory = findViewById<Button>(R.id.btnAccounts) // هذا هو زر المخزون في تصميمنا
        val btnSales = findViewById<Button>(R.id.btnSales)
        val btnPurchase = findViewById<Button>(R.id.btnPurchase)
        val btnReports = findViewById<Button>(R.id.btnReports)

        // 4. تفعيل زر "إدارة المخزون"
        btnInventory.setOnClickListener {
            val intent = Intent(this, InventoryActivity::class.java)
            startActivity(intent)
        }

        // 5. تفعيل زر "فاتورة المبيعات" (تم التفعيل الآن ✅)
        btnSales.setOnClickListener {
            val intent = Intent(this, SalesActivity::class.java)
            startActivity(intent)
        }

        // 6. أزرار المشتريات والتقارير (سنفعلها في الخطوات القادمة)
        btnPurchase.setOnClickListener {
             // Toast.makeText(this, "قيد البرمجة: نظام المشتريات", Toast.LENGTH_SHORT).show()
        }

        btnReports.setOnClickListener {
             // Toast.makeText(this, "قيد البرمجة: التقارير المالية", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * دالة للتحقق من وجود المدير في قاعدة البيانات
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

package com.alsamei.soft

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import com.alsamei.soft.database.DatabaseHelper
import com.alsamei.soft.ui.*

class MainActivity : Activity() {

    // تعريف dbHelper على مستوى الكلاس لاستخدامه بكفاءة
    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        dbHelper = DatabaseHelper(this)

        // 1. فحص الأمان الشامل
        if (!isAdminCreated()) {
            navigateTo(SetupActivity::class.java)
            finish() 
            return
        }

        // 2. تحميل الواجهة الرئيسية
        setContentView(R.layout.activity_main)

        // 3. تفعيل وربط الأزرار مع معالجة الأخطاء
        setupButtons()
    }

    private fun setupButtons() {
        // زر الحسابات والمخزون
        findViewById<Button>(R.id.btnAccounts).setOnClickListener {
            navigateTo(InventoryActivity::class.java)
        }

        // زر فاتورة المبيعات
        findViewById<Button>(R.id.btnSales).setOnClickListener {
            navigateTo(SalesActivity::class.java)
        }

        // زر فاتورة المشتريات
        findViewById<Button>(R.id.btnPurchase).setOnClickListener {
            navigateTo(PurchasesActivity::class.java)
        }

        // زر التقارير والأرباح
        findViewById<Button>(R.id.btnReports).setOnClickListener {
            navigateTo(ReportsActivity::class.java)
        }
    }

    /**
     * دالة موحدة للانتقال بين الشاشات مع رسالة خطأ في حال الفشل
     */
    private fun navigateTo(destination: Class<*>) {
        try {
            val intent = Intent(this, destination)
            startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(this, "عذراً، تعذر فتح الشاشة المطلوبة", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * التحقق من وجود حساب المدير في قاعدة البيانات
     */
    private fun isAdminCreated(): Boolean {
        var exists = false
        val db = dbHelper.readableDatabase
        try {
            val cursor = db.rawQuery("SELECT 1 FROM users WHERE role = 'ADMIN' LIMIT 1", null)
            exists = cursor.count > 0
            cursor.close()
        } catch (e: Exception) {
            exists = false
        }
        return exists
    }

    // إغلاق قاعدة البيانات عند إغلاق التطبيق نهائياً
    override fun onDestroy() {
        dbHelper.close()
        super.onDestroy()
    }
}

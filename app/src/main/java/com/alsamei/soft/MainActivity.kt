package com.alsamei.soft

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import com.alsamei.soft.database.DatabaseHelper
import com.alsamei.soft.ui.InventoryActivity
import com.alsamei.soft.ui.SalesActivity
import com.alsamei.soft.ui.PurchasesActivity
import com.alsamei.soft.ui.ReportsActivity
import com.alsamei.soft.ui.SetupActivity

class MainActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. نظام الحماية: التحقق من وجود المدير عند كل تشغيل
        if (!isAdminCreated()) {
            val intent = Intent(this, SetupActivity::class.java)
            startActivity(intent)
            finish() // منع المستخدم من العودة لهذه الشاشة
            return
        }

        // 2. تحميل لوحة التحكم الرئيسية
        setContentView(R.layout.activity_main)

        // 3. ربط وتفعيل أزرار النظام الأربعة
        
        // زر المخزون والحسابات
        findViewById<Button>(R.id.btnAccounts).setOnClickListener {
            startActivity(Intent(this, InventoryActivity::class.java))
        }

        // زر فاتورة المبيعات
        findViewById<Button>(R.id.btnSales).setOnClickListener {
            startActivity(Intent(this, SalesActivity::class.java))
        }

        // زر فاتورة المشتريات (التوريد)
        findViewById<Button>(R.id.btnPurchase).setOnClickListener {
            startActivity(Intent(this, PurchasesActivity::class.java))
        }

        // زر التقارير المالية والأرباح
        findViewById<Button>(R.id.btnReports).setOnClickListener {
            startActivity(Intent(this, ReportsActivity::class.java))
        }
        
        // رسالة ترحيب ذكية
        Toast.makeText(this, "مرحباً بك في السامعي سوفت", Toast.LENGTH_SHORT).show()
    }

    /**
     * وظيفة للتحقق من قاعدة البيانات: هل تم إعداد النظام لأول مرة؟
     */
    private fun isAdminCreated(): Boolean {
        return try {
            val dbHelper = DatabaseHelper(this)
            val db = dbHelper.readableDatabase
            val cursor = db.rawQuery("SELECT * FROM users WHERE role = 'ADMIN'", null)
            val count = cursor.count
            cursor.close()
            count > 0
        } catch (e: Exception) {
            false
        }
    }
}

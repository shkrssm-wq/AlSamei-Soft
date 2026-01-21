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
import com.alsamei.soft.ui.SetupActivity

class MainActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. فحص الأمان: هل تم إعداد حساب المدير؟
        if (!isAdminCreated()) {
            val intent = Intent(this, SetupActivity::class.java)
            startActivity(intent)
            finish() 
            return
        }

        // 2. تحميل الواجهة الرئيسية (Dashboard)
        setContentView(R.layout.activity_main)

        // 3. تفعيل زر "إدارة المخزون / الحسابات"
        findViewById<Button>(R.id.btnAccounts).setOnClickListener {
            val intent = Intent(this, InventoryActivity::class.java)
            startActivity(intent)
        }

        // 4. تفعيل زر "فاتورة المبيعات"
        findViewById<Button>(R.id.btnSales).setOnClickListener {
            val intent = Intent(this, SalesActivity::class.java)
            startActivity(intent)
        }

        // 5. تفعيل زر "فاتورة المشتريات / التوريد" ✅
        findViewById<Button>(R.id.btnPurchase).setOnClickListener {
            val intent = Intent(this, PurchasesActivity::class.java)
            startActivity(intent)
        }

        // 6. زر التقارير (سيتم تفعيله في المرحلة القادمة)
        findViewById<Button>(R.id.btnReports).setOnClickListener {
            Toast.makeText(this, "قيد البرمجة: جاري تجهيز تقارير الأرباح والخسائر", Toast.LENGTH_SHORT).show()
             // startActivity(Intent(this, ReportsActivity::class.java))
        }
    }

    /**
     * دالة للتحقق من وجود مستخدم بصلاحية مدير في قاعدة البيانات
     */
    private fun isAdminCreated(): Boolean {
        return try {
            val dbHelper = DatabaseHelper(this)
            val db = dbHelper.readableDatabase
            val cursor = db.rawQuery("SELECT * FROM users WHERE role = 'ADMIN'", null)
            val exists = cursor.count > 0
            cursor.close()
            exists
        } catch (e: Exception) {
            false
        }
    }
}

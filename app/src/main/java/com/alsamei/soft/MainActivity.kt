package com.alsamei.soft

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import com.alsamei.soft.ui.InventoryActivity
import com.alsamei.soft.ui.SalesActivity
import com.alsamei.soft.ui.PurchasesActivity
import com.alsamei.soft.ui.ReportsActivity
import com.alsamei.soft.ui.AccountsActivity

class MainActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // 1. تحميل الواجهة الرئيسية (تأكد أن IDs الأزرار مطابقة لملف XML)
        setContentView(R.layout.activity_main)

        // 2. تفعيل التنقل بين الشاشات
        setupNavigation()
        
        // 3. رسالة ترحيبية بسيطة
        Toast.makeText(this, "مرحباً بك في السامعي سوفت", Toast.LENGTH_SHORT).show()
    }

    private fun setupNavigation() {
        // زر المخزن (الأصناف)
        findViewById<Button>(R.id.btnInventory)?.setOnClickListener {
            startActivity(Intent(this, InventoryActivity::class.java))
        }

        // زر المبيعات (الفواتير)
        findViewById<Button>(R.id.btnSales)?.setOnClickListener {
            startActivity(Intent(this, SalesActivity::class.java))
        }

        // زر المشتريات (التوريد)
        findViewById<Button>(R.id.btnPurchase)?.setOnClickListener {
            startActivity(Intent(this, PurchasesActivity::class.java))
        }

        // زر التقارير (الأرباح والخسائر)
        findViewById<Button>(R.id.btnReports)?.setOnClickListener {
            startActivity(Intent(this, ReportsActivity::class.java))
        }

        // زر كشوفات الحسابات (المالية التفصيلية)
        findViewById<Button>(R.id.btnAccounts)?.setOnClickListener {
            startActivity(Intent(this, AccountsActivity::class.java))
        }
    }

    // إغلاق التطبيق بالكامل عند الضغط على زر الرجوع من الشاشة الرئيسية
    override fun onBackPressed() {
        finishAffinity() 
    }
}

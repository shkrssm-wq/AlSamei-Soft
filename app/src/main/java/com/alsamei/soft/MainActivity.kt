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

class MainActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // تحميل الواجهة الرئيسية مباشرة بدون حماية
        setContentView(R.layout.activity_main)

        // تفعيل الأزرار وربطها بالشاشات
        setupNavigation()
        
        // رسالة ترحيبية بسيطة عند الفتح
        Toast.makeText(this, "مرحباً بك في السامعي سوفت", Toast.LENGTH_SHORT).show()
    }

    private fun setupNavigation() {
        // زر المخزن والحسابات
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
    }

    // تذكير للمستخدم عند الضغط على زر الرجوع لإغلاق التطبيق
    override fun onBackPressed() {
        super.onBackPressed()
        finishAffinity() // إغلاق كافة الأنشطة والخروج من التطبيق
    }
}

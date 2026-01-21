package com.alsamei.soft.ui

import android.app.Activity
import android.os.Bundle
import android.widget.TextView
import com.alsamei.soft.R
import com.alsamei.soft.database.DatabaseHelper

class ReportsActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reports)

        // تعريف الحقول في الواجهة
        val tvSales = findViewById<TextView>(R.id.tvTotalSales)
        val tvPurchases = findViewById<TextView>(R.id.tvTotalPurchases)
        val tvExpenses = findViewById<TextView>(R.id.tvTotalExpenses)
        val tvNet = findViewById<TextView>(R.id.tvNetProfit)

        val dbHelper = DatabaseHelper(this)
        val db = dbHelper.readableDatabase

        // 1. جلب إجمالي المبيعات (حساب رقم 2 - إيرادات)
        val cursorSales = db.rawQuery("SELECT balance FROM accounts WHERE acc_id = 2", null)
        var salesAmount = 0.0
        if (cursorSales.moveToFirst()) {
            salesAmount = cursorSales.getDouble(0)
        }
        cursorSales.close()

        // 2. جلب إجمالي المشتريات (حساب رقم 3 - مصروفات متاجرة)
        val cursorPurchases = db.rawQuery("SELECT balance FROM accounts WHERE acc_id = 3", null)
        var purchasesAmount = 0.0
        if (cursorPurchases.moveToFirst()) {
            purchasesAmount = cursorPurchases.getDouble(0)
        }
        cursorPurchases.close()

        // 3. جلب إجمالي المصاريف العمومية (حساب رقم 4 - مصروفات تشغيلية)
        val cursorExpenses = db.rawQuery("SELECT balance FROM accounts WHERE acc_id = 4", null)
        var expensesAmount = 0.0
        if (cursorExpenses.moveToFirst()) {
            expensesAmount = cursorExpenses.getDouble(0)
        }
        cursorExpenses.close()

        // 4. المعادلة المحاسبية لصافي الربح
        // صافي الربح = المبيعات - (المشتريات + المصاريف)
        val totalOut = purchasesAmount + expensesAmount
        val netProfit = salesAmount - totalOut

        // 5. تحديث الواجهة بالأرقام النهائية
        tvSales.text = String.format("%.2f", salesAmount)
        tvPurchases.text = String.format("%.2f", purchasesAmount)
        tvExpenses.text = String.format("%.2f", expensesAmount)
        tvNet.text = String.format("%.2f", netProfit)

        // تلوين صافي الربح (أخضر إذا كان ربحاً، أحمر إذا كانت خسارة)
        if (netProfit >= 0) {
            tvNet.setTextColor(android.graphics.Color.parseColor("#2E7D32")) // أخضر
        } else {
            tvNet.setTextColor(android.graphics.Color.parseColor("#C62828")) // أحمر
        }
    }
}

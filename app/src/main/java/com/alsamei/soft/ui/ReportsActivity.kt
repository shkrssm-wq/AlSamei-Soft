package com.alsamei.soft.ui

import android.app.Activity
import android.os.Bundle
import android.widget.ListView
import android.widget.TextView
import android.widget.ArrayAdapter
import com.alsamei.soft.R
import com.alsamei.soft.database.DatabaseHelper

class ReportsActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reports)

        val tvProfit = findViewById<TextView>(R.id.tvTotalProfit)
        val tvCash = findViewById<TextView>(R.id.tvCashBalance)
        val lvStock = findViewById<ListView>(R.id.lvStockReport)

        val dbHelper = DatabaseHelper(this)
        val db = dbHelper.readableDatabase

        // 1. حساب رصيد الصندوق من جدول الحسابات
        val cashCursor = db.rawQuery("SELECT balance FROM accounts WHERE account_id = 1", null)
        if (cashCursor.moveToFirst()) {
            tvCash.text = String.format("%.2f", cashCursor.getDouble(0))
        }
        cashCursor.close()

        // 2. حساب الأرباح التقريبية (هذه معادلة مبسطة للمرحلة الحالية)
        // الربح = (سعر البيع - سعر الشراء) * الكمية المباعة (تحتاج لجدول فواتير مفصل لاحقاً)
        // حالياً سنعرض قيمة المخزون بسعر البيع
        tvProfit.text = "جارِ الحساب.." 

        // 3. عرض تقرير المخزون
        val stockList = mutableListOf<String>()
        val stockCursor = db.rawQuery("SELECT item_name, qty, purchase_price, price FROM items", null)
        var totalPotentialProfit = 0.0

        while (stockCursor.moveToNext()) {
            val name = stockCursor.getString(0)
            val qty = stockCursor.getDouble(1)
            val buy = stockCursor.getDouble(2)
            val sell = stockCursor.getDouble(3)
            
            val status = if (qty <= 5) "⚠️ منخفض" else "✅ متوفر"
            stockList.add("$name | الكمية: $qty | $status")
            
            // حساب الربح المتوقع في المخزون
            totalPotentialProfit += (sell - buy) * qty
        }
        tvProfit.text = String.format("%.2f", totalPotentialProfit)
        stockCursor.close()

        lvStock.adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, stockList)
    }
}

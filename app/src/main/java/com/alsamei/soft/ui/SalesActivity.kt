package com.alsamei.soft.ui

import android.app.Activity
import android.os.Bundle
import android.widget.*
import com.alsamei.soft.R
import com.alsamei.soft.database.DatabaseHelper

class SalesActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sales)

        val spItems = findViewById<Spinner>(R.id.spItems)
        val etQty = findViewById<EditText>(R.id.etSaleQty)
        val etPrice = findViewById<EditText>(R.id.etSalePrice)
        val btnSave = findViewById<Button>(R.id.btnSaveSale)

        val dbHelper = DatabaseHelper(this)

        // 1. تحميل الأصناف من المخزون إلى الـ Spinner
        val itemsMap = loadItemsToSpinner(spItems, dbHelper)

        // 2. عند الضغط على حفظ
        btnSave.setOnClickListener {
            val selectedItem = spItems.selectedItem.toString()
            val itemId = itemsMap[selectedItem] ?: -1
            val qty = etQty.text.toString().toDoubleOrNull() ?: 0.0
            val price = etPrice.text.toString().toDoubleOrNull() ?: 0.0

            if (itemId != -1 && qty > 0) {
                val db = dbHelper.writableDatabase
                
                // بدء عملية (Transaction) لضمان سلامة البيانات
                db.beginTransaction()
                try {
                    // أ. تحديث المخزون (خصم الكمية)
                    db.execSQL("UPDATE items SET qty = qty - ? WHERE item_id = ?", arrayOf(qty, itemId))
                    
                    // ب. تسجيل القيد المالي (تبسيط: زيادة رصيد الصندوق)
                    db.execSQL("UPDATE accounts SET balance = balance + ? WHERE account_id = 1", arrayOf(qty * price))
                    
                    db.setTransactionSuccessful()
                    Toast.makeText(this, "تمت عملية البيع بنجاح", Toast.LENGTH_SHORT).show()
                    finish() // العودة للرئيسية
                } catch (e: Exception) {
                    Toast.makeText(this, "خطأ في البيع: ${e.message}", Toast.LENGTH_LONG).show()
                } finally {
                    db.endTransaction()
                }
            }
        }
    }

    private fun loadItemsToSpinner(spinner: Spinner, dbHelper: DatabaseHelper): Map<String, Int> {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT item_id, item_name FROM items", null)
        val itemsMap = mutableMapOf<String, Int>()
        val itemsList = mutableListOf<String>()

        while (cursor.moveToNext()) {
            val id = cursor.getInt(0)
            val name = cursor.getString(1)
            itemsMap[name] = id
            itemsList.add(name)
        }
        cursor.close()

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, itemsList)
        spinner.adapter = adapter
        return itemsMap
    }
}

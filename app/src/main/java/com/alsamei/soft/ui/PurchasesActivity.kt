package com.alsamei.soft.ui

import android.app.Activity
import android.os.Bundle
import android.widget.*
import com.alsamei.soft.R
import com.alsamei.soft.database.DatabaseHelper

class PurchasesActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_purchases)

        val spItems = findViewById<Spinner>(R.id.spItemsPurchase)
        val etQty = findViewById<EditText>(R.id.etPurchaseQty)
        val etCost = findViewById<EditText>(R.id.etPurchaseCost)
        val btnSave = findViewById<Button>(R.id.btnSavePurchase)

        val dbHelper = DatabaseHelper(this)
        val itemsMap = loadItems(spItems, dbHelper)

        btnSave.setOnClickListener {
            val selectedItem = spItems.selectedItem.toString()
            val itemId = itemsMap[selectedItem] ?: -1
            val qty = etQty.text.toString().toDoubleOrNull() ?: 0.0
            val cost = etCost.text.toString().toDoubleOrNull() ?: 0.0

            if (itemId != -1 && qty > 0) {
                val db = dbHelper.writableDatabase
                db.beginTransaction()
                try {
                    // زيادة المخزون
                    db.execSQL("UPDATE items SET qty = qty + ? WHERE item_id = ?", arrayOf(qty, itemId))
                    
                    // تحديث سعر التكلفة (اختياري)
                    db.execSQL("UPDATE items SET purchase_price = ? WHERE item_id = ?", arrayOf(cost, itemId))

                    db.setTransactionSuccessful()
                    Toast.makeText(this, "تم توريد الكمية بنجاح", Toast.LENGTH_SHORT).show()
                    finish()
                } finally {
                    db.endTransaction()
                }
            }
        }
    }

    private fun loadItems(spinner: Spinner, dbHelper: DatabaseHelper): Map<String, Int> {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT item_id, item_name FROM items", null)
        val map = mutableMapOf<String, Int>()
        val list = mutableListOf<String>()
        while (cursor.moveToNext()) {
            map[cursor.getString(1)] = cursor.getInt(0)
            list.add(cursor.getString(1))
        }
        cursor.close()
        spinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, list)
        return map
    }
}

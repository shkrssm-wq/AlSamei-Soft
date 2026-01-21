package com.alsamei.soft.ui

import android.app.Activity
import android.content.ContentValues
import android.os.Bundle
import android.widget.*
import com.alsamei.soft.R
import com.alsamei.soft.database.DatabaseHelper

class InventoryActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inventory)

        val etName = findViewById<EditText>(R.id.etItemName)
        val etPrice = findViewById<EditText>(R.id.etItemPrice)
        val etQty = findViewById<EditText>(R.id.etItemQty)
        val btnAdd = findViewById<Button>(R.id.btnAddItem)
        val listView = findViewById<ListView>(R.id.lvItems)

        val dbHelper = DatabaseHelper(this)

        // دالة لتحديث القائمة
        fun refreshList() {
            val db = dbHelper.readableDatabase
            val cursor = db.rawQuery("SELECT item_name, qty, price FROM items", null)
            val itemsList = mutableListOf<String>()
            
            while (cursor.moveToNext()) {
                val name = cursor.getString(0)
                val qty = cursor.getDouble(1)
                val price = cursor.getDouble(2)
                itemsList.add("$name - الكمية: $qty - السعر: $price")
            }
            cursor.close()
            listView.adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, itemsList)
        }

        refreshList()

        btnAdd.setOnClickListener {
            val name = etName.text.toString()
            val price = etPrice.text.toString().toDoubleOrNull() ?: 0.0
            val qty = etQty.text.toString().toDoubleOrNull() ?: 0.0

            if (name.isNotEmpty()) {
                val db = dbHelper.writableDatabase
                val values = ContentValues().apply {
                    put("item_name", name)
                    put("qty", qty)
                    put("price", price)
                }
                db.insert("items", null, values)
                Toast.makeText(this, "تم حفظ الصنف", Toast.LENGTH_SHORT).show()
                etName.text.clear()
                etPrice.text.clear()
                etQty.text.clear()
                refreshList()
            }
        }
    }
}

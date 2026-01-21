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
        
        // تحميل الأصناف من المخزن
        val itemsMap = loadItems(spItems, dbHelper)

        btnSave.setOnClickListener {
            val selectedItemName = spItems.selectedItem?.toString() ?: ""
            val itemId = itemsMap[selectedItemName] ?: -1
            val qty = etQty.text.toString().toDoubleOrNull() ?: 0.0
            val costPrice = etCost.text.toString().toDoubleOrNull() ?: 0.0
            val totalAmount = qty * costPrice

            if (itemId != -1 && qty > 0 && costPrice > 0) {
                val db = dbHelper.writableDatabase
                db.beginTransaction()
                try {
                    // 1. زيادة كمية المخزون وتحديث سعر التكلفة
                    db.execSQL("UPDATE items SET qty = qty + ?, purchase_price = ? WHERE item_id = ?", 
                               arrayOf(qty, costPrice, itemId))

                    // 2. إنشاء رأس القيد (Journal Entry)
                    val description = "فاتورة مشتريات (توريد): $selectedItemName"
                    db.execSQL("INSERT INTO journal_entries (description) VALUES (?)", arrayOf(description))
                    
                    val cursor = db.rawQuery("SELECT last_insert_rowid()", null)
                    cursor.moveToFirst()
                    val entryId = cursor.getLong(0)
                    cursor.close()

                    // 3. الطرف المدين: حساب المشتريات (رقم 3) - يزداد
                    db.execSQL("""
                        INSERT INTO journal_details (entry_id, acc_id, debit, credit) 
                        VALUES (?, 3, ?, 0.0)
                    """, arrayOf(entryId, totalAmount))
                    db.execSQL("UPDATE accounts SET balance = balance + ? WHERE acc_id = 3", arrayOf(totalAmount))

                    // 4. الطرف الدائن: الصندوق الرئيسي (رقم 1) - ينقص
                    db.execSQL("""
                        INSERT INTO journal_details (entry_id, acc_id, debit, credit) 
                        VALUES (?, 1, 0.0, ?)
                    """, arrayOf(entryId, totalAmount))
                    db.execSQL("UPDATE accounts SET balance = balance - ? WHERE acc_id = 1", arrayOf(totalAmount))

                    db.setTransactionSuccessful()
                    Toast.makeText(this, "تم التوريد للمخزن وخصم المبلغ من الصندوق", Toast.LENGTH_LONG).show()
                    finish()
                } catch (e: Exception) {
                    Toast.makeText(this, "خطأ: ${e.message}", Toast.LENGTH_LONG).show()
                } finally {
                    db.endTransaction()
                }
            } else {
                Toast.makeText(this, "تأكد من إدخال الكمية والسعر بشكل صحيح", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loadItems(spinner: Spinner, dbHelper: DatabaseHelper): Map<String, Int> {
        val db = dbHelper.readableDatabase
        val map = mutableMapOf<String, Int>()
        val list = mutableListOf<String>()
        val cursor = db.rawQuery("SELECT item_id, item_name FROM items", null)
        while (cursor.moveToNext()) {
            val id = cursor.getInt(0)
            val name = cursor.getString(1)
            map[name] = id
            list.add(name)
        }
        cursor.close()
        spinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, list)
        return map
    }
}

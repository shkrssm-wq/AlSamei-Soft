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
        
        // تحميل الأصناف المتوفرة في المخزن
        val itemsMap = loadItemsToSpinner(spItems, dbHelper)

        btnSave.setOnClickListener {
            val selectedItemName = spItems.selectedItem?.toString() ?: ""
            val itemId = itemsMap[selectedItemName] ?: -1
            val qty = etQty.text.toString().toDoubleOrNull() ?: 0.0
            val price = etPrice.text.toString().toDoubleOrNull() ?: 0.0
            val totalAmount = qty * price

            if (itemId != -1 && qty > 0 && price > 0) {
                val db = dbHelper.writableDatabase
                
                // بدء المعاملة المالية (Transaction) لضمان عدم حدوث خطأ جزئي
                db.beginTransaction()
                try {
                    // 1. تحديث كمية المخزون (الخصم)
                    db.execSQL("UPDATE items SET qty = qty - ? WHERE item_id = ?", arrayOf(qty, itemId))

                    // 2. إدراج رأس القيد في جدول journal_entries
                    val description = "فاتورة مبيعات صنف: $selectedItemName (كمية: $qty)"
                    db.execSQL("INSERT INTO journal_entries (description) VALUES (?)", arrayOf(description))
                    
                    // جلب رقم القيد الذي تم إنشاؤه للتو
                    val cursor = db.rawQuery("SELECT last_insert_rowid()", null)
                    cursor.moveToFirst()
                    val entryId = cursor.getLong(0)
                    cursor.close()

                    // 3. الطرف المدين: حساب الصندوق (رقم 1 في شجرة الحسابات) - يزداد
                    db.execSQL("""
                        INSERT INTO journal_details (entry_id, acc_id, debit, credit) 
                        VALUES (?, 1, ?, 0.0)
                    """, arrayOf(entryId, totalAmount))
                    
                    // تحديث رصيد حساب الصندوق في جدول الحسابات
                    db.execSQL("UPDATE accounts SET balance = balance + ? WHERE acc_id = 1", arrayOf(totalAmount))

                    // 4. الطرف الدائن: حساب المبيعات (رقم 2 في شجرة الحسابات) - يزداد كإيراد
                    db.execSQL("""
                        INSERT INTO journal_details (entry_id, acc_id, debit, credit) 
                        VALUES (?, 2, 0.0, ?)
                    """, arrayOf(entryId, totalAmount))
                    
                    // تحديث رصيد حساب المبيعات
                    db.execSQL("UPDATE accounts SET balance = balance + ? WHERE acc_id = 2", arrayOf(totalAmount))

                    db.setTransactionSuccessful()
                    Toast.makeText(this, "تم حفظ الفاتورة وترحيل القيد بنجاح", Toast.LENGTH_LONG).show()
                    finish() // العودة للشاشة الرئيسية
                } catch (e: Exception) {
                    Toast.makeText(this, "خطأ في النظام: ${e.message}", Toast.LENGTH_LONG).show()
                } finally {
                    db.endTransaction()
                }
            } else {
                Toast.makeText(this, "يرجى التأكد من البيانات والكمية", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loadItemsToSpinner(spinner: Spinner, dbHelper: DatabaseHelper): Map<String, Int> {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT item_id, item_name FROM items WHERE qty > 0", null)
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
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
        return itemsMap
    }
}

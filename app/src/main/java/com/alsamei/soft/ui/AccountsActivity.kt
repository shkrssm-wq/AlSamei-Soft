package com.alsamei.soft.ui

import android.app.Activity
import android.os.Bundle
import android.widget.*
import com.alsamei.soft.R
import com.alsamei.soft.database.DatabaseHelper

class AccountsActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account_details)

        val spAcc = findViewById<Spinner>(R.id.spAllAccounts)
        val lvJournal = findViewById<ListView>(R.id.lvJournalEntries)
        val tvBal = findViewById<TextView>(R.id.tvCurrentBalance)
        
        val dbHelper = DatabaseHelper(this)

        // 1. تحميل كافة الحسابات (صندوق، مبيعات، موردين...)
        val accMap = loadAccounts(spAcc, dbHelper)

        spAcc.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: android.view.View?, p2: Int, p3: Long) {
                val accName = spAcc.selectedItem.toString()
                val accId = accMap[accName] ?: -1
                showJournalEntries(accId, lvJournal, tvBal, dbHelper)
            }
            override fun onNothingSelected(p0: AdapterView<*>?) {}
        }
    }

    private fun showJournalEntries(id: Int, lv: ListView, tv: TextView, dbHelper: DatabaseHelper) {
        val db = dbHelper.readableDatabase
        val entries = mutableListOf<String>()
        
        // جلب القيود حيث يكون الحساب مديناً (+) أو دائناً (-)
        val cursor = db.rawQuery("""
            SELECT description, amount, 
            CASE WHEN debit_acc = ? THEN 'مدين (+)' ELSE 'دائن (-)' END as type 
            FROM journal WHERE debit_acc = ? OR credit_acc = ?
        """, arrayOf(id.toString(), id.toString(), id.toString()))

        var total = 0.0
        while (cursor.moveToNext()) {
            val desc = cursor.getString(0)
            val amt = cursor.getDouble(1)
            val type = cursor.getString(2)
            entries.add("$desc | $amt | $type")
            if (type.contains("مدين")) total += amt else total -= amt
        }
        cursor.close()
        lv.adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, entries)
        tv.text = "الرصيد النهائي: $total"
    }

    private fun loadAccounts(spinner: Spinner, dbHelper: DatabaseHelper): Map<String, Int> {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT acc_id, acc_name FROM accounts", null)
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

package com.alsamei.soft.ui

import android.app.Activity
import android.os.Bundle
import android.widget.*
import com.alsamei.soft.R
import com.alsamei.soft.database.DatabaseHelper
import java.util.*

class AccountsActivity : Activity() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var spAcc: Spinner
    private lateinit var etStart: EditText
    private lateinit var etEnd: EditText
    private lateinit var lvJournal: ListView
    private lateinit var tvBal: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account_details)

        dbHelper = DatabaseHelper(this)
        spAcc = findViewById(R.id.spAllAccounts)
        etStart = findViewById(R.id.etStartDate)
        etEnd = findViewById(R.id.etEndDate)
        lvJournal = findViewById(R.id.lvJournalEntries)
        tvBal = findViewById(R.id.tvCurrentBalance)
        val btnFilter = findViewById<Button>(R.id.btnFilterDate)

        val accMap = loadAccounts()

        btnFilter.setOnClickListener {
            val accId = accMap[spAcc.selectedItem.toString()] ?: -1
            val startDate = etStart.text.toString().ifEmpty { "2000-01-01" }
            val endDate = etEnd.text.toString().ifEmpty { "2100-12-31" }
            updateStatement(accId, startDate, endDate)
        }
    }

    private fun updateStatement(accId: Int, start: String, end: String) {
        val db = dbHelper.readableDatabase
        val entries = mutableListOf<String>()
        
        // استعلام يبحث في القيود ضمن نطاق التاريخ المحدد
        val cursor = db.rawQuery("""
            SELECT e.description, d.debit, d.credit, e.entry_date 
            FROM journal_details d 
            JOIN journal_entries e ON d.entry_id = e.entry_id 
            WHERE d.acc_id = ? AND date(e.entry_date) BETWEEN ? AND ?
            ORDER BY e.entry_date ASC
        """, arrayOf(accId.toString(), start, end))

        var runningBalance = 0.0
        while (cursor.moveToNext()) {
            val desc = cursor.getString(0)
            val deb = cursor.getDouble(1)
            val cre = cursor.getDouble(2)
            val date = cursor.getString(3).substring(0, 10) // جلب التاريخ فقط بدون الوقت
            
            runningBalance += (deb - cre)
            entries.add("$date | $desc\nمدين: $deb | دائن: $cre | الرصيد: $runningBalance")
        }
        cursor.close()
        
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, entries)
        lvJournal.adapter = adapter
        tvBal.text = "الرصيد الإجمالي للفترة: $runningBalance"
    }

    private fun loadAccounts(): Map<String, Int> {
        val db = dbHelper.readableDatabase
        val map = mutableMapOf<String, Int>()
        val list = mutableListOf<String>()
        val cursor = db.rawQuery("SELECT acc_id, acc_name FROM accounts", null)
        while (cursor.moveToNext()) {
            map[cursor.getString(1)] = cursor.getInt(0)
            list.add(cursor.getString(1))
        }
        cursor.close()
        spAcc.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, list)
        return map
    }
}

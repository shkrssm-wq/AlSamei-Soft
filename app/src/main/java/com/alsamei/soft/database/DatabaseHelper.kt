package com.alsamei.soft.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, "AlSameiSoft.db", null, 3) {

    override fun onCreate(db: SQLiteDatabase) {
        // 1. جدول المستخدمين (للدخول والحماية)
        db.execSQL("""
            CREATE TABLE users (
                user_id INTEGER PRIMARY KEY AUTOINCREMENT, 
                username TEXT UNIQUE, 
                password TEXT, 
                role TEXT
            )
        """)

        // 2. شجرة الحسابات (الأصول، الخصوم، الإيرادات، المصروفات)
        db.execSQL("""
            CREATE TABLE accounts (
                acc_id INTEGER PRIMARY KEY AUTOINCREMENT,
                acc_name TEXT UNIQUE,
                acc_type TEXT, 
                balance REAL DEFAULT 0.0
            )
        """)

        // 3. جدول الأصناف (إدارة المخزون)
        db.execSQL("""
            CREATE TABLE items (
                item_id INTEGER PRIMARY KEY AUTOINCREMENT, 
                item_name TEXT UNIQUE, 
                qty REAL DEFAULT 0.0, 
                purchase_price REAL DEFAULT 0.0, 
                price REAL DEFAULT 0.0
            )
        """)

        // 4. رأس القيود اليومية (الدفتر العام)
        db.execSQL("""
            CREATE TABLE journal_entries (
                entry_id INTEGER PRIMARY KEY AUTOINCREMENT,
                entry_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                description TEXT
            )
        """)

        // 5. تفاصيل القيود (نظام القيد المزدوج: مدين ودائن)
        db.execSQL("""
            CREATE TABLE journal_details (
                detail_id INTEGER PRIMARY KEY AUTOINCREMENT,
                entry_id INTEGER,
                acc_id INTEGER,
                debit REAL DEFAULT 0.0,
                credit REAL DEFAULT 0.0,
                FOREIGN KEY(entry_id) REFERENCES journal_entries(entry_id),
                FOREIGN KEY(acc_id) REFERENCES accounts(acc_id)
            )
        """)

        // إدراج الحسابات الأساسية للنظام آلياً عند أول تشغيل
        seedInitialData(db)
    }

    private fun seedInitialData(db: SQLiteDatabase) {
        // حسابات الأصول
        db.execSQL("INSERT OR IGNORE INTO accounts (acc_id, acc_name, acc_type) VALUES (1, 'الصندوق الرئيسي', 'أصول')")
        db.execSQL("INSERT OR IGNORE INTO accounts (acc_id, acc_name, acc_type) VALUES (5, 'مخزون البضاعة', 'أصول')")
        
        // حسابات الإيرادات
        db.execSQL("INSERT OR IGNORE INTO accounts (acc_id, acc_name, acc_type) VALUES (2, 'حساب المبيعات', 'إيرادات')")
        
        // حسابات المصروفات
        db.execSQL("INSERT OR IGNORE INTO accounts (acc_id, acc_name, acc_type) VALUES (3, 'حساب المشتريات', 'مصروفات')")
        db.execSQL("INSERT OR IGNORE INTO accounts (acc_id, acc_name, acc_type) VALUES (4, 'حساب المصاريف العامة', 'مصروفات')")
    }

    override fun onUpgrade(db: SQLiteDatabase, old: Int, new: Int) {
        // في حال تغيير الإصدار، نقوم بإعادة بناء الهيكلية لضمان التوافق
        if (old < 3) {
            db.execSQL("DROP TABLE IF EXISTS journal_details")
            db.execSQL("DROP TABLE IF EXISTS journal_entries")
            db.execSQL("DROP TABLE IF EXISTS items")
            db.execSQL("DROP TABLE IF EXISTS accounts")
            db.execSQL("DROP TABLE IF EXISTS users")
            onCreate(db)
        }
    }
}

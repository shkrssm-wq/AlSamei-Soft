package com.alsamei.soft.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, "AlSameiSoft.db", null, 1) {

    override fun onCreate(db: SQLiteDatabase) {
        // 1. جدول المستخدمين
        db.execSQL("CREATE TABLE users (user_id INTEGER PRIMARY KEY AUTOINCREMENT, username TEXT, password TEXT, role TEXT)")
        
        // 2. جدول الحسابات المالية
        db.execSQL("CREATE TABLE accounts (account_id INTEGER PRIMARY KEY, account_name TEXT, balance REAL DEFAULT 0.0)")
        
        // 3. جدول الأصناف (المخزون)
        db.execSQL("CREATE TABLE items (item_id INTEGER PRIMARY KEY AUTOINCREMENT, item_name TEXT, qty REAL, price REAL)")
        
        // بذر الحسابات الأساسية
        db.execSQL("INSERT INTO accounts (account_id, account_name) VALUES (1, 'الصندوق الرئيسي')")
    }

    override fun onUpgrade(db: SQLiteDatabase, old: Int, new: Int) {}
}

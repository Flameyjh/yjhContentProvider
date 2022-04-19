package com.yjh.yjhcontentprovider

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.widget.Toast

class MyDataBaseHelper(val context: Context, name: String, version: Int) :
    SQLiteOpenHelper(context, name, null, version) {

    private val createBook = "create table Book(" +
            "id integer primary key autoincrement," +
            "author text," +
            "price real," +
            "pages integer," +
            "name text," +
            "category_id integer)"

    private val createCategory = "create table Category(" +
            "id integer primary key autoincrement," +
            "category_name text," +
            "category_code integer)"

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(createBook)
        db.execSQL(createCategory)
    }

    //版本更新时调用，若版本不更新直接运行程序是不会再调用onCreate()方法的
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
//        //不完美的方法，用户更新会会失去本地数据
//        //如果已经存在表Book或者Category，就删除全部表，然后重新调用onCreate重新创建
//        db.execSQL("drop table if exists Book")
//        db.execSQL("drop table if exists Category")
//        onCreate(db)

        //3. 升级数据库的最佳写法
        if (oldVersion <= 1){
            db.execSQL(createCategory)
        }
        if (oldVersion <= 2){
            db.execSQL("alter table Book add column category_id integer")
        }
    }
}
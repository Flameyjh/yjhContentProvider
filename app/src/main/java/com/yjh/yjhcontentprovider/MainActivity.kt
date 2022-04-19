package com.yjh.yjhcontentprovider

import android.content.ContentValues
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.yjh.yjhcontentprovider.databinding.ActivityMainBinding
import kotlin.Exception

/*
* 创建自己的 ContentProvider
* 使用之前的 yjhDatabaseTest工程，把里面的数据提供给外部
* 实现重点：DatabaseProvider文件
* */
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val dbHelper = MyDataBaseHelper(this, "BookStore.db", 2)

        //创建表（Book表 和 Category表）
        binding.createDatabase.setOnClickListener {
            dbHelper.writableDatabase
        }

        //增
        binding.addData.setOnClickListener {
            val db = dbHelper.writableDatabase
            val values1 = ContentValues().apply {
                //开始组装第一条数据
                put("name", "The Da Vinci Code")
                put("author", "Dan Brown")
                put("pages", 454)
                put("price", 16.96)
            }
            db.insert("Book", null, values1) //插入第一条数据
            val values2 = ContentValues().apply {
                //开始组装第二条数据
                put("name", "The Last Symbol")
                put("author", "Dan Brown")
                put("pages", 510)
                put("price", 19.95)
            }
            db.insert("Book", null, values2) //插入第二条数据
        }

        //改
        binding.updateData.setOnClickListener {
            val db = dbHelper.writableDatabase
            val values = ContentValues()
            values.put("price", 10.99)
            db.update("Book", values,"name = ?", arrayOf("The Da Vinci Code"))
        }

        //删
        binding.deleteData.setOnClickListener {
            val db = dbHelper.writableDatabase
            db.delete("Book", "pages > ?", arrayOf("500")) //删除pages大于500页的行
        }

        //查
        binding.queryData.setOnClickListener {
            val db = dbHelper.writableDatabase
            //查询Book表中的所有数据
            val cursor = db.query("Book", null, null, null, null, null, null)
            if(cursor.moveToFirst()){
                do{
                    //遍历cursor对象，取出数据并打印
                    val name = cursor.getString(cursor.getColumnIndexOrThrow("name"))
                    val author = cursor.getString(cursor.getColumnIndexOrThrow("author"))
                    val pages = cursor.getInt(cursor.getColumnIndexOrThrow("pages"))
                    val price = cursor.getDouble(cursor.getColumnIndexOrThrow("price"))

                    Log.d("MainActivity", "book name is $name")
                    Log.d("MainActivity", "book author is $author")
                    Log.d("MainActivity", "book pages is $pages")
                    Log.d("MainActivity", "book price is $price")
                }while (cursor.moveToNext())
            }
            cursor.close()
        }

        //使用事务
        binding.replaceData.setOnClickListener {
            val db = dbHelper.writableDatabase
            db.beginTransaction() //开启事务
            try {
                db.delete("Book", null, null)
//                if (true){
//                    //手动抛出一个异常，让任务失败，用于测试
//                    throw NullPointerException()
//                }
                val values = ContentValues().apply {
                    put("name", "Game of Thrones")
                    put("author", "George Martin")
                    put("pages", 720)
                    put("price", 20.85)
                }
                db.insert("Book", null, values)
                db.setTransactionSuccessful() //事务已经执行成功
            }catch (e: Exception){
                e.printStackTrace()
            } finally {
                db.endTransaction() //结束事务
            }
        }
    }
}
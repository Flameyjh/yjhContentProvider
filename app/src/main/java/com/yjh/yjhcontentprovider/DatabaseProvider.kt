package com.yjh.yjhcontentprovider

import android.content.ContentProvider
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.net.Uri

/*
* 该文件是把数据提供外部的重点
* */
class DatabaseProvider : ContentProvider() {

    private val bookDir = 0
    private val bookItem = 1
    private val categoryDir = 2
    private val categoryItem = 3
    private val authority = "com.example.databasetest.provider"
    private var dbHelper: MyDataBaseHelper? = null

    private val uriMatcher by lazy {
        val matcher = UriMatcher(UriMatcher.NO_MATCH)
        matcher.addURI(authority, "book", bookDir) //某表
        matcher.addURI(authority, "book/#", bookItem) //某表的某行
        matcher.addURI(authority, "category", categoryDir)
        matcher.addURI(authority, "category/#", categoryItem)
        matcher
    }

    override fun onCreate(): Boolean = context?.let {
        dbHelper = MyDataBaseHelper(it, "BookStore.db", 2)
        true
    } ?: false

    //查
    override fun query(
        uri: Uri, projection: Array<String>?, selection: String?,
        selectionArgs: Array<String>?, sortOrder: String?
    ): Cursor? = dbHelper?.let {
        val db = it.readableDatabase
        val cursor: Cursor? = when(uriMatcher.match(uri)){
            bookDir -> db.query("Book", projection, selection, selectionArgs, null, null, sortOrder)
            bookItem -> {
                val bookId = uri.pathSegments[1]
                db.query("Book", projection, "id = ?", arrayOf(bookId), null, null, sortOrder)
            }
            categoryDir -> {
                db.query("Category", projection, selection, selectionArgs, null, null, sortOrder)
            }
            categoryItem -> {
                val categoryId = uri.pathSegments[1]
                db.query("Category", projection, "id = ?", arrayOf(categoryId), null ,null, sortOrder)
            }
            else -> null
        }
        cursor
    }

    //增
    override fun insert(uri: Uri, values: ContentValues?): Uri? = dbHelper?.let {
        val db = it.writableDatabase
        val uriReturn: Uri? = when(uriMatcher.match(uri)){
            bookDir, bookItem -> {
                val newBookId = db.insert("Book", null, values)
                Uri.parse("content://$authority/book/$newBookId")
            }
            categoryDir, categoryItem -> {
                val newCategoryId = db.insert("Category", null, values)
                Uri.parse("content://$authority/category/$newCategoryId")
            }
            else -> null
        }
        uriReturn
    }

    //改
    override fun update(
        uri: Uri, values: ContentValues?, selection: String?,
        selectionArgs: Array<String>?
    ): Int = dbHelper?.let {
        val db = it.writableDatabase
        val updatedRows: Int = when (uriMatcher.match(uri)){
            bookDir -> db.update("Book", values, selection,selectionArgs)
            bookItem -> {
                val bookId = uri.pathSegments[1]
                db.update("Book", values, "id = ?", arrayOf(bookId))
            }
            categoryDir -> {
                db.update("Category", values, selection,selectionArgs)
            }
            categoryItem -> {
                val categoryId = uri.pathSegments[1]
                db.update("Category",values, "id = ?", arrayOf(categoryId))
            }
            else -> 0
        }
        updatedRows
    } ?: 0

    //删
    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int = dbHelper?.let {
        val db = it.writableDatabase
        val deletedRows: Int = when(uriMatcher.match(uri)){
            bookDir -> db.delete("Book",selection, selectionArgs)
            bookItem -> {
                val bookId = uri.pathSegments[1]
                db.delete("Book", "id = ?", arrayOf(bookId))
            }
            categoryDir -> {
                db.delete("Category", selection, selectionArgs)
            }
            categoryItem -> {
                val categoryId = uri.pathSegments[1]
                db.delete("Category","id = ?", arrayOf(categoryId))
            }
            else -> 0
        }
        deletedRows
    } ?: 0

    override fun getType(uri: Uri): String? = when(uriMatcher.match(uri)){
        bookDir -> "vnd.android.cursor.dir/vnd.com.example.databasetest.provider.book"
        bookItem -> "vnd.android.cursor.item/vnd.com.example.databasetest.provider.book"
        categoryDir -> "vnd.android.cursor.dir/vnd.com.example.databasetest.provider.category"
        categoryItem -> "vnd.android.cursor.item/vnd.com.example.databasetest.provider.category"
        else -> null
    }

}
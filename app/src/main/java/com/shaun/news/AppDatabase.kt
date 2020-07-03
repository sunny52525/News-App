package com.shaun.news

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

private const val TAG = "AppDatabase"
private const val DATABASE_NAME = "MyNews.db"
private const val DATABASE_VERSION = 1

internal class AppDatabase constructor(context: Context) : SQLiteOpenHelper(
    context, DATABASE_NAME, null,
    DATABASE_VERSION
) {
    override fun onCreate(db: SQLiteDatabase) {

        val sSQL = """ CREATE TABLE ${NewsContract.TABLE_NAME}(
        ${NewsContract.Columns.ID} INTEGER PRIMARY KEY NOT NULL,
        ${NewsContract.Columns.NEWS_TITLE} TEXT ,
        ${NewsContract.Columns.NEWS_DESCRIPTION} TEXT ,
        ${NewsContract.Columns.NEWS_WEBSITE} TEXT ,
        ${NewsContract.Columns.NEWS_DATE} TEXT ,
        ${NewsContract.Columns.NEWS_LINK} TEXT ,
        ${NewsContract.Columns.NEWS_IMG} TEXT );""".replaceIndent(" ")
        Log.d(TAG, sSQL)
        db.execSQL(sSQL)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        Log.d(TAG, "onUgrade : Starts")
        when (oldVersion) {
            1 -> {
                //on Upgrade logic
            }

            else -> throw IllegalStateException("onUpgrade() with unknown newversion: $newVersion")
        }
    }
    companion object : SingletonHolder<AppDatabase,Context>(::AppDatabase)

}
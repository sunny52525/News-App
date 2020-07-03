package com.shaun.news

import android.content.ContentProvider
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.database.SQLException
import android.database.sqlite.SQLiteQueryBuilder
import android.net.Uri
import android.util.Log

private const val TAG = "AppProvider"
const val CONTENT_AUTHORITY = "com.shaun.news.provider"

private const val NOTES = 100
private const val NOTES_ID = 101

val CONTENT_AUTHORITY_URI: Uri = Uri.parse("content://$CONTENT_AUTHORITY")

class AppProvider : ContentProvider() {
    private val uriMatcher by lazy { buildUriMatcher() }
    private fun buildUriMatcher(): UriMatcher {
        Log.d(TAG, "Build Uri Matcher Starts")
        val matcher = UriMatcher(UriMatcher.NO_MATCH)
        matcher.addURI(CONTENT_AUTHORITY, NewsContract.TABLE_NAME, NOTES)
    matcher.addURI(CONTENT_AUTHORITY, "${NewsContract.TABLE_NAME}/#", NOTES_ID)
        return matcher
    }

    override fun onCreate(): Boolean {
        Log.d(TAG, "onCreate starts")
        return true
    }


    override fun getType(uri: Uri): String? {
     return null
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        Log.d(TAG,"Insert:Called with Uri $uri")
        val match=uriMatcher.match(uri)
        Log.d(TAG,"Insert match is $match")

        val recordId:Long
        val returnUri:Uri
        when(match){
            NOTES -> {
                val db =AppDatabase.getInstance(context!!).writableDatabase
                recordId=db.insert(NewsContract.TABLE_NAME,null,values)
                if(recordId!=-1L){
                    returnUri=NewsContract.buildUriFromId(recordId)
                }else{
                    throw SQLException("Failed to Insert, uri was $uri")
                }
            }
            else -> throw  java.lang.IllegalArgumentException("UnkwonnURI :$uri")
        }
        if(recordId>0){
            context?.contentResolver?.notifyChange(uri,null)
        }
        Log.d(TAG,"Exiting Insert,returned $returnUri")
    return returnUri
    }

    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?
    ): Cursor? {
        Log.d(TAG, "Query : Called With URI $uri")
        val match = uriMatcher.match(uri)
        Log.d(TAG, "query :match is $match")
        val queryBuilder = SQLiteQueryBuilder()
        when (match) {
            NOTES -> queryBuilder.tables = NewsContract.TABLE_NAME
            NOTES_ID -> {
                queryBuilder.tables = NewsContract.TABLE_NAME
                val noteId = NewsContract.getId(uri)
                queryBuilder.appendWhere("${NewsContract.Columns.ID} = ")
                queryBuilder.appendWhereEscapeString("$noteId")
            }
            else
            -> throw IllegalArgumentException("UNKNOWN URI : $uri")
        }
        val db = AppDatabase.getInstance(context!!).readableDatabase

        val cursor =
            queryBuilder.query(db, projection, selection, selectionArgs, null, null, sortOrder)
        Log.d(TAG,"Cursor is $cursor")
        return cursor
    }


    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<out String>?
    ): Int {
      Log.d(TAG,"Update called with uri $uri")
        val match=uriMatcher.match(uri)
        Log.d(TAG,"update match is $match")
        val count:Int
        var criteria:String
        when (match){
            NOTES ->{
                val db =AppDatabase.getInstance(context!!).writableDatabase
                count=db.update(NewsContract.TABLE_NAME,values,selection,selectionArgs)
            }
            NOTES_ID->{
                val db= AppDatabase.getInstance(context!!).writableDatabase
                val id=NewsContract.getId(uri)
                criteria="${NewsContract.Columns.ID}=$id"
                if(selection!=null && selection.isNotEmpty()){
                    criteria +="AND ($selection)"
                }
                count=db.update(NewsContract.TABLE_NAME,values,criteria,selectionArgs)
            }
            else -> throw java.lang.IllegalArgumentException("Unkown Uri $uri")

        }
        if(count>0)
            context?.contentResolver?.notifyChange(uri,null)
        Log.d(TAG,"exit update : $count")
        return count
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int {
        Log.d(TAG, "Delete: Called with uri $uri")
        val match = uriMatcher.match(uri)
        Log.d(TAG, "Delete: match is  $match")

        val count: Int
        var selectionCriteria: String
        when (match) {
            NOTES -> {
                val db = AppDatabase.getInstance(context!!).writableDatabase
                count = db.delete(NewsContract.TABLE_NAME, selection, selectionArgs)

            }
            NOTES_ID -> {
                val db = AppDatabase.getInstance(context!!).writableDatabase
                val id = NewsContract.getId(uri)
                selectionCriteria = "${NewsContract.Columns.ID}= $id"
                if (selection != null && selection.isNotEmpty()) {
                    selectionCriteria += "AND ($selection)"
                }
                count = db.delete(NewsContract.TABLE_NAME, selectionCriteria, selectionArgs)
            }


            else -> throw IllegalArgumentException("Unkown uri:$uri")
        }
        if (count > 0) {
            context?.contentResolver?.notifyChange(uri, null)
        }
        Log.d(TAG, "Exiting delete, returning $count")
        return count
    }


}
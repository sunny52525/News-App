package com.shaun.news

import android.content.ContentUris
import android.net.Uri
import android.provider.BaseColumns

object NewsContract {
    internal const val TABLE_NAME="News"
    val CONTENT_URI: Uri = Uri.withAppendedPath(CONTENT_AUTHORITY_URI, TABLE_NAME)
    const val CONTENT_TYPE="vnd.android.cursor.dir/vnd.$CONTENT_AUTHORITY"
    const val CONTENT_ITEM_TYPE="vnd.android.cursor.item/vnd.$CONTENT_AUTHORITY.$TABLE_NAME"

    object Columns{
            const val ID= BaseColumns._ID
            const val NEWS_TITLE="Title"
            const val NEWS_DESCRIPTION="Description"
            const val NEWS_WEBSITE="Website"
            const val NEWS_DATE="Date"
            const val NEWS_LINK="link"
            const val NEWS_IMG="IMG"
        }
    fun getId(uri: Uri): Long {
        return ContentUris.parseId(uri)
    }
    fun buildUriFromId(id: Long): Uri {
        return ContentUris.withAppendedId(CONTENT_URI, id)
    }

}




/*
package com.shaun.notesapp

import android.content.ContentUris
import android.net.Uri
import android.provider.BaseColumns

object NotesContract {
    internal const val TABLE_NAME="Notes"

    val CONTENT_URI: Uri = Uri.withAppendedPath(CONTENT_AUTHORITY_URI, TABLE_NAME)
    const val CONTENT_TYPE="vnd.android.cursor.dir/vnd.$CONTENT_AUTHORITY"
    const val CONTENT_ITEM_TYPE="vnd.android.cursor.item/vnd.$CONTENT_AUTHORITY.$TABLE_NAME"

    object Columns{
        const val ID=BaseColumns._ID
        const val NOTES_TITLE="Title"
        const val NOTES_DESCRIPTION="Description"
    }
    fun getId(uri: Uri): Long {
        return ContentUris.parseId(uri)
    }

    fun buildUriFromId(id: Long): Uri {
        return ContentUris.withAppendedId(CONTENT_URI, id)
    }
}
 */
package com.shaun.news

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_bookmarks.*

private val TAG = "Bookmark"

class Bookmarks : AppCompatActivity(), BookmarkViewAdapter.OnNewsClickListener {

    private val recyclerViewAdapter = BookmarkViewAdapter(null, ArrayList(), this, this)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bookmarks)
        bookmark_refresh.isRefreshing = true
        val projection = arrayOf(
            NewsContract.Columns.ID,
            NewsContract.Columns.NEWS_TITLE,
            NewsContract.Columns.NEWS_DESCRIPTION,
            NewsContract.Columns.NEWS_WEBSITE,
            NewsContract.Columns.NEWS_DATE,
            NewsContract.Columns.NEWS_LINK,
            NewsContract.Columns.NEWS_IMG

        )
//        recyclerViewAdapter.swapCursor()
//        var newsBookmarksData:ArrayList<NewsBookmarks>=arrayListof()
        val newsBookmarksData = arrayListOf<NewsBookmarks>()
        val cursor = contentResolver.query(
            NewsContract.CONTENT_URI
            , projection
            , null
            , null
            , null
        )

        cursor.use {
            while (it!!.moveToNext()) {
                Log.d(TAG, "onCreate: ****************************** $cursor")
                val title = it.getString(1)
                val description = it.getString(2)
                val websiteName = it.getString(3)
                val date = it.getString(4)
                val urlArticle = it.getString(5)
                val urlAImg = it.getString(6)
                val news = NewsBookmarks(title, description, websiteName, date, urlArticle, urlAImg)

                newsBookmarksData.add(news)
                Log.d("*********", "onCreate: $title")
            }
        }
        recyclerViewAdapter.loaddata(newsBookmarksData)

        recycler_view_bookmark.layoutManager = LinearLayoutManager(this)
        recycler_view_bookmark.adapter = recyclerViewAdapter
        bookmark_refresh.isRefreshing = false


    }

    override fun onNewsClick(position: Int) {
        Log.d(TAG, "onNewsClick********************>: $position")
        val uri = NewsContract.buildUriFromId(position+1.toLong())
        val affected = contentResolver.delete(uri, null, null)
        Log.d(TAG, "onNewsClick: --$affected")
    }
}
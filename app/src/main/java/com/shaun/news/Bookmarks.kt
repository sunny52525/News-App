package com.shaun.news

import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_bookmarks.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private val TAG = "Bookmark"

class Bookmarks : AppCompatActivity(), BookmarkViewAdapter.OnNewsClickListener {

    private val recyclerViewAdapter = BookmarkViewAdapter(null, ArrayList(), this, this)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bookmarks)
        bookmark_refresh.isEnabled = false
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
                Log.d(TAG, "onCreate: **    **************************** $cursor")
                val title = it.getString(1)
                val description = it.getString(2)
                val websiteName = it.getString(3)
                val date = it.getString(4)
                val urlArticle = it.getString(5)
                val urlAImg = it.getString(6)
                val id = it.getLong(0)
                val news =
                    NewsBookmarks(title, description, websiteName, date, urlArticle, urlAImg, id)

                newsBookmarksData.add(news)
                Log.d("*********", "onCreate: $title")
            }
        }
        recyclerViewAdapter.loaddata(newsBookmarksData.asReversed())

        recycler_view_bookmark.layoutManager = LinearLayoutManager(this)
        recycler_view_bookmark.adapter = recyclerViewAdapter
        bookmark_refresh.isRefreshing = false


    }

    override fun onNewsClick(position: Long) {

        val uri = NewsContract.buildUriFromId(position)
        Log.d(TAG, "onNewsClick: URI WAS $uri")
        val affected = contentResolver.delete(uri, null, null)
        Log.d(TAG, "onNewsClick: --$affected")
        bookmark_refresh.isEnabled = true
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
//        recyclerViewAdapter.sw
        GlobalScope.launch {
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
                    Log.d(TAG, "onCreate: **    **************************** $cursor")
                    val title = it.getString(1)
                    val description = it.getString(2)
                    val websiteName = it.getString(3)
                    val date = it.getString(4)
                    val urlArticle = it.getString(5)
                    val urlAImg = it.getString(6)
                    val id = it.getLong(0)
                    val news = NewsBookmarks(
                        title,
                        description,
                        websiteName,
                        date,
                        urlArticle,
                        urlAImg,
                        id
                    )

                    newsBookmarksData.add(news)
                    Log.d("*********", "onCreate: $title")
                }
            }
            withContext(Dispatchers.Main) {
                recyclerViewAdapter.loaddata(newsBookmarksData.asReversed())
                Handler().postDelayed({
                    bookmark_refresh.isRefreshing = false
                }, 500L)

            }
        }


    }
}
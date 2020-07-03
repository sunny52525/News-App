package com.shaun.news


import android.app.AlertDialog
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.*
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import androidx.core.view.doOnLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_EXPANDED
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.backdrop_fragment.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*


private const val Tag = "MainActivity"
private var cachedData = ""

private var found = true

@Suppress("NAME_SHADOWING")
class MainActivity : AppCompatActivity(), GetRawData.OnDownloadComplete,
    JsonDataParser.OnDataParsed,
    RecyclerItemClickListener.OnRecyclerClickListener {
    private val recyclerViewAdapter = RecyclerViewAdapterNews(ArrayList(), this)
    private var mBottomSheetBehavior: BottomSheetBehavior<View?>? = null

    private var currentQuery =
        "https://newsapi.org/v2/top-headlines?q=india&sortBy=published&pageSize=100&apiKey=c5505b6406384fe2b1060c7dd66e957c"
    private var aboutDialog: AlertDialog? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(Tag, "ONCREA")

        super.onCreate(savedInstanceState)
        Log.d(Tag, "ONCREAd")
        setContentView(R.layout.activity_main)
        recycler_view_news.visibility = View.GONE
        test.isRefreshing = true
        configureBackdrop()

        val splashData = intent.getStringExtra("RAW")

        Log.d(Tag, "RAW DATA FROM SPLASH IS $splashData")
        val getRawData = GetRawData(this)
        if (splashData!!.isNotEmpty()) {
            onDownloadComplete(Pair(splashData, -1), DownloadStatus.OK, 1)
        } else
            if (cachedData.isEmpty())
                getRawData.downloadRawData(currentQuery, 1)
            else
                onDownloadComplete(Pair(cachedData, -1), DownloadStatus.OK, 1)

        test.setOnRefreshListener {
            getRawData.downloadRawData(currentQuery, 1)
        }

        recycler_view_news.layoutManager = LinearLayoutManager(this)
        recycler_view_news.addOnItemTouchListener(
            RecyclerItemClickListener(
                this,
                this,
                recycler_view_news
            )
        )

        recycler_view_news.adapter = recyclerViewAdapter

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                window.setSoftInputMode(
                    WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
                )
                currentQuery =
                    "https://newsapi.org/v2/top-headlines?q=$query&pageSize=100&apiKey=c5505b6406384fe2b1060c7dd66e957c"
                val getRawData = GetRawData(this@MainActivity)
                getRawData.downloadRawData(currentQuery, 1)


                Log.d(Tag, "Current query is $currentQuery")
                test.isRefreshing = true

                Handler().postDelayed(
                    {
                        onBackPressed()
                        searchView.setQuery("", false)
                    }, 300
                )
                return false
            }

            override fun onQueryTextChange(query: String?): Boolean {

                return true
            }
        })

        recycler_view_news.addOnScrollListener(object : RecyclerView.OnScrollListener() {

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {

                if (dy < 0) {
                    floatingActionButton.show()
                } else
                    if (dy > 0) {
                        floatingActionButton.hide()
                    }

            }
        })

        val listener = View.OnClickListener { v ->
            val b = v as Button
            val link = b.text.toString().toLowerCase()
            currentQuery =

                "https://newsapi.org/v2/top-headlines?q=$link&pageSize=100&apiKey=c5505b6406384fe2b1060c7dd66e957c"
            getRawData.downloadRawData(currentQuery, 1)
            Handler().postDelayed(
                {
                    onBackPressed()
                }, 500
            )
            test.isRefreshing = true

        }
        backdrop_Health.setOnClickListener(listener)
        backdrop_business.setOnClickListener(listener)
        backdrop_entertainment.setOnClickListener(listener)
        backdrop_science.setOnClickListener(listener)
        backdrop_sports.setOnClickListener(listener)
        backdrop_technology.setOnClickListener(listener)

    }

    override fun onDownloadComplete(data: Pair<String, Int>, status: DownloadStatus, id: Int) {
        if (data.second != -1)
            cachedData = data.first
        Log.d("MainActivity", "Download Complete")
        val jsonDataParser = JsonDataParser(this)
        jsonDataParser.parseJson(data.first, id)
        if (id == 1) {
            Log.d(Tag, "How tf it got here")
            currentQuery = currentQuery.replace("top-headlines", "everything")
            val getRawData = GetRawData(this)
            getRawData.downloadRawData(currentQuery, 2)
        }
    }

    override fun onDataParsed(data: ArrayList<newsData>, id: Int) {
        Log.d(Tag, "Data Parsed ${data}")
        if (id == 1) {
            if (data.size != 0)
                dataFound()
            recyclerViewAdapter.loadNewData(data)
            if (data.size == 0) {
                noDataFound("No Headlines Found,Searching for Everything related to the query")
            }
        } else {
            if (data.size != 0)
                dataFound()
            recyclerViewAdapter.appenddata(data)
        }
        if (id == 2 && recyclerViewAdapter.itemCount == 0) {
            noDataFound("No News found,Try searching Something else")
        }

        recycler_view_news.visibility = View.VISIBLE
        if (data.size != 0 || id == 2)
            test.isRefreshing = false
        recycler_view_news.smoothScrollToPosition(0)
        Log.d(Tag, "onData Pared ends")

    }

    override fun onError(exception: Exception) {
        if (cachedData.isNotEmpty()) {
            Log.d(Tag, "cached data is $cachedData")
            val jsonDataParser = JsonDataParser(this)
            jsonDataParser.parseJson(cachedData, 2)
        }
        Log.d(Tag, "error with $exception")
        Log.d(Tag, "Cached data is $cachedData")

        if (found) {
            val snackbar = Snackbar
                .make(test, "No Connection", Snackbar.LENGTH_LONG)
                .setAction("Retry") {
                    val getRawData = GetRawData(this)
                    getRawData.downloadRawData(currentQuery, 1)
                    test.isRefreshing = true
                }
            snackbar.show()
        }
    }


    private fun configureBackdrop() {
        val fragment = supportFragmentManager.findFragmentById(R.id.filter_fragment2)

        fragment?.let {
            BottomSheetBehavior.from(fragment.requireView()).let { bsb ->
                bsb.state = BottomSheetBehavior.STATE_HIDDEN

                floatingActionButton.setOnClickListener {


                    Log.d(Tag, "WORKING")
                    searchView.onActionViewExpanded()
                    searchView.doOnLayout {
                        searchView.clearFocus()
                    }
                    bsb.state = STATE_EXPANDED
                    test.isEnabled = false
                }
                test.isEnabled = true
                Log.d(Tag, "here")
                mBottomSheetBehavior = bsb

                if (bsb.state == BottomSheetBehavior.STATE_HIDDEN) {
                    test.isEnabled = true
                }
            }
        }
    }

    override fun onBackPressed() {
        mBottomSheetBehavior?.let {
            if (it.state == STATE_EXPANDED) {
                it.state = BottomSheetBehavior.STATE_COLLAPSED
            } else {
                super.onBackPressed()
            }
        } ?: super.onBackPressed()
        test.isEnabled = true
        hide()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_refresh, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        Log.d(Tag, "onOptionsItemSelected: ${item}")
        when (item.itemId) {
            R.id.refresh -> {
                test.isRefreshing = true
                val getRawData = GetRawData(this)
                getRawData.downloadRawData(currentQuery, 1)
                test.isEnabled = true

            }
            R.id.menu_aboutMe -> {
                showaboutdialog()
            }
            R.id.menu_bookmark -> {
                val intent = Intent(this, Bookmarks::class.java)
                startActivity(intent)

            }
            else -> {
                Toast.makeText(this, "Not Possible", Toast.LENGTH_SHORT).show()

            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showaboutdialog() {
        val messgView = layoutInflater.inflate(R.layout.about, null, false)
        val builder = AlertDialog.Builder(this)

        builder.setTitle(R.string.app_name)
        builder.setIcon(R.mipmap.ic_launcher)
        aboutDialog = builder.setView(messgView).create()
        aboutDialog?.setCanceledOnTouchOutside(true)
        aboutDialog?.show()
    }

    fun noDataFound(msg: String) {
        Log.d(Tag, "NODATA FOUND")
        activity_noData.text = msg
        activity_noData.visibility = View.VISIBLE
    }

    fun dataFound() {
        Log.d(Tag, "Data FOUND")
        activity_noData.visibility = View.GONE
    }

    override fun onItemClick(view: View, postion: Int) {

        vibrate(10L)
        Log.d(Tag, "OnItemClick Is working")
        val websitenews = recyclerViewAdapter.getNews(postion)
        Log.d(Tag, "$websitenews")
        /**
         *   //USE THIS CODE TO USE WEBVIEW INSTEAD OF CUSTOM TABS
         *   ////////////////////////////////////////////////////
        if (websitenews != null) {
        val intent = Intent(this, WebViewSampleActivity::class.java)
        intent.putExtra("data", websitenews.urlToArticle)
        startActivity(intent)
        }
         */


        /**
         * Instead of Using WebView, It will now use chrome(is Installed),else firefoxw custom Tabs to open Articles
         */
        val builder = CustomTabsIntent.Builder()
        builder.enableUrlBarHiding()
        builder.setShowTitle(true)
        builder.setToolbarColor(Color.TRANSPARENT)
        val customTabsIntent = builder.build()
        if (isAppInstalled("com.android.chrome"))
            customTabsIntent.intent.setPackage("com.android.chrome")
        else if (isAppInstalled("org.mozilla.firefox"))
            customTabsIntent.intent.setPackage("org.mozilla.firefox")
        customTabsIntent.launchUrl(this, Uri.parse(websitenews?.urlToArticle))

    }

    override fun onItemLongClick(view: View, postion: Int) {

        val websitenews = recyclerViewAdapter.getNews(postion)
        val popup = PopupMenu(this, view)
        popup.inflate(R.menu.menu_options)
        popup.setOnMenuItemClickListener(object : PopupMenu.OnMenuItemClickListener {
            override fun onMenuItemClick(item: MenuItem): Boolean {
                when (item.itemId) {
                    R.id.options_share -> {
                        share(websitenews!!.title, websitenews.urlToArticle)
                        return true
                    }
                    R.id.options_bookmark -> {
                        addtoBookmark(websitenews)
                        return true
                    }
                    else -> return false
                }
            }
        })
        popup.show()
        vibrate(30L)
    }

    private fun addtoBookmark(websitenews: newsData?) {
        val news = NewsBookmarks(
            websitenews!!.title,
            websitenews.description,
            websitenews.websiteName,
            websitenews.datePublished,
            websitenews.urlToArticle,
            websitenews.urlToImage,
            0
        )
        val values = ContentValues()
        values.put(NewsContract.Columns.NEWS_TITLE, news.title)
        values.put(NewsContract.Columns.NEWS_DESCRIPTION, news.description)
        values.put(NewsContract.Columns.NEWS_WEBSITE, news.websiteName)
        values.put(NewsContract.Columns.NEWS_DATE, news.date)
        values.put(NewsContract.Columns.NEWS_IMG, news.urlToImage)
        values.put(NewsContract.Columns.NEWS_LINK, news.urlToArticlle)
        GlobalScope.launch {
            val uri = application.contentResolver?.insert(
                NewsContract.CONTENT_URI,
                values
            )
            if (uri != null) {
                news.id = NewsContract.getId(uri)
            }
        }
        println("***************************************")
    }

    private fun vibrate(sec: Long) {
        val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        if (vibrator.hasVibrator()) { // Vibrator availability checking
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(
                    VibrationEffect.createOneShot(
                        sec,
                        VibrationEffect.DEFAULT_AMPLITUDE
                    )
                ) // New vibrate method for API Level 26 or higher
            } else {
                vibrator.vibrate(sec) // Vibrate method for below API Level 26
            }
        }
    }

    private fun hide() {
        window.setSoftInputMode(
            WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        )
    }

    private fun isAppInstalled(PackageCustom: String): Boolean {
        return try {
            packageManager.getPackageInfo(PackageCustom, 0)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
    }

    private fun share(title: String, link: String) {
        val strBuilder = StringBuilder()
        strBuilder.appendln(title)
        strBuilder.appendln(link)
        strBuilder.append("Share Via NewsApp@Sunny")

        val shareIntent =
            Intent(Intent.ACTION_SEND)
        shareIntent.type = "text/plain"
        shareIntent.putExtra(Intent.EXTRA_TEXT, strBuilder.toString())
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "News Share")
        ContextCompat.startActivity(
            this,
            Intent.createChooser(shareIntent, "Share..."),
            Bundle.EMPTY
        )

    }
}
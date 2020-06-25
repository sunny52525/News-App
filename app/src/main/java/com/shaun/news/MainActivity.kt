package com.shaun.news


import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.*
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_EXPANDED
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.backdrop_fragment.*
import java.util.*

private const val Tag = "MainActivity"
private var cachedData = ""
class MainActivity : AppCompatActivity(), GetRawData.OnDownloadComplete,
    JsonDataParser.OnDataParsed, RecyclerViewAdapterNews.NoDatafound,
    RecyclerItemClickListener.OnRecyclerClickListener {
    private val recyclerViewAdapter = RecyclerViewAdapterNews(this, ArrayList())
    private var mBottomSheetBehavior: BottomSheetBehavior<View?>? = null
    private var currentQuery =
        "https://newsapi.org/v2/top-headlines?q=india&sortBy=published&pageSize=100&apiKey=c5505b6406384fe2b1060c7dd66e957c"

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(Tag, "ONCREA")

        super.onCreate(savedInstanceState)
        Log.d(Tag, "ONCREAd")
        setContentView(R.layout.activity_main)
        recycler_view_news.visibility = View.GONE
        test.isRefreshing = true;
        configureBackdrop()

        val getRawData = GetRawData(this)
        if (cachedData.isEmpty())
            getRawData.downloadRawData("$currentQuery", 1)
        else
            onDownloadComplete(cachedData, DownloadStatus.OK, 1)

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
                getWindow().setSoftInputMode(
                    WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
                );
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
            hide(this)
            var link = b.text.toString().toLowerCase()

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

    override fun onDownloadComplete(data: String, status: DownloadStatus, id: Int) {
        cachedData = data
        if (data.length < 50 && id == 1) {
            currentQuery = currentQuery.replace("top-headlines", "everything")
            val getRawData = GetRawData(this)
            getRawData.downloadRawData(currentQuery, 2)
        } else{
            Log.d("MainActivity", "Download Complete")
            val jsonDataParser = JsonDataParser(this)
            jsonDataParser.parseJson(data)
        }
    }

    override fun onDataParsed(data: ArrayList<newsData>) {
        Log.d(Tag, "Data Parsed ${data.size}")


        recyclerViewAdapter.loadNewData(data)
        recycler_view_news.visibility = View.VISIBLE
        test.isRefreshing = false
        recycler_view_news.smoothScrollToPosition(0)
        Log.d(Tag, "onData Pared ends")

    }

    override fun onError(exception: Exception) {
        Log.d(Tag, "error with $exception")
      TODO("SHOW POPUP")
    }


    private fun configureBackdrop() {
        val fragment = supportFragmentManager.findFragmentById(R.id.filter_fragment2)

        fragment?.let {
            BottomSheetBehavior.from(fragment.requireView())?.let { bsb ->
                bsb.state = BottomSheetBehavior.STATE_HIDDEN

                floatingActionButton.setOnClickListener {
                    Log.d(Tag,"*********************************")

                    Log.d(Tag,"WORKING")
                    searchView.onActionViewExpanded()
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
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_refresh, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.refresh -> {
                test.isRefreshing = true
                val getRawData = GetRawData(this)
                getRawData.downloadRawData(currentQuery, 1)
                test.isEnabled = true
                true
            }
            else -> {
                Toast.makeText(this, "Not Possible", Toast.LENGTH_SHORT).show()
                true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun noDataFound() {
        TODO()
    }

    override fun dataFound() {

    }

    override fun onItemClick(view: View, postion: Int) {

        vibrate(10L)
        Log.d(Tag, "OnItemClick Is working")
        val websitenews = recyclerViewAdapter.getNews(postion)
        Log.d(Tag, "$websitenews")
        if (websitenews != null) {
            val intent = Intent(this, WebViewSampleActivity::class.java)
            intent.putExtra("data", websitenews.urlToArticle)
            startActivity(intent)
        }

    }

    override fun onItemLongClick(view: View, postion: Int) {
        vibrate(30L)
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
    private fun hide(activity: Activity) {
        val imm: InputMethodManager =
            activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0) // hide
    }

}
package com.shaun.news


import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.each_news.view.*


class NewsRecyclerViewAdapter(view: View) : RecyclerView.ViewHolder(view) {

    val newsTitle: TextView = view.each_new_title
    val description: TextView = view.each_news_description
    val website: TextView = view.each_news_websiteName
    val date: TextView = view.each_news_date
    val newsImage: ImageView = view.imageView

}


class RecyclerViewAdapterNews(
    private val listener: NoDatafound,
    private var newsadapter: List<newsData>
) :
    RecyclerView.Adapter<NewsRecyclerViewAdapter>() {
    interface NoDatafound {
        fun noDataFound()
        fun dataFound()
    }

    private val tag = "RecyclerViewAdapt"

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsRecyclerViewAdapter {
        Log.d(tag, "onCreateViewHolder new view requested")
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.each_news, parent, false)
        return NewsRecyclerViewAdapter(view)
    }

    override fun getItemCount(): Int {
        Log.d(tag, ".getItemCount called ${newsadapter.size}")
        return if (newsadapter.isNotEmpty()) newsadapter.size else 0
    }

    fun getNews(position: Int): newsData? {
        return if (newsadapter.isNotEmpty()) newsadapter[position] else null
    }

    fun loadNewData(newdata: List<newsData>) {
        newsadapter = newdata
        notifyDataSetChanged()
    }


    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: NewsRecyclerViewAdapter, position: Int) {

        if (newsadapter.isEmpty()) {
            listener.noDataFound()
        } else {
            listener.dataFound()
            val newsDataAdapter = newsadapter[position]
            if (newsDataAdapter.urlToImage.isNotEmpty()) {
                Picasso.get().load(newsDataAdapter.urlToImage!!)
                    .error(R.drawable.news)
                    .resize(2048, 1600)
                    .onlyScaleDown()
                    .placeholder(R.drawable.news)
                    .into(holder.newsImage)
            }

            holder.newsTitle.text = newsDataAdapter.title
            holder.description.text = newsDataAdapter.description
            holder.date.text = newsDataAdapter.datePublished
            holder.website.text = newsDataAdapter.websiteName


        }
    }
}
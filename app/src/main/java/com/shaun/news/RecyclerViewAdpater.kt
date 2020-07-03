package com.shaun.news


import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.each_news.view.*


/**
 * Code Inspiration from Tim, LearnProgramming.academy
 */

class NewsRecyclerViewAdapter(view: View) : RecyclerView.ViewHolder(view) {

    val newsTitle: TextView = view.each_new_title
    val description: TextView = view.each_news_description
    val website: TextView = view.each_news_websiteName
    val date: TextView = view.each_news_date
    val newsImage: ImageView = view.imageView
    val newslink: TextView = view.each_link
    val img: TextView = view.each_img_link

}


class RecyclerViewAdapterNews(
    private var newsadapter: List<newsData>, context: Context
) :
    RecyclerView.Adapter<NewsRecyclerViewAdapter>() {
    private val contex = context

    //    val viewModel: MyNotesViewModel by activityViewModels()
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

    fun appenddata(newsData: List<newsData>) {
        newsadapter += newsData
        notifyDataSetChanged()
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: NewsRecyclerViewAdapter, position: Int) {

        if (newsadapter.isEmpty()) {
            Log.d("TAG", "NodataFound")
//            listener.noDataFound()
        } else {

            Log.d("TAG", "data Found")
            val newsDataAdapter = newsadapter[position]
            if (newsDataAdapter.urlToImage.isNotEmpty()) {
                Picasso.get().load(newsDataAdapter.urlToImage)
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
            holder.newslink.text = newsDataAdapter.urlToArticle
            holder.img.text = newsDataAdapter.urlToImage

        }

    }


    private fun share(title: String, link: String) {
        val strBuilder = StringBuilder();
        strBuilder.appendln(title)
        strBuilder.appendln(link);
        strBuilder.append("Share Via NewsApp@Sunny")

        val shareIntent =
            Intent(Intent.ACTION_SEND)
        shareIntent.type = "text/plain"
        shareIntent.putExtra(Intent.EXTRA_TEXT, strBuilder.toString())
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "News Share")
        startActivity(contex,Intent.createChooser(shareIntent, "Share..."), Bundle.EMPTY)

    }
}
package com.shaun.news


import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.PopupMenu
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
    val newslink:TextView=view.each_link

}


class RecyclerViewAdapterNews(
    private var newsadapter: List<newsData>, context: Context
) :
    RecyclerView.Adapter<NewsRecyclerViewAdapter>() {
    private val contex = context

    private val tag = "RecyclerViewAdapt"
    interface OnshareClicked {
        fun share(title:String,link:String)
    }
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
            holder.newslink.text=newsDataAdapter.urlToArticle

        }
        /////////////////////
        holder.itemView.setOnLongClickListener {
            val popup = PopupMenu(contex, holder.itemView)
            popup.inflate(R.menu.menu_options)
            popup.setOnMenuItemClickListener(object : PopupMenu.OnMenuItemClickListener {
                override fun onMenuItemClick(item: MenuItem): Boolean {
                    when (item.getItemId()) {
                        R.id.options_share -> {
                           share(holder.newsTitle.text.toString(),holder.newslink.text.toString())
                            return true
                        }
                        R.id.options_bookmark->{
                            Toast.makeText(contex,"Coming Soon", Toast.LENGTH_SHORT).show()
                           return true
                        }
                        else -> return false
                    }
                }
            })
            popup.show()
            true
        }

    }


    private fun share(title:String,link:String){
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
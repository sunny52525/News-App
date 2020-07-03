package com.shaun.news

import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.PopupMenu
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.each_news.view.*


private const val TAG = "Recycler View Adapter"

class BookmarkHolder(view: View) : RecyclerView.ViewHolder(view) {


    val title: TextView = view.each_new_title
    val description: TextView = view.each_news_description
    val websiteName: TextView = view.each_news_websiteName
    val date: TextView = view.each_news_date
    val thumnail: ImageView = view.imageView
    val link:TextView=view.each_link
    val imgLink:TextView=view.each_img_link



}

class BookmarkViewAdapter(
    private var cursor: Cursor?,private var newsadapter:List<NewsBookmarks>,context: Context
                          , private val listener: OnNewsClickListener
) :
    RecyclerView.Adapter<BookmarkHolder>() {

    interface OnNewsClickListener{
    fun onNewsClick(position: Int)
}
private val contex = context
    private var databaseCursor:Cursor?=null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookmarkHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.each_news, parent, false)
        return BookmarkHolder(view)
    }

 fun loaddata(data:List<NewsBookmarks>){
    newsadapter=data
    notifyDataSetChanged()
}

    override fun onBindViewHolder(holder: BookmarkHolder, position: Int) {

        val newsView=newsadapter[position]

            holder.title.text=newsView.title
            holder.description.text=newsView.description
            holder.websiteName.text=newsView.websiteName
            holder.date.text=newsView.date
            holder.imgLink.text=newsView.urlToImage
            holder.link.text=newsView.urlToArticlle
            Picasso.get().load(newsView.urlToImage)
                .into(holder.thumnail)



        holder.itemView.setOnLongClickListener {
            val popup = PopupMenu(contex, holder.itemView)
            popup.inflate(R.menu.menu_bookmark)
            popup.setOnMenuItemClickListener(object : PopupMenu.OnMenuItemClickListener {
                override fun onMenuItemClick(item: MenuItem): Boolean {
                    when (item.getItemId()) {
                        R.id.bookmark_share -> {

                            share(holder.title.text.toString(),holder.link.text.toString())
                            return true
                        }
                        R.id.bookmark_menu_delete->{
//                            Toast.makeText(contex,"Coming Soon", Toast.LENGTH_SHORT).show()
                            Delete(position)
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

    private fun Delete(position: Int) {
       listener.onNewsClick(position)

    }

    override fun getItemCount(): Int {
      return  if (newsadapter.isEmpty()) 0 else  newsadapter.size
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
        ContextCompat.startActivity(
            contex,
            Intent.createChooser(shareIntent, "Share..."),
            Bundle.EMPTY
        )

    }
}




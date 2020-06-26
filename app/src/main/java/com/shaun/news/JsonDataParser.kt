package com.shaun.news

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONException
import org.json.JSONObject
import kotlin.math.min

class JsonDataParser(private  val listener:OnDataParsed) {
    private val Tag="JsonDataParser"
    interface OnDataParsed {
       fun onDataParsed(data:ArrayList<newsData>)
        fun onError(exception: Exception)
    }

    fun parseJson(rawData: String){
        GlobalScope.launch {
            val result = parseNews(rawData)
            withContext(Dispatchers.Main){
                listener.onDataParsed(result)
            }
        }
    }

    private fun parseNews(rawData: String): ArrayList<newsData> {
    Log.d(Tag,"parseNews Starts")
     val newsList=ArrayList<newsData>()
        try {
            val jsonData=JSONObject(rawData)
            val itemsArray=jsonData.getJSONArray("articles")
            for (i in 0..min(100,itemsArray.length()-1)){
                val currentNewsdata=itemsArray.getJSONObject(i)
                val source=currentNewsdata.getJSONObject("source")
                val websiteName=source.getString("name")
                val author =currentNewsdata.getString("author")
                val title =currentNewsdata.getString("title")
                var description=currentNewsdata.getString("description")
                val urltoImage=currentNewsdata.getString("urlToImage")
                val urlToArticle=currentNewsdata.getString("url")
                val date=currentNewsdata.getString("publishedAt")
                if(100<description.length) {
                    description=description.substring(0,99)
                }
                if(!websiteName.equals("Visual.ly")) //blackListed by me
               {
                   val newsObject = newsData(
                       websiteName,
                       author,
                       title,
                       description + ".....",
                       urlToArticle,
                       urltoImage,
                       date.substring(0, 10)
                   )
                   newsList.add(newsObject)
                   Log.d(Tag, "parseNews with Data $newsObject")}
            }
        }catch (e:JSONException){
            e.printStackTrace()
            Log.d(Tag, "doInBackground Error  ${e.message}")
            listener.onError(e)
        }
        Log.d(Tag,"parseNews Ends")
        return newsList
    }
}

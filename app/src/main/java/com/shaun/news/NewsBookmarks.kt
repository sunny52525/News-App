package com.shaun.news

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class  NewsBookmarks (val title:String, val description:String, val websiteName:String, val date:String, val urlToArticlle:String, val urlToImage:String,
                           var id:Long=0):
    Parcelable

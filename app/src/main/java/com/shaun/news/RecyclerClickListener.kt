package com.shaun.news

import android.content.Context
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import androidx.core.view.GestureDetectorCompat
import androidx.recyclerview.widget.RecyclerView
/**
 * Code Inspiration from Tim, LearnProgramming.academy
 */
class RecyclerItemClickListener(private val listener: MainActivity,
    context: Context,
    recylerview:RecyclerView
): RecyclerView.SimpleOnItemTouchListener(){

    private  val TAG="RecyclerItemClickList"
    interface OnRecyclerClickListener{
        fun onItemClick(view:View,postion:Int)
        fun onItemLongClick(view:View,postion: Int)

    }

    private val gestureDetector=GestureDetectorCompat(context,object:GestureDetector.SimpleOnGestureListener(){
        override fun onSingleTapUp(e: MotionEvent): Boolean {
            Log.d(TAG,"OnSingleTapUp:Starts")
            val childview=recylerview.findChildViewUnder(e.x,e.y)
            if (childview != null) {
                listener.onItemClick(childview,recylerview.getChildAdapterPosition(childview))
            }
            return true
        }

        override fun onLongPress(e: MotionEvent) {
            val childview=recylerview.findChildViewUnder(e.x,e.y)
            if (childview != null) {
                listener.onItemLongClick(childview,recylerview.getChildAdapterPosition(childview))
            }
        }
    })

    override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {

        Log.d(TAG,"OnInterceptTouchEventCalled $e")
        val  result=gestureDetector.onTouchEvent(e)
        return result
    }


}
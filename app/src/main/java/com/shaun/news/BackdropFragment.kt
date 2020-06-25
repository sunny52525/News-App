package com.shaun.news

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

class BackdropFragment : Fragment() {
    private val isCurrentVisible = false
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d("tag", "nana")
        return inflater.inflate(R.layout.backdrop_fragment, container, false)
    }

}
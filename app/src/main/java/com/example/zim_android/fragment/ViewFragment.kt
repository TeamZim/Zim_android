package com.example.zim_android.fragment

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.zim_android.R

class ViewFragment : Fragment(R.layout.view_fragment) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val headerLayout = view.findViewById<View>(R.id.common_header)
        val titleTextView = headerLayout.findViewById<TextView>(R.id.tv_title)
        titleTextView.text = "모음"

    }

}

package com.example.zim_android.Fragment

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.zim_android.R

class RecordFragment: Fragment(R.layout.record_fragment){

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val headerLayout = view.findViewById<View>(R.id.common_header)
        val titleTextView = view.findViewById<TextView>(R.id.tv_title)
        titleTextView.text = "기록하기"


    }

}

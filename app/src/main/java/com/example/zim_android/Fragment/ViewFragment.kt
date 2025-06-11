package com.example.zim_android.Fragment

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat
import androidx.fragment.app.Fragment
import com.example.zim_android.R
import com.example.zim_android.ui.theme.CommonHeaderView
import android.graphics.Color
import androidx.core.content.ContextCompat


class ViewFragment : Fragment(R.layout.view_fragment) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // header 설정
        val headerLayout = view.findViewById<View>(R.id.common_header)
        val titleTextView = headerLayout.findViewById<TextView>(R.id.tv_title)
        titleTextView.text = "모음"

        // switch + textview 설정
        val switch1 = view.findViewById<SwitchCompat>(R.id.switch1)

        switch1.thumbDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.custom_thumb)
        switch1.trackDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.custom_track)

        val textLeft = view.findViewById<TextView>(R.id.textLeft)
        val textRight = view.findViewById<TextView>(R.id.textRight)


        switch1.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                textLeft.setTextColor(Color.parseColor("#A7A4A0"))
                textRight.setTextColor(Color.parseColor("#000000"))
            } else {
                textLeft.setTextColor(Color.parseColor("#000000"))  // 꺼짐쪽 흐리게
                textRight.setTextColor(Color.parseColor("#A7A4A0")) // 켜짐쪽 진하게
            }
        }

    }




}

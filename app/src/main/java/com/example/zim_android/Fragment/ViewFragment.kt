package com.example.zim_android.Fragment

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.zim_android.R
import com.example.zim_android.ui.theme.CardItemDecoration

class ViewFragment : Fragment(R.layout.view_fragment) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // header 설정
        val headerLayout = view.findViewById<View>(R.id.common_header)
        val titleTextView = headerLayout.findViewById<TextView>(R.id.tv_title)
        titleTextView.text = "모음"

        // switch + textview 설정
        val switch1 = view.findViewById<SwitchCompat>(R.id.switch1)
        val textLeft = view.findViewById<TextView>(R.id.textLeft)
        val textRight = view.findViewById<TextView>(R.id.textRight)

        switch1.thumbDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.custom_thumb)
        switch1.trackDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.custom_track)

        switch1.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                textLeft.setTextColor(Color.parseColor("#A7A4A0"))
                textRight.setTextColor(Color.parseColor("#000000"))
            } else {
                textLeft.setTextColor(Color.parseColor("#000000"))
                textRight.setTextColor(Color.parseColor("#A7A4A0"))
            }
        }

        // ViewPager2 설정
        val viewPager = view.findViewById<ViewPager2>(R.id.card_viewpager)

        val dummyCards = listOf("카드1", "카드2", "카드3", "카드4") // 임시 데이터
        val adapter = CardAdapter(dummyCards, viewPager)

        val recyclerView = viewPager.getChildAt(0) as RecyclerView
        recyclerView.clipToPadding = false
        recyclerView.clipChildren = false


        viewPager.addItemDecoration(CardItemDecoration(19))  // 카드 사이 간격 40dp


        viewPager.adapter = adapter
        viewPager.orientation = ViewPager2.ORIENTATION_HORIZONTAL
    }
}

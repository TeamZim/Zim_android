package com.example.zim_android.Fragment

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.zim_android.R
import com.example.zim_android.ui.theme.CardItemDecoration
import com.example.zim_android.Adapter.CardAdapter

class ViewFragment : Fragment(R.layout.view_fragment) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 헤더 설정
        val headerLayout = view.findViewById<View>(R.id.common_header)
        val titleTextView = headerLayout.findViewById<TextView>(R.id.tv_title)
        titleTextView.text = "모음"

        // 스위치 설정
        val switch1 = view.findViewById<SwitchCompat>(R.id.switch1)
        val textLeft = view.findViewById<TextView>(R.id.textLeft)
        val textRight = view.findViewById<TextView>(R.id.textRight)

        switch1.thumbDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.slide_thumb)
        switch1.trackDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.custom_track)

        val viewPager = view.findViewById<ViewPager2>(R.id.card_viewpager)
        val mapContainer = view.findViewById<View>(R.id.map_container)

        switch1.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                // 지도 모드
                textLeft.setTextColor(Color.parseColor("#A7A4A0"))
                textRight.setTextColor(Color.parseColor("#000000"))

                viewPager.visibility = View.GONE
                mapContainer.visibility = View.VISIBLE

            } else {
                // 카드 모드
                textLeft.setTextColor(Color.parseColor("#000000"))
                textRight.setTextColor(Color.parseColor("#A7A4A0"))

                viewPager.visibility = View.VISIBLE
                mapContainer.visibility = View.GONE
            }
        }

        // ViewPager2 설정

        val dummyCards = listOf("카드1", "카드2", "카드3", "카드4")
        val adapter = CardAdapter(dummyCards)
        viewPager.adapter = adapter
        viewPager.orientation = ViewPager2.ORIENTATION_HORIZONTAL

        val seekBar = view.findViewById<SeekBar>(R.id.progress_bar)
        seekBar.max = dummyCards.size - 1


        // flip 상태 초기화용
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            private var lastPosition = -1

            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                if (lastPosition != -1 && lastPosition != position) {
                    adapter.resetFlip(lastPosition)
                }
                lastPosition = position

                // seekBar 동기화
                seekBar.progress = position
            }
        })

        // RecyclerView 설정
        val recyclerView = viewPager.getChildAt(0) as RecyclerView
        recyclerView.clipToPadding = false
        recyclerView.clipChildren = false
        viewPager.offscreenPageLimit = 3

        viewPager.addItemDecoration(CardItemDecoration(19))

        recyclerView.setPadding(80, 0, 80, 0)

        viewPager.offscreenPageLimit = 3
        viewPager.setPageTransformer { page, position ->
            page.scaleY = 0.95f
            page.translationX = -40 * position
        }


        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    viewPager.currentItem = progress
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }
}

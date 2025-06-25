package com.example.zim_android.Fragment

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.SeekBar
import androidx.appcompat.widget.SwitchCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.zim_android.R
import com.example.zim_android.ui.theme.CardItemDecoration
import com.example.zim_android.Adapter.CardAdapter
import com.example.zim_android.databinding.ViewFragmentBinding

class ViewFragment : Fragment(R.layout.view_fragment) {

    private var _binding: ViewFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = ViewFragmentBinding.bind(view)

        // 헤더 설정
        binding.commonHeader.tvTitle.text = "모음"

        // 스위치 설정
        binding.switch1.apply {
            thumbDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.slide_thumb)
            trackDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.custom_track)
        }

        //스위치 전환 색 변환
        binding.switch1.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                binding.textLeft.setTextColor(Color.parseColor("#A7A4A0"))
                binding.textRight.setTextColor(Color.parseColor("#000000"))
                binding.cardViewpager.visibility = View.GONE
                binding.mapContainer.visibility = View.VISIBLE
            } else {
                binding.textLeft.setTextColor(Color.parseColor("#000000"))
                binding.textRight.setTextColor(Color.parseColor("#A7A4A0"))
                binding.cardViewpager.visibility = View.VISIBLE
                binding.mapContainer.visibility = View.GONE
            }
        }

        // ViewPager2 설정
        val dummyCards = listOf("카드1", "카드2", "카드3", "카드4")
        val adapter = CardAdapter(dummyCards)
        val viewPager = binding.cardViewpager
        viewPager.adapter = adapter
        viewPager.orientation = ViewPager2.ORIENTATION_HORIZONTAL

        val seekBar = binding.progressBar
        seekBar.max = dummyCards.size - 1

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            private var lastPosition = -1
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                if (lastPosition != -1 && lastPosition != position) {
                    adapter.resetFlip(lastPosition)
                }
                lastPosition = position
                seekBar.progress = position
            }
        })

        val recyclerView = viewPager.getChildAt(0) as RecyclerView
        recyclerView.clipToPadding = false
        recyclerView.clipChildren = false
        recyclerView.setPadding(80, 0, 80, 0)
        viewPager.offscreenPageLimit = 3
        viewPager.addItemDecoration(CardItemDecoration(19))
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

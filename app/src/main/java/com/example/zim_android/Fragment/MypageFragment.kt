package com.example.zim_android.Fragment

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.zim_android.R

class MypageFragment: Fragment(R.layout.mypage_fragment){

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val headerLayout = view.findViewById<View>(R.id.common_header)
        val titleTextView = headerLayout.findViewById<TextView>(R.id.tv_title)
        titleTextView.text = "마이페이지"

        // 이미지 컨테이너 불러오기
        val imageContainer = view.findViewById<LinearLayout>(R.id.flag_image_container)

        // 백엔드에서 받아온 이미지 리스트 (수는 제한 없음)
        val imageUrlList = listOf(
            R.drawable.view_gray,
            R.drawable.view_gray,
            R.drawable.view_gray,
            R.drawable.view_gray,
            R.drawable.view_gray,
            R.drawable.view_gray,
            R.drawable.view_gray,
            R.drawable.view_gray,
            R.drawable.view_gray,
            R.drawable.view_gray,
            R.drawable.view_gray,
            R.drawable.view_gray,
            R.drawable.view_gray,// 11번째 → 무시됨
        )

        // 최대 15개까지만 보여줌
        val limitedList = imageUrlList.take(15)

        for (resId in imageUrlList.take(15)) {
            val imageView = ImageView(requireContext()).apply {
                layoutParams = LinearLayout.LayoutParams(40, 40).apply {
                    setMargins(7, 0, 7, 0)
                }
                scaleType = ImageView.ScaleType.CENTER_CROP
                setImageResource(resId)
            }
            imageContainer.addView(imageView)
        }

    }

}

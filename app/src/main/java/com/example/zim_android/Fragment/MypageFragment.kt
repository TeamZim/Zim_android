package com.example.zim_android.Fragment

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.zim_android.R

class MypageFragment: Fragment(R.layout.mypage_fragment){

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val headerLayout = view.findViewById<View>(R.id.common_header)
        val titleTextView = headerLayout.findViewById<TextView>(R.id.tv_title)
        titleTextView.text = "마이페이지"

        // 이모지 넣을 텍스트뷰 가져오기
        val imageContainer = view.findViewById<TextView>(R.id.country_flag_text_1)
        // 백엔드에서 방문한 나라 리스트 받아와 텍스트에 개수에 따라 추가하는 로직 추가해야함.

        // 백엔드에서 리스트 받아오기

        // 리스트의 길이에 따라 조건 나누기
        // 10개 이하인 경우
            // flag_image_container1 에 넣기
            // imageView3 의 길이 조절

        // 10개 이상인 경우
            // flag_image_container2에 넣기
            // imageView3의 길이 조절

    }

}

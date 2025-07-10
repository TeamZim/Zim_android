package com.example.zim_android.Adapter

import android.content.Context
import android.graphics.Color
import android.graphics.PorterDuff
import android.view.LayoutInflater
import android.view.View
import com.example.zim_android.data.model.Emotion
import com.example.zim_android.databinding.DialogSelectEmotionColorItemBinding

class DialogEmotionSelectAdapter(
    private val context: Context,
    private val items: List<Emotion>,
    private val onItemSelected: (Emotion) -> Unit // 람다명
) {
    private var selectedPosition: Int = -1

    fun getView(position: Int): View {
        // Fragment, Activity에서와 달리 어댑터에서는 이렇게 바인딩 해야함.
        val inflater = LayoutInflater.from(context)
        val binding = DialogSelectEmotionColorItemBinding.inflate(inflater, null, false)

        val item = items[position]

        binding.emotionColorImg.setColorFilter(Color.parseColor(item.colorCode))
        binding.emotionColorText.text = item.name // 텍스트 지정


        // 체크 표시 보이기/숨기기
        binding.checkIcon.visibility =
            if (position == selectedPosition) View.VISIBLE else View.INVISIBLE // 체크 유무 표시


        // 감정색 선택 시
        binding.emotionColorLayout.setOnClickListener {
            selectedPosition = position
            onItemSelected(item) // 감정 이름 전달
                                 // 여기가 람다 호출 부분
            notifyGridLayoutChanged()
        }


        return binding.root
    }

    // 아래 두 함수는 그리드 새로고침을 위한 콜백 함수
    private var notifyGridLayoutChanged: () -> Unit = {}

    fun setOnGridUpdateCallback(callback: () -> Unit) {
        notifyGridLayoutChanged = callback
    }
    // 함수 안 내용은 호출할 때 정의하는 것

    // 선택된 아이템 반환함
    fun getSelectedItem(): Emotion? =
        if (selectedPosition in items.indices) items[selectedPosition] else null

    fun getCount() = items.size


}
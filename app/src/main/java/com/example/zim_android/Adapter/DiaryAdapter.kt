package com.example.zim_android.Adapter

import android.graphics.Color
import android.graphics.PorterDuff
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.zim_android.data.model.DiaryResponse
import com.example.zim_android.databinding.DiaryItemBinding
import com.bumptech.glide.Glide

class DiaryAdapter(private val itemList: List<DiaryResponse>) :
    RecyclerView.Adapter<DiaryAdapter.DiaryViewHolder>() {

    inner class DiaryViewHolder(val binding: DiaryItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DiaryViewHolder {
        val binding = DiaryItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DiaryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DiaryViewHolder, position: Int) {
        val item = itemList[position]
        val binding = holder.binding
        val defaultColor = "#F1F1EF"

        // 감정색, 날씨, 녹음 아이콘 기본 세팅
        binding.emotionColorImg.setColorFilter(Color.parseColor(defaultColor), PorterDuff.Mode.SRC_IN)
        binding.weatherIcon.setColorFilter(Color.parseColor(defaultColor), PorterDuff.Mode.SRC_IN)
        binding.audioBtn.setColorFilter(Color.parseColor(defaultColor), PorterDuff.Mode.SRC_IN)

        // 텍스트 바인딩
        binding.countryName.text = item.countryName // emoji로 바꿔야함.
        binding.countryName.text = item.countryName
        binding.cityName.text = item.city
        binding.date.text = item.dateTime.split("T").getOrNull(0) ?: "-"
        binding.time.text = item.dateTime.split("T").getOrNull(1) ?: "-"
        binding.detailedLocation.text = item.detailedLocation ?: "-"
        binding.contextText.text = item.content ?: "입력된 기록이 없어요."

        // 감정색
        binding.emotionColorImg.setColorFilter(Color.parseColor(item.emotionColor), PorterDuff.Mode.SRC_IN)
        // binding.emotionColorText.text = item. ?: "-" // 감정색명 text 추가

        // 날씨
//        if (!item.weather.isNullOrBlank()) binding.photo1.setImageResource(item.~) //  날씨 이미지 추가
        binding.weatherText.text = item.weather ?: "-"

        // 녹음
        if (!item.audioUrl.isNullOrBlank()) {
            binding.audioText.text = ""
//            binding.audioBtn.setImageResource(item.~) // 녹음 버튼 이미지 변경
        }


        // 대표 이미지 바인딩
        val repImg = item.images.firstOrNull { it.isRepresentative }
        val otherImg = item.images.firstOrNull { !it.isRepresentative }
        Glide.with(binding.root.context).load(repImg?.imageUrl).into(binding.photo1)
        Glide.with(binding.root.context).load(otherImg?.imageUrl).into(binding.photo2)


        // 대표 이미지 마크 처리
        binding.representiveIcon1.visibility = if (item.images[0].isRepresentative) View.VISIBLE else View.GONE
        binding.representiveIcon2.visibility = if (item.images[2].isRepresentative) View.VISIBLE else View.GONE

    }

    override fun getItemCount(): Int = itemList.size
}
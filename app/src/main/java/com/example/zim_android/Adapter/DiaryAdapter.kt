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
import com.example.zim_android.R
import android.media.MediaPlayer
import android.util.Log
import androidx.core.content.ContextCompat
import com.example.zim_android.Adapter.DiaryAdapter.DiaryViewHolder
import com.example.zim_android.util.PreferenceUtil.Constants


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
        binding.countryEmoji.text = item.countryEmoji // emoji로 바꿔야함.
        binding.countryName.text = item.countryName
        binding.cityName.text = item.city
        binding.date.text = item.dateTime.split("T").getOrNull(0) ?: "-"
        //분단위까지 자르기
        val timePart = item.dateTime.split("T").getOrNull(1)?.split(":")
        val hourMinute = if (timePart != null && timePart.size >= 2) {
            "${timePart[0]}:${timePart[1]}"
        } else "-"
        binding.time.text = hourMinute

        binding.detailedLocation.text = item.detailedLocation ?: "-"
        if (item.content != null) {
            binding.contextText.text = item.content ?: "입력된 기록이 없어요."
            binding.contextText.setTextColor(ContextCompat.getColor(binding.root.context, R.color.primary_700))

            binding.contextView1.setBackgroundColor(ContextCompat.getColor(binding.root.context, R.color.unselected))
            binding.contextView2.setBackgroundColor(ContextCompat.getColor(binding.root.context, R.color.unselected))
        }

        Log.d("diaryId", item.id.toString())
        Log.d("emotionColor", item.emotionColor.toString())

        // 감정색
        binding.emotionColorImg.setColorFilter(Color.parseColor(item.emotionColor), PorterDuff.Mode.SRC_IN)
        binding.emotionColorText.text = item.emotionName ?: "-" // 감정색명 text 추가

        // 날씨
        val fullUrl = Constants.BASE_URL + item.weatherIconUrl
        if (!item.weather.isNullOrBlank())
            Glide.with(binding.root.context).load(fullUrl).centerCrop().into(binding.weatherIcon) //  날씨 이미지 추가
        binding.weatherText.text = item.weather ?: "-"

        // 녹음
        if (!item.audioUrl.isNullOrBlank()) {
            binding.audioText.visibility = View.GONE
            binding.audioBtn.clearColorFilter()
            binding.audioBtn.setImageResource(R.drawable.record_play)
            binding.audioBtn.isClickable = true

            binding.audioBtn.setOnClickListener {
                // 이미 재생 중이고 동일 뷰를 누른 경우 -> 정지
                if (isPlaying && currentlyPlayingHolder == holder) {
                    mediaPlayer?.pause()
                    mediaPlayer?.seekTo(0)
                    isPlaying = false
                    binding.audioBtn.setImageResource(R.drawable.record_play)
                    return@setOnClickListener
                }

                // 다른 뷰에서 재생 중이던 미디어가 있다면 정지
                currentlyPlayingHolder?.let {
                    it.binding.audioBtn.setImageResource(R.drawable.record_play)
                }
                mediaPlayer?.release()
                mediaPlayer = null

                // 새로운 미디어 재생
                mediaPlayer = MediaPlayer().apply {
                    setDataSource(item.audioUrl)
                    setOnPreparedListener {
                        start()
                        DiaryAdapter.isPlaying = true
                        binding.audioBtn.setImageResource(R.drawable.record_pause)
                        currentlyPlayingHolder = holder
                    }
                    setOnCompletionListener {
                        release()
                        DiaryAdapter.isPlaying = false
                        binding.audioBtn.setImageResource(R.drawable.record_play)
                        currentlyPlayingHolder = null
                    }
                    setOnErrorListener { _, what, extra ->
                        Log.e("AudioPlay", "오디오 재생 실패: $what, $extra")
                        true
                    }
                    prepareAsync()
                }
            }
        } else {
            binding.audioText.visibility = View.VISIBLE
            binding.audioBtn.setImageResource(R.drawable.default_color_img)
            binding.audioBtn.isClickable = false
            binding.audioBtn.setOnClickListener(null)
        }


        // 대표 이미지 바인딩
        val repImg = item.images.firstOrNull { it.isRepresentative }
        val otherImg = item.images.firstOrNull { !it.isRepresentative }
        Glide.with(binding.root.context).load(repImg?.imageUrl).centerCrop().into(binding.photo1)
        Glide.with(binding.root.context).load(otherImg?.imageUrl).centerCrop().into(binding.photo2)


        // 대표 이미지 마크 처리
        binding.representiveIcon1.visibility = if (item.images[0].isRepresentative) View.VISIBLE else View.GONE
        binding.representiveIcon2.visibility = if (item.images[1].isRepresentative) View.VISIBLE else View.GONE
    }

    companion object {
        // 녹음 재생 상태 추적용 변수들
        private var mediaPlayer: MediaPlayer? = null
        private var currentlyPlayingHolder: DiaryViewHolder? = null
        private var isPlaying: Boolean = false

        fun stopPlayingAudioIfNeeded() {
            if (isPlaying) {
                mediaPlayer?.pause()
                mediaPlayer?.seekTo(0)
                mediaPlayer?.release()
                mediaPlayer = null
                currentlyPlayingHolder?.binding?.audioBtn?.setImageResource(R.drawable.record_play)
                isPlaying = false
                currentlyPlayingHolder = null
            }
        }
    }

    override fun getItemCount(): Int = itemList.size
}


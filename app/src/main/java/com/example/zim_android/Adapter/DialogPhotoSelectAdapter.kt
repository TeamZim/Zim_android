package com.example.zim_android.Adapter

import android.content.Context
import android.graphics.Color
import android.graphics.PorterDuff
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.bumptech.glide.Glide
import com.example.zim_android.data.model.TripImageResponse
import com.example.zim_android.databinding.DialogSelectPhotoGridItemBinding

class DialogPhotoSelectAdapter (
    private val context: Context,
    private val items: List<TripImageResponse>, // 데이터 클래스
    private val onItemClick: (TripImageResponse) -> Unit
) : BaseAdapter() {
    private var selectedItem: TripImageResponse? = null

    // 외부에서 선택된 아이템 갱신 시 호출
    fun setSelectedItem(item: TripImageResponse?) {
        selectedItem = item
        notifyDataSetChanged()
    }

    override fun getCount(): Int = items.size

    override fun getItem(position: Int): Any = items[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val binding: DialogSelectPhotoGridItemBinding = if (convertView == null) {
            DialogSelectPhotoGridItemBinding.inflate(LayoutInflater.from(context), parent, false)
        } else {
            DialogSelectPhotoGridItemBinding.bind(convertView)
        }

        val item = items[position]
        Log.d("ImageURL", "item.imageUrl = ${item.imageUrl}")


        // 이미지 URL 로드
        Glide.with(context)
            .load(item.imageUrl)
            .into(binding.imageView)

        // 선택된 이미지 외에는 어둡게 처리
        if (selectedItem != null && selectedItem != item) {
            binding.imageView.setColorFilter(Color.parseColor("#CC000000"), PorterDuff.Mode.SRC_OVER)
        } else {
            binding.imageView.clearColorFilter()
        }

        binding.root.setOnClickListener {
            onItemClick(item)
        }

        return binding.root
    }
}
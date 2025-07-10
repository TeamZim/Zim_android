package com.example.zim_android.Adapter

import android.content.Context
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

        // 이미지 URL 로드
        Glide.with(context)
            .load(item.imageUrl)
            .into(binding.imageView)

        binding.root.setOnClickListener{
            onItemClick(item) // 콜백 실행
        }

        return binding.root
    }
}
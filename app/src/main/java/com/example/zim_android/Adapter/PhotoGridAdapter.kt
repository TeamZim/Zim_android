package com.example.zim_android.Adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.zim_android.R
import com.example.zim_android.data.model.TripImageResponse
import com.example.zim_android.databinding.ItemCardBinding
import com.example.zim_android.databinding.ItemPhotoGridBinding

class PhotoGridAdapter(private val photos: List<Int>,
                       private val onItemClick: (Int) -> Unit // position 또는 id 전달 - api 연결하며 수정
    ) :
    RecyclerView.Adapter<PhotoGridAdapter.PhotoViewHolder>() {

    private var lastVisibleIndex: Int = -1

    inner class PhotoViewHolder(val binding: ItemPhotoGridBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            binding.gridImage.setImageResource(photos[position])

            binding.gridImage.setOnClickListener {
                onItemClick(position)
            }
            binding.gridImage.alpha = if (position == lastVisibleIndex) 0.3f else 1.0f
        }
    }


    fun setLastVisibleIndex(index: Int) {
        lastVisibleIndex = index
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        val binding = ItemPhotoGridBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PhotoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
    }

    override fun getItemCount(): Int = photos.size
}

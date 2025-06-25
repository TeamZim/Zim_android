package com.example.zim_android.Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.zim_android.R
import com.example.zim_android.databinding.ItemPhotoGridBinding

class PhotoGridAdapter(private val photos: List<Int>) :
    RecyclerView.Adapter<PhotoGridAdapter.PhotoViewHolder>() {

    inner class PhotoViewHolder(val binding: ItemPhotoGridBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        val binding = ItemPhotoGridBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PhotoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        val imageView = holder.binding.gridImage
        imageView.setImageResource(photos[position])
        imageView.alpha = if (position == photos.size - 1) 0.3f else 1.0f
    }

    override fun getItemCount(): Int = photos.size
}

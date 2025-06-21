package com.example.zim_android.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.zim_android.R

class PhotoGridAdapter(private val photos: List<Int>) :
    RecyclerView.Adapter<PhotoGridAdapter.PhotoViewHolder>() {

    class PhotoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val image: ImageView = itemView.findViewById(R.id.grid_image)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_photo_grid, parent, false)
        return PhotoViewHolder(view)
    }

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        holder.image.setImageResource(photos[position])


        // ✅ 마지막 사진 흐리게
        if (position == photos.size - 1) {
            holder.image.alpha = 0.3f
        } else {
            holder.image.alpha = 1.0f
        }
    }

    override fun getItemCount(): Int = photos.size
}



package com.example.zim_android.Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.zim_android.data.model.TripImageResponse
import com.example.zim_android.databinding.ItemPhotoGridBinding

class PhotoGridAdapter(private val photos: List<TripImageResponse>) :
    RecyclerView.Adapter<PhotoGridAdapter.PhotoViewHolder>() {

    private var lastVisibleIndex: Int = -1

    fun setLastVisibleIndex(index: Int) {
        lastVisibleIndex = index
        notifyDataSetChanged()
    }

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

        //Glide를 사용해 URL 로드
        Glide.with(holder.itemView.context)
            .load(photos[position].imageUrl)
            .into(imageView)

        // 선택된 인덱스일 경우 알파값 변경
        imageView.alpha = if (position == lastVisibleIndex) 0.3f else 1.0f


        //사진 클릭 시 로그 출력
        imageView.setOnClickListener {
            android.util.Log.d("PhotoGrid", "📸 ${position + 1}번째 사진 클릭됨 (URL: ${photos[position].imageUrl})")
        }
    }

    override fun getItemCount(): Int = photos.size
}

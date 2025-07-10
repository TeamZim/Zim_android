package com.example.zim_android.Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.zim_android.data.model.DiaryResponse
import com.example.zim_android.databinding.DiaryItemBinding

class DiaryAdapter(private val itemList: List<DiaryResponse>) :
    RecyclerView.Adapter<DiaryAdapter.DiaryViewHolder>() {

    inner class DiaryViewHolder(val binding: DiaryItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DiaryViewHolder {
        val binding = DiaryItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DiaryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DiaryViewHolder, position: Int) {
        // 지금은 아무 데이터 안 넣어도 됨
    }

    override fun getItemCount(): Int = itemList.size
}
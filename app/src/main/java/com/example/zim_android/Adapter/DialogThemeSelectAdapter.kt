package com.example.zim_android.Adapter

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.PorterDuff
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.zim_android.R
import com.example.zim_android.data.model.Theme
import com.example.zim_android.databinding.ThemeItemBinding

class DialogThemeSelectAdapter(
    private val context: Context,
    private val items: List<Theme>,
    private val onItemClick: (Theme) -> Unit
) : RecyclerView.Adapter<DialogThemeSelectAdapter.ViewHolder>() {

    private var selectedTheme: Theme? = null

    fun setSelectedTheme(item: Theme?) {
        selectedTheme = item
        notifyDataSetChanged()
    }

    fun getSelectedTheme(): Theme? = selectedTheme

    inner class ViewHolder(val binding: ThemeItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Theme) {
            binding.themeBtnText.text = item.themeName

            Glide.with(context)
                .load(item.sampleImageUrl)
                .centerCrop()
                .into(binding.themeBtnImg)

            // 선택된 이미지 외에는 어둡게 처리
            if (selectedTheme != null && selectedTheme != item) {
                binding.themeBtnImg.setColorFilter(
                    Color.parseColor("#A6000000"),
                    PorterDuff.Mode.SRC_OVER
                )
            } else {
                binding.themeBtnImg.clearColorFilter()
            }

            // 스트로크 처리
            val strokeColorResId =
                if (selectedTheme == item) R.color.primary_700 else R.color.unselected
            val strokeColor = ContextCompat.getColor(context, strokeColorResId)
            binding.themeBtnImg.strokeColor = ColorStateList.valueOf(strokeColor)

            binding.root.setOnClickListener {
                onItemClick(item)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ThemeItemBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size
}
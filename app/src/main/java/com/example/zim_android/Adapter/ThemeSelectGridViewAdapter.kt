package com.example.zim_android.Adapter

import android.content.Context
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.zim_android.R
import com.example.zim_android.data.model.Theme
import com.example.zim_android.databinding.ThemeItemBinding


class ThemeSelectGridViewAdapter(
    private val context: Context,
    private val itemList: List<Theme>,
    private val onItemClick: (Theme) -> Unit // ✅ 콜백 추가
) : BaseAdapter() {

    private var selectedTheme: Theme? = null

    fun setSelectedTheme(item: Theme?) {
        selectedTheme = item
        notifyDataSetChanged()
    }

    fun getSelectedTheme(): Theme? = selectedTheme

    override fun getCount(): Int = itemList.size

    override fun getItem(position: Int): Any = itemList[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val binding: ThemeItemBinding
        val view: View

        if (convertView == null) {
            val inflater = LayoutInflater.from(context)
            binding = ThemeItemBinding.inflate(inflater, parent, false)
            view = binding.root
            view.tag = binding
        } else {
            view = convertView
            binding = view.tag as ThemeItemBinding
        }

        val item = itemList[position]
        binding.themeBtnText.text = item.themeName
        Glide.with(context)
            .load(item.sampleImageUrl)
            .centerCrop()
            .into(binding.themeBtnImg)

        // 스트로크 색상 처리
        val strokeColorResId =
            if (selectedTheme == item) R.color.primary_700 else R.color.unselected
        val strokeColor = ContextCompat.getColor(context, strokeColorResId)
        binding.themeBtnImg.strokeColor = ColorStateList.valueOf(strokeColor)

        view.setOnClickListener {
            onItemClick(item)
        }

        return view
    }
}

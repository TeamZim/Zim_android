package com.example.zim_android.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.zim_android.databinding.Onboarding5Binding



class OnBoardingAdapter(private val items: List<Int>) :
    RecyclerView.Adapter<OnBoardingAdapter.OnBoardingViewHolder>() {

    inner class OnBoardingViewHolder(val binding: Onboarding5Binding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OnBoardingViewHolder {
        val binding = Onboarding5Binding.inflate(LayoutInflater.from(parent.context), parent, false)
        return OnBoardingViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OnBoardingViewHolder, position: Int) {

        val isProfilePage = position == 4

        // 입력 필드들
        val isPage5 = position == 4

        if (!isProfilePage) {
            holder.binding.imageView.visibility = View.VISIBLE
            holder.binding.imageView.setImageResource(items[position]) // ✅ 이게 작동하려면 XML에 src 없어야 함
        } else {
            holder.binding.imageView.visibility = View.GONE
        }

        holder.binding.koreanLabel.visibility = if (isPage5) View.VISIBLE else View.GONE
        holder.binding.birthLabel.visibility = if (isPage5) View.VISIBLE else View.GONE
        holder.binding.koreanNameEdit.visibility = if (isPage5) View.VISIBLE else View.GONE
        holder.binding.birthdayEdit.visibility = if (isPage5) View.VISIBLE else View.GONE
        holder.binding.lastNameLabel.visibility = if (isPage5) View.VISIBLE else View.GONE
        holder.binding.firstNameLabel.visibility = if (isPage5) View.VISIBLE else View.GONE
        holder.binding.lastNameEngEdit.visibility = if (isPage5) View.VISIBLE else View.GONE
        holder.binding.firstNameEngEdit.visibility = if (isPage5) View.VISIBLE else View.GONE
        holder.binding.profileTitle.visibility = if (isPage5) View.VISIBLE else View.GONE
        holder.binding.photoUploadBox.visibility = if (isPage5) View.VISIBLE else View.GONE
    }



    override fun getItemCount(): Int = items.size
}


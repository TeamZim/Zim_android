package com.example.zim_android.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.zim_android.R
import com.example.zim_android.databinding.Onboarding5Binding



class OnBoardingAdapter(private val layoutIds: List<Int>) :
    RecyclerView.Adapter<OnBoardingAdapter.ViewHolder>() {

    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(viewType, parent, false)
        return ViewHolder(view)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val view = holder.view
        val isProfilePage = position == 4
        val isPage5 = position == 4

        if (!isProfilePage) {
            view.findViewById<View>(R.id.imageView)?.visibility = View.VISIBLE
            // R.drawable.onboarding_1 같은 걸 써야 하지만 현재는 layout을 보여주고 있으므로 이 줄은 의미 없을 수 있음
            // view.findViewById<ImageView>(R.id.imageView)?.setImageResource(...)
        } else {
            view.findViewById<View>(R.id.imageView)?.visibility = View.GONE
        }

        // Page5 전용 입력 필드들
        view.findViewById<View>(R.id.koreanLabel)?.visibility = if (isPage5) View.VISIBLE else View.GONE
        view.findViewById<View>(R.id.birthLabel)?.visibility = if (isPage5) View.VISIBLE else View.GONE
        view.findViewById<View>(R.id.koreanNameEdit)?.visibility = if (isPage5) View.VISIBLE else View.GONE
        view.findViewById<View>(R.id.birthdayEdit)?.visibility = if (isPage5) View.VISIBLE else View.GONE
        view.findViewById<View>(R.id.lastNameLabel)?.visibility = if (isPage5) View.VISIBLE else View.GONE
        view.findViewById<View>(R.id.firstNameLabel)?.visibility = if (isPage5) View.VISIBLE else View.GONE
        view.findViewById<View>(R.id.lastNameEngEdit)?.visibility = if (isPage5) View.VISIBLE else View.GONE
        view.findViewById<View>(R.id.firstNameEngEdit)?.visibility = if (isPage5) View.VISIBLE else View.GONE
        view.findViewById<View>(R.id.profileTitle)?.visibility = if (isPage5) View.VISIBLE else View.GONE
        view.findViewById<View>(R.id.photoUploadBox)?.visibility = if (isPage5) View.VISIBLE else View.GONE
    }




    override fun getItemCount(): Int = layoutIds.size

    override fun getItemViewType(position: Int): Int {
        return layoutIds[position]  // 각 페이지에 해당하는 layout ID를 반환
    }
}

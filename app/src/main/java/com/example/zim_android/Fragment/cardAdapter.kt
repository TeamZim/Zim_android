package com.example.zim_android.Fragment

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.zim_android.R


class CardAdapter(
    private val cards: List<String>,  // 임시 데이터
    private val viewPager: ViewPager2
) : RecyclerView.Adapter<CardAdapter.CardViewHolder>() {

    inner class CardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val front: View = itemView.findViewById(R.id.card_front)
        val back: View = itemView.findViewById(R.id.card_back)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_card, parent, false)
        return CardViewHolder(view)
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        holder.itemView.setOnClickListener {
            flipCard(holder.front, holder.back) {
                viewPager.setCurrentItem(position + 1, true)
            }
        }
    }

    override fun getItemCount(): Int = cards.size

    private fun flipCard(front: View, back: View, onFlipped: () -> Unit) {
        val scale = front.context.resources.displayMetrics.density
        front.cameraDistance = 8000 * scale
        back.cameraDistance = 8000 * scale

        val flipOut = ObjectAnimator.ofFloat(front, "rotationY", 0f, 90f)
        val flipIn = ObjectAnimator.ofFloat(back, "rotationY", -90f, 0f)

        flipOut.duration = 300
        flipIn.duration = 300

        flipOut.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                front.visibility = View.GONE
                back.visibility = View.VISIBLE
                flipIn.start()
                onFlipped()
            }
        })

        flipOut.start()
    }
}

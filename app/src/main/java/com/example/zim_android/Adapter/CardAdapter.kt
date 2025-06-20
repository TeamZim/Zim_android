package com.example.zim_android.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.zim_android.R

class CardAdapter(
    private val items: List<String>
) : RecyclerView.Adapter<CardAdapter.ViewHolder>() {

    private val isFlippedList = MutableList(items.size) { false }  // 카드 별 flip 상태

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val front = itemView.findViewById<View>(R.id.card_front)
        private val back = itemView.findViewById<View>(R.id.card_back)

        fun bind(position: Int) {
            val isFlipped = isFlippedList[position]

            // 현재 상태 반영
            front.visibility = if (isFlipped) View.GONE else View.VISIBLE
            back.visibility = if (isFlipped) View.VISIBLE else View.GONE

            itemView.setOnClickListener {
                flipCard(position)
            }
        }

        private fun flipCard(position: Int) {
            val isCurrentlyFlipped = isFlippedList[position]

            val showView = if (isCurrentlyFlipped) front else back
            val hideView = if (isCurrentlyFlipped) back else front

            hideView.animate().rotationY(90f).setDuration(150).withEndAction {
                hideView.visibility = View.GONE
                showView.visibility = View.VISIBLE
                showView.rotationY = -90f
                showView.animate().rotationY(0f).setDuration(150).start()
            }.start()

            isFlippedList[position] = !isCurrentlyFlipped
        }
    }

    fun resetFlip(position: Int) {
        isFlippedList[position] = false
        notifyItemChanged(position)
    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_card, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
    }
}


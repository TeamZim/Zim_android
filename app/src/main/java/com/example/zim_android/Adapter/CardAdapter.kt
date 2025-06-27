package com.example.zim_android.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.zim_android.R
import com.example.zim_android.ui.theme.SpaceItemDecoration

class CardAdapter(
    private val items: List<String>
) : RecyclerView.Adapter<CardAdapter.ViewHolder>() {

    private val isFlippedList = MutableList(items.size) { false }

    // 이미지 더미 데이터 (예시)
    val dummyImageList = listOf(
        R.drawable.images,
        R.drawable.images,
        R.drawable.images,
        R.drawable.images,
        R.drawable.images,
        R.drawable.images,
        R.drawable.images,
        R.drawable.images,
        R.drawable.images,
        R.drawable.images,
        R.drawable.images,
        R.drawable.images,
    )


    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val front = itemView.findViewById<View>(R.id.card_front)
        private val back = itemView.findViewById<View>(R.id.card_back)
        private val flipButton = itemView.findViewById<View>(R.id.flip_button)
        private val dotsButton = itemView.findViewById<ImageButton>(R.id.dots_button)
        private val editLayout = itemView.findViewById<View>(R.id.edit_layout)

        fun bind(position: Int) {
            val isFlipped = isFlippedList[position]

            front.visibility = if (isFlipped) View.GONE else View.VISIBLE
            back.visibility = if (isFlipped) View.VISIBLE else View.GONE

            flipButton.setOnClickListener {
                flipCard(position)
            }

            dotsButton.setOnClickListener {
                editLayout.visibility = if (editLayout.visibility == View.VISIBLE) View.GONE else View.VISIBLE
            }

            // 뒷면 리사이클러뷰 초기 설정
            val photoRecyclerView = itemView.findViewById<RecyclerView>(R.id.grid_image)
            if (photoRecyclerView.adapter == null) {
                photoRecyclerView.layoutManager = GridLayoutManager(itemView.context, 2)
                photoRecyclerView.adapter = PhotoGridAdapter(dummyImageList)
                photoRecyclerView.addItemDecoration(SpaceItemDecoration(13))
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



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_card, parent, false)
        return ViewHolder(view)


    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
    }

    fun resetFlip(position: Int) {
        isFlippedList[position] = false
        notifyItemChanged(position)
    }
}

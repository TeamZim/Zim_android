package com.example.zim_android.Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.zim_android.databinding.ItemCardBinding
import com.example.zim_android.R
import com.example.zim_android.ui.theme.SpaceItemDecoration

class CardAdapter(
    private val items: List<String>,
) : RecyclerView.Adapter<CardAdapter.ViewHolder>() {

    private val isFlippedList = MutableList(items.size) { false }

    val dummyImageList = listOf(
        R.drawable.images, R.drawable.images, R.drawable.images, R.drawable.images,
        R.drawable.images, R.drawable.images, R.drawable.images, R.drawable.images,
        R.drawable.images, R.drawable.images, R.drawable.images, R.drawable.images,
    )

    // 카드 수정 클릭용
    interface OnCardEditFieldClickListener {
        fun onTitleClick(position: Int)
        fun onDateClick(position: Int)
        fun onMemoClick(position: Int)
        fun onImageClick(position: Int)
    }

    // 뒷장의 이미지 클릭시 프래그먼트 이동용
    interface OnPhotoClickListener {
        fun onPhotoClick(cardPosition: Int, imagePosition: Int)
    }

    private var onCardEditFieldClickListener: OnCardEditFieldClickListener? = null
    private var onPhotoClickListener: OnPhotoClickListener? = null

    fun setOnCardEditFieldClickListener(listener: OnCardEditFieldClickListener) {
        onCardEditFieldClickListener = listener
    }

    interface OnEditClickListener {
        fun onEditButtonClicked(position: Int)
    }

    private var onEditClickListener: OnEditClickListener? = null

    fun setOnEditClickListener(listener: OnEditClickListener) {
        onEditClickListener = listener
    }


    fun setOnPhotoClickListener(listener: OnPhotoClickListener) {
        onPhotoClickListener = listener
    }



    // 현재 카드만 밝게
    private var focusedPosition: Int? = null

    fun setFocusCard(position: Int?) {
        focusedPosition = position
        notifyDataSetChanged()
    }

    inner class ViewHolder(private val binding: ItemCardBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(position: Int) {
            val isFlipped = isFlippedList[position]

            // 앞/뒤면 상태 설정
            binding.cardFront.visibility = if (isFlipped) android.view.View.GONE else android.view.View.VISIBLE
            binding.cardBack.visibility = if (isFlipped) android.view.View.VISIBLE else android.view.View.GONE

            // Flip 버튼
            binding.flipButton.setOnClickListener {
                flipCard(position)
            }

            // 더보기 버튼
            binding.dotsButton.setOnClickListener {
                binding.editLayout.visibility =
                    if (binding.editLayout.visibility == android.view.View.VISIBLE) android.view.View.GONE else android.view.View.VISIBLE
            }

            // 수정 버튼 클릭 시 -> 각 필드에 리스너 할당
            binding.editButton.setOnClickListener {
                onEditClickListener?.onEditButtonClicked(position)

                binding.travelTitle.setOnClickListener {
                    onCardEditFieldClickListener?.onTitleClick(position)
                }

                binding.travelDate.setOnClickListener {
                    onCardEditFieldClickListener?.onDateClick(position)
                }

                binding.travelTest.setOnClickListener {
                    onCardEditFieldClickListener?.onMemoClick(position)
                }

                binding.travelImage.setOnClickListener {
                    onCardEditFieldClickListener?.onImageClick(position)
                }
            }

            // 뒷면 이미지 그리드 설정
            val recyclerView = binding.gridImage
            if (recyclerView.adapter == null) {
                recyclerView.layoutManager = GridLayoutManager(binding.root.context, 2)
                recyclerView.adapter = PhotoGridAdapter(dummyImageList){
                    onItemClick ->
                    onPhotoClickListener?.onPhotoClick(position, 1) // 우선 이미지 순서 1로 지정해둠.
                }
                recyclerView.addItemDecoration(SpaceItemDecoration(13))
            }
        }

        private fun flipCard(position: Int) {
            val isCurrentlyFlipped = isFlippedList[position]
            val showView = if (isCurrentlyFlipped) binding.cardFront else binding.cardBack
            val hideView = if (isCurrentlyFlipped) binding.cardBack else binding.cardFront

            hideView.animate().rotationY(90f).setDuration(150).withEndAction {
                hideView.visibility = android.view.View.GONE
                showView.visibility = android.view.View.VISIBLE
                showView.rotationY = -90f
                showView.animate().rotationY(0f).setDuration(150).start()
            }.start()



            isFlippedList[position] = !isCurrentlyFlipped
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)

        holder.itemView.alpha = if (focusedPosition != null && focusedPosition != position) 0.15f else 1.0f
    }

    override fun getItemCount(): Int = items.size

    fun resetFlip(position: Int) {
        isFlippedList[position] = false
        notifyItemChanged(position)
    }
}

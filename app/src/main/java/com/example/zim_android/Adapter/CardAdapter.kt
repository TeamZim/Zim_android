package com.example.zim_android.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.zim_android.databinding.ItemCardBinding
import com.example.zim_android.data.model.TripResponse
import com.example.zim_android.data.model.TripImageResponse
import com.example.zim_android.fragment.ViewCardFragmentDirections
import com.example.zim_android.ui.theme.SpaceItemDecoration
class CardAdapter(
    private val items: List<TripResponse>,
    private val imageMap: Map<Long, List<TripImageResponse>>
) : RecyclerView.Adapter<CardAdapter.ViewHolder>() {

    private val isFlippedList = MutableList(items.size) { false }

    interface OnCardEditFieldClickListener {
        fun onTitleClick(position: Int)
        fun onDateClick(position: Int)
        fun onMemoClick(position: Int)
        fun onImageClick(position: Int)
    }

    private var onCardEditFieldClickListener: OnCardEditFieldClickListener? = null
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

    private var focusedPosition: Int? = null
    fun setFocusCard(position: Int?) {
        focusedPosition = position
        notifyDataSetChanged()
    }



    inner class ViewHolder(private val binding: ItemCardBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(trip: TripResponse, position: Int) {
            val isFlipped = isFlippedList[position]

            binding.cardFront.visibility = if (isFlipped) View.GONE else View.VISIBLE
            binding.cardBack.visibility = if (isFlipped) View.VISIBLE else View.GONE

            binding.travelTitle.text = trip.tripName
            binding.travelDate.text = "${trip.startDate} ~ ${trip.endDate}"
            binding.travelTest.text = trip.description
            binding.travelTitleBack.text = trip.tripName

            Glide.with(binding.root.context)
                .load(trip.representativeImageUrl)
                .centerCrop()
                .into(binding.travelImage)

            binding.flipButton.setOnClickListener {
                flipCard(position)
            }

            binding.dotsButton.setOnClickListener {
                binding.editLayout.visibility =
                    if (binding.editLayout.visibility == View.VISIBLE) View.GONE else View.VISIBLE
            }

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


            val recyclerView = binding.gridImage
            recyclerView.layoutManager = GridLayoutManager(binding.root.context, 2)
            val images = tripImages[trip.id] ?: emptyList()
            recyclerView.adapter = PhotoGridAdapter(images) {
                    diaryId ->
                val action = ViewCardFragmentDirections.actionViewCardFragmentToDiaryFragment(diaryId)
                binding.root.findNavController().navigate(action)
            }
            recyclerView.addItemDecoration(SpaceItemDecoration(13))
        }

        private fun flipCard(position: Int) {
            val isCurrentlyFlipped = isFlippedList[position]
            val showView = if (isCurrentlyFlipped) binding.cardFront else binding.cardBack
            val hideView = if (isCurrentlyFlipped) binding.cardBack else binding.cardFront

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
        val binding = ItemCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position], position)
        holder.itemView.alpha = if (focusedPosition != null && focusedPosition != position) 0.15f else 1.0f
    }

    override fun getItemCount(): Int = items.size

    fun resetFlip(position: Int) {
        isFlippedList[position] = false
        notifyItemChanged(position)
    }
}

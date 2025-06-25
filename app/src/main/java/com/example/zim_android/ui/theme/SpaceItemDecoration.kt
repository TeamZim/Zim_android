package com.example.zim_android.ui.theme

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class SpaceItemDecoration(private val space: Int) : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State
    ) {
        val position = parent.getChildAdapterPosition(view)
        val column = position % 2

        outRect.left = if (column == 0) 0 else space / 2
        outRect.right = if (column == 0) space / 2 else 0
        outRect.top = if (position < 2) 0 else space
    }
}

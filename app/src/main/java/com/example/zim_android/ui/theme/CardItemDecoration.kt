package com.example.zim_android.ui.theme

import android.view.View
import androidx.recyclerview.widget.RecyclerView
class CardItemDecoration(private val space: Int) : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(
        outRect: android.graphics.Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        outRect.left = space / 2
        outRect.right = space / 2
    }
}


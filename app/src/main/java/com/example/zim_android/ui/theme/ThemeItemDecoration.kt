package com.example.zim_android.ui.theme

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class ThemeItemDecoration(
    private val spanCount: Int,
    private val spacing: Int // spacing은 px 단위로 넘기세요 (예: 16dp → 16 * density)
) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State
    ) {
        val position = parent.getChildAdapterPosition(view)
        val column = position % spanCount

        if (column == 0) {
            outRect.right = spacing / 2
        } else if (column == 1) {
            outRect.left = spacing / 2
        }

        outRect.bottom = spacing
    }
}
package com.example.zim_android.ui.theme

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View


class DimOverlayView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : View(context, attrs) {

    private val dimPaint = Paint().apply {
        color = Color.parseColor("#B3000000") // 반투명 검정
    }

    private val clearPaint = Paint().apply {
        xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
    }

    private var holeRect: RectF? = null

    fun setHoleArea(rect: RectF) {
        holeRect = rect
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val sc = canvas.saveLayer(null, null)

        // 전체 어둡게
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), dimPaint)

        // 현재 카드 영역 지우기 (밝게)
        holeRect?.let {
            canvas.drawRoundRect(it, 32f, 32f, clearPaint)
        }

        canvas.restoreToCount(sc)
    }
}

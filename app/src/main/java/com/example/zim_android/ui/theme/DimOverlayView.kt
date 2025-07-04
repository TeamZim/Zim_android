package com.example.zim_android.ui.theme

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.View

class DimOverlayView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : View(context, attrs) {

    private val dimPaint = Paint().apply {
        color = Color.parseColor("#B3000000") // 어두운 dim 색
        style = Paint.Style.FILL
    }

    private val clearPaint = Paint().apply {
        xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
    }

    private var holeRect: RectF? = null

    init {
        // 구멍을 그리기 위해 반드시 SOFTWARE 레이어 사용해야 함
        setLayerType(LAYER_TYPE_SOFTWARE, null)
    }

    fun setHoleArea(hole: RectF) {
        holeRect = hole
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        Log.d("DimOverlayView", "🟥 onDraw called: $width x $height")

        // 전체 dim 배경
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), dimPaint)

        // 구멍 뚫기
        holeRect?.let { hole ->
            canvas.drawRoundRect(hole, 16f, 16f, clearPaint) // radius 조절 가능
        }
    }
}

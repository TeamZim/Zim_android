package com.example.zim_android.ui.theme

import android.content.Context
import android.util.AttributeSet
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.zim_android.R

class CommonHeaderView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private val titleText: TextView

    init {
        inflate(context, R.layout.common_header, this)
        titleText = findViewById(R.id.tv_title)

    }

    fun setTitle(title: String) {
        titleText.text = title
    }
}

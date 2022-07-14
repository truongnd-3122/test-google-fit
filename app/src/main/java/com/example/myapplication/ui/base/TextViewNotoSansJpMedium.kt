package com.example.myapplication.ui.base

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import com.example.myapplication.R

@SuppressLint("AppCompatCustomView")
class TextViewNotoSansJpMedium : TextView {

    constructor(context: Context?) : super(context) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init()
    }

    private fun init() {
        typeface = ResourcesCompat.getFont(context, R.font.noto_sans_jp_medium)
        includeFontPadding = false
    }
}
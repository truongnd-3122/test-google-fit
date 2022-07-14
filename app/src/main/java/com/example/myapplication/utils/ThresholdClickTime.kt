package com.example.myapplication.utils

import android.os.Handler
import com.example.myapplication.data.constants.Constants

class ThresholdClickTime {
    private var isBlockClick = false

    fun isBlockClick(): Boolean {
        return isBlockClick
    }

    fun setBlockClick(blockClick: Boolean) {
        isBlockClick = blockClick
        if (blockClick) {
            Handler().postDelayed(
                { isBlockClick = false },
                Constants.THRESHOLD_CLICK_TIME_400.toLong()
            )
        }
    }
}
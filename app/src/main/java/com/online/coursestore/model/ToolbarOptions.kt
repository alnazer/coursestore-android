package com.online.coursestore.model

import androidx.annotation.DrawableRes
import com.online.coursestore.R

class ToolbarOptions {

    enum class Icon(@DrawableRes val icon: Int) {
        BACK(R.drawable.ic_arrow_right_dark),
        NAV(R.drawable.ic_nav),
        CART(R.drawable.ic_cart_dark_blue),
        FILTER(R.drawable.ic_filters_black)
    }

    var startIcon: Icon? = null

    var endIcon: Icon? = null
}
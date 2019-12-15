package com.example.movies.models

import com.example.movies.common.interfaces.BaseItem
import com.example.movies.common.utils.ViewTypeConstants

class Progress: BaseItem {
    override fun getItemType(): Int {
        return ViewTypeConstants.ITEM_PROGRESS
    }
}
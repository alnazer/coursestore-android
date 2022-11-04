package com.online.coursestore.manager.listener

import com.online.coursestore.model.ChapterFileItem

interface ChapterClickListener {
    fun onClick(chapterFileItem: ChapterFileItem)
}
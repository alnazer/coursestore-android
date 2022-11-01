package com.online.coursestore.model.view

import com.online.coursestore.model.CourseCommonItem

class ContentItem {
    lateinit var title: String
    lateinit var chapterItems: MutableList<CourseCommonItem>
}
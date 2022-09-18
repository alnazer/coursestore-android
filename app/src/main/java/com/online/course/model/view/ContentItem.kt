package com.online.course.model.view

import com.online.course.model.CourseCommonItem

class ContentItem {
    lateinit var title: String
    lateinit var chapterItems: MutableList<CourseCommonItem>
}
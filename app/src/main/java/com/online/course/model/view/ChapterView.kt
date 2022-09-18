package com.online.course.model.view

import com.online.course.R
import com.online.course.model.Category
import com.online.course.model.CourseCommonItem
import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup


class ChapterView(title: String, val description: String, items: List<CourseCommonItem>) :
    ExpandableGroup<CourseCommonItem>(title, items)
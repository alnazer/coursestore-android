package com.online.coursestore.model.view

import com.online.coursestore.model.CourseCommonItem
import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup


class ChapterView(title: String, val description: String, items: List<CourseCommonItem>) :
    ExpandableGroup<CourseCommonItem>(title, items)
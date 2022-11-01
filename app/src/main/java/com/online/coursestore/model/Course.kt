package com.online.coursestore.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlin.math.roundToInt

class Course() : ItemPicker, Parcelable {

    enum class Type(val value: String) {
        WEBINAR("webinar"),
        COURSE("coursestore"),
        TEXT_COURSE("text_lesson")
    }

    enum class WebinarStatus(val value: String) {
        FINISHED("finished"),
        NOT_CONDUCTED("not_conducted"),
        IN_PROGRESS("in_progress")
    }

    @SerializedName("id")
    var id = 0

    @SerializedName("teacher_id")
    var teacherId = 0

    @SerializedName("creator_id")
    var creatorId = 0

    @SerializedName("category_id")
    var categoryId = 0

    @SerializedName("type")
    var type: String = ""

    @SerializedName("private")
    var private = 0

    @SerializedName("title")
    lateinit var title: String

    @SerializedName("start_date")
    var startDate = 0L

    @SerializedName("duration")
    var duration = 0

    @SerializedName("image")
    var img: String? = null

//    @SerializedName("video_demo")
//    var video: VideoCipherData? = null

    @SerializedName("video_demo")
    var video: String? = null

    @SerializedName("capacity")
    var capacity = 0

    @SerializedName("price")
    var price = 0.0

    @SerializedName("best_ticket_price")
    var priceWithDiscount = 0.0

    @SerializedName("rate")
    var rating = 0f

    @SerializedName("rate_type")
    var rateType: Review? = null

    @SerializedName("description")
    var description: String? = null

    @SerializedName("created_at")
    var createdAt = 0L

    @SerializedName("updated_at")
    var updatedAt = 0L

    @SerializedName("discount_percent")
    var actualDiscount = 0f

    val discount get() = actualDiscount.roundToInt()

    @SerializedName("can_buy_with_points")
    var canBuyWithPoints = false

    @SerializedName("progress_percent")
    var progress: Float? = null

    @SerializedName("teacher")
    lateinit var teacher: User

    @SerializedName("category")
    var category: String? = null

    @SerializedName("auth_has_bought")
    var hasUserBought = false

    @SerializedName("live_webinar_status")
    var liveCourseStatus: String? = null

    @SerializedName("is_favorite")
    var isFavorite = false

    @SerializedName("isDownloadable")
    var isDownloadable = false

    @SerializedName("supported")
    var supported = false

    @SerializedName("link")
    var link: String? = null

    @SerializedName("reviews")
    var reviews: List<Review> = emptyList()

    @SerializedName("comments")
    var comments: List<Comment> = emptyList()

    @SerializedName("tickets")
    var pricingPlans: List<PricingPlan> = emptyList()

    @SerializedName("prerequisites")
    var prerequisites: List<PrerequisiteCourse> = emptyList()

    @SerializedName("quizzes")
    var quizzes: List<Quiz> = emptyList()

    @SerializedName("certificate")
    var certificates: List<Quiz> = emptyList()

    @SerializedName("reviews_count")
    var reviewsCount = 0

    @SerializedName("students_count")
    var studentsCount = 0

    @SerializedName("active_special_offer")
    var activeSpecialOffer: SpecialOffer? = null

    @SerializedName("subscribe")
    var isSubscribable = false

    @SerializedName("auth_has_subscription")
    var authHasSubscription = false

    @SerializedName("sessiones_count")
    var sessionesCount = 0

    @SerializedName("sales")
    var sales: Sales? = null

    @SerializedName("text_lesson_chapters")
    var textLessonChapters: List<TextChapter> = emptyList()

    @SerializedName("session_chapters")
    var sessionChapters: List<SessionChapter> = emptyList()

    @SerializedName("files_chapters")
    var filesChapters: List<FileChapter> = emptyList()

    @SerializedName("faqs")
    var faqs: List<Faq> = emptyList()

    @SerializedName("points")
    var points: Int? = null

    fun isLive(): Boolean {
        return type == "webinar"
    }

    constructor(parcel: Parcel) : this() {
        id = parcel.readInt()
        teacherId = parcel.readInt()
        creatorId = parcel.readInt()
        categoryId = parcel.readInt()
        type = parcel.readString()!!
        private = parcel.readInt()
        title = parcel.readString()!!
        startDate = parcel.readLong()
        duration = parcel.readInt()
        img = parcel.readString()
//        video = parcel.readParcelable(VideoCipherData::class.java.classLoader)
        video = parcel.readString()
        capacity = parcel.readInt()
        price = parcel.readDouble()
        rating = parcel.readFloat()
        description = parcel.readString()
        createdAt = parcel.readLong()
        updatedAt = parcel.readLong()
        priceWithDiscount = parcel.readDouble()
        progress = parcel.readFloat()
        link = parcel.readString()
        sales = parcel.readParcelable(Sales::class.java.classLoader)
        hasUserBought = parcel.readByte() != 0.toByte()
        isFavorite = parcel.readByte() != 0.toByte()
        isDownloadable = parcel.readByte() != 0.toByte()
        supported = parcel.readByte() != 0.toByte()
        isSubscribable = parcel.readByte() != 0.toByte()
        authHasSubscription = parcel.readByte() != 0.toByte()
        reviews = parcel.createTypedArrayList(Review)!!
        comments = parcel.createTypedArrayList(Comment)!!
        pricingPlans = parcel.createTypedArrayList(PricingPlan)!!
        prerequisites = parcel.createTypedArrayList(PrerequisiteCourse)!!
        quizzes = parcel.createTypedArrayList(Quiz)!!
        certificates = parcel.createTypedArrayList(Quiz)!!
        reviewsCount = parcel.readInt()
        studentsCount = parcel.readInt()
        activeSpecialOffer = parcel.readParcelable(SpecialOffer::class.java.classLoader)
        rateType = parcel.readParcelable(Review::class.java.classLoader)
        sessionesCount = parcel.readInt()
        liveCourseStatus = parcel.readString()
        textLessonChapters = parcel.createTypedArrayList(TextChapter)!!
        sessionChapters = parcel.createTypedArrayList(SessionChapter)!!
        filesChapters = parcel.createTypedArrayList(FileChapter)!!
        faqs = parcel.createTypedArrayList(Faq)!!
        points = parcel.readInt()
        canBuyWithPoints = parcel.readByte() != 0.toByte()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeInt(teacherId)
        parcel.writeInt(creatorId)
        parcel.writeInt(categoryId)
        parcel.writeString(type)
        parcel.writeInt(private)
        parcel.writeString(title)
        parcel.writeLong(startDate)
        parcel.writeParcelable(sales, flags)
        parcel.writeInt(duration)
        parcel.writeString(img)
//        parcel.writeParcelable(video, flags)
        parcel.writeString(video)
        parcel.writeInt(capacity)
        parcel.writeDouble(price)
        parcel.writeFloat(rating)
        parcel.writeString(description)
        parcel.writeLong(createdAt)
        parcel.writeLong(updatedAt)
        parcel.writeDouble(priceWithDiscount)
        parcel.writeString(link)
        parcel.writeByte(if (hasUserBought) 1 else 0)
        parcel.writeByte(if (isFavorite) 1 else 0)
        parcel.writeByte(if (isDownloadable) 1 else 0)
        parcel.writeByte(if (supported) 1 else 0)
        parcel.writeByte(if (isSubscribable) 1 else 0)
        parcel.writeByte(if (authHasSubscription) 1 else 0)
        parcel.writeTypedList(reviews)
        parcel.writeTypedList(comments)
        parcel.writeTypedList(pricingPlans)
        parcel.writeTypedList(textLessonChapters)
        parcel.writeTypedList(sessionChapters)
        parcel.writeTypedList(filesChapters)
        parcel.writeInt(reviewsCount)
        parcel.writeTypedList(prerequisites)
        parcel.writeTypedList(quizzes)
        parcel.writeTypedList(certificates)
        parcel.writeTypedList(faqs)
        parcel.writeInt(studentsCount)
        parcel.writeString(liveCourseStatus)
        parcel.writeParcelable(activeSpecialOffer, flags)
        parcel.writeParcelable(rateType, flags)
        parcel.writeInt(sessionesCount)
        progress?.let { parcel.writeFloat(it) }
        points?.let { parcel.writeInt(it) }
        parcel.writeByte(if (canBuyWithPoints) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Course> {
        override fun createFromParcel(parcel: Parcel): Course {
            return Course(parcel)
        }

        override fun newArray(size: Int): Array<Course?> {
            return arrayOfNulls(size)
        }
    }

    override fun itemTitle(): String {
        return title
    }
}
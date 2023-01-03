package com.online.coursestore.presenter

import com.online.coursestore.manager.listener.ItemCallback
import com.online.coursestore.manager.net.OnDownloadProgressListener
import com.online.coursestore.model.*
import com.online.coursestore.ui.frag.UsersOrganizationsFrag
import java.io.File

class Presenter {

    interface ThirdPartyPresenter {
        fun facebookSignInUp(thirdPartyLogin: ThirdPartyLogin)
        fun googleSignInUp(thirdPartyLogin: ThirdPartyLogin)
    }

    interface SignInPresenter : ThirdPartyPresenter {
        fun login(login: Login)
    }

    interface SignUpPresenter : ThirdPartyPresenter {
        fun signUp(signUp: EmailSignUp)
        fun signUp(signUp: MobileSignUp)
    }

    interface SplashScreenPresenter {
        fun getAppConfig()
    }

    interface VerifyAccountPresenter {
        fun signUp(signUp: EmailSignUp)
        fun signUp(signUp: MobileSignUp)
        fun verifyAccount(accountVerification: AccountVerification)
    }

    interface UserRegistrationPresenter {
        fun register(user: User)
    }

    interface HomePresenter {
        fun getFeaturedCourses()
        fun getSoonCourses()
        fun getNewestCourses(map: HashMap<String, String>)
        fun getAllCourses(map: HashMap<String, String>)
        fun getBestRatedCourses(map: HashMap<String, String>)
        fun getBestSellingCourses(map: HashMap<String, String>)
        fun getDiscountedCourses(map: HashMap<String, String>)
        fun getFreeCourses(map: HashMap<String, String>)
    }

    interface SearchResultPresenter {
        fun search(s: String)
    }

    interface BaseCategoriesPresenter {
        fun getCategories()
    }

    interface CategoriesPresenter : BaseCategoriesPresenter {
        fun getTrendingCategories()
    }

    interface CategoryPresenter {
        fun getCatFiltersAndCourses(categoryId: Int)
        fun getCatFeaturedCourses(categoryId: Int)
        fun getCatFeaturedCourses(
            categoryId: Int,
            selectedOptions: ArrayList<KeyValuePair>?,
            selectedFilters: ArrayList<KeyValuePair>?
        )
    }

    interface BlogPresenter {
        fun getBlogs()
        fun getBlogs(catId: Int)
    }

    interface ReportReplyCommentPresenter {
        fun comment(comment: Comment)
        fun reply(comment: Comment)
        fun reportComment(comment: Comment)
        fun editComment(comment: Comment)
        fun reportCourse(comment: Comment)
        fun getReasons()
    }

    interface ProvidersPresenter {
        fun getProviders(type: UsersOrganizationsFrag.ProviderType, map: List<KeyValuePair>?)
    }

    interface ProvidersFiltersPresenter {
        fun getCategories()
    }

    interface CommentsPresenter {
        fun getComments(callback: ItemCallback<Comments>)
    }

    interface CommentDetailsPresenter {
        fun removeComment(commentId: Int)
    }

    interface MeetingsPresenter {
        fun getMeetings()
    }

    interface SettingsGeneralPresenter {
        fun changeProfileSettings(changeSettings: UserChangeSettings)
    }

    interface SettingsSecurityPresenter {
        fun changePassword(changePassword: ChangePassword)
    }

    interface MeetingDetailsMorePresenter {
        fun finishMeeting(meetingId: Int)
    }

    interface MeetingJoinDetailsPresenter {
        fun createJoin(reserveMeeting: ReserveMeeting)
    }

    interface CertificatesPresenter {
        fun getAchievementCertificates()
        fun getClassCertificates()
    }

    interface ProgressiveLoadingPresenter {
        fun downloadFile(
            fileDir: String?,
            fileUrl: String,
            downloadListener: OnDownloadProgressListener,
            toDownloads: Boolean,
            fileNameFromHeader: Boolean
        )
    }

    interface ClassesPresenter {
        fun getCourses()
    }

    interface SearchPresenter {
        fun getBestRatedCourses()
    }

    interface ProfilePresenter {
        fun getUserProfile(userId: Int)
    }

    interface ProfileAboutPresenter {
        fun follow(userId: Int, follow: Follow)
    }

    interface ReserveMeetingDialogPresenter {
        fun getAvailableMeetingTimes(userId: Int, date: String)
    }

    interface FinalizeReserveMeetingPresenter {
        fun reserveMeeting(reserveMeeting: ReserveTimeMeeting)
    }

    interface NotifPresenter {
        fun getNotifs()
    }

    interface SupportPresenter {
        fun getTickets()
        fun getClassSupport()
        fun getMyClassSupport()
    }

    interface NewTicketPresenter {
        fun getDepartments()
        fun addTicket(ticket: Ticket, file: File?)
        fun addTicketChat(conversation: TicketConversation, file: File?)
        fun getCourses()
    }

    interface TicketConversationPresenter {
        fun closeTicket(ticketId: Int)
        fun getFileSize(url: String, sizeCallback: ItemCallback<Long>)
    }

    interface QuzziesPresenter {
        fun getMyResults()
        fun getNotParticipated()
        fun getQuizList()
        fun getStudentResults()
    }

    interface QuizOverviewPresenter {
        fun startQuiz(id: Int)
    }

    interface QuizPresenter {
        fun storeResult(quizId: Int, answer: QuizAnswer)
    }

    interface QuizResultInfoPresenter {
        fun startQuiz(id: Int)
        fun getQuizResult(quizId: Int)
    }

    interface QuizReviewPresenter {
        fun storeReviewResult(resultId: Int, review: List<QuizAnswerItem>)
    }

    interface FinancialSummaryPresenter {
        fun getSummary()
        fun chargeAccount(paymentRequest: PaymentRequest)
    }

    interface CommonApiPresenter {
        fun getQuickInfo(callback: ItemCallback<QuickInfo>)
        fun addToCart(addToCart: AddToCart, callback: ItemCallback<BaseResponse>)
        fun getUserInfo(userId: Int, callback: ItemCallback<UserProfile>)
        fun getCourseDetails(courseId: Int, callback: ItemCallback<Course>)
    }

    interface BanksInfoPresenter {
        fun getBanksInfo()
    }

    interface FinancialOfflinePaymentsPresenter {
        fun getOfflinePayments()
    }

    interface FinancialPayoutPresenter {
        fun getPayouts()
    }

    interface PayoutRequestPresenter {
        fun requestPayout()
    }

    interface FinancialSalesPresenter {
        fun getSales()
    }

    interface OfflinePaymentDialogPresenter {
        fun addOfflinePayment(offlinePayment: OfflinePayment)
    }

    interface FavoritesPresenter {
        fun getFavorites()
        fun removeFromFavorite(courseId: Int, adapterPosition: Int)
    }

    interface MyClassesPresenter {
        fun getMyClasses()
        fun getPurchased()
        fun getOrganizations()
    }

    interface SubscriptionPresenter {
        fun getSubscriptions()
        fun checkoutSubscription(subscriptionItem: SubscriptionItem)
    }

    interface CartPresenter {
        fun getCart()
        fun removeFromCart(cartItemId: Int, position: Int)
//        fun checkout(coupon: Coupon?)
        fun checkout(checkout: Checkout)
    }

    interface PaymentPresenter {
        fun checkout(checkout: Checkout)
        fun verifyPayment(verifyPayment: VerifyPayment)
    }

    interface CouponPresenter {
        fun validateCoupon(coupon: Coupon)
    }

    interface NewMessagePresenter {
        fun addMessage(userId: Int, message: Message)
    }

    interface CourseDetailsMorePresenter {
        fun addToFavorite(itemId: Int)
    }

    interface CourseReviewPresenter {
        fun addReview(review: Review)

    }

    interface CourseDetailsPresenter {
        fun subscribe(addToCart: AddToCart)
        fun addCourseToUserCourse(courseId: Int)
    }

    interface NotifDialogPresenter {
        fun setStatusToSeen(notifId: Int)
    }

    interface CertificateDetailsPresenter {
        fun getStudents()
    }

    interface ChargeAccountPaymentPresenter {
        fun requestPayment(paymentRequest: PaymentRequest)
        fun chargeAccount(paymentRequest: PaymentRequest)
        fun requestPaymentFromCharge(paymentRequest: PaymentRequest)
    }

    interface ChargeAccountPresenter {
        fun chargeAccount(paymentRequest: PaymentRequest)
    }

    interface SettingsPresenter {
        fun uploadPhoto(path: String)
    }

    interface SettingsFinancialPresenter {
        fun uploadFinancialSettings(financialSettings: FinancialSettings)
        fun getAccountTypes()
        fun uploadFinancialSettingsFiles(identityFile: File, certFile: File)
    }

    interface CourseChapterItemPresenter {
        fun changeItemStatus(chapterItemMark: ChapterItemMark)
        fun downloadFile(file: ChapterFileItem, progressListener: OnDownloadProgressListener)
        fun cancelDownload(fileId: Int)
    }

    interface ForgotPasswordPresenter {
        fun sendChangePasswordLink(forgetPassword: ForgetPassword)
    }

    interface BlogCategoriesPresenter {
        fun getBlogCategories()
    }

    interface LogoutPresenter {
        fun logout()
    }

    interface RewardPointsPresenter {
        fun getPoints()
    }

    interface SaasPackagePresenter {
        fun getSaasPackages()
        fun checkoutSubscription(saasPackageItem: SaasPackageItem)
    }

    interface RewardCoursesPresenter {
        fun getRewardCourses(categories: List<KeyValuePair>?, options: List<KeyValuePair>?)
    }

    interface PricingPlansPresenter {
        fun purchaseWithPoints(courseId: Int)
    }

    interface SettingsLocalizationPresenter {
        fun getTimeZones()
        fun getCountries()
        fun getProvinces(countryId: Int)
        fun getCities(provinceId: Int)
        fun getDistricts(cityId: Int)
        fun changeProfileSettings(changeSettings: UserChangeLocalization)
    }
}
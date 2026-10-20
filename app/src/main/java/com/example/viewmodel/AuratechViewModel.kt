package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AuratechDataStore
import com.example.data.mock.MockData
import com.example.data.models.*
import com.example.data.repository.AuratechRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class AuratechViewModel(application: Application) : AndroidViewModel(application) {

    val dataStore = AuratechDataStore(application)
    val repository = AuratechRepository()

    // Preferences
    val language: StateFlow<String> = dataStore.languageFlow.stateIn(
        viewModelScope, SharingStarted.Eagerly, "ar"
    )

    val themeMode: StateFlow<String> = dataStore.themeFlow.stateIn(
        viewModelScope, SharingStarted.Eagerly, "system"
    )

    val onboardingCompleted: StateFlow<Boolean> = dataStore.onboardingCompletedFlow.stateIn(
        viewModelScope, SharingStarted.Eagerly, false
    )

    val currentUser = repository.currentUser
    val cartItems = repository.cartItems
    val favoriteDoctorIds = repository.favoriteDoctorIds
    val favoriteClinicIds = repository.favoriteClinicIds
    val favoritePharmacyIds = repository.favoritePharmacyIds
    val appointments = repository.appointments
    val orders = repository.orders
    val familyMembers = repository.familyMembers
    val addresses = repository.addresses
    val savedTemplates = repository.savedTemplates
    val notifications = repository.notifications
    val refunds = repository.refunds
    val chatMessages = repository.chatMessages

    // Notification Prefs
    val notifReminders = dataStore.notifRemindersFlow.stateIn(viewModelScope, SharingStarted.Eagerly, true)
    val notifOrders = dataStore.notifOrdersFlow.stateIn(viewModelScope, SharingStarted.Eagerly, true)
    val notifPromos = dataStore.notifPromosFlow.stateIn(viewModelScope, SharingStarted.Eagerly, true)
    val notifMessages = dataStore.notifMessagesFlow.stateIn(viewModelScope, SharingStarted.Eagerly, true)
    val notifAlerts = dataStore.notifAlertsFlow.stateIn(viewModelScope, SharingStarted.Eagerly, true)

    // Search and filter states
    private val _homeSearchQuery = MutableStateFlow("")
    val homeSearchQuery = _homeSearchQuery.asStateFlow()

    private val _drugSearchQuery = MutableStateFlow("")
    val drugSearchQuery = _drugSearchQuery.asStateFlow()

    private val _drugFilter = MutableStateFlow("all") // all, delivers, nearby, top_rated
    val drugFilter = _drugFilter.asStateFlow()

    private val _selectedSpecialtyId = MutableStateFlow<String?>(null)
    val selectedSpecialtyId = _selectedSpecialtyId.asStateFlow()

    // Snackbars & messages
    private val _snackbarMessage = MutableSharedFlow<String>()
    val snackbarMessage = _snackbarMessage.asSharedFlow()

    fun showMessage(msg: String) {
        viewModelScope.launch { _snackbarMessage.emit(msg) }
    }

    fun setLanguage(lang: String) {
        viewModelScope.launch { dataStore.setLanguage(lang) }
    }

    fun setTheme(theme: String) {
        viewModelScope.launch { dataStore.setTheme(theme) }
    }

    fun setOnboardingCompleted(completed: Boolean) {
        viewModelScope.launch { dataStore.setOnboardingCompleted(completed) }
    }

    fun updateNotifPref(key: String, enabled: Boolean) {
        viewModelScope.launch { dataStore.updateNotificationPref(key, enabled) }
    }

    fun login(phone: String, name: String = "أحمد محمد العولقي") {
        repository.login(name, phone)
        viewModelScope.launch { dataStore.setLoggedIn(true, name, phone) }
    }

    fun logout() {
        repository.logout()
        viewModelScope.launch { dataStore.setLoggedIn(false) }
    }

    fun setHomeSearchQuery(query: String) {
        _homeSearchQuery.value = query
    }

    fun setDrugSearchQuery(query: String) {
        _drugSearchQuery.value = query
    }

    fun setDrugFilter(filter: String) {
        _drugFilter.value = filter
    }

    fun selectSpecialty(specialtyId: String?) {
        _selectedSpecialtyId.value = specialtyId
    }

    // Filtered drugs
    val filteredDrugs: StateFlow<List<Drug>> = combine(
        drugSearchQuery,
        drugFilter
    ) { query, filter ->
        var list = MockData.drugs
        if (query.isNotBlank()) {
            list = list.filter {
                it.name.contains(query, ignoreCase = true) ||
                it.pharmacyNameAr.contains(query, ignoreCase = true) ||
                it.category.contains(query, ignoreCase = true)
            }
        }
        when (filter) {
            "delivers" -> list.filter { it.delivers }
            "nearby" -> list.sortedBy { it.distanceKm }
            "top_rated" -> list.sortedByDescending { it.rating }
            else -> list
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, MockData.drugs)

    // Actions
    fun addToCart(drug: Drug, quantity: Int = 1) {
        repository.addToCart(drug, quantity)
        showMessage("تمت إضافة ${drug.name} إلى السلة")
    }

    fun updateCartQuantity(drugId: String, delta: Int) {
        repository.updateCartQuantity(drugId, delta)
    }

    fun clearCart() {
        repository.clearCart()
    }

    fun toggleFavoriteDoctor(id: String) {
        repository.toggleFavoriteDoctor(id)
    }

    fun toggleFavoriteClinic(id: String) {
        repository.toggleFavoriteClinic(id)
    }

    fun toggleFavoritePharmacy(id: String) {
        repository.toggleFavoritePharmacy(id)
    }

    fun bookAppointment(doctor: Doctor, date: String, slot: String): Appointment {
        val apt = repository.bookAppointment(doctor, date, slot)
        showMessage("تم تأكيد حجز الموعد بنجاح")
        return apt
    }

    fun cancelAppointment(id: String) {
        repository.cancelAppointment(id)
        showMessage("تم إلغاء الموعد بنجاح")
    }

    fun rateAppointment(id: String, rating: Float) {
        repository.rateAppointment(id, rating)
        showMessage("شكراً لك على تقييم الزيارة")
    }

    fun createOrder(
        items: List<CartItem>,
        isDelivery: Boolean,
        address: String,
        pharmacyNameAr: String,
        pharmacyNameEn: String,
        receiptNumber: String
    ): Order {
        val order = repository.createOrder(items, isDelivery, address, pharmacyNameAr, pharmacyNameEn, receiptNumber)
        showMessage("تم إرسال الطلب للصيدلية بنجاح")
        return order
    }

    fun saveOrderTemplate(name: String, items: List<CartItem>, total: Double) {
        repository.addSavedTemplate(name, items.map { it.drug.name }, total)
        showMessage("تم حفظ القالب بنجاح")
    }

    fun deleteSavedTemplate(id: String) {
        repository.deleteSavedTemplate(id)
    }

    fun addFamilyMember(name: String, relationAr: String, relationEn: String, birthDate: String, isMale: Boolean) {
        repository.addFamilyMember(name, relationAr, relationEn, birthDate, isMale)
        showMessage("تمت إضافة فرد العائلة بنجاح")
    }

    fun deleteFamilyMember(id: String) {
        repository.deleteFamilyMember(id)
    }

    fun addAddress(titleAr: String, titleEn: String, detailsAr: String, detailsEn: String, makeDefault: Boolean) {
        repository.addAddress(titleAr, titleEn, detailsAr, detailsEn, makeDefault)
        showMessage("تم حفظ العنوان بنجاح")
    }

    fun setDefaultAddress(id: String) {
        repository.setDefaultAddress(id)
    }

    fun markNotificationRead(id: String) {
        repository.markNotificationAsRead(id)
    }

    fun markAllNotificationsRead() {
        repository.markAllNotificationsAsRead()
        showMessage("تم تحديد جميع الإشعارات كمقروءة")
    }

    fun deleteNotification(id: String) {
        repository.deleteNotification(id)
    }

    fun sendChatMessage(text: String) {
        repository.sendChatMessage(text)
    }

    fun submitReportProblem(topic: String, description: String) {
        showMessage("تم استلام البلاغ رقم #${System.currentTimeMillis().toString().takeLast(5)}، شكراً لمساعدتنا في التحسين")
    }

    fun submitJoinUsRequest(facilityName: String) {
        showMessage("تم استلام طلب انضمام $facilityName وسيتواصل معكم فريق العلاقات الطبية")
    }
}

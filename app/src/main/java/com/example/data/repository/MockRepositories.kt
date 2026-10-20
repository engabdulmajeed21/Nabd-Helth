package com.example.data.repository

import com.example.data.mock.MockData
import com.example.data.models.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.*

class AuratechRepository {

    // Auth State
    private val _currentUser = MutableStateFlow(User(isLoggedIn = false))
    val currentUser: StateFlow<User> = _currentUser.asStateFlow()

    fun login(name: String = "أحمد محمد العولقي", phone: String = "+967 777 123 456") {
        _currentUser.value = User(
            id = "u_1",
            name = name,
            phone = phone,
            email = "ahmed.patient@auratech.med",
            isLoggedIn = true
        )
    }

    fun logout() {
        _currentUser.value = User(isLoggedIn = false)
    }

    // Cart
    private val _cartItems = MutableStateFlow<List<CartItem>>(
        listOf(
            CartItem(MockData.drugs[0], 2), // Panadol Extra
            CartItem(MockData.drugs[2], 1)  // Voltaren Emulgel
        )
    )
    val cartItems: StateFlow<List<CartItem>> = _cartItems.asStateFlow()

    fun addToCart(drug: Drug, quantity: Int = 1) {
        val current = _cartItems.value.toMutableList()
        val existingIndex = current.indexOfFirst { it.drug.id == drug.id }
        if (existingIndex >= 0) {
            val existing = current[existingIndex]
            current[existingIndex] = existing.copy(quantity = existing.quantity + quantity)
        } else {
            current.add(CartItem(drug, quantity))
        }
        _cartItems.value = current
    }

    fun updateCartQuantity(drugId: String, delta: Int) {
        val current = _cartItems.value.toMutableList()
        val index = current.indexOfFirst { it.drug.id == drugId }
        if (index >= 0) {
            val item = current[index]
            val newQty = item.quantity + delta
            if (newQty <= 0) {
                current.removeAt(index)
            } else {
                current[index] = item.copy(quantity = newQty)
            }
            _cartItems.value = current
        }
    }

    fun clearCart() {
        _cartItems.value = emptyList()
    }

    // Favorites
    private val _favoriteDoctorIds = MutableStateFlow(setOf("doc_1", "doc_2", "doc_6"))
    val favoriteDoctorIds: StateFlow<Set<String>> = _favoriteDoctorIds.asStateFlow()

    private val _favoriteClinicIds = MutableStateFlow(setOf("clinic_1", "clinic_3"))
    val favoriteClinicIds: StateFlow<Set<String>> = _favoriteClinicIds.asStateFlow()

    private val _favoritePharmacyIds = MutableStateFlow(setOf("pharm_1", "pharm_3"))
    val favoritePharmacyIds: StateFlow<Set<String>> = _favoritePharmacyIds.asStateFlow()

    fun toggleFavoriteDoctor(id: String) {
        val current = _favoriteDoctorIds.value.toMutableSet()
        if (current.contains(id)) current.remove(id) else current.add(id)
        _favoriteDoctorIds.value = current
    }

    fun toggleFavoriteClinic(id: String) {
        val current = _favoriteClinicIds.value.toMutableSet()
        if (current.contains(id)) current.remove(id) else current.add(id)
        _favoriteClinicIds.value = current
    }

    fun toggleFavoritePharmacy(id: String) {
        val current = _favoritePharmacyIds.value.toMutableSet()
        if (current.contains(id)) current.remove(id) else current.add(id)
        _favoritePharmacyIds.value = current
    }

    // Appointments
    private val _appointments = MutableStateFlow(MockData.appointments)
    val appointments: StateFlow<List<Appointment>> = _appointments.asStateFlow()

    fun bookAppointment(
        doctor: Doctor,
        date: String,
        timeSlot: String
    ): Appointment {
        val newAptNumber = (5682 + _appointments.value.size).toString()
        val nextQueue = 13 + (_appointments.value.count { it.date == date })
        val qrContent = "AURATECH|APT:$newAptNumber|DOC:${doctor.nameEn}|DATE:$date|SLOT:$timeSlot|Q:$nextQueue"

        val newAppointment = Appointment(
            id = "apt_${System.currentTimeMillis()}",
            appointmentNumber = newAptNumber,
            doctorNameAr = doctor.nameAr,
            doctorNameEn = doctor.nameEn,
            specialtyAr = doctor.specialtyAr,
            specialtyEn = doctor.specialtyEn,
            clinicNameAr = doctor.clinicNameAr,
            clinicNameEn = doctor.clinicNameEn,
            date = date,
            timeSlot = timeSlot,
            fee = doctor.consultationFee,
            queueNumber = nextQueue,
            status = AppointmentStatus.CONFIRMED,
            qrCodeData = qrContent
        )
        _appointments.value = listOf(newAppointment) + _appointments.value
        return newAppointment
    }

    fun cancelAppointment(aptId: String, reason: String = "طلب المريض") {
        _appointments.value = _appointments.value.map { apt ->
            if (apt.id == aptId) {
                apt.copy(status = AppointmentStatus.CANCELED, cancelReason = reason)
            } else apt
        }
    }

    fun rateAppointment(aptId: String, rating: Float) {
        _appointments.value = _appointments.value.map { apt ->
            if (apt.id == aptId) apt.copy(userRating = rating) else apt
        }
    }

    // Orders
    private val _orders = MutableStateFlow(MockData.orders)
    val orders: StateFlow<List<Order>> = _orders.asStateFlow()

    fun createOrder(
        items: List<CartItem>,
        isDelivery: Boolean,
        address: String,
        pharmacyNameAr: String,
        pharmacyNameEn: String,
        receiptNumber: String
    ): Order {
        val nextOrderNum = (1249 + _orders.value.size).toString()
        val total = items.sumOf { it.drug.price * it.quantity } + (if (isDelivery) 1000.0 else 0.0)
        val newOrder = Order(
            id = "ord_${System.currentTimeMillis()}",
            orderNumber = nextOrderNum,
            pharmacyNameAr = pharmacyNameAr,
            pharmacyNameEn = pharmacyNameEn,
            items = items,
            totalAmount = total,
            status = OrderStatus.PREPARING,
            orderTime = "الآن",
            estimatedMinutes = 25,
            isDelivery = isDelivery,
            address = address,
            receiptNumber = receiptNumber
        )
        _orders.value = listOf(newOrder) + _orders.value
        clearCart()
        return newOrder
    }

    // Family Members
    private val _familyMembers = MutableStateFlow(MockData.familyMembers)
    val familyMembers: StateFlow<List<Dependent>> = _familyMembers.asStateFlow()

    fun addFamilyMember(name: String, relationAr: String, relationEn: String, birthDate: String, isMale: Boolean) {
        val newMember = Dependent(
            id = "dep_${System.currentTimeMillis()}",
            name = name,
            relationshipAr = relationAr,
            relationshipEn = relationEn,
            birthDate = birthDate,
            isMale = isMale
        )
        _familyMembers.value = _familyMembers.value + newMember
    }

    fun deleteFamilyMember(id: String) {
        _familyMembers.value = _familyMembers.value.filter { it.id != id }
    }

    // Addresses
    private val _addresses = MutableStateFlow(MockData.addresses)
    val addresses: StateFlow<List<Address>> = _addresses.asStateFlow()

    fun addAddress(titleAr: String, titleEn: String, detailsAr: String, detailsEn: String, makeDefault: Boolean) {
        val newAddr = Address(
            id = "addr_${System.currentTimeMillis()}",
            titleAr = titleAr,
            titleEn = titleEn,
            detailsAr = detailsAr,
            detailsEn = detailsEn,
            isDefault = makeDefault
        )
        val current = if (makeDefault) {
            _addresses.value.map { it.copy(isDefault = false) } + newAddr
        } else {
            _addresses.value + newAddr
        }
        _addresses.value = current
    }

    fun setDefaultAddress(id: String) {
        _addresses.value = _addresses.value.map { it.copy(isDefault = it.id == id) }
    }

    // Saved Templates
    private val _savedTemplates = MutableStateFlow(MockData.savedTemplates)
    val savedTemplates: StateFlow<List<SavedTemplate>> = _savedTemplates.asStateFlow()

    fun addSavedTemplate(name: String, drugNames: List<String>, total: Double) {
        val newTemplate = SavedTemplate(
            id = "tpl_${System.currentTimeMillis()}",
            name = name,
            drugNames = drugNames,
            lastOrderDate = "اليوم",
            totalAmount = total
        )
        _savedTemplates.value = listOf(newTemplate) + _savedTemplates.value
    }

    fun deleteSavedTemplate(id: String) {
        _savedTemplates.value = _savedTemplates.value.filter { it.id != id }
    }

    // Notifications
    private val _notifications = MutableStateFlow(MockData.notifications)
    val notifications: StateFlow<List<NotificationItem>> = _notifications.asStateFlow()

    fun markNotificationAsRead(id: String) {
        _notifications.value = _notifications.value.map {
            if (it.id == id) it.copy(isRead = true) else it
        }
    }

    fun markAllNotificationsAsRead() {
        _notifications.value = _notifications.value.map { it.copy(isRead = true) }
    }

    fun deleteNotification(id: String) {
        _notifications.value = _notifications.value.filter { it.id != id }
    }

    // Refunds
    private val _refunds = MutableStateFlow(MockData.refunds)
    val refunds: StateFlow<List<RefundItem>> = _refunds.asStateFlow()

    // Support Chat
    private val _chatMessages = MutableStateFlow(
        listOf(
            ChatMessage("m1", "أوراتك - الدعم الطبي", false, "مرحباً بك في خدمة عملاء أوراتك! كيف يمكننا مساعدتك اليوم؟", "10:00 ص"),
            ChatMessage("m2", "أحمد العولقي", true, "أهلاً، أود الاستفسار عن أقرب موعد متاح لدى د. أحمد الصنعاني للقلب.", "10:02 ص"),
            ChatMessage("m3", "أوراتك - الدعم الطبي", false, "د. أحمد الصنعاني متاح اليوم في برج النور ابتداءً من 9:30 ص، ويمكنك حجز دورك فوراً من التطبيق.", "10:03 ص")
        )
    )
    val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages.asStateFlow()

    fun sendChatMessage(text: String) {
        val userMsg = ChatMessage(
            id = "msg_${System.currentTimeMillis()}",
            senderName = "المريض",
            isUser = true,
            messageText = text,
            timestamp = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date())
        )
        _chatMessages.value = _chatMessages.value + userMsg

        // Auto-reply
        val reply = ChatMessage(
            id = "reply_${System.currentTimeMillis() + 1}",
            senderName = "أوراتك - الدعم الطبي",
            isUser = false,
            messageText = "شكراً لتواصلك. تلقينا رسالتك: '$text'، وسيتم الرد عليك ومتابعة استفسارك في أقرب لحظة.",
            timestamp = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date())
        )
        _chatMessages.value = _chatMessages.value + reply
    }
}

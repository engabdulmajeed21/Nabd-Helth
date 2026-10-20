package com.example.data.models

enum class UserRole { PATIENT, GUEST }

data class User(
    val id: String = "u_1",
    val name: String = "أحمد محمد العولقي",
    val phone: String = "+967 777 123 456",
    val email: String = "ahmed.patient@auratech.med",
    val avatarUrl: String = "",
    val isLoggedIn: Boolean = false
)

data class MedicalTower(
    val id: String,
    val nameAr: String,
    val nameEn: String,
    val addressAr: String,
    val addressEn: String,
    val distanceKm: Double,
    val rating: Double,
    val reviewsCount: Int,
    val isOpen: Boolean,
    val workingHoursAr: String = "9:00 ص - 10:00 م",
    val workingHoursEn: String = "9:00 AM - 10:00 PM",
    val imageUrl: String,
    val clinicIds: List<String> = emptyList(),
    val pharmacyIds: List<String> = emptyList()
)

data class Specialty(
    val id: String,
    val nameAr: String,
    val nameEn: String,
    val iconEmoji: String
) {
    val icon: String get() = iconEmoji
}

data class Clinic(
    val id: String,
    val nameAr: String,
    val nameEn: String,
    val specialtyAr: String,
    val specialtyEn: String,
    val specialtyId: String,
    val towerNameAr: String? = null,
    val towerNameEn: String? = null,
    val addressAr: String,
    val addressEn: String,
    val distanceKm: Double,
    val rating: Double,
    val reviewsCount: Int,
    val isOpen: Boolean,
    val workingHoursAr: String = "8:30 ص - 9:00 م",
    val workingHoursEn: String = "8:30 AM - 9:00 PM",
    val imageUrl: String,
    val sectionNamesAr: List<String> = listOf("عيادة الباطنية العامة", "وحدة الفحص الدوري", "المختبر الاستشاري"),
    val sectionNamesEn: List<String> = listOf("General Internal Medicine", "Periodic Checkup Unit", "Consultant Lab")
) {
    val clinicSections: List<String> get() = sectionNamesAr
    val sections: List<String> get() = sectionNamesAr
}

data class Pharmacy(
    val id: String,
    val nameAr: String,
    val nameEn: String,
    val addressAr: String,
    val addressEn: String,
    val distanceKm: Double,
    val rating: Double,
    val reviewsCount: Int,
    val isOpen: Boolean,
    val delivers: Boolean,
    val deliveryRadiusKm: Double = 5.0,
    val workingHoursAr: String = "24 ساعة / طوال الأسبوع",
    val workingHoursEn: String = "24 Hours / 7 Days",
    val imageUrl: String
)

data class Doctor(
    val id: String,
    val nameAr: String,
    val nameEn: String,
    val specialtyAr: String,
    val specialtyEn: String,
    val subSpecialtyAr: String,
    val subSpecialtyEn: String,
    val clinicId: String,
    val clinicNameAr: String,
    val clinicNameEn: String,
    val rating: Double,
    val reviewsCount: Int,
    val consultationFee: Int, // e.g. 5000 YER
    val experienceYears: Int,
    val imageUrl: String,
    val nextAvailableSlot: String = "10:30 AM"
) {
    val avatarUrl: String get() = imageUrl
    val nextAvailableSlotAr: String get() = nextAvailableSlot
    val nextAvailableSlotEn: String get() = nextAvailableSlot
}

data class Drug(
    val id: String,
    val name: String,
    val dosage: String,
    val pharmacyId: String,
    val pharmacyNameAr: String,
    val pharmacyNameEn: String,
    val price: Double,
    val distanceKm: Double,
    val rating: Double,
    val delivers: Boolean,
    val requiresPrescription: Boolean = false,
    val category: String = "عام",
    val imageUrl: String = ""
)

enum class OrderStatus {
    REVIEW,
    PREPARING,
    DELIVERING,
    DELIVERED,
    REJECTED,
    CANCELED
}

data class Order(
    val id: String,
    val orderNumber: String,
    val pharmacyNameAr: String,
    val pharmacyNameEn: String,
    val items: List<CartItem>,
    val totalAmount: Double,
    val status: OrderStatus,
    val orderTime: String,
    val estimatedMinutes: Int = 25,
    val isDelivery: Boolean = true,
    val address: String = "المنزل - شارع الزبيري، صنعاء",
    val receiptNumber: String = "45872",
    val templateName: String? = null
)

enum class AppointmentStatus {
    CONFIRMED,
    COMPLETED,
    CANCELED,
    NO_SHOW,
    CANCELLED_BY_CLINIC
}

data class Appointment(
    val id: String,
    val appointmentNumber: String,
    val doctorNameAr: String,
    val doctorNameEn: String,
    val specialtyAr: String,
    val specialtyEn: String,
    val clinicNameAr: String,
    val clinicNameEn: String,
    val date: String,
    val timeSlot: String,
    val fee: Int,
    val queueNumber: Int,
    val status: AppointmentStatus,
    val cancelReason: String? = null,
    val userRating: Float? = null,
    val qrCodeData: String
)

data class Review(
    val id: String,
    val targetId: String,
    val userName: String,
    val rating: Double,
    val comment: String,
    val date: String
)

enum class NotificationCategory { ORDERS, BOOKINGS, SYSTEM }

data class NotificationItem(
    val id: String,
    val titleAr: String,
    val titleEn: String,
    val bodyAr: String,
    val category: NotificationCategory,
    val timestamp: String,
    var isRead: Boolean = false,
    val bodyEn: String = bodyAr
) {
    val type: String get() = category.name.lowercase()
    val title: String get() = titleAr
    val body: String get() = bodyAr
    val timeAgo: String get() = timestamp
}

data class Dependent(
    val id: String,
    val name: String,
    val relationshipAr: String,
    val relationshipEn: String,
    val birthDate: String,
    val isMale: Boolean
)

data class Address(
    val id: String,
    val titleAr: String,
    val titleEn: String,
    val detailsAr: String,
    val detailsEn: String,
    val isDefault: Boolean = false,
    val isOutOfRange: Boolean = false
)

data class CartItem(
    val drug: Drug,
    val quantity: Int = 1
)

data class SavedTemplate(
    val id: String,
    val name: String,
    val drugNames: List<String>,
    val lastOrderDate: String,
    val totalAmount: Double
)

data class RefundItem(
    val id: String,
    val referenceNumber: String,
    val date: String,
    val amount: Double,
    val isRefunded: Boolean,
    val paymentMethod: String = "E-Wallet (777123456)"
) {
    val transactionId: String get() = referenceNumber
    val status: String get() = if (isRefunded) "مكتمل" else "قيد المعالجة"
    val reason: String get() = referenceNumber
    val eWalletNumber: String get() = paymentMethod
}

data class ChatMessage(
    val id: String,
    val senderName: String,
    val isUser: Boolean,
    val messageText: String,
    val timestamp: String
)

data class FaqItem(
    val questionAr: String,
    val questionEn: String,
    val answerAr: String,
    val answerEn: String
) {
    val question: String get() = questionAr
    val answer: String get() = answerAr
}

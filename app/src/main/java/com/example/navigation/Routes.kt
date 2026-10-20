package com.example.navigation

object Routes {
    const val SPLASH = "splash"
    const val ONBOARDING = "onboarding"

    // Bottom Nav Tabs
    const val HOME = "home"
    const val MY_BOOKINGS = "my_bookings"
    const val MY_ORDERS = "my_orders"
    const val PROFILE = "profile"

    // Auth
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val OTP = "otp/{phone}"
    const val PRIVACY_POLICY = "privacy_policy"

    // Medical Towers
    const val TOWERS_LIST = "towers_list"
    const val TOWER_DETAIL = "tower_detail/{towerId}"

    // Pharmacies & Drugs
    const val PHARMACIES_LIST = "pharmacies_list"
    const val GLOBAL_DRUG_SEARCH = "global_drug_search"
    const val PHARMACY_DETAIL = "pharmacy_detail/{pharmacyId}"
    const val CART = "cart"
    const val CHECKOUT = "checkout"
    const val ORDER_TRACKING = "order_tracking/{orderId}"

    // Clinics & Doctors
    const val CLINICS_LIST = "clinics_list"
    const val CLINIC_SEARCH_FILTER = "clinic_search_filter"
    const val CLINIC_DETAIL = "clinic_detail/{clinicId}"
    const val DOCTORS_LIST = "doctors_list"
    const val BOOK_APPOINTMENT = "book_appointment/{doctorId}"
    const val APPOINTMENT_PAYMENT = "appointment_payment/{doctorId}/{date}/{slot}"
    const val BOOKING_SUCCESS = "booking_success/{appointmentId}"

    // Profile Screens
    const val FAMILY_MEMBERS = "family_members"
    const val ADDRESS_BOOK = "address_book"
    const val FAVORITES = "favorites"
    const val SAVED_TEMPLATES = "saved_templates"
    const val HISTORY = "history"
    const val REFUNDS = "refunds"
    const val NOTIFICATIONS = "notifications"
    const val SETTINGS = "settings"
    const val HELP_CENTER = "help_center"
    const val SUPPORT_CHAT = "support_chat"
    const val REPORT_PROBLEM = "report_problem"
    const val JOIN_US_B2B = "join_us_b2b"

    fun otp(phone: String) = "otp/$phone"
    fun towerDetail(id: String) = "tower_detail/$id"
    fun pharmacyDetail(id: String) = "pharmacy_detail/$id"
    fun clinicDetail(id: String) = "clinic_detail/$id"
    fun orderTracking(id: String) = "order_tracking/$id"
    fun bookAppointment(doctorId: String) = "book_appointment/$doctorId"
    fun appointmentPayment(doctorId: String, date: String, slot: String) =
        "appointment_payment/$doctorId/${java.net.URLEncoder.encode(date, "UTF-8")}/${java.net.URLEncoder.encode(slot, "UTF-8")}"
    fun bookingSuccess(appointmentId: String) = "booking_success/$appointmentId"
}

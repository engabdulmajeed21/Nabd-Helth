package com.example.navigation

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.example.localization.LocalAppStrings
import com.example.localization.LocalLanguage
import com.example.ui.appointments.*
import com.example.ui.auth.*
import com.example.ui.cart.*
import com.example.ui.clinics.*
import com.example.ui.home.HomeScreen
import com.example.ui.onboarding.OnboardingScreen
import com.example.ui.orders.MyOrdersScreen
import com.example.ui.pharmacies.*
import com.example.ui.profile.*
import com.example.ui.towers.*
import com.example.ui.tracking.OrderTrackingScreen
import com.example.viewmodel.AuratechViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest

sealed class BottomNavItem(
    val route: String,
    val titleAr: String,
    val titleEn: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    object Home : BottomNavItem(Routes.HOME, "الرئيسية", "Home", Icons.Filled.Home, Icons.Outlined.Home)
    object Bookings : BottomNavItem(Routes.MY_BOOKINGS, "مواعيدي", "Bookings", Icons.Filled.CalendarMonth, Icons.Outlined.CalendarMonth)
    object Orders : BottomNavItem(Routes.MY_ORDERS, "طلباتي", "Orders", Icons.Filled.ShoppingBag, Icons.Outlined.ShoppingBag)
    object Profile : BottomNavItem(Routes.PROFILE, "حسابي", "Profile", Icons.Filled.Person, Icons.Outlined.Person)
}

@Composable
fun AppNavGraph(
    viewModel: AuratechViewModel,
    navController: NavHostController = rememberNavController()
) {
    val strings = LocalAppStrings.current
    val lang = LocalLanguage.current
    val snackbarHostState = remember { SnackbarHostState() }
    val onboardingCompleted by viewModel.onboardingCompleted.collectAsState()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val bottomNavItems = listOf(
        BottomNavItem.Home,
        BottomNavItem.Bookings,
        BottomNavItem.Orders,
        BottomNavItem.Profile
    )

    val showBottomBar = currentRoute in listOf(
        Routes.HOME,
        Routes.MY_BOOKINGS,
        Routes.MY_ORDERS,
        Routes.PROFILE
    )

    LaunchedEffect(Unit) {
        viewModel.snackbarMessage.collectLatest { msg ->
            snackbarHostState.showSnackbar(msg)
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 6.dp
                ) {
                    bottomNavItems.forEach { item ->
                        val isSelected = currentRoute == item.route
                        NavigationBarItem(
                            selected = isSelected,
                            onClick = {
                                if (currentRoute != item.route) {
                                    navController.navigate(item.route) {
                                        popUpTo(Routes.HOME) { saveState = true }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            },
                            icon = {
                                Icon(
                                    imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                                    contentDescription = if (lang == "en") item.titleEn else item.titleAr
                                )
                            },
                            label = {
                                Text(
                                    text = if (lang == "en") item.titleEn else item.titleAr,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 12.sp
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.primary,
                                selectedTextColor = MaterialTheme.colorScheme.primary,
                                indicatorColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                            )
                        )
                    }
                }
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = Routes.SPLASH,
            modifier = Modifier.padding(padding)
        ) {
            // Splash Screen
            composable(Routes.SPLASH) {
                SplashScreen(
                    onTimeout = {
                        val destination = if (onboardingCompleted) Routes.HOME else Routes.ONBOARDING
                        navController.navigate(destination) {
                            popUpTo(Routes.SPLASH) { inclusive = true }
                        }
                    }
                )
            }

            // Onboarding Screen
            composable(Routes.ONBOARDING) {
                OnboardingScreen(
                    onFinish = {
                        viewModel.setOnboardingCompleted(true)
                        navController.navigate(Routes.HOME) {
                            popUpTo(Routes.ONBOARDING) { inclusive = true }
                        }
                    }
                )
            }

            // Home Screen
            composable(Routes.HOME) {
                HomeScreen(
                    viewModel = viewModel,
                    onNavigateToTowers = { navController.navigate(Routes.TOWERS_LIST) },
                    onNavigateToTowerDetail = { navController.navigate(Routes.towerDetail(it)) },
                    onNavigateToClinics = { navController.navigate(Routes.CLINICS_LIST) },
                    onNavigateToClinicDetail = { navController.navigate(Routes.clinicDetail(it)) },
                    onNavigateToPharmacies = { navController.navigate(Routes.PHARMACIES_LIST) },
                    onNavigateToPharmacyDetail = { navController.navigate(Routes.pharmacyDetail(it)) },
                    onNavigateToDrugSearch = { navController.navigate(Routes.GLOBAL_DRUG_SEARCH) },
                    onNavigateToCart = { navController.navigate(Routes.CART) },
                    onNavigateToLogin = { navController.navigate(Routes.LOGIN) },
                    onNavigateToNotifications = { navController.navigate(Routes.NOTIFICATIONS) }
                )
            }

            // Auth Flow
            composable(Routes.LOGIN) {
                LoginScreen(
                    viewModel = viewModel,
                    onNavigateToRegister = { navController.navigate(Routes.REGISTER) },
                    onNavigateToOtp = { navController.navigate(Routes.otp(it)) },
                    onLoginSuccess = { navController.popBackStack() },
                    onBack = { navController.popBackStack() }
                )
            }

            composable(Routes.REGISTER) {
                RegisterScreen(
                    viewModel = viewModel,
                    onNavigateToOtp = { navController.navigate(Routes.otp(it)) },
                    onNavigateToPrivacy = { navController.navigate(Routes.PRIVACY_POLICY) },
                    onNavigateToLogin = { navController.navigate(Routes.LOGIN) },
                    onBack = { navController.popBackStack() }
                )
            }

            composable(
                route = Routes.OTP,
                arguments = listOf(navArgument("phone") { type = NavType.StringType })
            ) { backStackEntry ->
                val phone = backStackEntry.arguments?.getString("phone") ?: ""
                OtpScreen(
                    viewModel = viewModel,
                    phone = phone,
                    onVerificationSuccess = {
                        navController.navigate(Routes.HOME) {
                            popUpTo(Routes.LOGIN) { inclusive = true }
                        }
                    },
                    onBack = { navController.popBackStack() }
                )
            }

            composable(Routes.PRIVACY_POLICY) {
                PrivacyPolicyScreen(onBack = { navController.popBackStack() })
            }

            // Medical Towers
            composable(Routes.TOWERS_LIST) {
                MedicalTowersListScreen(
                    onNavigateToDetail = { navController.navigate(Routes.towerDetail(it)) },
                    onBack = { navController.popBackStack() }
                )
            }

            composable(
                route = Routes.TOWER_DETAIL,
                arguments = listOf(navArgument("towerId") { type = NavType.StringType })
            ) { backStackEntry ->
                val towerId = backStackEntry.arguments?.getString("towerId") ?: ""
                TowerProfileScreen(
                    towerId = towerId,
                    viewModel = viewModel,
                    onNavigateToClinicDetail = { navController.navigate(Routes.clinicDetail(it)) },
                    onNavigateToPharmacyDetail = { navController.navigate(Routes.pharmacyDetail(it)) },
                    onBack = { navController.popBackStack() }
                )
            }

            // Pharmacies & Drugs
            composable(Routes.PHARMACIES_LIST) {
                PharmaciesListScreen(
                    onNavigateToDetail = { navController.navigate(Routes.pharmacyDetail(it)) },
                    onNavigateToGlobalSearch = { navController.navigate(Routes.GLOBAL_DRUG_SEARCH) },
                    onBack = { navController.popBackStack() }
                )
            }

            composable(Routes.GLOBAL_DRUG_SEARCH) {
                GlobalDrugSearchScreen(
                    viewModel = viewModel,
                    onNavigateToPharmacyDetail = { navController.navigate(Routes.pharmacyDetail(it)) },
                    onBack = { navController.popBackStack() }
                )
            }

            composable(
                route = Routes.PHARMACY_DETAIL,
                arguments = listOf(navArgument("pharmacyId") { type = NavType.StringType })
            ) { backStackEntry ->
                val pharmacyId = backStackEntry.arguments?.getString("pharmacyId") ?: ""
                PharmacyProfileScreen(
                    pharmacyId = pharmacyId,
                    viewModel = viewModel,
                    onNavigateToCart = { navController.navigate(Routes.CART) },
                    onBack = { navController.popBackStack() }
                )
            }

            // Cart & Checkout
            composable(Routes.CART) {
                CartScreen(
                    viewModel = viewModel,
                    onNavigateToCheckout = { navController.navigate(Routes.CHECKOUT) },
                    onBack = { navController.popBackStack() }
                )
            }

            composable(Routes.CHECKOUT) {
                CheckoutScreen(
                    viewModel = viewModel,
                    onOrderPlaced = { orderId ->
                        navController.navigate(Routes.orderTracking(orderId)) {
                            popUpTo(Routes.CART) { inclusive = true }
                        }
                    },
                    onBack = { navController.popBackStack() }
                )
            }

            composable(
                route = Routes.ORDER_TRACKING,
                arguments = listOf(navArgument("orderId") { type = NavType.StringType })
            ) { backStackEntry ->
                val orderId = backStackEntry.arguments?.getString("orderId") ?: ""
                OrderTrackingScreen(
                    orderId = orderId,
                    viewModel = viewModel,
                    onNavigateToHome = {
                        navController.navigate(Routes.HOME) {
                            popUpTo(Routes.HOME) { inclusive = true }
                        }
                    },
                    onNavigateToSupportChat = { navController.navigate(Routes.SUPPORT_CHAT) },
                    onBack = { navController.popBackStack() }
                )
            }

            // Clinics & Doctors
            composable(Routes.CLINICS_LIST) {
                ClinicsListScreen(
                    onNavigateToDetail = { navController.navigate(Routes.clinicDetail(it)) },
                    onNavigateToSearchFilter = { navController.navigate(Routes.CLINIC_SEARCH_FILTER) },
                    onBack = { navController.popBackStack() }
                )
            }

            composable(Routes.CLINIC_SEARCH_FILTER) {
                ClinicSearchFilterScreen(
                    viewModel = viewModel,
                    onSpecialtySelected = {
                        navController.navigate(Routes.DOCTORS_LIST)
                    },
                    onBack = { navController.popBackStack() }
                )
            }

            composable(
                route = Routes.CLINIC_DETAIL,
                arguments = listOf(navArgument("clinicId") { type = NavType.StringType })
            ) { backStackEntry ->
                val clinicId = backStackEntry.arguments?.getString("clinicId") ?: ""
                ClinicProfileScreen(
                    clinicId = clinicId,
                    viewModel = viewModel,
                    onNavigateToBookDoctor = { navController.navigate(Routes.bookAppointment(it)) },
                    onBack = { navController.popBackStack() }
                )
            }

            composable(Routes.DOCTORS_LIST) {
                DoctorsListScreen(
                    viewModel = viewModel,
                    onNavigateToBook = { navController.navigate(Routes.bookAppointment(it)) },
                    onBack = { navController.popBackStack() }
                )
            }

            composable(
                route = Routes.BOOK_APPOINTMENT,
                arguments = listOf(navArgument("doctorId") { type = NavType.StringType })
            ) { backStackEntry ->
                val doctorId = backStackEntry.arguments?.getString("doctorId") ?: ""
                BookAppointmentScreen(
                    doctorId = doctorId,
                    viewModel = viewModel,
                    onNavigateToPayment = { docId, date, slot ->
                        navController.navigate(Routes.appointmentPayment(docId, date, slot))
                    },
                    onBack = { navController.popBackStack() }
                )
            }

            composable(
                route = Routes.APPOINTMENT_PAYMENT,
                arguments = listOf(
                    navArgument("doctorId") { type = NavType.StringType },
                    navArgument("date") { type = NavType.StringType },
                    navArgument("slot") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val docId = backStackEntry.arguments?.getString("doctorId") ?: ""
                val date = java.net.URLDecoder.decode(backStackEntry.arguments?.getString("date") ?: "", "UTF-8")
                val slot = java.net.URLDecoder.decode(backStackEntry.arguments?.getString("slot") ?: "", "UTF-8")
                AppointmentPaymentScreen(
                    doctorId = docId,
                    date = date,
                    slot = slot,
                    viewModel = viewModel,
                    onBookingSuccess = { aptId ->
                        navController.navigate(Routes.bookingSuccess(aptId)) {
                            popUpTo(Routes.BOOK_APPOINTMENT) { inclusive = true }
                        }
                    },
                    onBack = { navController.popBackStack() }
                )
            }

            composable(
                route = Routes.BOOKING_SUCCESS,
                arguments = listOf(navArgument("appointmentId") { type = NavType.StringType })
            ) { backStackEntry ->
                val aptId = backStackEntry.arguments?.getString("appointmentId") ?: ""
                BookingSuccessScreen(
                    appointmentId = aptId,
                    viewModel = viewModel,
                    onNavigateToMyBookings = {
                        navController.navigate(Routes.MY_BOOKINGS) {
                            popUpTo(Routes.HOME)
                        }
                    },
                    onNavigateToHome = {
                        navController.navigate(Routes.HOME) {
                            popUpTo(Routes.HOME) { inclusive = true }
                        }
                    }
                )
            }

            // Bottom Nav Top-Level Destinations
            composable(Routes.MY_BOOKINGS) {
                MyBookingsScreen(
                    viewModel = viewModel,
                    onNavigateToHome = { navController.navigate(Routes.HOME) }
                )
            }

            composable(Routes.MY_ORDERS) {
                MyOrdersScreen(
                    viewModel = viewModel,
                    onNavigateToTracking = { navController.navigate(Routes.orderTracking(it)) },
                    onNavigateToHome = { navController.navigate(Routes.HOME) }
                )
            }

            composable(Routes.PROFILE) {
                ProfileHomeScreen(
                    viewModel = viewModel,
                    onNavigateToFamily = { navController.navigate(Routes.FAMILY_MEMBERS) },
                    onNavigateToAddresses = { navController.navigate(Routes.ADDRESS_BOOK) },
                    onNavigateToFavorites = { navController.navigate(Routes.FAVORITES) },
                    onNavigateToTemplates = { navController.navigate(Routes.SAVED_TEMPLATES) },
                    onNavigateToHistory = { navController.navigate(Routes.HISTORY) },
                    onNavigateToRefunds = { navController.navigate(Routes.REFUNDS) },
                    onNavigateToNotifications = { navController.navigate(Routes.NOTIFICATIONS) },
                    onNavigateToSettings = { navController.navigate(Routes.SETTINGS) },
                    onNavigateToHelpCenter = { navController.navigate(Routes.HELP_CENTER) },
                    onNavigateToSupportChat = { navController.navigate(Routes.SUPPORT_CHAT) },
                    onNavigateToReportProblem = { navController.navigate(Routes.REPORT_PROBLEM) },
                    onNavigateToJoinUs = { navController.navigate(Routes.JOIN_US_B2B) },
                    onNavigateToPrivacy = { navController.navigate(Routes.PRIVACY_POLICY) },
                    onNavigateToLogin = { navController.navigate(Routes.LOGIN) }
                )
            }

            // Profile Secondary Screens
            composable(Routes.FAMILY_MEMBERS) { FamilyMembersScreen(viewModel, onBack = { navController.popBackStack() }) }
            composable(Routes.ADDRESS_BOOK) { AddressBookScreen(viewModel, onBack = { navController.popBackStack() }) }
            composable(Routes.FAVORITES) {
                FavoritesScreen(
                    viewModel,
                    onNavigateToDoctor = { navController.navigate(Routes.bookAppointment(it)) },
                    onNavigateToClinic = { navController.navigate(Routes.clinicDetail(it)) },
                    onNavigateToPharmacy = { navController.navigate(Routes.pharmacyDetail(it)) },
                    onBack = { navController.popBackStack() }
                )
            }
            composable(Routes.SAVED_TEMPLATES) {
                SavedTemplatesScreen(
                    viewModel,
                    onNavigateToGlobalSearch = { navController.navigate(Routes.GLOBAL_DRUG_SEARCH) },
                    onBack = { navController.popBackStack() }
                )
            }
            composable(Routes.HISTORY) { HistoryScreen(viewModel, onBack = { navController.popBackStack() }) }
            composable(Routes.REFUNDS) { RefundsLedgerScreen(viewModel, onBack = { navController.popBackStack() }) }
            composable(Routes.NOTIFICATIONS) { NotificationsCenterScreen(viewModel, onBack = { navController.popBackStack() }) }
            composable(Routes.SETTINGS) { SettingsScreen(viewModel, onBack = { navController.popBackStack() }) }
            composable(Routes.HELP_CENTER) {
                HelpCenterScreen(
                    viewModel,
                    onNavigateToChat = { navController.navigate(Routes.SUPPORT_CHAT) },
                    onBack = { navController.popBackStack() }
                )
            }
            composable(Routes.SUPPORT_CHAT) { SupportChatScreen(viewModel, onBack = { navController.popBackStack() }) }
            composable(Routes.REPORT_PROBLEM) { ReportProblemScreen(viewModel, onBack = { navController.popBackStack() }) }
            composable(Routes.JOIN_US_B2B) { JoinUsB2BScreen(viewModel, onBack = { navController.popBackStack() }) }
        }
    }
}

@Composable
fun SplashScreen(onTimeout: () -> Unit) {
    LaunchedEffect(Unit) {
        delay(1400)
        onTimeout()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primary),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = Color.White,
                modifier = Modifier.size(96.dp),
                shadowElevation = 8.dp
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.MedicalServices,
                        contentDescription = null,
                        modifier = Modifier.size(54.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "أوراتك - Auratech",
                color = Color.White,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "المنصة الطبية الذكية لرعاية المرضى",
                color = Color.White.copy(alpha = 0.85f),
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

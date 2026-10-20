package com.example.ui.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.mock.MockData
import com.example.data.models.Clinic
import com.example.data.models.MedicalTower
import com.example.data.models.Pharmacy
import com.example.localization.LocalAppStrings
import com.example.localization.LocalLanguage
import com.example.ui.common.*
import com.example.ui.theme.MedicalBluePrimary
import com.example.ui.theme.SuccessGreen
import com.example.viewmodel.AuratechViewModel

data class CategoryItem(
    val id: String,
    val titleAr: String,
    val titleEn: String,
    val icon: ImageVector,
    val isAvailable: Boolean = true
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: AuratechViewModel,
    onNavigateToTowers: () -> Unit,
    onNavigateToTowerDetail: (String) -> Unit,
    onNavigateToClinics: () -> Unit,
    onNavigateToClinicDetail: (String) -> Unit,
    onNavigateToPharmacies: () -> Unit,
    onNavigateToPharmacyDetail: (String) -> Unit,
    onNavigateToDrugSearch: () -> Unit,
    onNavigateToCart: () -> Unit,
    onNavigateToLogin: () -> Unit,
    onNavigateToNotifications: () -> Unit
) {
    val strings = LocalAppStrings.current
    val lang = LocalLanguage.current
    val currentUser by viewModel.currentUser.collectAsState()
    val cartItems by viewModel.cartItems.collectAsState()
    val notifications by viewModel.notifications.collectAsState()
    val unreadNotifsCount = notifications.count { !it.isRead }

    var showLocationDialog by remember { mutableStateOf(false) }

    val categories = listOf(
        CategoryItem("towers", "الأبراج الطبية", "Medical Towers", Icons.Default.Apartment),
        CategoryItem("clinics", "العيادات", "Clinics", Icons.Default.MedicalServices),
        CategoryItem("pharmacies", "الصيدليات", "Pharmacies", Icons.Default.LocalPharmacy),
        CategoryItem("hospitals", "المستشفيات", "Hospitals", Icons.Default.LocalHospital, isAvailable = false),
        CategoryItem("labs", "المختبرات", "Labs", Icons.Default.Biotech, isAvailable = false)
    )

    Scaffold(
        topBar = {
            Surface(
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 2.dp
            ) {
                Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 10.dp)) {
                    // Location & Notification Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .clickable { showLocationDialog = true }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = strings.currentLocation,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Icon(
                                imageVector = Icons.Default.KeyboardArrowDown,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        // Notification Icon with Badge
                        IconButton(onClick = onNavigateToNotifications) {
                            BadgedBox(
                                badge = {
                                    if (unreadNotifsCount > 0) {
                                        Badge {
                                            Text(text = "$unreadNotifsCount")
                                        }
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Notifications,
                                    contentDescription = "Notifications"
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Search Trigger Bar
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .clip(RoundedCornerShape(24.dp))
                            .clickable { onNavigateToDrugSearch() },
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(24.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = strings.searchPlaceholder,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        },
        floatingActionButton = {
            AnimatedVisibility(visible = cartItems.isNotEmpty()) {
                ExtendedFloatingActionButton(
                    onClick = onNavigateToCart,
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = Color.White,
                    icon = {
                        BadgedBox(
                            badge = {
                                Badge {
                                    Text(text = "${cartItems.sumOf { it.quantity }}")
                                }
                            }
                        ) {
                            Icon(imageVector = Icons.Default.ShoppingCart, contentDescription = null)
                        }
                    },
                    text = {
                        Text(
                            text = "${strings.myCart} (${cartItems.sumOf { it.drug.price * it.quantity }.toInt()} ${strings.yerCurrency})",
                            fontWeight = FontWeight.Bold
                        )
                    }
                )
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            // Guest Mode Banner
            if (!currentUser.isLoggedIn) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f)
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primary),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = null,
                                    tint = Color.White
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = if (lang == "en") "Welcome to Auratech (Guest Mode)" else "مرحباً بك في أوراتك (وضع الزائر)",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = if (lang == "en") "Login to book doctors and order medicine easily" else "سجل دخولك لتتمكن من حجز الأطباء وطلب الأدوية",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(
                                onClick = onNavigateToLogin,
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                            ) {
                                Text(strings.login, style = MaterialTheme.typography.labelMedium)
                            }
                        }
                    }
                }
            }

            // Categories Grid / Row
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    categories.forEach { cat ->
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .weight(1f)
                                .clickable {
                                    when (cat.id) {
                                        "towers" -> onNavigateToTowers()
                                        "clinics" -> onNavigateToClinics()
                                        "pharmacies" -> onNavigateToPharmacies()
                                        else -> viewModel.showMessage(strings.comingSoon)
                                    }
                                }
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(56.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(
                                        if (cat.isAvailable) MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                                        else MaterialTheme.colorScheme.surfaceVariant
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = cat.icon,
                                    contentDescription = cat.titleEn,
                                    tint = if (cat.isAvailable) MaterialTheme.colorScheme.primary else Color.Gray,
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = if (lang == "en") cat.titleEn else cat.titleAr,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.SemiBold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            if (!cat.isAvailable) {
                                Surface(
                                    color = Color.Gray.copy(alpha = 0.2f),
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text(
                                        text = strings.comingSoon,
                                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Medical Towers Carousel
            item {
                SectionHeader(
                    title = strings.medicalTowers,
                    actionText = strings.viewAll,
                    onActionClick = onNavigateToTowers
                )
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    items(MockData.medicalTowers) { tower ->
                        TowerCardItem(
                            tower = tower,
                            lang = lang,
                            onClick = { onNavigateToTowerDetail(tower.id) }
                        )
                    }
                }
            }

            // Featured Clinics Carousel
            item {
                Spacer(modifier = Modifier.height(14.dp))
                SectionHeader(
                    title = strings.clinics,
                    actionText = strings.viewAll,
                    onActionClick = onNavigateToClinics
                )
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    items(MockData.clinics.take(5)) { clinic ->
                        ClinicCardItem(
                            clinic = clinic,
                            lang = lang,
                            onClick = { onNavigateToClinicDetail(clinic.id) }
                        )
                    }
                }
            }

            // Top Pharmacies Carousel
            item {
                Spacer(modifier = Modifier.height(14.dp))
                SectionHeader(
                    title = strings.pharmacies,
                    actionText = strings.viewAll,
                    onActionClick = onNavigateToPharmacies
                )
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    items(MockData.pharmacies.take(5)) { pharmacy ->
                        PharmacyCardItem(
                            pharmacy = pharmacy,
                            lang = lang,
                            onClick = { onNavigateToPharmacyDetail(pharmacy.id) }
                        )
                    }
                }
            }
        }
    }

    // Location Permission Dialog
    if (showLocationDialog) {
        AlertDialog(
            onDismissRequest = { showLocationDialog = false },
            icon = {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(36.dp)
                )
            },
            title = {
                Text(
                    text = strings.locationPermissionTitle,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = strings.locationPermissionDesc,
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showLocationDialog = false
                        viewModel.showMessage("تم تحديث موقعك الجغرافي بدقة")
                    }
                ) {
                    Text(strings.allow)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLocationDialog = false }) {
                    Text(strings.later)
                }
            }
        )
    }
}

@Composable
fun TowerCardItem(tower: MedicalTower, lang: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .width(260.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
            ) {
                AsyncImage(
                    model = tower.imageUrl,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.5f))
                            )
                        )
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    OpenStatusBadge(isOpen = tower.isOpen)
                    DistanceBadge(distanceKm = tower.distanceKm)
                }
            }
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = if (lang == "en") tower.nameEn else tower.nameAr,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = if (lang == "en") tower.addressEn else tower.addressAr,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    RatingBadge(rating = tower.rating, reviewsCount = tower.reviewsCount)
                    Text(
                        text = if (lang == "en") tower.workingHoursEn else tower.workingHoursAr,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
fun ClinicCardItem(clinic: Clinic, lang: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .width(240.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp)
            ) {
                AsyncImage(
                    model = clinic.imageUrl,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Surface(
                        color = MaterialTheme.colorScheme.primary,
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = if (lang == "en") clinic.specialtyEn else clinic.specialtyAr,
                            color = Color.White,
                            style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                    DistanceBadge(distanceKm = clinic.distanceKm)
                }
            }
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = if (lang == "en") clinic.nameEn else clinic.nameAr,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = if (lang == "en") clinic.addressEn else clinic.addressAr,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(8.dp))
                RatingBadge(rating = clinic.rating, reviewsCount = clinic.reviewsCount)
            }
        }
    }
}

@Composable
fun PharmacyCardItem(pharmacy: Pharmacy, lang: String, onClick: () -> Unit) {
    val strings = LocalAppStrings.current
    Card(
        modifier = Modifier
            .width(230.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp)
            ) {
                AsyncImage(
                    model = pharmacy.imageUrl,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    if (pharmacy.delivers) {
                        Surface(
                            color = SuccessGreen,
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = strings.delivers,
                                color = Color.White,
                                style = MaterialTheme.typography.labelSmall,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                    DistanceBadge(distanceKm = pharmacy.distanceKm)
                }
            }
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = if (lang == "en") pharmacy.nameEn else pharmacy.nameAr,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = if (lang == "en") pharmacy.addressEn else pharmacy.addressAr,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(8.dp))
                RatingBadge(rating = pharmacy.rating, reviewsCount = pharmacy.reviewsCount)
            }
        }
    }
}

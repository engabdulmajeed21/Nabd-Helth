package com.example.ui.profile

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.mock.MockData
import com.example.data.models.ChatMessage
import com.example.data.models.Dependent
import com.example.localization.LocalAppStrings
import com.example.localization.LocalLanguage
import com.example.ui.common.AuratechTopAppBar
import com.example.ui.common.EmptyStateView
import com.example.ui.common.RatingBadge
import com.example.ui.theme.DangerRed
import com.example.ui.theme.SuccessGreen
import com.example.viewmodel.AuratechViewModel

@Composable
fun ProfileHomeScreen(
    viewModel: AuratechViewModel,
    onNavigateToFamily: () -> Unit,
    onNavigateToAddresses: () -> Unit,
    onNavigateToFavorites: () -> Unit,
    onNavigateToTemplates: () -> Unit,
    onNavigateToHistory: () -> Unit,
    onNavigateToRefunds: () -> Unit,
    onNavigateToNotifications: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToHelpCenter: () -> Unit,
    onNavigateToSupportChat: () -> Unit,
    onNavigateToReportProblem: () -> Unit,
    onNavigateToJoinUs: () -> Unit,
    onNavigateToPrivacy: () -> Unit,
    onNavigateToLogin: () -> Unit
) {
    val strings = LocalAppStrings.current
    val currentUser by viewModel.currentUser.collectAsState()
    val appointments by viewModel.appointments.collectAsState()
    val orders by viewModel.orders.collectAsState()

    Scaffold(
        topBar = { AuratechTopAppBar(title = strings.profile) }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Patient Header Card
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(68.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primaryContainer),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = null,
                                    modifier = Modifier.size(40.dp),
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = if (currentUser.isLoggedIn) currentUser.name else "زائر (غير مسجل)",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = if (currentUser.isLoggedIn) currentUser.phone else "سجل دخولك لحفظ بياناتك",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        if (currentUser.isLoggedIn) {
                            Spacer(modifier = Modifier.height(16.dp))
                            HorizontalDivider()
                            Spacer(modifier = Modifier.height(12.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceAround
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("${appointments.size}", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                    Text(strings.myBookings, style = MaterialTheme.typography.labelSmall)
                                }
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("${orders.size}", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                    Text(strings.myOrders, style = MaterialTheme.typography.labelSmall)
                                }
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("6", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                    Text(strings.favorites, style = MaterialTheme.typography.labelSmall)
                                }
                            }
                        }
                    }
                }
            }

            // Menu Items List
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column {
                        ProfileMenuItem(Icons.Default.People, strings.familyMembers, onNavigateToFamily)
                        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                        ProfileMenuItem(Icons.Default.LocationOn, strings.addressBook, onNavigateToAddresses)
                        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                        ProfileMenuItem(Icons.Default.Favorite, strings.favorites, onNavigateToFavorites)
                        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                        ProfileMenuItem(Icons.Default.Bookmark, strings.savedTemplates, onNavigateToTemplates)
                        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                        ProfileMenuItem(Icons.Default.History, strings.medicalHistory, onNavigateToHistory)
                        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                        ProfileMenuItem(Icons.Default.AccountBalanceWallet, strings.refundsLedger, onNavigateToRefunds)
                        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                        ProfileMenuItem(Icons.Default.Notifications, strings.notificationsCenter, onNavigateToNotifications)
                    }
                }
            }

            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column {
                        ProfileMenuItem(Icons.Default.Settings, strings.settings, onNavigateToSettings)
                        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                        ProfileMenuItem(Icons.AutoMirrored.Filled.Help, strings.helpCenter, onNavigateToHelpCenter)
                        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                        ProfileMenuItem(Icons.Default.Chat, strings.liveSupportChat, onNavigateToSupportChat)
                        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                        ProfileMenuItem(Icons.Default.ReportProblem, strings.reportProblem, onNavigateToReportProblem)
                        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                        ProfileMenuItem(Icons.Default.Domain, strings.joinUsB2B, onNavigateToJoinUs)
                        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                        ProfileMenuItem(Icons.Default.PrivacyTip, strings.privacyPolicyTitle, onNavigateToPrivacy)
                    }
                }
            }

            // Auth Action (Login or Logout)
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    if (currentUser.isLoggedIn) {
                        ProfileMenuItem(
                            icon = Icons.AutoMirrored.Filled.Logout,
                            title = strings.logout,
                            onClick = {
                                viewModel.logout()
                                viewModel.showMessage("تم تسجيل الخروج بنجاح")
                            },
                            tint = DangerRed
                        )
                    } else {
                        ProfileMenuItem(
                            icon = Icons.Default.Login,
                            title = strings.login,
                            onClick = onNavigateToLogin,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ProfileMenuItem(
    icon: ImageVector,
    title: String,
    onClick: () -> Unit,
    tint: Color = MaterialTheme.colorScheme.onSurface
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(imageVector = icon, contentDescription = null, tint = tint, modifier = Modifier.size(22.dp))
        Spacer(modifier = Modifier.width(16.dp))
        Text(text = title, style = MaterialTheme.typography.bodyLarge, color = tint, modifier = Modifier.weight(1f))
        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(20.dp))
    }
}

// ----------------------------------------------------
// Screen 27: Family Members
// ----------------------------------------------------
@Composable
fun FamilyMembersScreen(viewModel: AuratechViewModel, onBack: () -> Unit) {
    val strings = LocalAppStrings.current
    val lang = LocalLanguage.current
    val members by viewModel.familyMembers.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = { AuratechTopAppBar(title = strings.familyMembers, onBackClick = onBack) },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Add")
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(members) { member ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Person, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = member.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Text(
                                text = "${if (lang == "en") member.relationshipEn else member.relationshipAr} • ${member.birthDate}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        IconButton(onClick = { viewModel.deleteFamilyMember(member.id) }) {
                            Icon(Icons.Default.DeleteOutline, contentDescription = "Delete", tint = DangerRed)
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        var name by remember { mutableStateOf("") }
        var relation by remember { mutableStateOf("ابن") }
        var birthDate by remember { mutableStateOf("2018-05-12") }

        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text(strings.addFamilyMember, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("الاسم الكامل") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = relation, onValueChange = { relation = it }, label = { Text("صلة القرابة (ابن، زوجة، والد...)") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = birthDate, onValueChange = { birthDate = it }, label = { Text("تاريخ الميلاد (YYYY-MM-DD)") }, modifier = Modifier.fillMaxWidth())
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (name.isNotBlank()) {
                            viewModel.addFamilyMember(name, relation, relation, birthDate, true)
                            showAddDialog = false
                        }
                    }
                ) { Text(strings.save) }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) { Text(strings.cancel) }
            }
        )
    }
}

// ----------------------------------------------------
// Screen 28: Address Book
// ----------------------------------------------------
@Composable
fun AddressBookScreen(viewModel: AuratechViewModel, onBack: () -> Unit) {
    val strings = LocalAppStrings.current
    val lang = LocalLanguage.current
    val addresses by viewModel.addresses.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = { AuratechTopAppBar(title = strings.addressBook, onBackClick = onBack) },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Default.AddLocationAlt, contentDescription = "Add")
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(addresses) { addr ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Place, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = if (lang == "en") addr.titleEn else addr.titleAr,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                if (addr.isDefault) {
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Surface(color = SuccessGreen.copy(alpha = 0.15f), shape = RoundedCornerShape(4.dp)) {
                                        Text("افتراضي", color = SuccessGreen, style = MaterialTheme.typography.labelSmall, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                    }
                                }
                            }
                            Text(
                                text = if (lang == "en") addr.detailsEn else addr.detailsAr,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        if (!addr.isDefault) {
                            TextButton(onClick = { viewModel.setDefaultAddress(addr.id) }) {
                                Text("تعيين كافتراضي", fontSize = 11.sp)
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        var title by remember { mutableStateOf("") }
        var details by remember { mutableStateOf("") }
        var makeDefault by remember { mutableStateOf(false) }

        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text(strings.addNewAddress, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("اسم العنوان (المنزل، العمل)") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = details, onValueChange = { details = it }, label = { Text("تفاصيل العنوان (المدينة، الشارع)") }, modifier = Modifier.fillMaxWidth())
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(checked = makeDefault, onCheckedChange = { makeDefault = it })
                        Text("جعله العنوان الافتراضي للتوصيل")
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (title.isNotBlank()) {
                            viewModel.addAddress(title, title, details, details, makeDefault)
                            showAddDialog = false
                        }
                    }
                ) { Text(strings.save) }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) { Text(strings.cancel) }
            }
        )
    }
}

// ----------------------------------------------------
// Screen 29: Favorites
// ----------------------------------------------------
@Composable
fun FavoritesScreen(
    viewModel: AuratechViewModel,
    onNavigateToDoctor: (String) -> Unit,
    onNavigateToClinic: (String) -> Unit,
    onNavigateToPharmacy: (String) -> Unit,
    onBack: () -> Unit
) {
    val strings = LocalAppStrings.current
    val lang = LocalLanguage.current
    var selectedTab by remember { mutableStateOf(0) }

    val favDocIds by viewModel.favoriteDoctorIds.collectAsState()
    val favClinicIds by viewModel.favoriteClinicIds.collectAsState()
    val favPharmIds by viewModel.favoritePharmacyIds.collectAsState()

    val favDoctors = remember(favDocIds) { MockData.doctors.filter { favDocIds.contains(it.id) } }
    val favClinics = remember(favClinicIds) { MockData.clinics.filter { favClinicIds.contains(it.id) } }
    val favPharmacies = remember(favPharmIds) { MockData.pharmacies.filter { favPharmIds.contains(it.id) } }

    Scaffold(
        topBar = { AuratechTopAppBar(title = strings.favorites, onBackClick = onBack) }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).background(MaterialTheme.colorScheme.background)) {
            TabRow(selectedTabIndex = selectedTab) {
                Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }, text = { Text(strings.doctors) })
                Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }, text = { Text(strings.clinics) })
                Tab(selected = selectedTab == 2, onClick = { selectedTab = 2 }, text = { Text(strings.pharmacies) })
            }

            when (selectedTab) {
                0 -> {
                    LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        items(favDoctors) { doc ->
                            Card(
                                modifier = Modifier.fillMaxWidth().clickable { onNavigateToDoctor(doc.id) },
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                    Text(if (lang == "en") doc.nameEn else doc.nameAr, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                                    IconButton(onClick = { viewModel.toggleFavoriteDoctor(doc.id) }) {
                                        Icon(Icons.Default.Favorite, contentDescription = null, tint = DangerRed)
                                    }
                                }
                            }
                        }
                    }
                }
                1 -> {
                    LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        items(favClinics) { clinic ->
                            Card(
                                modifier = Modifier.fillMaxWidth().clickable { onNavigateToClinic(clinic.id) },
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                    Text(if (lang == "en") clinic.nameEn else clinic.nameAr, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                                    IconButton(onClick = { viewModel.toggleFavoriteClinic(clinic.id) }) {
                                        Icon(Icons.Default.Favorite, contentDescription = null, tint = DangerRed)
                                    }
                                }
                            }
                        }
                    }
                }
                2 -> {
                    LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        items(favPharmacies) { pharm ->
                            Card(
                                modifier = Modifier.fillMaxWidth().clickable { onNavigateToPharmacy(pharm.id) },
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                    Text(if (lang == "en") pharm.nameEn else pharm.nameAr, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                                    IconButton(onClick = { viewModel.toggleFavoritePharmacy(pharm.id) }) {
                                        Icon(Icons.Default.Favorite, contentDescription = null, tint = DangerRed)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// ----------------------------------------------------
// Screen 30: Saved Templates
// ----------------------------------------------------
@Composable
fun SavedTemplatesScreen(
    viewModel: AuratechViewModel,
    onNavigateToGlobalSearch: () -> Unit,
    onBack: () -> Unit
) {
    val strings = LocalAppStrings.current
    val templates by viewModel.savedTemplates.collectAsState()

    Scaffold(
        topBar = { AuratechTopAppBar(title = strings.savedTemplates, onBackClick = onBack) }
    ) { padding ->
        if (templates.isEmpty()) {
            EmptyStateView(
                icon = Icons.Default.BookmarkBorder,
                title = "لا توجد قوالب أدوية محفوظة",
                description = "يمكنك حفظ أي طلب أدوية مسبق لإعادة طلبه بنقرة واحدة مستقبلاً",
                actionLabel = "البحث عن أدوية",
                onActionClick = onNavigateToGlobalSearch,
                modifier = Modifier.padding(padding)
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding).background(MaterialTheme.colorScheme.background),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(templates) { tpl ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(tpl.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                IconButton(onClick = { viewModel.deleteSavedTemplate(tpl.id) }) {
                                    Icon(Icons.Default.DeleteOutline, contentDescription = "Delete", tint = DangerRed)
                                }
                            }
                            Text("الأدوية المشمولة:", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            tpl.drugNames.forEach { d ->
                                Text("• $d", style = MaterialTheme.typography.bodySmall)
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            Button(
                                onClick = { viewModel.showMessage("تم تجهيز سلة الشراء بمحتويات ${tpl.name}") },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("إعادة طلب هذا القالب الآن (${tpl.totalAmount.toInt()} ${strings.yerCurrency})")
                            }
                        }
                    }
                }
            }
        }
    }
}

// ----------------------------------------------------
// Screen 31: Medical History
// ----------------------------------------------------
@Composable
fun HistoryScreen(viewModel: AuratechViewModel, onBack: () -> Unit) {
    val strings = LocalAppStrings.current
    var tabIndex by remember { mutableStateOf(0) }
    val appointments by viewModel.appointments.collectAsState()
    val orders by viewModel.orders.collectAsState()

    Scaffold(
        topBar = { AuratechTopAppBar(title = strings.medicalHistory, onBackClick = onBack) }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).background(MaterialTheme.colorScheme.background)) {
            TabRow(selectedTabIndex = tabIndex) {
                Tab(selected = tabIndex == 0, onClick = { tabIndex = 0 }, text = { Text("الكشوفات السابقة") })
                Tab(selected = tabIndex == 1, onClick = { tabIndex = 1 }, text = { Text("طلبات الأدوية السابقة") })
            }

            if (tabIndex == 0) {
                LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(appointments) { apt ->
                        Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(apt.doctorNameAr, fontWeight = FontWeight.Bold)
                                Text("${apt.clinicNameAr} | ${apt.date}", style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                }
            } else {
                LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(orders) { ord ->
                        Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text("طلب رقم #${ord.orderNumber} - ${ord.pharmacyNameAr}", fontWeight = FontWeight.Bold)
                                Text("الإجمالي: ${ord.totalAmount.toInt()} ${strings.yerCurrency}", style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                }
            }
        }
    }
}

// ----------------------------------------------------
// Screen 32: Refunds Ledger
// ----------------------------------------------------
@Composable
fun RefundsLedgerScreen(viewModel: AuratechViewModel, onBack: () -> Unit) {
    val strings = LocalAppStrings.current
    val refunds by viewModel.refunds.collectAsState()

    Scaffold(
        topBar = { AuratechTopAppBar(title = strings.refundsLedger, onBackClick = onBack) }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding).background(MaterialTheme.colorScheme.background),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(refunds) { ref ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("عملية استرداد #${ref.transactionId}", fontWeight = FontWeight.Bold)
                            Surface(
                                color = if (ref.status == "مكتمل") SuccessGreen.copy(alpha = 0.15f) else Color.Gray.copy(alpha = 0.15f),
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    ref.status,
                                    color = if (ref.status == "مكتمل") SuccessGreen else Color.Gray,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                    style = MaterialTheme.typography.labelSmall
                                )
                            }
                        }
                        Text("السبب: ${ref.reason}", style = MaterialTheme.typography.bodyMedium)
                        Text("المبلغ: ${ref.amount.toInt()} ${strings.yerCurrency}", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        Text("محفظة التحويل: ${ref.eWalletNumber} | ${ref.date}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
    }
}

// ----------------------------------------------------
// Screen 33: Notifications Center
// ----------------------------------------------------
@Composable
fun NotificationsCenterScreen(viewModel: AuratechViewModel, onBack: () -> Unit) {
    val strings = LocalAppStrings.current
    val notifications by viewModel.notifications.collectAsState()

    Scaffold(
        topBar = {
            AuratechTopAppBar(
                title = strings.notificationsCenter,
                onBackClick = onBack,
                actions = {
                    TextButton(onClick = { viewModel.markAllNotificationsRead() }) {
                        Text(strings.markAllRead, color = MaterialTheme.colorScheme.primary)
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding).background(MaterialTheme.colorScheme.background),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(notifications) { notif ->
                Card(
                    modifier = Modifier.fillMaxWidth().clickable { viewModel.markNotificationRead(notif.id) },
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (!notif.isRead) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f) else MaterialTheme.colorScheme.surface
                    )
                ) {
                    Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.Top) {
                        Icon(
                            imageVector = when (notif.type) {
                                "appointment" -> Icons.Default.Event
                                "order" -> Icons.Default.DeliveryDining
                                else -> Icons.Default.LocalOffer
                            },
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(notif.title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(notif.body, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(notif.timeAgo, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                        }
                        IconButton(onClick = { viewModel.deleteNotification(notif.id) }) {
                            Icon(Icons.Default.Close, contentDescription = "Delete", modifier = Modifier.size(16.dp), tint = Color.Gray)
                        }
                    }
                }
            }
        }
    }
}

// ----------------------------------------------------
// Screen 34: Settings (Language, Dark Theme, Notifications)
// ----------------------------------------------------
@Composable
fun SettingsScreen(viewModel: AuratechViewModel, onBack: () -> Unit) {
    val strings = LocalAppStrings.current
    val currentLang by viewModel.language.collectAsState()
    val currentTheme by viewModel.themeMode.collectAsState()

    val notifReminders by viewModel.notifReminders.collectAsState()
    val notifOrders by viewModel.notifOrders.collectAsState()
    val notifPromos by viewModel.notifPromos.collectAsState()
    val notifMessages by viewModel.notifMessages.collectAsState()
    val notifAlerts by viewModel.notifAlerts.collectAsState()

    Scaffold(
        topBar = { AuratechTopAppBar(title = strings.settings, onBackClick = onBack) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Language
            Card(shape = RoundedCornerShape(14.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(strings.language, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Button(
                            onClick = { viewModel.setLanguage("ar") },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (currentLang == "ar") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                                contentColor = if (currentLang == "ar") Color.White else MaterialTheme.colorScheme.onSurface
                            )
                        ) { Text(strings.arabic) }

                        Button(
                            onClick = { viewModel.setLanguage("en") },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (currentLang == "en") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                                contentColor = if (currentLang == "en") Color.White else MaterialTheme.colorScheme.onSurface
                            )
                        ) { Text(strings.english) }
                    }
                }
            }

            // Theme Mode
            Card(shape = RoundedCornerShape(14.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(strings.theme, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FilterChip(
                            selected = currentTheme == "system",
                            onClick = { viewModel.setTheme("system") },
                            label = { Text(strings.systemTheme) }
                        )
                        FilterChip(
                            selected = currentTheme == "light",
                            onClick = { viewModel.setTheme("light") },
                            label = { Text(strings.lightTheme) }
                        )
                        FilterChip(
                            selected = currentTheme == "dark",
                            onClick = { viewModel.setTheme("dark") },
                            label = { Text(strings.darkTheme) }
                        )
                    }
                }
            }

            // Notification Toggles
            Card(shape = RoundedCornerShape(14.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("إعدادات التنبيهات والإشعارات", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    NotificationToggleRow("تذكير بمواعيد الكشف الطبي", notifReminders) { viewModel.updateNotifPref("reminders", it) }
                    NotificationToggleRow("حالة تجهيز وتوصيل الأدوية", notifOrders) { viewModel.updateNotifPref("orders", it) }
                    NotificationToggleRow("العروض والنصائح الطبية الأسبوعية", notifPromos) { viewModel.updateNotifPref("promos", it) }
                    NotificationToggleRow("رسائل خدمة العملاء والمحادثات", notifMessages) { viewModel.updateNotifPref("messages", it) }
                    NotificationToggleRow("تنبيهات النظام والطوارئ", notifAlerts) { viewModel.updateNotifPref("alerts", it) }
                }
            }
        }
    }
}

@Composable
fun NotificationToggleRow(title: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(title, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1f))
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}

// ----------------------------------------------------
// Screen 35: Help Center & FAQs
// ----------------------------------------------------
@Composable
fun HelpCenterScreen(viewModel: AuratechViewModel, onNavigateToChat: () -> Unit, onBack: () -> Unit) {
    val strings = LocalAppStrings.current
    var expandedFaqIndex by remember { mutableStateOf<Int?>(null) }

    Scaffold(
        topBar = { AuratechTopAppBar(title = strings.helpCenter, onBackClick = onBack) }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding).background(MaterialTheme.colorScheme.background),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("هل تحتاج مساعدة عاجلة؟", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Text("فريق الدعم الفني الطبي متاح لمساعدتك على مدار الساعة", style = MaterialTheme.typography.bodySmall)
                        Spacer(modifier = Modifier.height(10.dp))
                        Button(onClick = onNavigateToChat) {
                            Icon(Icons.Default.Chat, contentDescription = null)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(strings.liveSupportChat)
                        }
                    }
                }
            }

            items(MockData.faqs.size) { i ->
                val faq = MockData.faqs[i]
                val isExpanded = expandedFaqIndex == i
                Card(
                    modifier = Modifier.fillMaxWidth().clickable { expandedFaqIndex = if (isExpanded) null else i },
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Text(faq.question, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                            Icon(if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown, contentDescription = null)
                        }
                        if (isExpanded) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(faq.answer, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
        }
    }
}

// ----------------------------------------------------
// Screen 36: Live Support Chat
// ----------------------------------------------------
@Composable
fun SupportChatScreen(viewModel: AuratechViewModel, onBack: () -> Unit) {
    val strings = LocalAppStrings.current
    val messages by viewModel.chatMessages.collectAsState()
    var inputText by remember { mutableStateOf("") }

    Scaffold(
        topBar = { AuratechTopAppBar(title = strings.liveSupportChat, onBackClick = onBack) },
        bottomBar = {
            Surface(color = MaterialTheme.colorScheme.surface, shadowElevation = 8.dp) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = inputText,
                        onValueChange = { inputText = it },
                        modifier = Modifier.weight(1f),
                        placeholder = { Text("اكتب استفسارك هنا...") },
                        shape = RoundedCornerShape(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(
                        onClick = {
                            if (inputText.isNotBlank()) {
                                viewModel.sendChatMessage(inputText)
                                inputText = ""
                            }
                        }
                    ) {
                        Icon(Icons.Default.Send, contentDescription = "Send", tint = MaterialTheme.colorScheme.primary)
                    }
                }
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding).background(MaterialTheme.colorScheme.background),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(messages) { msg ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = if (msg.isUser) Arrangement.End else Arrangement.Start
                ) {
                    Surface(
                        color = if (msg.isUser) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(
                            topStart = 16.dp,
                            topEnd = 16.dp,
                            bottomStart = if (msg.isUser) 16.dp else 0.dp,
                            bottomEnd = if (msg.isUser) 0.dp else 16.dp
                        ),
                        modifier = Modifier.widthIn(max = 280.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = msg.messageText,
                                color = if (msg.isUser) Color.White else MaterialTheme.colorScheme.onSurface,
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = msg.timestamp,
                                color = if (msg.isUser) Color.White.copy(alpha = 0.7f) else Color.Gray,
                                style = MaterialTheme.typography.labelSmall,
                                modifier = Modifier.align(Alignment.End)
                            )
                        }
                    }
                }
            }
        }
    }
}

// ----------------------------------------------------
// Screen 37: Report a Problem
// ----------------------------------------------------
@Composable
fun ReportProblemScreen(viewModel: AuratechViewModel, onBack: () -> Unit) {
    val strings = LocalAppStrings.current
    var topic by remember { mutableStateOf("حجز العيادات") }
    var description by remember { mutableStateOf("") }
    var screenshotAttached by remember { mutableStateOf(false) }

    Scaffold(
        topBar = { AuratechTopAppBar(title = strings.reportProblem, onBackClick = onBack) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text("اختر نوع المشكلة:", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            val topics = listOf("حجز العيادات", "طلب وتوصيل الأدوية", "التحويل الإلكتروني والدفع", "تطبيق أوراتك الفني", "أخرى")
            topics.forEach { t ->
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clickable { topic = t }) {
                    RadioButton(selected = topic == t, onClick = { topic = t })
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(t)
                }
            }

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("اشرح المشكلة بالتفصيل") },
                modifier = Modifier.fillMaxWidth().height(120.dp),
                shape = RoundedCornerShape(12.dp)
            )

            OutlinedButton(
                onClick = {
                    screenshotAttached = true
                    viewModel.showMessage("تم إرفاق لقطة الشاشة")
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.AttachFile, contentDescription = null)
                Spacer(modifier = Modifier.width(6.dp))
                Text(if (screenshotAttached) "تم إرفاق صورة الشاشة" else "إرفاق لقطة شاشة (اختياري)")
            }

            Button(
                onClick = {
                    if (description.isNotBlank()) {
                        viewModel.submitReportProblem(topic, description)
                        onBack()
                    }
                },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("إرسال البلاغ", fontWeight = FontWeight.Bold)
            }
        }
    }
}

// ----------------------------------------------------
// Screen 38: Join Us as Medical Facility (B2B)
// ----------------------------------------------------
@Composable
fun JoinUsB2BScreen(viewModel: AuratechViewModel, onBack: () -> Unit) {
    val strings = LocalAppStrings.current
    var facilityName by remember { mutableStateOf("") }
    var facilityType by remember { mutableStateOf("عيادة تخصصية") }
    var contactPerson by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("صنعاء") }

    Scaffold(
        topBar = { AuratechTopAppBar(title = strings.joinUsB2B, onBackClick = onBack) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text("انضم لمنظومة أوراتك الطبية", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            Text("سجل عيادتك، صيدليتك أو برجك الطبي للوصول لآلاف المرضى وتفعيل نظام الحجوزات الذكية", style = MaterialTheme.typography.bodyMedium)

            OutlinedTextField(value = facilityName, onValueChange = { facilityName = it }, label = { Text("اسم المنشأة الطبية") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = facilityType, onValueChange = { facilityType = it }, label = { Text("نوع المنشأة (عيادة، صيدلية، برج طبي، مستشفى)") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = contactPerson, onValueChange = { contactPerson = it }, label = { Text("اسم الشخص المسؤول") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = phone, onValueChange = { phone = it }, label = { Text("رقم هاتف التواصل") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = city, onValueChange = { city = it }, label = { Text("المدينة / المحافظة") }, modifier = Modifier.fillMaxWidth())

            Button(
                onClick = {
                    if (facilityName.isNotBlank() && phone.isNotBlank()) {
                        viewModel.submitJoinUsRequest(facilityName)
                        onBack()
                    }
                },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("إرسال طلب الانضمام", fontWeight = FontWeight.Bold)
            }
        }
    }
}

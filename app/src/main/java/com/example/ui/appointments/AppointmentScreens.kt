package com.example.ui.appointments

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.mock.MockData
import com.example.data.models.Appointment
import com.example.data.models.AppointmentStatus
import com.example.data.models.Doctor
import com.example.localization.LocalAppStrings
import com.example.localization.LocalLanguage
import com.example.ui.common.*
import com.example.ui.theme.DangerRed
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.WarningOrange
import com.example.viewmodel.AuratechViewModel

@Composable
fun BookAppointmentScreen(
    doctorId: String,
    viewModel: AuratechViewModel,
    onNavigateToPayment: (String, String, String) -> Unit,
    onBack: () -> Unit
) {
    val strings = LocalAppStrings.current
    val lang = LocalLanguage.current
    val doctor = remember(doctorId) {
        MockData.doctors.find { it.id == doctorId } ?: MockData.doctors.first()
    }

    val dates = listOf(
        "اليوم (20 سبتمبر)" to "Today (Sep 20)",
        "غداً (21 سبتمبر)" to "Tomorrow (Sep 21)",
        "الأحد (22 سبتمبر)" to "Sun (Sep 22)",
        "الإثنين (23 سبتمبر)" to "Mon (Sep 23)",
        "الثلاثاء (24 سبتمبر)" to "Tue (Sep 24)",
        "الأربعاء (25 سبتمبر)" to "Wed (Sep 25)",
        "الخميس (26 سبتمبر)" to "Thu (Sep 26)"
    )
    var selectedDateIndex by remember { mutableStateOf(0) }

    val slots = listOf(
        "09:30 ص", "10:00 ص", "10:30 ص", "11:00 ص",
        "11:30 ص", "12:00 م", "04:30 م", "05:00 م",
        "05:30 م", "06:00 م", "06:30 م", "07:00 م"
    )
    var selectedSlot by remember { mutableStateOf(slots[0]) }

    Scaffold(
        topBar = { AuratechTopAppBar(title = strings.bookAppointment, onBackClick = onBack) },
        bottomBar = {
            Surface(
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 8.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(strings.consultationFee, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(
                            text = "${doctor.consultationFee.toInt()} ${strings.yerCurrency}",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    Button(
                        onClick = {
                            val chosenDate = dates[selectedDateIndex].first
                            onNavigateToPayment(doctor.id, chosenDate, selectedSlot)
                        },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.height(48.dp)
                    ) {
                        Text(strings.confirmBooking, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
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
            // Doctor Card
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                    AsyncImage(
                        model = doctor.avatarUrl,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp).clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (lang == "en") doctor.nameEn else doctor.nameAr,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = if (lang == "en") doctor.specialtyEn else doctor.specialtyAr,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = if (lang == "en") doctor.clinicNameEn else doctor.clinicNameAr,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Live Queue Status Banner
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.HourglassTop, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("حالة الطابور اللحظي في العيادة", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("الكشف الحالي", style = MaterialTheme.typography.labelSmall)
                            Text("#12", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("متوسط الانتظار", style = MaterialTheme.typography.labelSmall)
                            Text("25 دقيقة", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("دورك المتوقع", style = MaterialTheme.typography.labelSmall)
                            Text("#13", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = SuccessGreen)
                        }
                    }
                }
            }

            // Date Selector (7 Days)
            Text(strings.selectDate, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                items(dates.size) { i ->
                    val isSelected = selectedDateIndex == i
                    Surface(
                        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
                        shape = RoundedCornerShape(12.dp),
                        shadowElevation = if (isSelected) 4.dp else 1.dp,
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { selectedDateIndex = i }
                    ) {
                        Column(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = if (lang == "en") dates[i].second else dates[i].first,
                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // Time Slots Grid
            Text(strings.selectTime, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                val rowChunked = slots.chunked(3)
                rowChunked.forEach { rowSlots ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        rowSlots.forEach { slot ->
                            val isSelected = selectedSlot == slot
                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .clickable { selectedSlot = slot },
                                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
                                shape = RoundedCornerShape(10.dp),
                                shadowElevation = if (isSelected) 3.dp else 1.dp
                            ) {
                                Text(
                                    text = slot,
                                    modifier = Modifier.padding(vertical = 12.dp),
                                    textAlign = TextAlign.Center,
                                    color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.labelLarge
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AppointmentPaymentScreen(
    doctorId: String,
    date: String,
    slot: String,
    viewModel: AuratechViewModel,
    onBookingSuccess: (String) -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val strings = LocalAppStrings.current
    val lang = LocalLanguage.current
    val doctor = remember(doctorId) {
        MockData.doctors.find { it.id == doctorId } ?: MockData.doctors.first()
    }

    var receiptUploaded by remember { mutableStateOf(false) }
    var showOcrDialog by remember { mutableStateOf(false) }
    var ocrAmount by remember { mutableStateOf("${doctor.consultationFee.toInt()} ريال") }
    var ocrReceiptNumber by remember { mutableStateOf("91823") }

    Scaffold(
        topBar = { AuratechTopAppBar(title = "تأكيد ودفع الكشف الطبي", onBackClick = onBack) }
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
            // Appointment Info Card
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = if (lang == "en") doctor.nameEn else doctor.nameAr,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${doctor.specialtyAr} - ${doctor.clinicNameAr}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    HorizontalDivider()
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("التاريخ والموعد:", style = MaterialTheme.typography.bodyMedium)
                        Text("$date ($slot)", fontWeight = FontWeight.Bold)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("رسوم الكشف الطبي:", style = MaterialTheme.typography.bodyMedium)
                        Text(
                            "${doctor.consultationFee.toInt()} ${strings.yerCurrency}",
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            // E-Wallet Card
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "طريقة التحويل الإلكتروني", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(10.dp))

                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(strings.eWalletNumber, style = MaterialTheme.typography.labelSmall)
                                Text("777123456", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                            }
                            Button(
                                onClick = {
                                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                    val clip = ClipData.newPlainText("E-Wallet Number", "777123456")
                                    clipboard.setPrimaryClip(clip)
                                    viewModel.showMessage(strings.copied)
                                },
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(strings.copy)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "حوّل مبلغ رسوم الكشف إلى المحفظة ثم ارفع الإشعار لتأكيد وتثبيت الحجز فوراً.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    if (receiptUploaded) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = SuccessGreen.copy(alpha = 0.15f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = SuccessGreen)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text("تم قراءة الإشعار بنجاح", fontWeight = FontWeight.Bold, color = SuccessGreen)
                                        Text("رقم الإشعار: $ocrReceiptNumber ($ocrAmount)", style = MaterialTheme.typography.bodySmall)
                                    }
                                }
                                TextButton(onClick = { showOcrDialog = true }) {
                                    Text(strings.edit)
                                }
                            }
                        }
                    } else {
                        OutlinedButton(
                            onClick = {
                                receiptUploaded = true
                                showOcrDialog = true
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(Icons.Default.UploadFile, contentDescription = null)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(strings.uploadPaymentReceipt, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    val apt = viewModel.bookAppointment(doctor, date, slot)
                    onBookingSuccess(apt.id)
                },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(strings.confirmBooking, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }
        }
    }

    if (showOcrDialog) {
        AlertDialog(
            onDismissRequest = { showOcrDialog = false },
            icon = {
                Icon(Icons.Default.DocumentScanner, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(36.dp))
            },
            title = { Text(strings.ocrTitle, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(strings.ocrNote, style = MaterialTheme.typography.bodySmall)
                    OutlinedTextField(
                        value = ocrAmount,
                        onValueChange = { ocrAmount = it },
                        label = { Text(strings.amount) },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = ocrReceiptNumber,
                        onValueChange = { ocrReceiptNumber = it },
                        label = { Text(strings.receiptNumber) },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(onClick = { showOcrDialog = false }) { Text(strings.confirm) }
            },
            dismissButton = {
                TextButton(onClick = { showOcrDialog = false }) { Text(strings.correctManually) }
            }
        )
    }
}

@Composable
fun BookingSuccessScreen(
    appointmentId: String,
    viewModel: AuratechViewModel,
    onNavigateToMyBookings: () -> Unit,
    onNavigateToHome: () -> Unit
) {
    val strings = LocalAppStrings.current
    val appointments by viewModel.appointments.collectAsState()
    val apt = remember(appointmentId, appointments) {
        appointments.find { it.id == appointmentId } ?: appointments.firstOrNull() ?: MockData.appointments.first()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Confetti Celebration
        ConfettiCelebration()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(20.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(SuccessGreen),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(48.dp))
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = strings.bookingSuccessTitle,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = strings.bookingSuccessSubtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Ticket Card with REAL ZXing QR Code
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "بطاقة دخول العيادة الإلكترونية",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    // Real ZXing QR Code Component
                    AuratechQrCode(
                        content = apt.qrCodeData,
                        size = 180.dp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Surface(
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "${strings.yourQueueNumber}: #${apt.queueNumber}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    HorizontalDivider()
                    Spacer(modifier = Modifier.height(14.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("الطبيب:", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(apt.doctorNameAr, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("العيادة:", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(apt.clinicNameAr, fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.bodyMedium)
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("الموعد:", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("${apt.date} (${apt.timeSlot})", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = { viewModel.showMessage("تم حفظ تذكرة الحجز كصورة في جهازك") },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(strings.saveTicket, fontSize = 12.sp)
                }

                OutlinedButton(
                    onClick = { viewModel.showMessage("تمت مشاركة تذكرة الحجز بنجاح") },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(strings.shareTicket, fontSize = 12.sp)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Button(
                onClick = onNavigateToMyBookings,
                modifier = Modifier.fillMaxWidth().height(48.dp),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(strings.viewInBookings, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun MyBookingsScreen(
    viewModel: AuratechViewModel,
    onNavigateToHome: () -> Unit
) {
    val strings = LocalAppStrings.current
    val lang = LocalLanguage.current
    val appointments by viewModel.appointments.collectAsState()
    var selectedTab by remember { mutableStateOf(0) } // 0 = Upcoming, 1 = Past, 2 = Cancelled

    var showQrDialogForApt by remember { mutableStateOf<Appointment?>(null) }
    var showRatingDialogForApt by remember { mutableStateOf<Appointment?>(null) }
    var showCancelDialogForApt by remember { mutableStateOf<Appointment?>(null) }

    val filteredAppointments = remember(selectedTab, appointments) {
        when (selectedTab) {
            0 -> appointments.filter { it.status == AppointmentStatus.CONFIRMED }
            1 -> appointments.filter { it.status == AppointmentStatus.COMPLETED }
            else -> appointments.filter { it.status == AppointmentStatus.CANCELED }
        }
    }

    Scaffold(
        topBar = {
            AuratechTopAppBar(title = strings.myBookings)
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            TabRow(selectedTabIndex = selectedTab) {
                Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }, text = { Text(strings.upcomingBookings) })
                Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }, text = { Text(strings.pastBookings) })
                Tab(selected = selectedTab == 2, onClick = { selectedTab = 2 }, text = { Text(strings.cancelledBookings) })
            }

            if (filteredAppointments.isEmpty()) {
                EmptyStateView(
                    icon = Icons.Default.CalendarToday,
                    title = strings.noBookings,
                    description = "لا توجد مواعيد في هذا القسم حالياً",
                    actionLabel = strings.bookAppointment,
                    onActionClick = onNavigateToHome
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    items(filteredAppointments) { apt ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Column {
                                        Text(
                                            text = if (lang == "en") apt.doctorNameEn else apt.doctorNameAr,
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = if (lang == "en") "${apt.specialtyEn} - ${apt.clinicNameEn}" else "${apt.specialtyAr} - ${apt.clinicNameAr}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }

                                    Surface(
                                        color = when (apt.status) {
                                            AppointmentStatus.CONFIRMED -> SuccessGreen.copy(alpha = 0.15f)
                                            AppointmentStatus.COMPLETED -> MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                                            else -> DangerRed.copy(alpha = 0.15f)
                                        },
                                        shape = RoundedCornerShape(6.dp)
                                    ) {
                                        Text(
                                            text = when (apt.status) {
                                                AppointmentStatus.CONFIRMED -> "مؤكد (#${apt.queueNumber})"
                                                AppointmentStatus.COMPLETED -> "مكتمل"
                                                AppointmentStatus.NO_SHOW -> "لم يحضر"
                                                AppointmentStatus.CANCELLED_BY_CLINIC -> "ملغي من العيادة"
                                                else -> "ملغي"
                                            },
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = when (apt.status) {
                                                AppointmentStatus.CONFIRMED -> SuccessGreen
                                                AppointmentStatus.COMPLETED -> MaterialTheme.colorScheme.primary
                                                else -> DangerRed
                                            }
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Event, contentDescription = null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.primary)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("${apt.date} | ${apt.timeSlot}", style = MaterialTheme.typography.bodySmall)
                                }

                                Spacer(modifier = Modifier.height(12.dp))
                                HorizontalDivider()
                                Spacer(modifier = Modifier.height(10.dp))

                                when (apt.status) {
                                    AppointmentStatus.CONFIRMED -> {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            Button(
                                                onClick = { showQrDialogForApt = apt },
                                                modifier = Modifier.weight(1f),
                                                shape = RoundedCornerShape(8.dp)
                                            ) {
                                                Icon(Icons.Default.QrCode, contentDescription = null, modifier = Modifier.size(16.dp))
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text(strings.showQrCode, fontSize = 11.sp)
                                            }
                                            OutlinedButton(
                                                onClick = { viewModel.showMessage("يمكنك اختيار موعد جديد بالضغط على حجز من جديد") },
                                                modifier = Modifier.weight(1f),
                                                shape = RoundedCornerShape(8.dp)
                                            ) {
                                                Text(strings.reschedule, fontSize = 11.sp)
                                            }
                                            OutlinedButton(
                                                onClick = { showCancelDialogForApt = apt },
                                                modifier = Modifier.weight(1f),
                                                shape = RoundedCornerShape(8.dp),
                                                colors = ButtonDefaults.outlinedButtonColors(contentColor = DangerRed)
                                            ) {
                                                Text(strings.cancelAppointment, fontSize = 11.sp)
                                            }
                                        }
                                    }
                                    AppointmentStatus.COMPLETED -> {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            if (apt.userRating != null) {
                                                RatingBadge(rating = apt.userRating.toDouble())
                                            } else {
                                                Text("لم يتم التقييم بعد", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                            }
                                            Button(
                                                onClick = { showRatingDialogForApt = apt },
                                                shape = RoundedCornerShape(8.dp)
                                            ) {
                                                Icon(Icons.Default.Star, contentDescription = null, modifier = Modifier.size(16.dp))
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text(strings.rateVisit)
                                            }
                                        }
                                    }
                                    else -> {
                                        Text(
                                            text = "سبب الإلغاء: ${apt.cancelReason ?: "طلب المريض"}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = DangerRed
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // QR Code Dialog
    if (showQrDialogForApt != null) {
        val apt = showQrDialogForApt!!
        AlertDialog(
            onDismissRequest = { showQrDialogForApt = null },
            title = { Text("رمز QR الحجز #${apt.appointmentNumber}", fontWeight = FontWeight.Bold) },
            text = {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                    AuratechQrCode(content = apt.qrCodeData, size = 200.dp)
                    Spacer(modifier = Modifier.height(10.dp))
                    Text("قدم هذا الرمز لموظف الاستقبال عند وصولك للعيادة", style = MaterialTheme.typography.bodySmall, textAlign = TextAlign.Center)
                }
            },
            confirmButton = {
                Button(onClick = { showQrDialogForApt = null }) { Text(strings.confirm) }
            }
        )
    }

    // Rating Dialog
    if (showRatingDialogForApt != null) {
        val apt = showRatingDialogForApt!!
        var ratingVal by remember { mutableStateOf(5) }
        AlertDialog(
            onDismissRequest = { showRatingDialogForApt = null },
            icon = { Icon(Icons.Default.Star, contentDescription = null, tint = WarningOrange, modifier = Modifier.size(36.dp)) },
            title = { Text(strings.rateVisit, fontWeight = FontWeight.Bold) },
            text = {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                    Text("كيف كانت تجربتك مع ${apt.doctorNameAr}؟", style = MaterialTheme.typography.bodyMedium)
                    Spacer(modifier = Modifier.height(14.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        (1..5).forEach { star ->
                            IconButton(onClick = { ratingVal = star }) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = null,
                                    tint = if (star <= ratingVal) WarningOrange else Color.LightGray,
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.rateAppointment(apt.id, ratingVal.toFloat())
                        showRatingDialogForApt = null
                    }
                ) {
                    Text(strings.confirm)
                }
            },
            dismissButton = {
                TextButton(onClick = { showRatingDialogForApt = null }) {
                    Text(strings.later)
                }
            }
        )
    }

    // Cancel Dialog
    if (showCancelDialogForApt != null) {
        val apt = showCancelDialogForApt!!
        AlertDialog(
            onDismissRequest = { showCancelDialogForApt = null },
            icon = { Icon(Icons.Default.Warning, contentDescription = null, tint = DangerRed, modifier = Modifier.size(36.dp)) },
            title = { Text(strings.cancelAppointment, fontWeight = FontWeight.Bold) },
            text = { Text("هل أنت متأكد من رغبتك في إلغاء حجز الموعد لدى ${apt.doctorNameAr}؟") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.cancelAppointment(apt.id)
                        showCancelDialogForApt = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = DangerRed)
                ) {
                    Text(strings.cancelAppointment)
                }
            },
            dismissButton = {
                TextButton(onClick = { showCancelDialogForApt = null }) { Text(strings.notNow) }
            }
        )
    }
}

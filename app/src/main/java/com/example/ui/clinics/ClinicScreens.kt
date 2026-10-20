package com.example.ui.clinics

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.mock.MockData
import com.example.data.models.Clinic
import com.example.data.models.Doctor
import com.example.data.models.Specialty
import com.example.localization.LocalAppStrings
import com.example.localization.LocalLanguage
import com.example.ui.common.*
import com.example.viewmodel.AuratechViewModel

@Composable
fun ClinicsListScreen(
    onNavigateToDetail: (String) -> Unit,
    onNavigateToSearchFilter: () -> Unit,
    onBack: () -> Unit
) {
    val strings = LocalAppStrings.current
    val lang = LocalLanguage.current
    var searchQuery by remember { mutableStateOf("") }

    val filteredClinics = remember(searchQuery) {
        if (searchQuery.isBlank()) MockData.clinics
        else MockData.clinics.filter {
            it.nameAr.contains(searchQuery, ignoreCase = true) ||
            it.nameEn.contains(searchQuery, ignoreCase = true) ||
            it.specialtyAr.contains(searchQuery, ignoreCase = true)
        }
    }

    Scaffold(
        topBar = {
            AuratechTopAppBar(
                title = strings.clinics,
                onBackClick = onBack,
                actions = {
                    IconButton(onClick = onNavigateToSearchFilter) {
                        Icon(Icons.Default.FilterList, contentDescription = strings.searchFilter)
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                placeholder = { Text(strings.searchPlaceholder) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                shape = RoundedCornerShape(14.dp),
                singleLine = true
            )

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                items(filteredClinics) { clinic ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onNavigateToDetail(clinic.id) },
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            AsyncImage(
                                model = clinic.imageUrl,
                                contentDescription = null,
                                modifier = Modifier
                                    .size(80.dp)
                                    .clip(RoundedCornerShape(12.dp)),
                                contentScale = ContentScale.Crop
                            )
                            Spacer(modifier = Modifier.width(14.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = if (lang == "en") clinic.nameEn else clinic.nameAr,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = if (lang == "en") clinic.specialtyEn else clinic.specialtyAr,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = if (lang == "en") clinic.addressEn else clinic.addressAr,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    RatingBadge(rating = clinic.rating, reviewsCount = clinic.reviewsCount)
                                    DistanceBadge(distanceKm = clinic.distanceKm)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ClinicSearchFilterScreen(
    viewModel: AuratechViewModel,
    onSpecialtySelected: (String) -> Unit,
    onBack: () -> Unit
) {
    val strings = LocalAppStrings.current
    val lang = LocalLanguage.current
    var query by remember { mutableStateOf("") }

    val specialties = remember(query) {
        if (query.isBlank()) MockData.specialties
        else MockData.specialties.filter {
            it.nameAr.contains(query, ignoreCase = true) || it.nameEn.contains(query, ignoreCase = true)
        }
    }

    Scaffold(
        topBar = {
            AuratechTopAppBar(title = strings.searchFilter, onBackClick = onBack)
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                placeholder = { Text("ابحث عن تخصص طبي أو عيادة...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                shape = RoundedCornerShape(14.dp),
                singleLine = true
            )

            Text(
                text = strings.chooseSpecialty,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )

            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(specialties) { spec ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                            .clickable {
                                viewModel.selectSpecialty(spec.id)
                                onSpecialtySelected(spec.id)
                            },
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(text = spec.icon, fontSize = 28.sp)
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = if (lang == "en") spec.nameEn else spec.nameAr,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.SemiBold,
                                textAlign = TextAlign.Center,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ClinicProfileScreen(
    clinicId: String,
    viewModel: AuratechViewModel,
    onNavigateToBookDoctor: (String) -> Unit,
    onBack: () -> Unit
) {
    val strings = LocalAppStrings.current
    val lang = LocalLanguage.current
    val clinic = remember(clinicId) {
        MockData.clinics.find { it.id == clinicId } ?: MockData.clinics.first()
    }
    val doctors = remember(clinic) {
        MockData.doctors.filter { it.clinicId == clinic.id }
    }

    Scaffold(
        topBar = {
            AuratechTopAppBar(
                title = if (lang == "en") clinic.nameEn else clinic.nameAr,
                onBackClick = onBack
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            // Cover Image
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp)
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
                            .padding(12.dp),
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
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                        DistanceBadge(distanceKm = clinic.distanceKm)
                    }
                }
            }

            // Clinic Info Card
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = if (lang == "en") clinic.nameEn else clinic.nameAr,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (lang == "en") clinic.addressEn else clinic.addressAr,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RatingBadge(rating = clinic.rating, reviewsCount = clinic.reviewsCount)
                            Text(
                                text = if (lang == "en") clinic.workingHoursEn else clinic.workingHoursAr,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }

            // Sections Inside Clinic
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = strings.clinicSections,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        for (sec in clinic.sectionNamesAr) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(vertical = 4.dp)
                            ) {
                                Icon(Icons.Default.Check, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(sec, style = MaterialTheme.typography.bodyMedium)
                            }
                        }
                    }
                }
            }

            // Doctors in this clinic
            item {
                Spacer(modifier = Modifier.height(16.dp))
                SectionHeader(title = strings.doctors)
            }

            items(doctors) { doctor ->
                DoctorCardItem(
                    doctor = doctor,
                    lang = lang,
                    strings = strings,
                    onBookClick = { onNavigateToBookDoctor(doctor.id) }
                )
            }
        }
    }
}

@Composable
fun DoctorsListScreen(
    viewModel: AuratechViewModel,
    onNavigateToBook: (String) -> Unit,
    onBack: () -> Unit
) {
    val strings = LocalAppStrings.current
    val lang = LocalLanguage.current
    var searchQuery by remember { mutableStateOf("") }
    val selectedSpecialtyId by viewModel.selectedSpecialtyId.collectAsState()

    val filteredDoctors = remember(searchQuery, selectedSpecialtyId) {
        var list = MockData.doctors
        if (selectedSpecialtyId != null) {
            val spec = MockData.specialties.find { it.id == selectedSpecialtyId }
            if (spec != null) {
                list = list.filter { it.specialtyAr.contains(spec.nameAr) || it.specialtyEn.contains(spec.nameEn) }
            }
        }
        if (searchQuery.isNotBlank()) {
            list = list.filter {
                it.nameAr.contains(searchQuery, ignoreCase = true) ||
                it.nameEn.contains(searchQuery, ignoreCase = true) ||
                it.specialtyAr.contains(searchQuery, ignoreCase = true)
            }
        }
        list
    }

    Scaffold(
        topBar = {
            AuratechTopAppBar(
                title = strings.doctors,
                onBackClick = onBack,
                actions = {
                    if (selectedSpecialtyId != null) {
                        TextButton(onClick = { viewModel.selectSpecialty(null) }) {
                            Text("إلغاء التصفية", color = MaterialTheme.colorScheme.primary)
                        }
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                placeholder = { Text("ابحث عن اسم الطبيب أو التخصص...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                shape = RoundedCornerShape(14.dp),
                singleLine = true
            )

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                items(filteredDoctors) { doctor ->
                    DoctorCardItem(
                        doctor = doctor,
                        lang = lang,
                        strings = strings,
                        onBookClick = { onNavigateToBook(doctor.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun DoctorCardItem(
    doctor: Doctor,
    lang: String,
    strings: com.example.localization.AppStrings,
    onBookClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                AsyncImage(
                    model = doctor.avatarUrl,
                    contentDescription = null,
                    modifier = Modifier
                        .size(68.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.width(14.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (lang == "en") doctor.nameEn else doctor.nameAr,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = if (lang == "en") doctor.specialtyEn else doctor.specialtyAr,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = if (lang == "en") doctor.clinicNameEn else doctor.clinicNameAr,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(strings.consultationFee, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(
                        text = "${doctor.consultationFee.toInt()} ${strings.yerCurrency}",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Column {
                    Text(strings.nextAvailableSlot, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(
                        text = if (lang == "en") doctor.nextAvailableSlotEn else doctor.nextAvailableSlotAr,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Button(
                    onClick = onBookClick,
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(strings.bookAppointment, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

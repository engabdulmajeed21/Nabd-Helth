package com.example.ui.pharmacies

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.mock.MockData
import com.example.data.models.Drug
import com.example.data.models.Pharmacy
import com.example.localization.LocalAppStrings
import com.example.localization.LocalLanguage
import com.example.ui.common.*
import com.example.ui.theme.DangerRed
import com.example.ui.theme.SuccessGreen
import com.example.viewmodel.AuratechViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PharmaciesListScreen(
    onNavigateToDetail: (String) -> Unit,
    onNavigateToGlobalSearch: () -> Unit,
    onBack: () -> Unit
) {
    val strings = LocalAppStrings.current
    val lang = LocalLanguage.current
    var searchQuery by remember { mutableStateOf("") }

    val filteredPharmacies = remember(searchQuery) {
        if (searchQuery.isBlank()) MockData.pharmacies
        else MockData.pharmacies.filter {
            it.nameAr.contains(searchQuery, ignoreCase = true) ||
            it.nameEn.contains(searchQuery, ignoreCase = true) ||
            it.addressAr.contains(searchQuery, ignoreCase = true)
        }
    }

    Scaffold(
        topBar = {
            AuratechTopAppBar(
                title = strings.pharmacies,
                onBackClick = onBack,
                actions = {
                    IconButton(onClick = onNavigateToGlobalSearch) {
                        Icon(Icons.Default.Search, contentDescription = strings.globalDrugSearch)
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
            // Search Input
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

            // Global Drug Search Banner
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
                    .clickable { onNavigateToGlobalSearch() },
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f))
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Medication,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = strings.globalDrugSearch,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "ابحث عن أي دواء لمقارنة الأسعار وتوفر التوصيل",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Icon(Icons.Default.ArrowForward, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                }
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                items(filteredPharmacies) { pharmacy ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onNavigateToDetail(pharmacy.id) },
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            AsyncImage(
                                model = pharmacy.imageUrl,
                                contentDescription = null,
                                modifier = Modifier
                                    .size(80.dp)
                                    .clip(RoundedCornerShape(12.dp)),
                                contentScale = ContentScale.Crop
                            )
                            Spacer(modifier = Modifier.width(14.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = if (lang == "en") pharmacy.nameEn else pharmacy.nameAr,
                                    style = MaterialTheme.typography.titleMedium,
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
                                Spacer(modifier = Modifier.height(6.dp))
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    RatingBadge(rating = pharmacy.rating, reviewsCount = pharmacy.reviewsCount)
                                    DistanceBadge(distanceKm = pharmacy.distanceKm)
                                    if (pharmacy.delivers) {
                                        Surface(
                                            color = SuccessGreen.copy(alpha = 0.15f),
                                            shape = RoundedCornerShape(6.dp)
                                        ) {
                                            Text(
                                                text = strings.delivers,
                                                color = SuccessGreen,
                                                style = MaterialTheme.typography.labelSmall,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
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
    }
}

@Composable
fun GlobalDrugSearchScreen(
    viewModel: AuratechViewModel,
    onNavigateToPharmacyDetail: (String) -> Unit,
    onBack: () -> Unit
) {
    val strings = LocalAppStrings.current
    val lang = LocalLanguage.current
    val query by viewModel.drugSearchQuery.collectAsState()
    val activeFilter by viewModel.drugFilter.collectAsState()
    val drugs by viewModel.filteredDrugs.collectAsState()

    Scaffold(
        topBar = {
            AuratechTopAppBar(title = strings.globalDrugSearch, onBackClick = onBack)
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Search Input
            OutlinedTextField(
                value = query,
                onValueChange = { viewModel.setDrugSearchQuery(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                placeholder = { Text(strings.searchMedicinePlaceholder) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                trailingIcon = {
                    if (query.isNotEmpty()) {
                        IconButton(onClick = { viewModel.setDrugSearchQuery("") }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear")
                        }
                    }
                },
                shape = RoundedCornerShape(14.dp),
                singleLine = true
            )

            // 4 Filter Chips: All, Delivers, Nearby, Top Rated
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = activeFilter == "all",
                    onClick = { viewModel.setDrugFilter("all") },
                    label = { Text(strings.filterAll) }
                )
                FilterChip(
                    selected = activeFilter == "delivers",
                    onClick = { viewModel.setDrugFilter("delivers") },
                    label = { Text(strings.filterDelivers) }
                )
                FilterChip(
                    selected = activeFilter == "nearby",
                    onClick = { viewModel.setDrugFilter("nearby") },
                    label = { Text(strings.filterNearby) }
                )
                FilterChip(
                    selected = activeFilter == "top_rated",
                    onClick = { viewModel.setDrugFilter("top_rated") },
                    label = { Text(strings.filterTopRated) }
                )
            }

            if (drugs.isEmpty()) {
                EmptyStateView(
                    icon = Icons.Default.SearchOff,
                    title = strings.noResultsFor + " \"$query\"",
                    description = "تأكد من كتابة اسم الدواء بشكل صحيح أو تصفح القوائم العامة",
                    actionLabel = "عرض جميع الأدوية",
                    onActionClick = { viewModel.setDrugSearchQuery("") }
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(drugs) { drug ->
                        val isOutOfRange = drug.distanceKm > 5.0
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
                                    Column(modifier = Modifier.weight(1f)) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                text = drug.name,
                                                style = MaterialTheme.typography.titleMedium,
                                                fontWeight = FontWeight.Bold
                                            )
                                            if (drug.requiresPrescription) {
                                                Spacer(modifier = Modifier.width(6.dp))
                                                PrescriptionBadge()
                                            }
                                        }
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = drug.dosage,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }

                                    Text(
                                        text = "${drug.price.toInt()} ${strings.yerCurrency}",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.clickable { onNavigateToPharmacyDetail(drug.pharmacyId) }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.LocalPharmacy,
                                        contentDescription = null,
                                        modifier = Modifier.size(15.dp),
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = if (lang == "en") drug.pharmacyNameEn else drug.pharmacyNameAr,
                                        style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    DistanceBadge(distanceKm = drug.distanceKm)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    RatingBadge(rating = drug.rating)
                                }

                                if (isOutOfRange) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Surface(
                                        color = DangerRed.copy(alpha = 0.1f),
                                        shape = RoundedCornerShape(6.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text(
                                            text = strings.outOfRange,
                                            color = DangerRed,
                                            style = MaterialTheme.typography.labelSmall,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                Button(
                                    onClick = { viewModel.addToCart(drug) },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Icon(Icons.Default.AddShoppingCart, contentDescription = null, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(strings.addToCart, fontWeight = FontWeight.Bold)
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
fun PharmacyProfileScreen(
    pharmacyId: String,
    viewModel: AuratechViewModel,
    onNavigateToCart: () -> Unit,
    onBack: () -> Unit
) {
    val strings = LocalAppStrings.current
    val lang = LocalLanguage.current
    val pharmacy = remember(pharmacyId) {
        MockData.pharmacies.find { it.id == pharmacyId } ?: MockData.pharmacies.first()
    }
    val cartItems by viewModel.cartItems.collectAsState()
    var searchInside by remember { mutableStateOf("") }

    val pharmacyDrugs = remember(pharmacy, searchInside) {
        val list = MockData.drugs.filter { it.pharmacyId == pharmacy.id }
        if (searchInside.isBlank()) list
        else list.filter { it.name.contains(searchInside, ignoreCase = true) }
    }

    Scaffold(
        topBar = {
            AuratechTopAppBar(
                title = if (lang == "en") pharmacy.nameEn else pharmacy.nameAr,
                onBackClick = onBack,
                actions = {
                    IconButton(onClick = onNavigateToCart) {
                        BadgedBox(
                            badge = {
                                if (cartItems.isNotEmpty()) {
                                    Badge { Text("${cartItems.sumOf { it.quantity }}") }
                                }
                            }
                        ) {
                            Icon(Icons.Default.ShoppingCart, contentDescription = "Cart")
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
            // Cover Image
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
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
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    OpenStatusBadge(isOpen = pharmacy.isOpen)
                    DistanceBadge(distanceKm = pharmacy.distanceKm)
                }
            }

            // Pharmacy Details Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = if (lang == "en") pharmacy.nameEn else pharmacy.nameAr,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (lang == "en") pharmacy.addressEn else pharmacy.addressAr,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RatingBadge(rating = pharmacy.rating, reviewsCount = pharmacy.reviewsCount)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.AccessTime,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (lang == "en") pharmacy.workingHoursEn else pharmacy.workingHoursAr,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }

            // Search Inside Pharmacy
            OutlinedTextField(
                value = searchInside,
                onValueChange = { searchInside = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                placeholder = { Text("ابحث عن دواء داخل هذه الصيدلية...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(pharmacyDrugs) { drug ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = drug.name,
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold
                                    )
                                    if (drug.requiresPrescription) {
                                        Spacer(modifier = Modifier.width(6.dp))
                                        PrescriptionBadge()
                                    }
                                }
                                Text(
                                    text = drug.dosage,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "${drug.price.toInt()} ${strings.yerCurrency}",
                                    style = MaterialTheme.typography.titleSmall,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Button(
                                onClick = { viewModel.addToCart(drug) },
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text(strings.addToCart, style = MaterialTheme.typography.labelMedium)
                            }
                        }
                    }
                }
            }
        }
    }
}

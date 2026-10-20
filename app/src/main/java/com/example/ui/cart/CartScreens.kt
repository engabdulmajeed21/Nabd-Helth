package com.example.ui.cart

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.Address
import com.example.data.models.CartItem
import com.example.localization.LocalAppStrings
import com.example.localization.LocalLanguage
import com.example.ui.common.*
import com.example.ui.theme.DangerRed
import com.example.ui.theme.SuccessGreen
import com.example.viewmodel.AuratechViewModel

@Composable
fun CartScreen(
    viewModel: AuratechViewModel,
    onNavigateToCheckout: () -> Unit,
    onBack: () -> Unit
) {
    val strings = LocalAppStrings.current
    val cartItems by viewModel.cartItems.collectAsState()
    var prescriptionUploaded by remember { mutableStateOf(false) }

    val requiresPrescription = cartItems.any { it.drug.requiresPrescription }
    val subtotal = cartItems.sumOf { it.drug.price * it.quantity }

    Scaffold(
        topBar = {
            AuratechTopAppBar(
                title = strings.myCart,
                onBackClick = onBack,
                actions = {
                    if (cartItems.isNotEmpty()) {
                        IconButton(onClick = { viewModel.clearCart() }) {
                            Icon(Icons.Default.DeleteSweep, contentDescription = "Clear")
                        }
                    }
                }
            )
        },
        bottomBar = {
            if (cartItems.isNotEmpty()) {
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    shadowElevation = 8.dp
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .navigationBarsPadding()
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = strings.total,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "${subtotal.toInt()} ${strings.yerCurrency}",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Button(
                            onClick = onNavigateToCheckout,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = strings.checkout,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    ) { padding ->
        if (cartItems.isEmpty()) {
            EmptyStateView(
                icon = Icons.Default.RemoveShoppingCart,
                title = "سلة المشتريات فارغة",
                description = "تصفح الصيدليات أو ابحث عن الأدوية لإضافتها إلى سلتك",
                actionLabel = "البحث عن الأدوية",
                onActionClick = onBack,
                modifier = Modifier.padding(padding)
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .background(MaterialTheme.colorScheme.background),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Prescription Notice Banner
                if (requiresPrescription) {
                    item {
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (prescriptionUploaded) SuccessGreen.copy(alpha = 0.12f) else DangerRed.copy(alpha = 0.12f)
                            )
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = if (prescriptionUploaded) Icons.Default.CheckCircle else Icons.Default.Warning,
                                        contentDescription = null,
                                        tint = if (prescriptionUploaded) SuccessGreen else DangerRed
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = if (prescriptionUploaded) "تم رفع الوصفة الطبية بنجاح (جاهزة للمراجعة)" else strings.prescriptionRequired,
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = if (prescriptionUploaded) SuccessGreen else DangerRed
                                    )
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                OutlinedButton(
                                    onClick = {
                                        prescriptionUploaded = true
                                        viewModel.showMessage("تمت محاكاة رفع صورة الوصفة الطبية بنجاح")
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Icon(Icons.Default.CameraAlt, contentDescription = null, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(if (prescriptionUploaded) "تغيير صورة الوصفة" else strings.uploadPrescription)
                                }
                            }
                        }
                    }
                }

                items(cartItems) { item ->
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
                                        text = item.drug.name,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    if (item.drug.requiresPrescription) {
                                        Spacer(modifier = Modifier.width(6.dp))
                                        PrescriptionBadge()
                                    }
                                }
                                Text(
                                    text = item.drug.dosage,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "${item.drug.price.toInt()} ${strings.yerCurrency}",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }

                            // Stepper
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(MaterialTheme.colorScheme.surfaceVariant)
                            ) {
                                IconButton(
                                    onClick = { viewModel.updateCartQuantity(item.drug.id, -1) },
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Icon(Icons.Default.Remove, contentDescription = "Decrease", modifier = Modifier.size(16.dp))
                                }
                                Text(
                                    text = "${item.quantity}",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp)
                                )
                                IconButton(
                                    onClick = { viewModel.updateCartQuantity(item.drug.id, 1) },
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Icon(Icons.Default.Add, contentDescription = "Increase", modifier = Modifier.size(16.dp))
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
fun CheckoutScreen(
    viewModel: AuratechViewModel,
    onOrderPlaced: (String) -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val strings = LocalAppStrings.current
    val lang = LocalLanguage.current
    val cartItems by viewModel.cartItems.collectAsState()
    val addresses by viewModel.addresses.collectAsState()

    var isDelivery by remember { mutableStateOf(true) }
    var selectedAddressId by remember { mutableStateOf(addresses.firstOrNull()?.id ?: "") }
    val selectedAddress = addresses.find { it.id == selectedAddressId } ?: addresses.firstOrNull()

    var receiptUploaded by remember { mutableStateOf(false) }
    var showOcrDialog by remember { mutableStateOf(false) }
    var ocrAmount by remember { mutableStateOf("") }
    var ocrReceiptNumber by remember { mutableStateOf("45872") }

    val subtotal = cartItems.sumOf { it.drug.price * it.quantity }
    val deliveryFee = if (isDelivery) 1000.0 else 0.0
    val totalAmount = subtotal + deliveryFee

    LaunchedEffect(totalAmount) {
        ocrAmount = "${totalAmount.toInt()} ريال"
    }

    Scaffold(
        topBar = { AuratechTopAppBar(title = strings.checkout, onBackClick = onBack) }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Delivery Method Toggle
            item {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = strings.deliveryMethod,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                onClick = { isDelivery = false },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    containerColor = if (!isDelivery) MaterialTheme.colorScheme.primaryContainer else Color.Transparent
                                )
                            ) {
                                Text(strings.pickupFromPharmacy, fontSize = 12.sp)
                            }
                            OutlinedButton(
                                onClick = { isDelivery = true },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    containerColor = if (isDelivery) MaterialTheme.colorScheme.primaryContainer else Color.Transparent
                                )
                            ) {
                                Text(strings.deliveryToHome, fontSize = 12.sp)
                            }
                        }
                    }
                }
            }

            // Address Selection (if Delivery)
            if (isDelivery) {
                item {
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(
                                text = strings.deliveryAddress,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            addresses.forEach { addr ->
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .clickable { selectedAddressId = addr.id }
                                        .padding(vertical = 6.dp)
                                ) {
                                    RadioButton(
                                        selected = selectedAddressId == addr.id,
                                        onClick = { selectedAddressId = addr.id }
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Column {
                                        Text(
                                            text = if (lang == "en") addr.titleEn else addr.titleAr,
                                            style = MaterialTheme.typography.titleSmall,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = if (lang == "en") addr.detailsEn else addr.detailsAr,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // E-Wallet Payment Transfer Section
            item {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = strings.payment,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        // E-Wallet Box
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = strings.eWalletNumber,
                                        style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = "777123456",
                                        style = MaterialTheme.typography.headlineSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
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
                            text = strings.transferInstruction,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // Upload Receipt Button & Status
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
                                            Text("تم رفع إشعار التحويل", fontWeight = FontWeight.Bold, color = SuccessGreen)
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
            }

            // Summary Card
            item {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("قيمة الأدوية", style = MaterialTheme.typography.bodyMedium)
                            Text("${subtotal.toInt()} ${strings.yerCurrency}", fontWeight = FontWeight.Bold)
                        }
                        if (isDelivery) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("رسوم التوصيل للمنزل", style = MaterialTheme.typography.bodyMedium)
                                Text("${deliveryFee.toInt()} ${strings.yerCurrency}", fontWeight = FontWeight.Bold)
                            }
                        }
                        HorizontalDivider()
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(strings.total, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Text(
                                "${totalAmount.toInt()} ${strings.yerCurrency}",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }

            // Confirm Order Button
            item {
                Button(
                    onClick = {
                        val pharmacyNameAr = cartItems.firstOrNull()?.drug?.pharmacyNameAr ?: "صيدلية النور"
                        val pharmacyNameEn = cartItems.firstOrNull()?.drug?.pharmacyNameEn ?: "Al-Noor Pharmacy"
                        val addressText = if (isDelivery) (selectedAddress?.detailsAr ?: "المنزل") else "استلام من الصيدلية"

                        val order = viewModel.createOrder(
                            items = cartItems,
                            isDelivery = isDelivery,
                            address = addressText,
                            pharmacyNameAr = pharmacyNameAr,
                            pharmacyNameEn = pharmacyNameEn,
                            receiptNumber = ocrReceiptNumber
                        )
                        onOrderPlaced(order.id)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(strings.confirmOrder, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                }
            }
        }
    }

    // Simulated OCR Dialog
    if (showOcrDialog) {
        AlertDialog(
            onDismissRequest = { showOcrDialog = false },
            icon = {
                Icon(Icons.Default.DocumentScanner, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(36.dp))
            },
            title = {
                Text(strings.ocrTitle, fontWeight = FontWeight.Bold)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(strings.ocrNote, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)

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
                Button(onClick = { showOcrDialog = false }) {
                    Text(strings.confirm)
                }
            },
            dismissButton = {
                TextButton(onClick = { showOcrDialog = false }) {
                    Text(strings.correctManually)
                }
            }
        )
    }
}

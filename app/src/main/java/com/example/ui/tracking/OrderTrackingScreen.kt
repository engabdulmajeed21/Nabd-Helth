package com.example.ui.tracking

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.mock.MockData
import com.example.data.models.OrderStatus
import com.example.localization.LocalAppStrings
import com.example.localization.LocalLanguage
import com.example.ui.common.AuratechTopAppBar
import com.example.ui.theme.MedicalBluePrimary
import com.example.ui.theme.SuccessGreen
import com.example.viewmodel.AuratechViewModel

@Composable
fun OrderTrackingScreen(
    orderId: String,
    viewModel: AuratechViewModel,
    onNavigateToHome: () -> Unit,
    onNavigateToSupportChat: () -> Unit,
    onBack: () -> Unit
) {
    val strings = LocalAppStrings.current
    val lang = LocalLanguage.current
    val orders by viewModel.orders.collectAsState()
    val order = remember(orderId, orders) {
        orders.find { it.id == orderId } ?: orders.firstOrNull() ?: MockData.orders.first()
    }

    var showSaveTemplateDialog by remember { mutableStateOf(false) }

    // Pulsing animation for active step
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.9f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )

    Scaffold(
        topBar = {
            AuratechTopAppBar(
                title = "${strings.orderTracking} #${order.orderNumber}",
                onBackClick = onBack,
                actions = {
                    IconButton(onClick = onNavigateToHome) {
                        Icon(Icons.Default.Home, contentDescription = "Home")
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
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Estimated Delivery Card
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.DeliveryDining, contentDescription = null, tint = Color.White, modifier = Modifier.size(28.dp))
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = strings.estimatedDelivery,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = "من: ${if (lang == "en") order.pharmacyNameEn else order.pharmacyNameAr}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                        )
                    }
                }
            }

            // 4-Step Vertical Timeline
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "مراحل تجهيز وتوصيل الطلب",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    val steps = listOf(
                        Triple(strings.underReview, "تم استلام الطلب من الصيدلية", OrderStatus.REVIEW),
                        Triple(strings.preparing, "يقوم الصيدلي بفحص الوصفة وتجهيز الأدوية", OrderStatus.PREPARING),
                        Triple(strings.outForDelivery, "الكابتن في طريقه إليك لتسليم الطلب", OrderStatus.DELIVERING),
                        Triple(strings.delivered, "تم تسليم الطلب إلى العنوان المحدد", OrderStatus.DELIVERED)
                    )

                    steps.forEachIndexed { index, step ->
                        val isCompleted = index <= 1 // Step 1 is active preparing
                        val isActive = index == 1

                        Row(modifier = Modifier.fillMaxWidth()) {
                            // Column for Icon & Connecting Line
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(
                                            when {
                                                isActive -> MaterialTheme.colorScheme.primary.copy(alpha = pulseAlpha)
                                                isCompleted -> SuccessGreen
                                                else -> MaterialTheme.colorScheme.surfaceVariant
                                            }
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = when (index) {
                                            0 -> Icons.Default.ReceiptLong
                                            1 -> Icons.Default.Medication
                                            2 -> Icons.Default.TwoWheeler
                                            else -> Icons.Default.Check
                                        },
                                        contentDescription = null,
                                        tint = if (isCompleted || isActive) Color.White else Color.Gray,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }

                                if (index < steps.size - 1) {
                                    Box(
                                        modifier = Modifier
                                            .width(3.dp)
                                            .height(38.dp)
                                            .background(
                                                if (index < 1) SuccessGreen
                                                else MaterialTheme.colorScheme.surfaceVariant
                                            )
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(14.dp))

                            // Step Details
                            Column(modifier = Modifier.weight(1f).padding(bottom = if (index < steps.size - 1) 24.dp else 0.dp)) {
                                Text(
                                    text = step.first,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = step.second,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }

            // Order Items Details
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "تفاصيل الطلب",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    HorizontalDivider()
                    order.items.forEach { item ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("${item.drug.name} × ${item.quantity}", style = MaterialTheme.typography.bodyMedium)
                            Text("${(item.drug.price * item.quantity).toInt()} ${strings.yerCurrency}", fontWeight = FontWeight.Bold)
                        }
                    }
                    HorizontalDivider()
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(strings.total, fontWeight = FontWeight.Bold)
                        Text("${order.totalAmount.toInt()} ${strings.yerCurrency}", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    }
                    Text("العنوان: ${order.address}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("رقم الحوالة: ${order.receiptNumber}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = onNavigateToSupportChat,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.Chat, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(strings.contactPharmacy, fontSize = 12.sp)
                }

                Button(
                    onClick = { showSaveTemplateDialog = true },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.BookmarkBorder, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("حفظ كقالب", fontSize = 12.sp)
                }
            }
        }
    }

    // Screen 15: Quick Reorder Dialog
    if (showSaveTemplateDialog) {
        var templateName by remember { mutableStateOf(order.templateName ?: "أدوية الضغط والصداع") }
        AlertDialog(
            onDismissRequest = { showSaveTemplateDialog = false },
            icon = {
                Icon(Icons.Default.BookmarkAdd, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(36.dp))
            },
            title = {
                Text(strings.saveOrderDialogTitle, fontWeight = FontWeight.Bold)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(strings.saveOrderDialogSubtitle, style = MaterialTheme.typography.bodySmall)
                    OutlinedTextField(
                        value = templateName,
                        onValueChange = { templateName = it },
                        label = { Text("اسم القالب") },
                        placeholder = { Text(strings.templateNamePlaceholder) },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.saveOrderTemplate(templateName, order.items, order.totalAmount)
                        showSaveTemplateDialog = false
                    }
                ) {
                    Text(strings.saveTemplate)
                }
            },
            dismissButton = {
                TextButton(onClick = { showSaveTemplateDialog = false }) {
                    Text(strings.notNow)
                }
            }
        )
    }
}

package com.example.ui.orders

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.OrderStatus
import com.example.localization.LocalAppStrings
import com.example.localization.LocalLanguage
import com.example.ui.common.AuratechTopAppBar
import com.example.ui.common.EmptyStateView
import com.example.ui.theme.DangerRed
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.WarningOrange
import com.example.viewmodel.AuratechViewModel

@Composable
fun MyOrdersScreen(
    viewModel: AuratechViewModel,
    onNavigateToTracking: (String) -> Unit,
    onNavigateToHome: () -> Unit
) {
    val strings = LocalAppStrings.current
    val lang = LocalLanguage.current
    val orders by viewModel.orders.collectAsState()

    Scaffold(
        topBar = { AuratechTopAppBar(title = strings.myOrders) }
    ) { padding ->
        if (orders.isEmpty()) {
            EmptyStateView(
                icon = Icons.Default.ShoppingBag,
                title = strings.noOrders,
                description = "لم تقم بأي طلبات أدوية حتى الآن",
                actionLabel = "تصفح الصيدليات",
                onActionClick = onNavigateToHome,
                modifier = Modifier.padding(padding)
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .background(MaterialTheme.colorScheme.background),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                items(orders) { order ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onNavigateToTracking(order.id) },
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "${strings.orderNumber} #${order.orderNumber}",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )

                                Surface(
                                    color = when (order.status) {
                                        OrderStatus.DELIVERED -> SuccessGreen.copy(alpha = 0.15f)
                                        OrderStatus.PREPARING, OrderStatus.DELIVERING -> MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                                        OrderStatus.REVIEW -> WarningOrange.copy(alpha = 0.15f)
                                        else -> DangerRed.copy(alpha = 0.15f)
                                    },
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = when (order.status) {
                                            OrderStatus.REVIEW -> strings.underReview
                                            OrderStatus.PREPARING -> strings.preparing
                                            OrderStatus.DELIVERING -> strings.outForDelivery
                                            OrderStatus.DELIVERED -> strings.delivered
                                            OrderStatus.REJECTED -> "مرفوض"
                                            else -> strings.cancelled
                                        },
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = when (order.status) {
                                            OrderStatus.DELIVERED -> SuccessGreen
                                            OrderStatus.PREPARING, OrderStatus.DELIVERING -> MaterialTheme.colorScheme.primary
                                            OrderStatus.REVIEW -> WarningOrange
                                            else -> DangerRed
                                        }
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = "من: ${if (lang == "en") order.pharmacyNameEn else order.pharmacyNameAr}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            order.items.forEach { item ->
                                Text(
                                    text = "• ${item.drug.name} × ${item.quantity}",
                                    style = MaterialTheme.typography.bodySmall
                                )
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
                                    Text(strings.total, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text(
                                        text = "${order.totalAmount.toInt()} ${strings.yerCurrency}",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }

                                Button(
                                    onClick = { onNavigateToTracking(order.id) },
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Icon(Icons.Default.Navigation, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(strings.trackOrder, fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

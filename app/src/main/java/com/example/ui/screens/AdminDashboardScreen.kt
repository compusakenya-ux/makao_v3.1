package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.EscrowRecord
import com.example.data.models.MaintenanceRequest
import com.example.data.models.Property
import com.example.data.models.RentalApplication
import com.example.data.models.TenantCreditRating
import com.example.ui.theme.AccentCyan
import com.example.ui.theme.AccentGold
import com.example.ui.theme.AccentGreen
import com.example.ui.theme.AccentMagenta
import com.example.ui.theme.AccentOrange
import com.example.ui.theme.BgCard
import com.example.ui.theme.BgDeep
import com.example.ui.theme.CardBorder
import com.example.ui.theme.SurfaceSubtle
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.viewmodel.MakaoViewModel

@Composable
fun AdminDashboardScreen(
    viewModel: MakaoViewModel,
    modifier: Modifier = Modifier
) {
    val creditRatings by viewModel.creditRatings.collectAsState()
    val applications by viewModel.applications.collectAsState()
    val properties by viewModel.allProperties.collectAsState()
    val escrowRecords by viewModel.escrowRecords.collectAsState()
    val maintenanceRequests by viewModel.maintenanceRequests.collectAsState()
    val showAddPropertyModal by viewModel.showAddPropertyModal.collectAsState()

    if (showAddPropertyModal) {
        AddPropertyModalDialog(
            viewModel = viewModel,
            onDismiss = { viewModel.showAddPropertyModal.value = false }
        )
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(BgDeep)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Admin & Landlord Console",
                        color = TextPrimary,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Real-time backend control: listings, escrow vault, credit scoring & applications",
                        color = TextMuted,
                        fontSize = 11.5.sp
                    )
                }

                IconButton(
                    onClick = { viewModel.resetDatabase() },
                    modifier = Modifier
                        .size(36.dp)
                        .background(CardBorder.copy(alpha = 0.6f), CircleShape)
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = "Reset DB", tint = AccentCyan, modifier = Modifier.size(18.dp))
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Action Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = { viewModel.showAddPropertyModal.value = true },
                    colors = ButtonDefaults.buttonColors(containerColor = AccentCyan),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.White)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Add Listing", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }

                OutlinedButton(
                    onClick = { viewModel.resetDatabase() },
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = TextPrimary),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp), tint = AccentGold)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Seed Defaults", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                }
            }
        }

        // Section 1: Application Review & Decision Center
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(0.8.dp, CardBorder, RoundedCornerShape(18.dp)),
                colors = CardDefaults.cardColors(containerColor = BgCard)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "APPLICATIONS DECISION DESK",
                            color = TextMuted,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.2.sp
                        )
                        Text(
                            text = "KSh 200/app collected",
                            color = AccentOrange,
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    if (applications.isEmpty()) {
                        Text(
                            text = "No applications received yet",
                            color = TextMuted,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            applications.forEach { app ->
                                val isPending = app.status.equals("Pending", ignoreCase = true)
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(SurfaceSubtle, RoundedCornerShape(12.dp))
                                        .border(0.5.dp, CardBorder, RoundedCornerShape(12.dp))
                                        .padding(12.dp)
                                ) {
                                    Column {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(app.propertyTitle, color = TextPrimary, fontSize = 13.5.sp, fontWeight = FontWeight.Bold)
                                            val badgeColor = when (app.status.lowercase()) {
                                                "approved" -> AccentGreen
                                                "rejected" -> AccentMagenta
                                                else -> AccentOrange
                                            }
                                            Text(app.status.uppercase(), color = badgeColor, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                        }

                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = "Applicant: ${app.tenantName} (ID: ${app.idNumber}, Tel: ${app.tel}) • Source: ${app.incomeSource}",
                                            color = TextMuted,
                                            fontSize = 11.sp
                                        )

                                        if (isPending) {
                                            Spacer(modifier = Modifier.height(10.dp))
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                                            ) {
                                                Button(
                                                    onClick = { viewModel.approveApplication(app.id) },
                                                    colors = ButtonDefaults.buttonColors(containerColor = AccentGreen),
                                                    shape = RoundedCornerShape(8.dp),
                                                    modifier = Modifier.weight(1f).height(34.dp)
                                                ) {
                                                    Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(14.dp), tint = Color.White)
                                                    Spacer(modifier = Modifier.width(4.dp))
                                                    Text("Approve", fontSize = 11.5.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                                }

                                                OutlinedButton(
                                                    onClick = { viewModel.rejectApplication(app.id) },
                                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = AccentMagenta),
                                                    border = androidx.compose.foundation.BorderStroke(1.dp, AccentMagenta),
                                                    shape = RoundedCornerShape(8.dp),
                                                    modifier = Modifier.weight(1f).height(34.dp)
                                                ) {
                                                    Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(14.dp), tint = AccentMagenta)
                                                    Spacer(modifier = Modifier.width(4.dp))
                                                    Text("Reject", fontSize = 11.5.sp, fontWeight = FontWeight.Bold, color = AccentMagenta)
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

        // Section 2: Tenant Credit Rating Engine (Live Score Adjustment)
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(0.8.dp, CardBorder, RoundedCornerShape(18.dp)),
                colors = CardDefaults.cardColors(containerColor = BgCard)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "CREDIT RATING ENGINE (500–100 SCALE)",
                        color = TextMuted,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.2.sp
                    )
                    Text(
                        text = "Score drops 1 pt/day for overdue rent. Award points for on-time verification.",
                        color = TextMuted,
                        fontSize = 11.sp,
                        modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
                    )

                    val horizontalScroll = rememberScrollState()
                    Column(modifier = Modifier.horizontalScroll(horizontalScroll)) {
                        Row(
                            modifier = Modifier
                                .background(CardBorder.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                                .padding(vertical = 8.dp, horizontal = 12.dp)
                        ) {
                            TableHeader("Tenant", 110)
                            TableHeader("Property", 140)
                            TableHeader("Score", 70)
                            TableHeader("Late", 60)
                            TableHeader("Adjust Engine", 150)
                        }

                        creditRatings.forEach { rating ->
                            val scoreColor = when {
                                rating.score >= 400 -> AccentGreen
                                rating.score >= 300 -> AccentGold
                                rating.score >= 200 -> AccentOrange
                                else -> AccentMagenta
                            }

                            Row(
                                modifier = Modifier.padding(vertical = 8.dp, horizontal = 12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                TableCell(rating.tenantName, 110, TextPrimary)
                                TableCell(rating.propertyTitle, 140, TextMuted)

                                Box(
                                    modifier = Modifier
                                        .width(70.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .background(scoreColor.copy(alpha = 0.15f), RoundedCornerShape(50))
                                            .border(0.5.dp, scoreColor, RoundedCornerShape(50))
                                            .padding(horizontal = 8.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = "${rating.score}",
                                            color = scoreColor,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }

                                TableCell("${rating.daysLate}d", 60, TextMuted)

                                Row(
                                    modifier = Modifier.width(150.dp),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Button(
                                        onClick = { viewModel.adjustTenantCreditScore(rating, 15, -1) },
                                        colors = ButtonDefaults.buttonColors(containerColor = AccentGreen.copy(alpha = 0.2f)),
                                        shape = RoundedCornerShape(6.dp),
                                        modifier = Modifier.height(28.dp)
                                    ) {
                                        Icon(Icons.Default.TrendingUp, contentDescription = null, tint = AccentGreen, modifier = Modifier.size(12.dp))
                                        Spacer(modifier = Modifier.width(2.dp))
                                        Text("+15", color = AccentGreen, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    }

                                    Button(
                                        onClick = { viewModel.adjustTenantCreditScore(rating, -10, 1) },
                                        colors = ButtonDefaults.buttonColors(containerColor = AccentMagenta.copy(alpha = 0.2f)),
                                        shape = RoundedCornerShape(6.dp),
                                        modifier = Modifier.height(28.dp)
                                    ) {
                                        Icon(Icons.Default.TrendingDown, contentDescription = null, tint = AccentMagenta, modifier = Modifier.size(12.dp))
                                        Spacer(modifier = Modifier.width(2.dp))
                                        Text("-10", color = AccentMagenta, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Section 3: Escrow Vault & Deposit Protection
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(0.8.dp, CardBorder, RoundedCornerShape(18.dp)),
                colors = CardDefaults.cardColors(containerColor = BgCard)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Lock, contentDescription = null, tint = AccentGold, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "SECURITY DEPOSIT ESCROW VAULT",
                                color = TextMuted,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.2.sp
                            )
                        }
                        Text(
                            text = "${escrowRecords.size} in vault",
                            color = AccentGold,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Text(
                        text = "2-Month security deposits held securely until check-out inspection.",
                        color = TextMuted,
                        fontSize = 11.sp,
                        modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
                    )

                    val horizontalScroll = rememberScrollState()
                    Column(modifier = Modifier.horizontalScroll(horizontalScroll)) {
                        Row(
                            modifier = Modifier
                                .background(CardBorder.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                                .padding(vertical = 8.dp, horizontal = 12.dp)
                        ) {
                            TableHeader("Escrow ID", 80)
                            TableHeader("Tenant", 100)
                            TableHeader("Property", 140)
                            TableHeader("Deposit", 90)
                            TableHeader("Status", 110)
                            TableHeader("Vault Action", 130)
                        }

                        escrowRecords.forEach { record ->
                            Row(
                                modifier = Modifier.padding(vertical = 8.dp, horizontal = 12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                TableCell(record.id, 80, AccentCyan)
                                TableCell(record.tenantName, 100, TextPrimary)
                                TableCell(record.propertyTitle, 140, TextMuted)
                                TableCell("KSh ${String.format("%,d", record.depositAmount)}", 90, AccentGreen)
                                TableCell(record.status, 110, AccentGold)

                                Row(
                                    modifier = Modifier.width(130.dp),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    if (record.status == "Held in Escrow") {
                                        Button(
                                            onClick = { viewModel.updateEscrowStatus(record, "Released") },
                                            colors = ButtonDefaults.buttonColors(containerColor = AccentGreen.copy(alpha = 0.2f)),
                                            shape = RoundedCornerShape(6.dp),
                                            modifier = Modifier.height(28.dp)
                                        ) {
                                            Text("Release", color = AccentGreen, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                        }

                                        Button(
                                            onClick = { viewModel.updateEscrowStatus(record, "Refunded") },
                                            colors = ButtonDefaults.buttonColors(containerColor = AccentCyan.copy(alpha = 0.2f)),
                                            shape = RoundedCornerShape(6.dp),
                                            modifier = Modifier.height(28.dp)
                                        ) {
                                            Text("Refund", color = AccentCyan, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                        }
                                    } else {
                                        Text("Settled", color = TextMuted, fontSize = 11.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Section 4: Maintenance & Caretaker Service Desk
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(0.8.dp, CardBorder, RoundedCornerShape(18.dp)),
                colors = CardDefaults.cardColors(containerColor = BgCard)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Build, contentDescription = null, tint = AccentCyan, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "MAINTENANCE & CARETAKER TICKETS",
                                color = TextMuted,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.2.sp
                            )
                        }
                        Text(
                            text = "${maintenanceRequests.size} tickets",
                            color = AccentCyan,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    maintenanceRequests.forEach { maint ->
                        val isResolved = maint.status.equals("Resolved", ignoreCase = true)
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .background(SurfaceSubtle, RoundedCornerShape(12.dp))
                                .border(0.5.dp, CardBorder, RoundedCornerShape(12.dp))
                                .padding(12.dp)
                        ) {
                            Column {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("${maint.id} • ${maint.issueType}", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                    Text(
                                        maint.status.uppercase(),
                                        color = if (isResolved) AccentGreen else AccentOrange,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Text("${maint.tenantName} — ${maint.propertyTitle}", color = TextMuted, fontSize = 11.sp)
                                Text(maint.description, color = TextPrimary, fontSize = 11.5.sp, modifier = Modifier.padding(vertical = 4.dp))

                                if (!isResolved) {
                                    Button(
                                        onClick = { viewModel.updateMaintenanceStatus(maint, "Resolved") },
                                        colors = ButtonDefaults.buttonColors(containerColor = AccentGreen),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.height(30.dp).align(Alignment.End)
                                    ) {
                                        Text("Mark as Resolved", fontSize = 10.5.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Section 5: Property Inventory & Quick Delete
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(0.8.dp, CardBorder, RoundedCornerShape(18.dp)),
                colors = CardDefaults.cardColors(containerColor = BgCard)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "ACTIVE LISTING INVENTORY (${properties.size})",
                        color = TextMuted,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.2.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    properties.forEach { p ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .background(SurfaceSubtle, RoundedCornerShape(10.dp))
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(p.title, color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold, maxLines = 1)
                                Text("${p.city} • KSh ${String.format("%,d", p.price)}/mo • ${p.beds} Bed • ${p.viewers} views", color = TextMuted, fontSize = 11.sp)
                            }

                            IconButton(
                                onClick = { viewModel.deleteProperty(p) },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete Listing", tint = AccentMagenta, modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Composable
fun TableHeader(text: String, widthDp: Int) {
    Text(
        text = text,
        color = AccentCyan,
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.width(widthDp.dp)
    )
}

@Composable
fun TableCell(text: String, widthDp: Int, color: androidx.compose.ui.graphics.Color) {
    Text(
        text = text,
        color = color,
        fontSize = 12.sp,
        maxLines = 1,
        modifier = Modifier.width(widthDp.dp)
    )
}

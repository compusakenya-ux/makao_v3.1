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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AccentCyan
import com.example.ui.theme.AccentGold
import com.example.ui.theme.AccentGreen
import com.example.ui.theme.AccentMagenta
import com.example.ui.theme.AccentOrange
import com.example.ui.theme.BgCard
import com.example.ui.theme.BgDeep
import com.example.ui.theme.CardBorder
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

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(BgDeep)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Admin Dashboard",
                color = TextPrimary,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Tenant credit ratings, application fees collected & property analytics",
                color = TextMuted,
                fontSize = 12.sp
            )
        }

        // Section 1: Tenant Credit Ratings (Document 2)
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(0.8.dp, CardBorder, RoundedCornerShape(18.dp)),
                colors = CardDefaults.cardColors(containerColor = BgCard)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "TENANT CREDIT RATINGS (500–100 RANGE)",
                        color = TextMuted,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.2.sp
                    )
                    Text(
                        text = "Score drops 1 point per day after contract deadline. Visible to Admin and respective Tenant only.",
                        color = TextMuted,
                        fontSize = 11.sp,
                        modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
                    )

                    val horizontalScroll = rememberScrollState()
                    Column(modifier = Modifier.horizontalScroll(horizontalScroll)) {
                        // Header Row
                        Row(
                            modifier = Modifier
                                .background(CardBorder.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                                .padding(vertical = 8.dp, horizontal = 12.dp)
                        ) {
                            TableHeader("Tenant", 120)
                            TableHeader("ID Number", 90)
                            TableHeader("Property", 160)
                            TableHeader("Score", 80)
                            TableHeader("Status", 100)
                            TableHeader("Days Late", 80)
                        }

                        // Data Rows
                        creditRatings.forEach { rating ->
                            val scoreColor = when {
                                rating.score >= 400 -> AccentGreen
                                rating.score >= 300 -> AccentGold
                                rating.score >= 200 -> AccentOrange
                                else -> AccentMagenta
                            }

                            Row(
                                modifier = Modifier.padding(vertical = 10.dp, horizontal = 12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                TableCell(rating.tenantName, 120, TextPrimary)
                                TableCell(rating.idNumber, 90, TextMuted)
                                TableCell(rating.propertyTitle, 160, TextPrimary)

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

                                TableCell(rating.statusLabel.split("—").firstOrNull()?.trim() ?: "", 100, scoreColor)
                                TableCell("${rating.daysLate}d", 80, TextMuted)
                            }
                        }
                    }
                }
            }
        }

        // Section 2: Application Fees Collected (KSh 200 Admin Account)
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(0.8.dp, CardBorder, RoundedCornerShape(18.dp)),
                colors = CardDefaults.cardColors(containerColor = BgCard)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "APPLICATION FEES COLLECTED (ADMIN ACCOUNT)",
                        color = TextMuted,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.2.sp
                    )
                    Text(
                        text = "KSh 200 per application. Non-refundable fee credited directly to Makao Admin Account.",
                        color = TextMuted,
                        fontSize = 11.sp,
                        modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
                    )

                    if (applications.isEmpty()) {
                        Text(
                            text = "No applications submitted yet",
                            color = TextMuted,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    } else {
                        val horizontalScroll = rememberScrollState()
                        Column(modifier = Modifier.horizontalScroll(horizontalScroll)) {
                            Row(
                                modifier = Modifier
                                    .background(CardBorder.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                                    .padding(vertical = 8.dp, horizontal = 12.dp)
                            ) {
                                TableHeader("App ID", 90)
                                TableHeader("Tenant", 120)
                                TableHeader("Property", 160)
                                TableHeader("Fee", 80)
                                TableHeader("Date", 90)
                                TableHeader("Destination", 140)
                            }

                            applications.forEach { app ->
                                Row(
                                    modifier = Modifier.padding(vertical = 10.dp, horizontal = 12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    TableCell(app.id, 90, AccentCyan)
                                    TableCell(app.tenantName, 120, TextPrimary)
                                    TableCell(app.propertyTitle, 160, TextPrimary)
                                    TableCell("KSh 200", 80, AccentOrange)
                                    TableCell(app.date, 90, TextMuted)
                                    TableCell("Makao Admin Acc", 140, AccentOrange)
                                }
                            }
                        }
                    }
                }
            }
        }

        // Section 3: Property Click Analytics
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(0.8.dp, CardBorder, RoundedCornerShape(18.dp)),
                colors = CardDefaults.cardColors(containerColor = BgCard)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "PROPERTY ENGAGEMENT & CONVERSION ANALYTICS",
                        color = TextMuted,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.2.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    val horizontalScroll = rememberScrollState()
                    Column(modifier = Modifier.horizontalScroll(horizontalScroll)) {
                        Row(
                            modifier = Modifier
                                .background(CardBorder.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                                .padding(vertical = 8.dp, horizontal = 12.dp)
                        ) {
                            TableHeader("Property", 160)
                            TableHeader("City", 90)
                            TableHeader("Type", 110)
                            TableHeader("Views", 70)
                            TableHeader("Apps", 60)
                            TableHeader("Conversion", 90)
                        }

                        properties.forEach { p ->
                            val appCount = applications.count { it.propertyId == p.id }
                            val convRate = if (p.viewers > 0) String.format("%.1f%%", (appCount.toDouble() / p.viewers) * 100) else "0.0%"

                            Row(
                                modifier = Modifier.padding(vertical = 10.dp, horizontal = 12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                TableCell(p.title, 160, TextPrimary)
                                TableCell(p.city, 90, TextMuted)
                                TableCell(p.type, 110, TextMuted)
                                TableCell("${p.viewers}", 70, AccentCyan)
                                TableCell("$appCount", 60, AccentGreen)
                                TableCell(convRate, 90, AccentGold)
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

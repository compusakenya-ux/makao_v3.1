package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.models.Property
import com.example.ui.theme.AccentCyan
import com.example.ui.theme.AccentGreen
import com.example.ui.theme.AccentMagenta
import com.example.ui.theme.BgCard
import com.example.ui.theme.BgDeep
import com.example.ui.theme.CardBorder
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun PaymentModalDialog(
    property: Property,
    onDismiss: () -> Unit,
    onPaymentSuccess: (phone: String, moveInDate: String, leaseMonths: Int) -> Unit
) {
    var selectedMethod by remember { mutableStateOf("mpesa") }
    var phone by remember { mutableStateOf("254712345678") }
    var moveInDate by remember { mutableStateOf("2026-08-01") }
    var leaseMonths by remember { mutableStateOf(12) }
    var isProcessing by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()
    val totalAmount = property.price * 3 // 2 months deposit + 1 month rent

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            shape = RoundedCornerShape(24.dp),
            color = BgCard,
            border = androidx.compose.foundation.BorderStroke(0.8.dp, CardBorder)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Complete Booking Payment", color = TextPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        Text(property.title, color = AccentCyan, fontSize = 12.sp)
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = TextMuted)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Breakdown Box
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(BgDeep, RoundedCornerShape(14.dp))
                        .border(0.8.dp, CardBorder, RoundedCornerShape(14.dp))
                        .padding(14.dp)
                ) {
                    Column {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("1 Month Rent:", color = TextMuted, fontSize = 12.sp)
                            Text("KSh ${String.format("%,d", property.price)}", color = TextPrimary, fontSize = 12.sp)
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("2 Months Deposit:", color = TextMuted, fontSize = 12.sp)
                            Text("KSh ${String.format("%,d", property.price * 2)}", color = TextPrimary, fontSize = 12.sp)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Box(modifier = Modifier.fillMaxWidth().height(0.5.dp).background(CardBorder))
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Total First Payment:", color = AccentCyan, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            Text("KSh ${String.format("%,d", totalAmount)}", color = AccentGreen, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text("SELECT PAYMENT METHOD", color = TextMuted, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))

                // Payment Options
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                    // M-Pesa
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .background(if (selectedMethod == "mpesa") AccentGreen.copy(alpha = 0.15f) else BgDeep, RoundedCornerShape(12.dp))
                            .border(0.8.dp, if (selectedMethod == "mpesa") AccentGreen else CardBorder, RoundedCornerShape(12.dp))
                            .clickable { selectedMethod = "mpesa" }
                            .padding(12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.PhoneAndroid, contentDescription = null, tint = AccentGreen, modifier = Modifier.height(20.dp))
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("M-Pesa", color = TextPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    // Bank
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .background(if (selectedMethod == "bank") AccentCyan.copy(alpha = 0.15f) else BgDeep, RoundedCornerShape(12.dp))
                            .border(0.8.dp, if (selectedMethod == "bank") AccentCyan else CardBorder, RoundedCornerShape(12.dp))
                            .clickable { selectedMethod = "bank" }
                            .padding(12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.AccountBalance, contentDescription = null, tint = AccentCyan, modifier = Modifier.height(20.dp))
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Bank", color = TextPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    // Card
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .background(if (selectedMethod == "card") AccentMagenta.copy(alpha = 0.15f) else BgDeep, RoundedCornerShape(12.dp))
                            .border(0.8.dp, if (selectedMethod == "card") AccentMagenta else CardBorder, RoundedCornerShape(12.dp))
                            .clickable { selectedMethod = "card" }
                            .padding(12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.CreditCard, contentDescription = null, tint = AccentMagenta, modifier = Modifier.height(20.dp))
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Card", color = TextPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                if (selectedMethod == "mpesa") {
                    Text("M-PESA TELEPHONE NUMBER", color = TextMuted, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("2547XX XXX XXX", color = TextMuted, fontSize = 13.sp) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = BgDeep,
                            unfocusedContainerColor = BgDeep,
                            focusedBorderColor = AccentGreen,
                            unfocusedBorderColor = CardBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                Button(
                    onClick = {
                        isProcessing = true
                        scope.launch {
                            delay(2000)
                            isProcessing = false
                            onPaymentSuccess(phone, moveInDate, leaseMonths)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AccentGreen),
                    shape = RoundedCornerShape(14.dp),
                    enabled = !isProcessing
                ) {
                    if (isProcessing) {
                        CircularProgressIndicator(modifier = Modifier.height(20.dp).width(20.dp), color = androidx.compose.ui.graphics.Color.White)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Sending M-Pesa STK Push...", color = androidx.compose.ui.graphics.Color.White, fontWeight = FontWeight.Bold)
                    } else {
                        Text("Pay KSh ${String.format("%,d", totalAmount)} via M-Pesa", color = androidx.compose.ui.graphics.Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                }
            }
        }
    }
}

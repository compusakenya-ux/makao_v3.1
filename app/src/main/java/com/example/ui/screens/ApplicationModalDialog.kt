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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.models.Property
import com.example.ui.theme.AccentCyan
import com.example.ui.theme.AccentOrange
import com.example.ui.theme.BgCard
import com.example.ui.theme.BgDeep
import com.example.ui.theme.CardBorder
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun ApplicationModalDialog(
    property: Property,
    onDismiss: () -> Unit,
    onSubmit: (name: String, idNum: String, tel: String, incomeSource: String, detail: String, docCount: Int) -> Unit
) {
    var tenantName by remember { mutableStateOf("") }
    var idNumber by remember { mutableStateOf("") }
    var tel by remember { mutableStateOf("2547") }
    var incomeSource by remember { mutableStateOf("Employed") }
    var employerDetail by remember { mutableStateOf("") }
    var collegeDetail by remember { mutableStateOf("") }
    var businessDetail by remember { mutableStateOf("") }

    var incomeSourceExpanded by remember { mutableStateOf(false) }
    var isSubmitting by remember { mutableStateOf(false) }

    // Uploaded doc file simulators (Document 1)
    val uploadedDocs = remember { mutableStateListOf<String>() }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            shape = RoundedCornerShape(24.dp),
            color = BgCard,
            border = androidx.compose.foundation.BorderStroke(0.8.dp, CardBorder)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Submit Lease Application", color = TextPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        Text(property.title, color = AccentCyan, fontSize = 12.sp)
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = TextMuted)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Tenant Full Name
                Text("TENANT FULL NAME *", color = TextMuted, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = tenantName,
                    onValueChange = { tenantName = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Full name as on National ID", color = TextMuted, fontSize = 13.sp) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = BgDeep,
                        unfocusedContainerColor = BgDeep,
                        focusedBorderColor = AccentCyan,
                        unfocusedBorderColor = CardBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                // National ID Number
                Text("NATIONAL ID NUMBER *", color = TextMuted, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = idNumber,
                    onValueChange = { idNumber = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("e.g. 12345678", color = TextMuted, fontSize = 13.sp) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = BgDeep,
                        unfocusedContainerColor = BgDeep,
                        focusedBorderColor = AccentCyan,
                        unfocusedBorderColor = CardBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Phone Number
                Text("TELEPHONE NUMBER *", color = TextMuted, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = tel,
                    onValueChange = { tel = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("2547XX XXX XXX", color = TextMuted, fontSize = 13.sp) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = BgDeep,
                        unfocusedContainerColor = BgDeep,
                        focusedBorderColor = AccentCyan,
                        unfocusedBorderColor = CardBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Income Source Dropdown
                Text("SOURCE OF INCOME *", color = TextMuted, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                Box(modifier = Modifier.fillMaxWidth()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(BgDeep, RoundedCornerShape(12.dp))
                            .border(0.8.dp, CardBorder, RoundedCornerShape(12.dp))
                            .clickable { incomeSourceExpanded = true }
                            .padding(14.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(incomeSource, color = TextPrimary, fontSize = 13.sp)
                            Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = AccentCyan)
                        }
                    }

                    DropdownMenu(
                        expanded = incomeSourceExpanded,
                        onDismissRequest = { incomeSourceExpanded = false },
                        modifier = Modifier.background(BgCard)
                    ) {
                        listOf("Employed", "Student", "Business").forEach { src ->
                            DropdownMenuItem(
                                text = { Text(src, color = TextPrimary) },
                                onClick = {
                                    incomeSource = src
                                    incomeSourceExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Conditional Income Source Input
                when (incomeSource) {
                    "Employed" -> {
                        Text("EMPLOYER NAME & TELEPHONE", color = TextMuted, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(4.dp))
                        OutlinedTextField(
                            value = employerDetail,
                            onValueChange = { employerDetail = it },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = { Text("Company name & phone number", color = TextMuted, fontSize = 13.sp) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = BgDeep,
                                unfocusedContainerColor = BgDeep,
                                focusedBorderColor = AccentCyan,
                                unfocusedBorderColor = CardBorder,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            ),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                    "Student" -> {
                        Text("COLLEGE / UNIVERSITY NAME & TELEPHONE", color = TextMuted, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(4.dp))
                        OutlinedTextField(
                            value = collegeDetail,
                            onValueChange = { collegeDetail = it },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = { Text("College name & contact", color = TextMuted, fontSize = 13.sp) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = BgDeep,
                                unfocusedContainerColor = BgDeep,
                                focusedBorderColor = AccentCyan,
                                unfocusedBorderColor = CardBorder,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            ),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                    "Business" -> {
                        Text("BUSINESS NAME & TELEPHONE", color = TextMuted, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(4.dp))
                        OutlinedTextField(
                            value = businessDetail,
                            onValueChange = { businessDetail = it },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = { Text("Registered business name", color = TextMuted, fontSize = 13.sp) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = BgDeep,
                                unfocusedContainerColor = BgDeep,
                                focusedBorderColor = AccentCyan,
                                unfocusedBorderColor = CardBorder,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            ),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Documents Upload Section (Document 1)
                Text("SUPPORTING DOCUMENTS (JPG, PNG, PDF <= 5MB)", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                Text("Upload National ID, Passport Photo, Student ID, Payslip/Contract.", color = TextMuted, fontSize = 11.sp)
                Spacer(modifier = Modifier.height(8.dp))

                listOf("National ID (JPG, PNG, PDF)", "Passport Photo (JPG, PNG)", "Student ID (JPG, PNG, PDF)", "Payslip / Contract (JPG, PNG, PDF)").forEach { docType ->
                    val isUploaded = uploadedDocs.contains(docType)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 3.dp)
                            .background(if (isUploaded) AccentCyan.copy(alpha = 0.1f) else BgDeep, RoundedCornerShape(10.dp))
                            .border(0.5.dp, if (isUploaded) AccentCyan else CardBorder, RoundedCornerShape(10.dp))
                            .clickable {
                                if (isUploaded) uploadedDocs.remove(docType) else uploadedDocs.add(docType)
                            }
                            .padding(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.AttachFile, contentDescription = null, tint = if (isUploaded) AccentCyan else TextMuted, modifier = Modifier.height(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(docType, color = TextPrimary, fontSize = 12.sp)
                            }

                            Text(
                                text = if (isUploaded) "✓ UPLOADED" else "+ TAP TO ATTACH",
                                color = if (isUploaded) AccentCyan else TextMuted,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Fee Summary Box
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(AccentOrange.copy(alpha = 0.12f), RoundedCornerShape(14.dp))
                        .border(0.8.dp, AccentOrange.copy(alpha = 0.4f), RoundedCornerShape(14.dp))
                        .padding(14.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                        Text("APPLICATION FEE", color = TextMuted, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        Text("KSh 200", color = AccentOrange, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                        Text("⚠ Non-refundable fee charged by Makao Admin Account. Does NOT guarantee approval.", color = AccentOrange, fontSize = 11.sp, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                Button(
                    onClick = {
                        val detail = when (incomeSource) {
                            "Employed" -> employerDetail.ifEmpty { "N/A" }
                            "Student" -> collegeDetail.ifEmpty { "N/A" }
                            else -> businessDetail.ifEmpty { "N/A" }
                        }
                        onSubmit(
                            tenantName.ifEmpty { "Tenant Applicant" },
                            idNumber.ifEmpty { "30124567" },
                            tel.ifEmpty { "254712345678" },
                            incomeSource,
                            detail,
                            uploadedDocs.size
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AccentOrange),
                    shape = RoundedCornerShape(14.dp),
                    enabled = !isSubmitting
                ) {
                    if (isSubmitting) {
                        CircularProgressIndicator(modifier = Modifier.height(20.dp).width(20.dp), color = AccentCyan)
                    } else {
                        Text("Pay KSh 200 & Submit Application", color = AccentCyan, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                }
            }
        }
    }
}

package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.models.Property
import com.example.ui.components.PropertyCard
import com.example.ui.theme.AccentCyan
import com.example.ui.theme.AccentGold
import com.example.ui.theme.AccentGreen
import com.example.ui.theme.AccentOrange
import com.example.ui.theme.AccentTeal
import com.example.ui.theme.BgCard
import com.example.ui.theme.BgDeep
import com.example.ui.theme.CardBorder
import com.example.ui.theme.SurfaceSubtle
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.MakaoViewModel

// City -> Estate Mappings based on Document 3
val cityEstatesMap = mapOf(
    "Nairobi" to listOf("All Estates", "Kilimani", "Westlands", "Lavington", "Kileleshwa", "Upper Hill", "Riverside", "Karen", "Runda", "Muthaiga", "Parklands", "Ngong Road", "Gigiri", "Hurlingham", "Yaya Centre", "CBD"),
    "Mombasa" to listOf("All Estates", "Bamburi", "Mikindani", "Magongo", "Chaani", "Tudor", "Jomvu", "Likoni", "Diani", "Nyali Estate", "Kisauni", "Mtwapa", "Shanzu", "Mombasa CBD"),
    "Nakuru" to listOf("All Estates", "Milimani", "Lanet", "Kiamunyi", "Ngata", "Section 58", "Free Area", "London", "Menengai", "Shabab", "Nakuru CBD", "Kiti", "Rhoda", "Kaptembwa", "Nakuru East"),
    "Eldoret" to listOf("All Estates", "Elgon View", "Kapsoya", "Pioneer", "Annex", "Kimumu", "West Indies", "Huruma", "Kamukunji", "Eldoret CBD", "Ainabkoi", "Langas", "Kipkaren", "Sosiani"),
    "Kisumu" to listOf("All Estates", "Milimani", "Nyalenda", "Manyatta", "Kondele", "Mamboleo", "Carwash", "Polyview", "Arina", "Kisumu CBD", "Nyamasaria", "Migosi", "Kibos", "Riat")
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ExploreScreen(
    viewModel: MakaoViewModel,
    onPropertyClick: (Property) -> Unit,
    modifier: Modifier = Modifier
) {
    val properties by viewModel.filteredProperties.collectAsState()
    val selectedCity by viewModel.selectedCity.collectAsState()
    val selectedEstate by viewModel.selectedEstate.collectAsState()
    val selectedType by viewModel.selectedType.collectAsState()
    val selectedBudget by viewModel.selectedMaxBudget.collectAsState()
    val sortOption by viewModel.sortOption.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()

    val cityList = listOf("All Towns", "Nairobi", "Mombasa", "Nakuru", "Eldoret", "Kisumu")
    val currentEstates = cityEstatesMap[selectedCity] ?: listOf("All Estates")
    val propertyTypes = listOf("All Types", "1 Bedroom Apartment", "2 Bedroom Apartment", "3 Bedroom Apartment", "4 Bedroom Apartment", "Bedsitter", "Single Room", "Servant Quarter", "Double Room")
    val budgetOptions = listOf(0 to "Any Budget", 15000 to "Under 15k", 30000 to "Under 30k", 50000 to "Under 50k", 80000 to "Under 80k", 120000 to "Under 120k", 200000 to "Under 200k")

    var estateDropdownExpanded by remember { mutableStateOf(false) }
    var typeDropdownExpanded by remember { mutableStateOf(false) }
    var budgetDropdownExpanded by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(BgDeep)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hero Section
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp)
                    .clip(RoundedCornerShape(22.dp))
                    .border(1.dp, CardBorder, RoundedCornerShape(22.dp)),
                colors = CardDefaults.cardColors(containerColor = BgCard),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.img_hero_banner_1785240588790),
                        contentDescription = "Makao Hero",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.matchParentSize()
                    )

                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color.Transparent,
                                        Color.Black.copy(alpha = 0.85f)
                                    )
                                )
                            )
                    )

                    Column(
                        modifier = Modifier
                            .padding(16.dp)
                            .align(Alignment.BottomStart)
                    ) {
                        // Live badge
                        Box(
                            modifier = Modifier
                                .background(Color.Black.copy(alpha = 0.6f), RoundedCornerShape(50))
                                .border(0.5.dp, Color.White.copy(alpha = 0.4f), RoundedCornerShape(50))
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(7.dp)
                                        .background(AccentGreen, CircleShape)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Live across Kenya • 1,247 verified units",
                                    color = Color.White,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Find Your Next Home",
                            color = Color.White,
                            fontSize = 23.sp,
                            fontWeight = FontWeight.Black
                        )

                        Text(
                            text = "Direct rentals in Nairobi, Mombasa, Nakuru, Eldoret & Kisumu with instant M-Pesa.",
                            color = Color.White.copy(alpha = 0.9f),
                            fontSize = 12.sp,
                            lineHeight = 16.sp
                        )
                    }
                }
            }
        }

        // Quick City Selector Horizontal Strip
        item {
            Column {
                Text(
                    text = "SELECT TOWN",
                    color = TextMuted,
                    fontSize = 10.5.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(cityList) { city ->
                        val isSelected = selectedCity == city
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) AccentCyan else BgCard)
                                .border(
                                    width = 1.dp,
                                    color = if (isSelected) AccentCyan else CardBorder,
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .clickable { viewModel.onSelectCity(city) }
                                .padding(horizontal = 14.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = city,
                                color = if (isSelected) Color.White else TextPrimary,
                                fontSize = 12.5.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }

        // Search Box & Filters Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, CardBorder, RoundedCornerShape(20.dp)),
                colors = CardDefaults.cardColors(containerColor = BgCard),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "SEARCH & FILTER",
                            color = TextMuted,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.2.sp
                        )
                        Icon(
                            imageVector = Icons.Default.Tune,
                            contentDescription = null,
                            tint = AccentCyan,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Keyword Search
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { viewModel.searchQuery.value = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Search title, estate or location...", color = TextMuted, fontSize = 13.sp) },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = AccentCyan) },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { viewModel.searchQuery.value = "" }) {
                                    Icon(Icons.Default.Clear, contentDescription = "Clear", tint = TextMuted)
                                }
                            }
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = SurfaceSubtle,
                            unfocusedContainerColor = SurfaceSubtle,
                            focusedBorderColor = AccentCyan,
                            unfocusedBorderColor = CardBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        shape = RoundedCornerShape(14.dp),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Filter dropdown grid
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Estate Dropdown
                        Box(modifier = Modifier.weight(1f)) {
                            FilterDropdownButton(
                                label = "Estate / Area",
                                value = selectedEstate,
                                onClick = { estateDropdownExpanded = true }
                            )
                            DropdownMenu(
                                expanded = estateDropdownExpanded,
                                onDismissRequest = { estateDropdownExpanded = false },
                                modifier = Modifier.background(BgCard)
                            ) {
                                currentEstates.forEach { estate ->
                                    DropdownMenuItem(
                                        text = { Text(estate, color = TextPrimary, fontSize = 13.sp) },
                                        onClick = {
                                            viewModel.selectedEstate.value = estate
                                            estateDropdownExpanded = false
                                        }
                                    )
                                }
                            }
                        }

                        // Type Dropdown
                        Box(modifier = Modifier.weight(1f)) {
                            FilterDropdownButton(
                                label = "Property Type",
                                value = selectedType,
                                onClick = { typeDropdownExpanded = true }
                            )
                            DropdownMenu(
                                expanded = typeDropdownExpanded,
                                onDismissRequest = { typeDropdownExpanded = false },
                                modifier = Modifier.background(BgCard)
                            ) {
                                propertyTypes.forEach { type ->
                                    DropdownMenuItem(
                                        text = { Text(type, color = TextPrimary, fontSize = 13.sp) },
                                        onClick = {
                                            viewModel.selectedType.value = type
                                            typeDropdownExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Budget Dropdown
                    val budgetText = budgetOptions.firstOrNull { it.first == selectedBudget }?.second ?: "Any"
                    FilterDropdownButton(
                        label = "Max Monthly Budget",
                        value = budgetText,
                        onClick = { budgetDropdownExpanded = true }
                    )
                    DropdownMenu(
                        expanded = budgetDropdownExpanded,
                        onDismissRequest = { budgetDropdownExpanded = false },
                        modifier = Modifier.background(BgCard)
                    ) {
                        budgetOptions.forEach { (valInt, labelStr) ->
                            DropdownMenuItem(
                                text = { Text(labelStr, color = TextPrimary, fontSize = 13.sp) },
                                onClick = {
                                    viewModel.selectedMaxBudget.value = valInt
                                    budgetDropdownExpanded = false
                                }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Sort Pills
                    Text(
                        text = "SORT LISTINGS BY",
                        color = TextMuted,
                        fontSize = 10.5.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        SortPill("Newest", "newest", sortOption) { viewModel.sortOption.value = "newest" }
                        SortPill("Price: Low", "price-low", sortOption) { viewModel.sortOption.value = "price-low" }
                        SortPill("Price: High", "price-high", sortOption) { viewModel.sortOption.value = "price-high" }
                        SortPill("Top Rated", "popular", sortOption) { viewModel.sortOption.value = "popular" }
                    }
                }
            }
        }

        // Stats Banner
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(BgCard, RoundedCornerShape(16.dp))
                    .border(1.dp, CardBorder, RoundedCornerShape(16.dp))
                    .padding(horizontal = 8.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                StatColumn("2,400+", "Active Units")
                Box(modifier = Modifier.width(1.dp).height(24.dp).background(CardBorder))
                StatColumn("98%", "Verified Landlords")
                Box(modifier = Modifier.width(1.dp).height(24.dp).background(CardBorder))
                StatColumn("< 2 min", "M-Pesa STK")
                Box(modifier = Modifier.width(1.dp).height(24.dp).background(CardBorder))
                StatColumn("4.9 ★", "Tenant Rating")
            }
        }

        // Section Title
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Available Properties",
                        color = TextPrimary,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${properties.size} units ready for instant booking",
                        color = TextMuted,
                        fontSize = 12.sp
                    )
                }
            }
        }

        // Property Cards List
        if (properties.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("🔍", fontSize = 44.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "No matching properties found",
                            color = TextPrimary,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Try clearing search keywords or selecting 'All Towns' / 'Any Budget'",
                            color = TextMuted,
                            fontSize = 12.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        } else {
            items(properties, key = { it.id }) { property ->
                PropertyCard(
                    property = property,
                    onPropertyClick = { onPropertyClick(property) },
                    onFavoriteClick = { viewModel.toggleFavorite(property) }
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Composable
fun FilterDropdownButton(
    label: String,
    value: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(SurfaceSubtle, RoundedCornerShape(12.dp))
            .border(1.dp, CardBorder, RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Column {
            Text(label.uppercase(), color = TextMuted, fontSize = 9.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(2.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = value,
                    color = TextPrimary,
                    fontSize = 12.5.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1
                )
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = null,
                    tint = AccentCyan,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
fun SortPill(
    title: String,
    key: String,
    currentSort: String,
    onClick: () -> Unit
) {
    val isSelected = currentSort == key
    Box(
        modifier = Modifier
            .background(if (isSelected) AccentCyan else SurfaceSubtle, RoundedCornerShape(50))
            .border(1.dp, if (isSelected) AccentCyan else CardBorder, RoundedCornerShape(50))
            .clickable { onClick() }
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Text(
            text = title,
            color = if (isSelected) Color.White else TextSecondary,
            fontSize = 10.5.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
        )
    }
}

@Composable
fun StatColumn(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            color = AccentCyan,
            fontSize = 14.5.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = label,
            color = TextMuted,
            fontSize = 9.sp
        )
    }
}


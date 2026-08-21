package com.example.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "properties")
data class Property(
    @PrimaryKey val id: Long,
    val title: String,
    val location: String,
    val city: String,
    val estate: String,
    val type: String,
    val price: Int,
    val beds: Int,
    val baths: Int,
    val sqft: Int,
    val rating: Double,
    val badge: String, // "new", "hot", "verified"
    val viewers: Int,
    val imageUrl: String,
    val description: String,
    val amenitiesJson: String, // Comma-separated or JSON list
    val neighborhood: String,
    val landlordName: String = "Makao Real Estate",
    val availableUnits: Int = 3,
    val totalUnits: Int = 12,
    val isFavorite: Boolean = false
)

@Entity(tableName = "applications")
data class RentalApplication(
    @PrimaryKey val id: String, // e.g. "APP123456"
    val propertyId: Long,
    val propertyTitle: String,
    val tenantName: String,
    val idNumber: String,
    val tel: String,
    val incomeSource: String,
    val sourceDetail: String,
    val amount: Int = 200,
    val feeType: String = "Application Fee",
    val date: String,
    val status: String = "Pending", // "Pending", "Approved", "Rejected"
    val docsSummary: String
)

@Entity(tableName = "bookings")
data class PropertyBooking(
    @PrimaryKey val id: String, // e.g. "BKG654321"
    val propertyId: Long,
    val propertyTitle: String,
    val amountPaid: Int,
    val paymentMethod: String,
    val date: String,
    val moveInDate: String,
    val leaseDurationMonths: Int = 12,
    val status: String = "Confirmed"
)

@Entity(tableName = "transactions")
data class WalletTransaction(
    @PrimaryKey val id: String, // e.g. "TXN001"
    val title: String,
    val date: String,
    val amount: Int,
    val type: String, // "credit", "debit", "admin"
    val status: String = "Completed"
)

@Entity(tableName = "credit_ratings")
data class TenantCreditRating(
    @PrimaryKey val id: Long,
    val tenantName: String,
    val idNumber: String,
    val propertyTitle: String,
    val score: Int, // 100 - 500
    val daysLate: Int,
    val statusLabel: String
)

@Entity(tableName = "wallet")
data class UserWallet(
    @PrimaryKey val id: Int = 1,
    val balance: Int = 150000
)

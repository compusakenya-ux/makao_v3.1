package com.example.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.models.EscrowRecord
import com.example.data.models.MaintenanceRequest
import com.example.data.models.Property
import com.example.data.models.PropertyBooking
import com.example.data.models.RentalApplication
import com.example.data.models.TenantCreditRating
import com.example.data.models.UserWallet
import com.example.data.models.WalletTransaction
import kotlinx.coroutines.flow.Flow

@Dao
interface MakaoDao {
    @Query("SELECT * FROM properties ORDER BY id DESC")
    fun getAllProperties(): Flow<List<Property>>

    @Query("SELECT * FROM properties WHERE id = :id")
    fun getPropertyById(id: Long): Flow<Property?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProperty(property: Property)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProperties(properties: List<Property>)

    @Update
    suspend fun updateProperty(property: Property)

    @Delete
    suspend fun deleteProperty(property: Property)

    @Query("DELETE FROM properties WHERE id = :id")
    suspend fun deletePropertyById(id: Long)

    @Query("SELECT * FROM applications ORDER BY date DESC")
    fun getAllApplications(): Flow<List<RentalApplication>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertApplication(application: RentalApplication)

    @Update
    suspend fun updateApplication(application: RentalApplication)

    @Query("UPDATE applications SET status = :status WHERE id = :id")
    suspend fun updateApplicationStatus(id: String, status: String)

    @Query("SELECT * FROM bookings ORDER BY date DESC")
    fun getAllBookings(): Flow<List<PropertyBooking>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBooking(booking: PropertyBooking)

    @Update
    suspend fun updateBooking(booking: PropertyBooking)

    @Query("SELECT * FROM transactions ORDER BY id DESC")
    fun getAllTransactions(): Flow<List<WalletTransaction>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: WalletTransaction)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransactions(transactions: List<WalletTransaction>)

    @Query("SELECT * FROM wallet WHERE id = 1")
    fun getWallet(): Flow<UserWallet?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWallet(wallet: UserWallet)

    @Query("SELECT * FROM credit_ratings ORDER BY score DESC")
    fun getAllCreditRatings(): Flow<List<TenantCreditRating>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCreditRating(rating: TenantCreditRating)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCreditRatings(ratings: List<TenantCreditRating>)

    @Update
    suspend fun updateCreditRating(rating: TenantCreditRating)

    @Query("UPDATE credit_ratings SET score = :score, daysLate = :daysLate, statusLabel = :statusLabel WHERE id = :id")
    suspend fun updateCreditScore(id: Long, score: Int, daysLate: Int, statusLabel: String)

    // Maintenance Requests
    @Query("SELECT * FROM maintenance_requests ORDER BY id DESC")
    fun getAllMaintenanceRequests(): Flow<List<MaintenanceRequest>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMaintenanceRequest(request: MaintenanceRequest)

    @Update
    suspend fun updateMaintenanceRequest(request: MaintenanceRequest)

    // Escrow Records
    @Query("SELECT * FROM escrow_records ORDER BY id DESC")
    fun getAllEscrowRecords(): Flow<List<EscrowRecord>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEscrowRecord(record: EscrowRecord)

    @Update
    suspend fun updateEscrowRecord(record: EscrowRecord)
}


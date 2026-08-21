package com.example.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.models.Property
import com.example.data.models.PropertyBooking
import com.example.data.models.RentalApplication
import com.example.data.models.TenantCreditRating
import com.example.data.models.UserWallet
import com.example.data.models.WalletTransaction
import kotlinx.coroutines.flow.Flow

@Dao
interface MakaoDao {
    @Query("SELECT * FROM properties ORDER BY id ASC")
    fun getAllProperties(): Flow<List<Property>>

    @Query("SELECT * FROM properties WHERE id = :id")
    fun getPropertyById(id: Long): Flow<Property?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProperties(properties: List<Property>)

    @Update
    suspend fun updateProperty(property: Property)

    @Query("SELECT * FROM applications ORDER BY date DESC")
    fun getAllApplications(): Flow<List<RentalApplication>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertApplication(application: RentalApplication)

    @Query("SELECT * FROM bookings ORDER BY date DESC")
    fun getAllBookings(): Flow<List<PropertyBooking>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBooking(booking: PropertyBooking)

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
    suspend fun insertCreditRatings(ratings: List<TenantCreditRating>)
}

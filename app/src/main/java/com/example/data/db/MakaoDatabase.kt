package com.example.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.models.Property
import com.example.data.models.PropertyBooking
import com.example.data.models.RentalApplication
import com.example.data.models.TenantCreditRating
import com.example.data.models.UserWallet
import com.example.data.models.WalletTransaction

@Database(
    entities = [
        Property::class,
        RentalApplication::class,
        PropertyBooking::class,
        WalletTransaction::class,
        TenantCreditRating::class,
        UserWallet::class
    ],
    version = 1,
    exportSchema = false
)
abstract class MakaoDatabase : RoomDatabase() {
    abstract fun makaoDao(): MakaoDao

    companion object {
        @Volatile
        private var INSTANCE: MakaoDatabase? = null

        fun getInstance(context: Context): MakaoDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MakaoDatabase::class.java,
                    "makao_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

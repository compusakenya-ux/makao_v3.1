package com.example.data.repository

import com.example.data.db.MakaoDao
import com.example.data.models.EscrowRecord
import com.example.data.models.MaintenanceRequest
import com.example.data.models.Property
import com.example.data.models.PropertyBooking
import com.example.data.models.RentalApplication
import com.example.data.models.TenantCreditRating
import com.example.data.models.UserWallet
import com.example.data.models.WalletTransaction
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull

class MakaoRepository(private val dao: MakaoDao) {

    val allProperties: Flow<List<Property>> = dao.getAllProperties()
    val allApplications: Flow<List<RentalApplication>> = dao.getAllApplications()
    val allBookings: Flow<List<PropertyBooking>> = dao.getAllBookings()
    val allTransactions: Flow<List<WalletTransaction>> = dao.getAllTransactions()
    val wallet: Flow<UserWallet?> = dao.getWallet()
    val allCreditRatings: Flow<List<TenantCreditRating>> = dao.getAllCreditRatings()
    val allMaintenanceRequests: Flow<List<MaintenanceRequest>> = dao.getAllMaintenanceRequests()
    val allEscrowRecords: Flow<List<EscrowRecord>> = dao.getAllEscrowRecords()

    suspend fun seedDatabaseIfEmpty() {
        val existing = allProperties.firstOrNull()
        if (existing.isNullOrEmpty()) {
            dao.insertProperties(defaultProperties)
            dao.insertWallet(UserWallet(1, 150000))
            dao.insertTransactions(defaultTransactions)
            dao.insertCreditRatings(defaultCreditRatings)
            defaultMaintenanceRequests.forEach { dao.insertMaintenanceRequest(it) }
            defaultEscrowRecords.forEach { dao.insertEscrowRecord(it) }
        }
    }

    suspend fun resetDatabase() {
        dao.insertProperties(defaultProperties)
        dao.insertWallet(UserWallet(1, 150000))
        dao.insertTransactions(defaultTransactions)
        dao.insertCreditRatings(defaultCreditRatings)
        defaultMaintenanceRequests.forEach { dao.insertMaintenanceRequest(it) }
        defaultEscrowRecords.forEach { dao.insertEscrowRecord(it) }
    }

    suspend fun addProperty(property: Property) {
        dao.insertProperty(property)
    }

    suspend fun deleteProperty(property: Property) {
        dao.deleteProperty(property)
    }

    suspend fun updateProperty(property: Property) {
        dao.updateProperty(property)
    }

    suspend fun toggleFavorite(property: Property) {
        dao.updateProperty(property.copy(isFavorite = !property.isFavorite))
    }

    suspend fun incrementViewers(property: Property) {
        dao.updateProperty(property.copy(viewers = property.viewers + 1))
    }

    suspend fun submitApplication(application: RentalApplication) {
        dao.insertApplication(application)
        // Deduct KSh 200 Admin Fee from wallet
        val currentWallet = dao.getWallet().firstOrNull() ?: UserWallet()
        val newBalance = (currentWallet.balance - 200).coerceAtLeast(0)
        dao.insertWallet(currentWallet.copy(balance = newBalance))

        // Record Transaction
        dao.insertTransaction(
            WalletTransaction(
                id = "TXN_${System.currentTimeMillis() % 1000000}",
                title = "Application Fee — ${application.propertyTitle}",
                date = "Just now",
                amount = 200,
                type = "admin",
                status = "Completed"
            )
        )
    }

    suspend fun updateApplicationStatus(appId: String, newStatus: String) {
        dao.updateApplicationStatus(appId, newStatus)
    }

    suspend fun confirmBooking(booking: PropertyBooking) {
        dao.insertBooking(booking)
        // Deduct payment amount
        val currentWallet = dao.getWallet().firstOrNull() ?: UserWallet()
        val newBalance = (currentWallet.balance - booking.amountPaid).coerceAtLeast(0)
        dao.insertWallet(currentWallet.copy(balance = newBalance))

        // Calculate 2 months deposit and put into escrow record
        val rentEstimate = booking.amountPaid / 3
        val depositAmount = rentEstimate * 2

        dao.insertEscrowRecord(
            EscrowRecord(
                id = "ESC_${System.currentTimeMillis() % 100000}",
                bookingId = booking.id,
                propertyTitle = booking.propertyTitle,
                tenantName = "You (Verified Tenant)",
                depositAmount = depositAmount,
                status = "Held in Escrow",
                heldDate = "Today",
                leaseEndDate = "12 Months from Move-in"
            )
        )

        // Record Transaction
        dao.insertTransaction(
            WalletTransaction(
                id = "TXN_${System.currentTimeMillis() % 1000000}",
                title = "Rent & Deposit Escrow — ${booking.propertyTitle}",
                date = "Just now",
                amount = booking.amountPaid,
                type = "debit",
                status = "Completed"
            )
        )
    }

    suspend fun topUpWallet(amount: Int) {
        val currentWallet = dao.getWallet().firstOrNull() ?: UserWallet()
        val newBalance = currentWallet.balance + amount
        dao.insertWallet(currentWallet.copy(balance = newBalance))

        dao.insertTransaction(
            WalletTransaction(
                id = "TXN_${System.currentTimeMillis() % 1000000}",
                title = "Wallet M-Pesa Top-up",
                date = "Just now",
                amount = amount,
                type = "credit",
                status = "Completed"
            )
        )
    }

    suspend fun withdrawWallet(amount: Int) {
        val currentWallet = dao.getWallet().firstOrNull() ?: UserWallet()
        if (currentWallet.balance >= amount) {
            val newBalance = currentWallet.balance - amount
            dao.insertWallet(currentWallet.copy(balance = newBalance))

            dao.insertTransaction(
                WalletTransaction(
                    id = "TXN_${System.currentTimeMillis() % 1000000}",
                    title = "M-Pesa Withdrawal",
                    date = "Just now",
                    amount = amount,
                    type = "debit",
                    status = "Completed"
                )
            )
        }
    }

    suspend fun submitMaintenanceRequest(request: MaintenanceRequest) {
        dao.insertMaintenanceRequest(request)
    }

    suspend fun updateMaintenanceStatus(request: MaintenanceRequest, newStatus: String) {
        dao.updateMaintenanceRequest(request.copy(status = newStatus))
    }

    suspend fun updateEscrowStatus(record: EscrowRecord, newStatus: String) {
        dao.updateEscrowRecord(record.copy(status = newStatus))
    }

    suspend fun adjustTenantCreditScore(rating: TenantCreditRating, delta: Int, additionalLateDays: Int = 0) {
        val newScore = (rating.score + delta).coerceIn(100, 500)
        val newDaysLate = (rating.daysLate + additionalLateDays).coerceAtLeast(0)
        val newLabel = when {
            newScore >= 400 -> "Excellent — Top-tier tenant"
            newScore >= 300 -> "Good — Reliable tenant"
            newScore >= 200 -> "Fair — Monitored tenant"
            else -> "Poor — High risk tenant"
        }
        dao.updateCreditScore(rating.id, newScore, newDaysLate, newLabel)
    }

    companion object {
        val defaultMaintenanceRequests = listOf(
            MaintenanceRequest(
                id = "MAINT-101",
                propertyTitle = "Skyline Studio — Kilimani",
                tenantName = "John Kamau",
                issueType = "Plumbing",
                description = "Kitchen sink drainage pipe slow drip leaking into cabinet.",
                urgency = "Medium",
                dateReported = "Aug 20, 2026",
                status = "Assigned",
                estimatedCost = 3500
            ),
            MaintenanceRequest(
                id = "MAINT-102",
                propertyTitle = "The Nexus — Westlands",
                tenantName = "Mary Wanjiku",
                issueType = "Electrical",
                description = "Master bedroom ceiling recessed LED spotlight fixture flickering.",
                urgency = "Low",
                dateReported = "Aug 18, 2026",
                status = "Resolved",
                estimatedCost = 1800
            )
        )

        val defaultEscrowRecords = listOf(
            EscrowRecord(
                id = "ESC-801",
                bookingId = "BKG98214",
                propertyTitle = "Skyline Studio — Kilimani",
                tenantName = "John Kamau",
                depositAmount = 90000,
                status = "Held in Escrow",
                heldDate = "Jul 01, 2026",
                leaseEndDate = "Jun 30, 2027"
            ),
            EscrowRecord(
                id = "ESC-802",
                bookingId = "BKG77432",
                propertyTitle = "The Nexus — Westlands",
                tenantName = "Mary Wanjiku",
                depositAmount = 170000,
                status = "Held in Escrow",
                heldDate = "Aug 01, 2026",
                leaseEndDate = "Jul 31, 2027"
            )
        )
        val defaultProperties = listOf(
            Property(
                id = 1L,
                title = "Skyline Studio — Kilimani",
                location = "Kilimani, Nairobi",
                city = "Nairobi",
                estate = "Kilimani",
                type = "1 Bedroom Apartment",
                price = 45000,
                beds = 1,
                baths = 1,
                sqft = 45,
                rating = 4.8,
                badge = "new",
                viewers = 124,
                imageUrl = "https://images.unsplash.com/photo-1522708323590-d24dbb6b0267?w=800&q=80",
                description = "A sleek, fully-furnished 1-bedroom apartment in the heart of Kilimani. Features floor-to-ceiling windows with city views, smart home automation, and 24/7 security. Perfect for young working professionals in Yaya Centre or Hurlingham area.",
                amenitiesJson = "WiFi 1Gbps, Smart Lock, Gym Access, Rooftop Lounge, Backup Generator, Borehole Water",
                neighborhood = "Kilimani is Nairobi's premier neighborhood for young professionals. Walking distance to Yaya Centre, Adlife Plaza, and vibrant dining spots. Safe with 24/7 transport."
            ),
            Property(
                id = 2L,
                title = "The Nexus — Westlands",
                location = "Westlands, Nairobi",
                city = "Nairobi",
                estate = "Westlands",
                type = "2 Bedroom Apartment",
                price = 85000,
                beds = 2,
                baths = 2,
                sqft = 95,
                rating = 4.9,
                badge = "hot",
                viewers = 342,
                imageUrl = "https://images.unsplash.com/photo-1502672260266-1c1ef2d93688?w=800&q=80",
                description = "Premium 2-bedroom unit in Westlands' newest development. Open-plan living with Italian finishes, integrated appliances, and private balcony overlooking the city skyline.",
                amenitiesJson = "Swimming Pool, Underground Parking, Concierge, Home Cinema, Solar Backup, Fiber Internet",
                neighborhood = "Westlands is Nairobi's commercial hub. Close to Sarit Centre, Westgate, and expressway connections to JKIA."
            ),
            Property(
                id = 3L,
                title = "Greenview Heights — Lavington",
                location = "Lavington, Nairobi",
                city = "Nairobi",
                estate = "Lavington",
                type = "3 Bedroom Apartment",
                price = 120000,
                beds = 3,
                baths = 2,
                sqft = 140,
                rating = 4.7,
                badge = "verified",
                viewers = 89,
                imageUrl = "https://images.unsplash.com/photo-1493809842364-78817add7ffb?w=800&q=80",
                description = "Spacious 3-bedroom family apartment in leafy Lavington. Large garden-facing balcony, en-suite master, and modern quartz kitchen.",
                amenitiesJson = "Garden View, Kids Play Area, Guest Parking, Water Storage, CCTV, Intercom",
                neighborhood = "Lavington offers peaceful suburban living near Lavington Mall and top schools."
            ),
            Property(
                id = 4L,
                title = "Pulse Apartments — Kileleshwa",
                location = "Kileleshwa, Nairobi",
                city = "Nairobi",
                estate = "Kileleshwa",
                type = "Bedsitter",
                price = 18000,
                beds = 1,
                baths = 1,
                sqft = 28,
                rating = 4.5,
                badge = "new",
                viewers = 67,
                imageUrl = "https://images.unsplash.com/photo-1502005229762-cf1b2da7c5d6?w=800&q=80",
                description = "Trendy industrial-chic bedsitter in Kileleshwa. Rooftop workspace, fast WiFi, and creative atmosphere.",
                amenitiesJson = "Rooftop Workspace, Community Events, Bike Storage, Laundry Room, Pet Friendly, Fast WiFi",
                neighborhood = "Kileleshwa is popular with creatives and remote workers, minutes from CBD and Westlands."
            ),
            Property(
                id = 5L,
                title = "The Metropolitan — Upper Hill",
                location = "Upper Hill, Nairobi",
                city = "Nairobi",
                estate = "Upper Hill",
                type = "2 Bedroom Apartment",
                price = 95000,
                beds = 2,
                baths = 2,
                sqft = 110,
                rating = 4.9,
                badge = "hot",
                viewers = 256,
                imageUrl = "https://images.unsplash.com/photo-1545324418-cc1a3fa10c00?w=800&q=80",
                description = "Executive 2-bedroom in Upper Hill financial district. Soundproofed with dedicated home office nook.",
                amenitiesJson = "Home Office, Soundproofing, Hotel Service, Valet Parking, Business Lounge, Gym & Spa",
                neighborhood = "Upper Hill is Nairobi's financial heart with major corporate HQs and medical facilities."
            ),
            Property(
                id = 6L,
                title = "Coastal Breeze — Nyali Estate",
                location = "Nyali Estate, Mombasa",
                city = "Mombasa",
                estate = "Nyali Estate",
                type = "3 Bedroom Apartment",
                price = 65000,
                beds = 3,
                baths = 2,
                sqft = 120,
                rating = 4.6,
                badge = "new",
                viewers = 78,
                imageUrl = "https://images.unsplash.com/photo-1512917774080-9991f1c4c750?w=800&q=80",
                description = "Beautiful 3-bedroom apartment in Nyali Estate with refreshing sea breeze and partial ocean views. Close to City Mall and Nyali Beach.",
                amenitiesJson = "Sea Breeze, Balcony, Parking, Security Guard, Water Tank, Ceiling Fans",
                neighborhood = "Nyali Estate is Mombasa's top residential address near beaches, cafes, and City Mall."
            ),
            Property(
                id = 7L,
                title = "Bamburi Heights — Bamburi",
                location = "Bamburi, Mombasa",
                city = "Mombasa",
                estate = "Bamburi",
                type = "Single Room",
                price = 8000,
                beds = 1,
                baths = 1,
                sqft = 18,
                rating = 4.2,
                badge = "verified",
                viewers = 45,
                imageUrl = "https://images.unsplash.com/photo-1502005229762-cf1b2da7c5d6?w=800&q=80",
                description = "Affordable single room in Bamburi for students and workers. Clean, secure compound near public transport.",
                amenitiesJson = "Shared Bathroom, Water Available, Electricity, Secure Gate, Near Market",
                neighborhood = "Bamburi is a bustling, cost-effective residential area in Mombasa."
            ),
            Property(
                id = 8L,
                title = "Milimani Courts — Nakuru",
                location = "Milimani, Nakuru",
                city = "Nakuru",
                estate = "Milimani",
                type = "2 Bedroom Apartment",
                price = 35000,
                beds = 2,
                baths = 1,
                sqft = 70,
                rating = 4.4,
                badge = "verified",
                viewers = 56,
                imageUrl = "https://images.unsplash.com/photo-1522708323590-d24dbb6b0267?w=800&q=80",
                description = "Modern 2-bedroom apartment in Milimani, Nakuru's premier estate. Fitted kitchen and lake views.",
                amenitiesJson = "Parking, Fitted Kitchen, Security, Water Storage, Near Town, Lake View",
                neighborhood = "Milimani is Nakuru's most prestigious neighborhood near Lake Nakuru National Park."
            ),
            Property(
                id = 9L,
                title = "Elgon View Villa — Eldoret",
                location = "Elgon View, Eldoret",
                city = "Eldoret",
                estate = "Elgon View",
                type = "3 Bedroom Apartment",
                price = 42000,
                beds = 3,
                baths = 2,
                sqft = 110,
                rating = 4.5,
                badge = "verified",
                viewers = 71,
                imageUrl = "https://images.unsplash.com/photo-1545324418-cc1a3fa10c00?w=800&q=80",
                description = "Spacious 3-bedroom in Elgon View, Eldoret. Compound with private garden and ample parking near Moi University Hospital.",
                amenitiesJson = "Compound, Parking, Modern Finish, Security, Near University, Garden",
                neighborhood = "Elgon View is Eldoret's elite residential estate, peaceful and tree-lined."
            ),
            Property(
                id = 10L,
                title = "Kisumu Milimani Suites — Kisumu",
                location = "Milimani, Kisumu",
                city = "Kisumu",
                estate = "Milimani",
                type = "2 Bedroom Apartment",
                price = 38000,
                beds = 2,
                baths = 1,
                sqft = 75,
                rating = 4.3,
                badge = "verified",
                viewers = 62,
                imageUrl = "https://images.unsplash.com/photo-1512917774080-9991f1c4c750?w=800&q=80",
                description = "Elegant 2-bedroom with Lake Victoria views. Modern kitchen, secure compound near Kisumu CBD.",
                amenitiesJson = "Lake View, Modern Kitchen, Parking, Security, Near CBD, Balcony",
                neighborhood = "Milimani Kisumu is the upscale center for professionals and lakeside enjoyment."
            )
        )

        val defaultTransactions = listOf(
            WalletTransaction("TXN001", "Wallet M-Pesa Top-up", "Today, 10:23 AM", 50000, "credit"),
            WalletTransaction("TXN002", "Rent Payment — Skyline Studio", "Jul 25, 2026", 45000, "debit"),
            WalletTransaction("TXN003", "Security Deposit Refund", "Jul 20, 2026", 90000, "credit"),
            WalletTransaction("TXN004", "Application Fee — Greenview Heights", "Jul 18, 2026", 200, "admin")
        )

        val defaultCreditRatings = listOf(
            TenantCreditRating(1L, "John Kamau", "29876543", "Skyline Studio — Kilimani", 500, 0, "Excellent — Top-tier tenant"),
            TenantCreditRating(2L, "Mary Wanjiku", "30124567", "The Nexus — Westlands", 487, 13, "Excellent — Top-tier tenant"),
            TenantCreditRating(3L, "Peter Ochieng", "27456321", "Coastal Breeze — Nyali", 465, 35, "Excellent — Top-tier tenant"),
            TenantCreditRating(4L, "Grace Muthoni", "31234567", "Elgon View Villa — Eldoret", 320, 180, "Good — Reliable tenant"),
            TenantCreditRating(5L, "James Kipchirchir", "28901234", "Pioneer Studios — Eldoret", 150, 350, "Poor — High risk tenant")
        )
    }
}

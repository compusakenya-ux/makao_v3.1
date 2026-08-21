package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.db.MakaoDatabase
import com.example.data.models.EscrowRecord
import com.example.data.models.MaintenanceRequest
import com.example.data.models.Property
import com.example.data.models.PropertyBooking
import com.example.data.models.RentalApplication
import com.example.data.models.TenantCreditRating
import com.example.data.models.UserWallet
import com.example.data.models.WalletTransaction
import com.example.data.repository.MakaoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

private data class FilterParams(val city: String, val estate: String, val type: String, val budget: Int)
private data class SearchParams(val sort: String, val query: String)

class MakaoViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: MakaoRepository

    init {
        val dao = MakaoDatabase.getInstance(application).makaoDao()
        repository = MakaoRepository(dao)
        viewModelScope.launch {
            repository.seedDatabaseIfEmpty()
        }
    }

    // Filter states
    val selectedCity = MutableStateFlow("All Towns")
    val selectedEstate = MutableStateFlow("All Estates")
    val selectedType = MutableStateFlow("All Types")
    val selectedMaxBudget = MutableStateFlow(0)
    val sortOption = MutableStateFlow("newest")
    val searchQuery = MutableStateFlow("")

    // UI Navigation & Dialog States
    val activeTab = MutableStateFlow(0) // 0: Explore, 1: Wallet, 2: Applications, 3: Admin
    val selectedProperty = MutableStateFlow<Property?>(null)
    val showApplicationModal = MutableStateFlow(false)
    val showPaymentModal = MutableStateFlow(false)
    val showTopUpModal = MutableStateFlow(false)
    val showWithdrawModal = MutableStateFlow(false)
    val toastMessage = MutableStateFlow<Pair<String, String>?>(null)

    // Raw Repository Data Flows
    val allProperties: StateFlow<List<Property>> = repository.allProperties.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val wallet: StateFlow<UserWallet?> = repository.wallet.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = UserWallet()
    )

    val transactions: StateFlow<List<WalletTransaction>> = repository.allTransactions.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val applications: StateFlow<List<RentalApplication>> = repository.allApplications.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val bookings: StateFlow<List<PropertyBooking>> = repository.allBookings.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val creditRatings: StateFlow<List<TenantCreditRating>> = repository.allCreditRatings.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val maintenanceRequests: StateFlow<List<MaintenanceRequest>> = repository.allMaintenanceRequests.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val escrowRecords: StateFlow<List<EscrowRecord>> = repository.allEscrowRecords.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val showAddPropertyModal = MutableStateFlow(false)
    val showMaintenanceModal = MutableStateFlow(false)

    // Filtered Properties Stream
    val filteredProperties: StateFlow<List<Property>> = combine(
        allProperties,
        combine(selectedCity, selectedEstate, selectedType, selectedMaxBudget) { c, e, t, b -> FilterParams(c, e, t, b) },
        combine(sortOption, searchQuery) { s, q -> SearchParams(s, q) }
    ) { props, filter, search ->
        var list = props.filter { p ->
            val matchCity = filter.city == "All Towns" || p.city.equals(filter.city, ignoreCase = true)
            val matchEstate = filter.estate == "All Estates" || p.estate.equals(filter.estate, ignoreCase = true)
            val matchType = filter.type == "All Types" || p.type.equals(filter.type, ignoreCase = true)
            val matchBudget = filter.budget == 0 || p.price <= filter.budget
            val matchQuery = search.query.isEmpty() ||
                    p.title.contains(search.query, ignoreCase = true) ||
                    p.location.contains(search.query, ignoreCase = true) ||
                    p.estate.contains(search.query, ignoreCase = true)

            matchCity && matchEstate && matchType && matchBudget && matchQuery
        }

        list = when (search.sort) {
            "price-low" -> list.sortedBy { it.price }
            "price-high" -> list.sortedByDescending { it.price }
            "popular" -> list.sortedByDescending { it.viewers }
            else -> list.sortedByDescending { it.id }
        }

        list
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun onSelectCity(city: String) {
        selectedCity.value = city
        selectedEstate.value = "All Estates"
    }

    fun toggleFavorite(property: Property) {
        viewModelScope.launch {
            repository.toggleFavorite(property)
            val msg = if (!property.isFavorite) "Added to your saved properties" else "Removed from saved properties"
            showToast("Favorites Updated", msg)
        }
    }

    fun openPropertyDetail(property: Property) {
        selectedProperty.value = property
        viewModelScope.launch {
            repository.incrementViewers(property)
        }
    }

    fun closePropertyDetail() {
        selectedProperty.value = null
    }

    fun submitApplication(
        tenantName: String,
        idNumber: String,
        tel: String,
        incomeSource: String,
        sourceDetail: String,
        uploadedDocsCount: Int
    ) {
        val property = selectedProperty.value ?: return
        viewModelScope.launch {
            val appId = "APP${(100000..999999).random()}"
            val app = RentalApplication(
                id = appId,
                propertyId = property.id,
                propertyTitle = property.title,
                tenantName = tenantName,
                idNumber = idNumber,
                tel = tel,
                incomeSource = incomeSource,
                sourceDetail = sourceDetail,
                amount = 200,
                feeType = "Application Fee",
                date = "Today",
                status = "Pending",
                docsSummary = "$uploadedDocsCount Documents Uploaded (Verified Format)"
            )
            repository.submitApplication(app)
            showApplicationModal.value = false
            showToast("Application Submitted", "KSh 200 Admin Fee deducted. Landlord will review within 48h.")
        }
    }

    fun processBookingPayment(phone: String, moveInDate: String, leaseDurationMonths: Int) {
        val property = selectedProperty.value ?: return
        viewModelScope.launch {
            val totalAmount = property.price * 3 // 2 months deposit + 1 month rent
            val bkgId = "BKG${(100000..999999).random()}"
            val booking = PropertyBooking(
                id = bkgId,
                propertyId = property.id,
                propertyTitle = property.title,
                amountPaid = totalAmount,
                paymentMethod = "M-Pesa STK Push ($phone)",
                date = "Today",
                moveInDate = moveInDate,
                leaseDurationMonths = leaseDurationMonths,
                status = "Confirmed"
            )
            repository.confirmBooking(booking)
            showPaymentModal.value = false
            showToast("Booking Confirmed", "M-Pesa STK Push receipt generated for KSh ${totalAmount}")
        }
    }

    fun topUpWallet(amount: Int) {
        viewModelScope.launch {
            repository.topUpWallet(amount)
            showTopUpModal.value = false
            showToast("Top Up Successful", "KSh $amount added to your Makao Wallet via M-Pesa")
        }
    }

    fun withdrawWallet(amount: Int) {
        val currentBalance = wallet.value?.balance ?: 0
        if (currentBalance < amount) {
            showToast("Insufficient Balance", "Your wallet balance is KSh $currentBalance")
            return
        }
        viewModelScope.launch {
            repository.withdrawWallet(amount)
            showWithdrawModal.value = false
            showToast("Withdrawal Processed", "KSh $amount sent to your M-Pesa number")
        }
    }

    fun approveApplication(appId: String) {
        viewModelScope.launch {
            repository.updateApplicationStatus(appId, "Approved")
            showToast("Application Approved", "Tenant notified. Digital lease agreement drafted & ready for signing.")
        }
    }

    fun rejectApplication(appId: String) {
        viewModelScope.launch {
            repository.updateApplicationStatus(appId, "Rejected")
            showToast("Application Rejected", "Application marked as rejected. KSh 200 non-refundable fee retained.")
        }
    }

    fun addNewProperty(
        title: String,
        location: String,
        city: String,
        estate: String,
        type: String,
        price: Int,
        beds: Int,
        baths: Int,
        sqft: Int,
        description: String,
        amenities: String
    ) {
        viewModelScope.launch {
            val newId = System.currentTimeMillis()
            val property = Property(
                id = newId,
                title = title,
                location = location,
                city = city,
                estate = estate,
                type = type,
                price = price,
                beds = beds,
                baths = baths,
                sqft = sqft,
                rating = 5.0,
                badge = "new",
                viewers = 1,
                imageUrl = "https://images.unsplash.com/photo-1522708323590-d24dbb6b0267?w=800&q=80",
                description = description,
                amenitiesJson = amenities,
                neighborhood = "$estate, $city is a highly sought-after neighborhood.",
                landlordName = "Makao Verified Partner",
                availableUnits = 1,
                totalUnits = 4
            )
            repository.addProperty(property)
            showAddPropertyModal.value = false
            showToast("Listing Published", "Property successfully added to Makao live database.")
        }
    }

    fun deleteProperty(property: Property) {
        viewModelScope.launch {
            repository.deleteProperty(property)
            showToast("Property Removed", "${property.title} was removed from the inventory.")
        }
    }

    fun adjustTenantCreditScore(rating: TenantCreditRating, delta: Int, additionalLateDays: Int = 0) {
        viewModelScope.launch {
            repository.adjustTenantCreditScore(rating, delta, additionalLateDays)
            val action = if (delta > 0) "awarded +$delta points" else "penalized $delta points"
            showToast("Credit Score Updated", "${rating.tenantName} $action.")
        }
    }

    fun submitMaintenanceRequest(
        propertyTitle: String,
        tenantName: String,
        issueType: String,
        description: String,
        urgency: String
    ) {
        viewModelScope.launch {
            val reqId = "MAINT-${(1000..9999).random()}"
            val request = MaintenanceRequest(
                id = reqId,
                propertyTitle = propertyTitle,
                tenantName = tenantName,
                issueType = issueType,
                description = description,
                urgency = urgency,
                dateReported = "Today",
                status = "Open",
                estimatedCost = when (urgency) {
                    "Emergency" -> 8500
                    "Medium" -> 3500
                    else -> 1500
                }
            )
            repository.submitMaintenanceRequest(request)
            showMaintenanceModal.value = false
            showToast("Ticket Created", "Maintenance ticket $reqId dispatched to property caretaker.")
        }
    }

    fun updateMaintenanceStatus(request: MaintenanceRequest, newStatus: String) {
        viewModelScope.launch {
            repository.updateMaintenanceStatus(request, newStatus)
            showToast("Ticket Updated", "${request.id} status changed to $newStatus.")
        }
    }

    fun updateEscrowStatus(record: EscrowRecord, newStatus: String) {
        viewModelScope.launch {
            repository.updateEscrowStatus(record, newStatus)
            showToast("Escrow Updated", "Escrow ${record.id} status updated to $newStatus.")
        }
    }

    fun resetDatabase() {
        viewModelScope.launch {
            repository.resetDatabase()
            showToast("Database Reset", "Default properties, escrow records, and seed data restored.")
        }
    }

    fun showToast(title: String, message: String) {
        toastMessage.value = Pair(title, message)
    }

    fun clearToast() {
        toastMessage.value = null
    }
}

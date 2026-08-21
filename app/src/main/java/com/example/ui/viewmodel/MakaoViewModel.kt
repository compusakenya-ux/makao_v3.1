package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.db.MakaoDatabase
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

    fun showToast(title: String, message: String) {
        toastMessage.value = Pair(title, message)
    }

    fun clearToast() {
        toastMessage.value = null
    }
}

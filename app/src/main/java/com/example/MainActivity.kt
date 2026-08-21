package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.MakaoBottomNavigation
import com.example.ui.components.MakaoTopBar
import com.example.ui.components.ToastNotification
import com.example.ui.screens.AdminDashboardScreen
import com.example.ui.screens.ApplicationModalDialog
import com.example.ui.screens.ApplicationsScreen
import com.example.ui.screens.ExploreScreen
import com.example.ui.screens.PaymentModalDialog
import com.example.ui.screens.PropertyDetailScreen
import com.example.ui.screens.TopUpModalDialog
import com.example.ui.screens.WalletScreen
import com.example.ui.screens.WithdrawModalDialog
import com.example.ui.theme.BgDeep
import com.example.ui.theme.MakaoTheme
import com.example.ui.viewmodel.MakaoViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MakaoTheme {
                MakaoApp()
            }
        }
    }
}

@Composable
fun MakaoApp(
    viewModel: MakaoViewModel = viewModel()
) {
    val activeTab by viewModel.activeTab.collectAsState()
    val selectedProperty by viewModel.selectedProperty.collectAsState()
    val showAppModal by viewModel.showApplicationModal.collectAsState()
    val showPayModal by viewModel.showPaymentModal.collectAsState()
    val showTopUpModal by viewModel.showTopUpModal.collectAsState()
    val showWithdrawModal by viewModel.showWithdrawModal.collectAsState()
    val toastMessage by viewModel.toastMessage.collectAsState()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = BgDeep,
        topBar = {
            if (selectedProperty == null) {
                MakaoTopBar()
            }
        },
        bottomBar = {
            if (selectedProperty == null) {
                MakaoBottomNavigation(
                    selectedTab = activeTab,
                    onTabSelected = { viewModel.activeTab.value = it }
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (selectedProperty != null) {
                PropertyDetailScreen(
                    property = selectedProperty!!,
                    onBackClick = { viewModel.closePropertyDetail() },
                    onBookNowClick = { viewModel.showPaymentModal.value = true },
                    onApplyNowClick = { viewModel.showApplicationModal.value = true }
                )
            } else {
                when (activeTab) {
                    0 -> ExploreScreen(
                        viewModel = viewModel,
                        onPropertyClick = { viewModel.openPropertyDetail(it) }
                    )
                    1 -> WalletScreen(viewModel = viewModel)
                    2 -> ApplicationsScreen(
                        viewModel = viewModel,
                        onExploreClick = { viewModel.activeTab.value = 0 }
                    )
                    3 -> AdminDashboardScreen(viewModel = viewModel)
                }
            }

            // Dialog Overlays
            if (showAppModal && selectedProperty != null) {
                ApplicationModalDialog(
                    property = selectedProperty!!,
                    onDismiss = { viewModel.showApplicationModal.value = false },
                    onSubmit = { name, idNum, tel, incomeSource, detail, docCount ->
                        viewModel.submitApplication(name, idNum, tel, incomeSource, detail, docCount)
                    }
                )
            }

            if (showPayModal && selectedProperty != null) {
                PaymentModalDialog(
                    property = selectedProperty!!,
                    onDismiss = { viewModel.showPaymentModal.value = false },
                    onPaymentSuccess = { phone, moveInDate, leaseMonths ->
                        viewModel.processBookingPayment(phone, moveInDate, leaseMonths)
                    }
                )
            }

            if (showTopUpModal) {
                TopUpModalDialog(
                    onDismiss = { viewModel.showTopUpModal.value = false },
                    onConfirmTopUp = { amount -> viewModel.topUpWallet(amount) }
                )
            }

            if (showWithdrawModal) {
                WithdrawModalDialog(
                    onDismiss = { viewModel.showWithdrawModal.value = false },
                    onConfirmWithdraw = { amount -> viewModel.withdrawWallet(amount) }
                )
            }

            // Toast Overlay
            ToastNotification(
                toastData = toastMessage,
                onDismiss = { viewModel.clearToast() },
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
    }
}


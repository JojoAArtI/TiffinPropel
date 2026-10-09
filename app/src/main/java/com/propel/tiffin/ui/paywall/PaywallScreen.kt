package com.propel.tiffin.ui.paywall

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.propel.tiffin.TiffinApplication
import com.propel.tiffin.ui.components.StickerBurst
import com.propel.tiffin.ui.theme.Surface
import com.propel.tiffin.ui.theme.SwiggyOrange
import com.propel.tiffin.ui.theme.TextPrimary
import com.propel.tiffin.ui.theme.TextSecondary
import com.propel.tiffin.ui.theme.TextTertiary

@Composable
fun PaywallScreen(
    trigger: String,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val container = (context.applicationContext as TiffinApplication).container
    val viewModel: PaywallViewModel = viewModel(
        factory = PaywallViewModelFactory(container, trigger)
    )
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val navBarPadding = WindowInsets.navigationBars.asPaddingValues()
    val haptic = LocalHapticFeedback.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Surface)
    ) {
        when (val current = state) {
            is PaywallUiState.Offer -> PaywallOffer(
                chargeDateText = current.chargeDateText,
                onStartTrial = {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    viewModel.confirmPurchase()
                },
                onDismiss = onDismiss,
                bottomPadding = navBarPadding.calculateBottomPadding()
            )
            is PaywallUiState.AlreadyPaid -> PaywallAlreadyPaid(
                onDismiss = onDismiss,
                bottomPadding = navBarPadding.calculateBottomPadding()
            )
            is PaywallUiState.JustPurchased -> PaywallJustPurchased(
                chargeDate = current.chargeDate,
                onDismiss = onDismiss,
                bottomPadding = navBarPadding.calculateBottomPadding()
            )
        }

        if (state is PaywallUiState.JustPurchased) {
            StickerBurst(modifier = Modifier.fillMaxSize())
        }
    }
}

@Composable
private fun PaywallOffer(
    chargeDateText: String,
    onStartTrial: () -> Unit,
    onDismiss: () -> Unit,
    bottomPadding: androidx.compose.ui.unit.Dp
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
            .padding(bottom = bottomPadding),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Unlock home-cooked meals",
            style = MaterialTheme.typography.titleLarge,
            color = TextPrimary,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "You'll be charged ₹1 today.\nYour ₹249/month plan starts\nautomatically on $chargeDateText.",
            style = MaterialTheme.typography.bodyLarge,
            color = TextSecondary,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = onStartTrial,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = SwiggyOrange,
                contentColor = Surface
            )
        ) {
            Text(
                text = "START ₹1 TRIAL",
                style = MaterialTheme.typography.labelLarge
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        TextButton(onClick = onDismiss) {
            Text(
                text = "Not now",
                style = MaterialTheme.typography.bodyMedium,
                color = TextTertiary
            )
        }
    }
}

@Composable
private fun PaywallAlreadyPaid(
    onDismiss: () -> Unit,
    bottomPadding: androidx.compose.ui.unit.Dp
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
            .padding(bottom = bottomPadding),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "You're subscribed",
            style = MaterialTheme.typography.titleLarge,
            color = TextPrimary,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Your trial is active.\nEnjoy home-cooked meals!",
            style = MaterialTheme.typography.bodyLarge,
            color = TextSecondary,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = onDismiss,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = SwiggyOrange,
                contentColor = Surface
            )
        ) {
            Text(
                text = "BROWSE KITCHENS",
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}

@Composable
private fun PaywallJustPurchased(
    chargeDate: String,
    onDismiss: () -> Unit,
    bottomPadding: androidx.compose.ui.unit.Dp
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
            .padding(bottom = bottomPadding),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "You're in!",
            style = MaterialTheme.typography.titleLarge,
            color = TextPrimary,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "₹1 charged now.\n₹249 on $chargeDate unless you cancel.",
            style = MaterialTheme.typography.bodyLarge,
            color = TextSecondary,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = onDismiss,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = SwiggyOrange,
                contentColor = Surface
            )
        ) {
            Text(
                text = "BROWSE KITCHENS",
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}

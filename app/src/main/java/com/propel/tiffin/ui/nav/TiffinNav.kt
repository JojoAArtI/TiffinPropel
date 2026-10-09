package com.propel.tiffin.ui.nav

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.propel.tiffin.TiffinApplication
import com.propel.tiffin.ui.AppViewModel
import com.propel.tiffin.ui.AppViewModelFactory
import com.propel.tiffin.ui.detail.DetailScreen
import com.propel.tiffin.ui.list.ListScreen
import com.propel.tiffin.ui.paywall.PaywallScreen

object Routes {
    const val LIST = "list"
    const val DETAIL = "detail/{kitchenId}"
    const val PAYWALL = "paywall?trigger={trigger}"
}

@Composable
fun TiffinApp() {
    val context = LocalContext.current
    val container = (context.applicationContext as TiffinApplication).container
    val appViewModel: AppViewModel = viewModel(factory = AppViewModelFactory(container))
    val isPaid by appViewModel.isPaid.collectAsStateWithLifecycle()
    val navController = rememberNavController()

    LaunchedEffect(Unit) {
        appViewModel.thirdLaunchPaywall.collect {
            navController.navigate("paywall?trigger=third_launch")
        }
    }

    NavHost(navController = navController, startDestination = Routes.LIST) {
        composable(Routes.LIST) {
            ListScreen(
                onKitchenClick = { kitchenId ->
                    navController.navigate("detail/$kitchenId")
                }
            )
        }

        composable(
            route = Routes.DETAIL,
            arguments = listOf(navArgument("kitchenId") { type = NavType.StringType })
        ) { backStackEntry ->
            val kitchenId = backStackEntry.arguments?.getString("kitchenId") ?: return@composable
            DetailScreen(
                kitchenId = kitchenId,
                isPaid = isPaid,
                onBack = { navController.popBackStack() },
                onSubscribe = {
                    navController.navigate("paywall?trigger=subscribe_tap")
                }
            )
        }

        composable(
            route = Routes.PAYWALL,
            arguments = listOf(navArgument("trigger") {
                type = NavType.StringType
                defaultValue = "unknown"
            })
        ) { backStackEntry ->
            val trigger = backStackEntry.arguments?.getString("trigger") ?: "unknown"
            PaywallScreen(
                trigger = trigger,
                onDismiss = { navController.popBackStack() }
            )
        }
    }
}

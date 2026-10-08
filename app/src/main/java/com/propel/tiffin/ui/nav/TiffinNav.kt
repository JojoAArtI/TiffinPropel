package com.propel.tiffin.ui.nav

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.propel.tiffin.ui.detail.DetailScreen
import com.propel.tiffin.ui.list.ListScreen

object Routes {
    const val LIST = "list"
    const val DETAIL = "detail/{kitchenId}"
    const val PAYWALL = "paywall?trigger={trigger}"
}

@Composable
fun TiffinApp() {
    val navController = rememberNavController()

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
                onBack = { navController.popBackStack() },
                onSubscribe = { id ->
                    Log.d("TiffinNav", "TODO paywall subscribe_tap for $id")
                }
            )
        }
    }
}

package com.aazm.easyadmin.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.aazm.easyadmin.ui.ProductViewModel
import com.aazm.easyadmin.ui.screens.products.ProductsRoute
import com.aazm.easyadmin.ui.screens.products.detail.ProductDetailRoute
import com.aazm.easyadmin.ui.screens.home.HomeRoute   // 👈 NUEVO
import com.aazm.easyadmin.ui.screens.scan.BarcodeScannerRoute
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch

object Routes {
    const val HOME = "home"                      // 👈 NUEVO
    const val PRODUCTS = "products"
    const val PRODUCT_DETAIL = "product/{cc}"
    const val SCAN = "scan"
    fun productDetail(cc: String) = "product/$cc"
}

@Composable
fun AppNavHost(
    navController: NavHostController,
    productVm: ProductViewModel
) {
    NavHost(
        navController = navController,
        startDestination = Routes.HOME          // 👈 Arranca en el menú principal
    ) {
        // HOME / MENÚ PRINCIPAL
        composable(Routes.HOME) {
            HomeRoute(
                onOpenInventory = { navController.navigate(Routes.PRODUCTS) },
                onOpenSales = { /* TODO: ventas */ },
                onOpenCount = { /* TODO: conteo */ },
                onOpenPrintCfg = { /* TODO: configuración impresión */ },
                onOpenInvoice = { /* TODO: crear factura */ }
            )
        }

        // LISTA DE PRODUCTOS
        composable(Routes.PRODUCTS) {
            ProductsRoute(
                vm = productVm,
                onNavigate = { route -> navController.navigate(route) },
                onOpenDetail = { cc -> navController.navigate(Routes.productDetail(cc)) }
            )
        }

        // DETALLE
        composable(
            route = Routes.PRODUCT_DETAIL,
            arguments = listOf(navArgument("cc") { type = NavType.StringType })
        ) { backStack ->
            val cc = backStack.arguments?.getString("cc").orEmpty()
            ProductDetailRoute(
                ccProducto = cc,
                vm = productVm,
                onBack = { navController.popBackStack() }
            )
        }

        // ESCÁNER
        composable(Routes.SCAN) {
            val scope = rememberCoroutineScope()
            val snack = remember { SnackbarHostState() }

            BarcodeScannerRoute(
                onBarcode = { code ->
                    scope.launch {
                        val product = productVm.findByBarcode(code)
                        if (product != null) {
                            navController.popBackStack()
                            navController.navigate(Routes.productDetail(product.CCProducto))
                        } else {
                            navController.popBackStack()
                            // Opcional: productVm.setSearch(code)
                        }
                    }
                },
                onClose = { navController.popBackStack() }
            )
        }
    }
}

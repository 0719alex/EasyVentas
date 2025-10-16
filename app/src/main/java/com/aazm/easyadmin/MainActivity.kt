package com.aazm.easyadmin

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.navigation.compose.rememberNavController
import com.aazm.easyadmin.ui.ProductViewModel
import com.aazm.easyadmin.ui.navigation.AppNavHost
import com.aazm.easyadmin.ui.theme.AppTheme

class MainActivity : ComponentActivity() {
    private val productVm: ProductViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme(useDynamic = true) {
                Surface(color = MaterialTheme.colorScheme.background) {
                    val navController = rememberNavController()
                    AppNavHost(
                        navController = navController,
                        productVm = productVm
                    )
                }
            }
        }
    }
}

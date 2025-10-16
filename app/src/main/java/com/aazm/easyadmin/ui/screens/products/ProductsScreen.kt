package com.aazm.easyadmin.ui.screens.products

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.QrCodeScanner   // 👈 NUEVO
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.aazm.easyadmin.data.db.ProductEntity
import com.aazm.easyadmin.ui.ProductViewModel
import com.aazm.easyadmin.ui.components.ProductCard
import com.aazm.easyadmin.ui.navigation.Routes             // 👈 NUEVO
import kotlinx.coroutines.launch

@Composable
fun ProductsRoute(
    vm: ProductViewModel,
    onNavigate: (String) -> Unit,
    onOpenDetail: (String) -> Unit
) {
    val ui by vm.ui.collectAsState()
    val snackbar = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(ui.error) {
        ui.error?.let { scope.launch { snackbar.showSnackbar(it) } }
    }

    ProductsScreen(
        isSyncing = ui.isSyncing,
        progressPct = ui.progressPct,
        items = ui.items,
        searchText = ui.search,
        onSearchChange = vm::setSearch,
        onSync = vm::sync,
        snackbarHostState = snackbar,
        onItemClick = { p -> onOpenDetail(p.CCProducto) },
        onScanClick = { onNavigate(Routes.SCAN) }  // 👈 NUEVO
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductsScreen(
    isSyncing: Boolean,
    progressPct: Int?,
    items: List<ProductEntity>,
    searchText: String,
    onSearchChange: (String) -> Unit,
    onSync: () -> Unit,
    snackbarHostState: SnackbarHostState,
    onItemClick: (ProductEntity) -> Unit,
    onScanClick: () -> Unit                              // 👈 NUEVO
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val spacing = adaptiveItemSpacing()

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            LargeTopAppBar(
                title = { Text("Inventario") },
                navigationIcon = { Icon(Icons.Default.ShoppingCart, contentDescription = null) },
                actions = {
                    IconButton(onClick = onSync, enabled = !isSyncing) {
                        Icon(Icons.Default.CloudSync, contentDescription = "Sincronizar")
                    }
                    IconButton(onClick = onScanClick) {                       // 👈 NUEVO
                        Icon(Icons.Default.QrCodeScanner, contentDescription = "Escanear")
                    }
                },
                scrollBehavior = scrollBehavior
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onSync,
                icon = { Icon(Icons.Default.CloudSync, contentDescription = null) },
                text = { Text(if (isSyncing) "Sincronizando…" else "Sincronizar") },
                expanded = !isSyncing
            )
        }
    ) { padding ->
        Column(
            Modifier
                .fillMaxSize()
                .nestedScroll(scrollBehavior.nestedScrollConnection)
                .padding(padding)
        ) {
            SearchBar(
                text = searchText,
                onTextChanged = onSearchChange,
                onScanClick = onScanClick               // 👈 NUEVO (icono en la barra de búsqueda)
            )

            AnimatedVisibility(visible = isSyncing && progressPct != null) {
                Column(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 6.dp)) {
                    LinearProgressIndicator(
                        progress = (progressPct?.coerceIn(0, 100)?.div(100f)) ?: 0f,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "Descargando inventario: ${progressPct ?: 0}%",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            AnimatedVisibility(visible = items.isEmpty() && !isSyncing) {
                Box(Modifier.fillMaxSize().padding(24.dp)) {
                    Text("No hay productos. Pulsa “Sincronizar”.")
                }
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(vertical = spacing, horizontal = spacing),
                verticalArrangement = Arrangement.spacedBy(spacing)
            ) {
                items(items) { p ->
                    ProductCard(
                        product = p,
                        compact = true,
                        onClick = { onItemClick(p) }
                    )
                }
            }
        }
    }
}

@Composable
private fun SearchBar(
    text: String,
    onTextChanged: (String) -> Unit,
    onScanClick: (() -> Unit)? = null                 // 👈 NUEVO
) {
    var tf by remember { mutableStateOf(TextFieldValue(text)) }
    LaunchedEffect(text) { if (text != tf.text) tf = tf.copy(text = text) }

    OutlinedTextField(
        value = tf,
        onValueChange = {
            tf = it
            onTextChanged(it.text)
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp),
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
        trailingIcon = {
            if (onScanClick != null) {
                IconButton(onClick = onScanClick) {
                    Icon(Icons.Default.QrCodeScanner, contentDescription = "Escanear")
                }
            }
        },
        label = { Text("Buscar por nombre o código") },
        singleLine = true
    )
}

@Composable
private fun adaptiveItemSpacing(): Dp {
    val w = LocalConfiguration.current.screenWidthDp
    return when {
        w < 360 -> 6.dp
        w < 600 -> 8.dp
        w < 840 -> 10.dp
        else -> 12.dp
    }
}

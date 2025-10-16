package com.aazm.easyadmin.ui.screens.products.detail

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.aazm.easyadmin.data.db.ProductEntity
import com.aazm.easyadmin.ui.ProductViewModel
import com.aazm.easyadmin.util.asLempira
import com.aazm.easyadmin.util.csvToList
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.ExperimentalLayoutApi

@Composable
fun ProductDetailRoute(
    ccProducto: String,
    vm: ProductViewModel,
    onBack: () -> Unit
) {
    val product by vm.observeProduct(ccProducto).collectAsState(initial = null)
    ProductDetailScreen(product = product, onBack = onBack)
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ProductDetailScreen(
    product: ProductEntity?,
    onBack: () -> Unit
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Detalle de producto") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás")
                    }
                },
                scrollBehavior = scrollBehavior
            )
        }
    ) { padding ->
        if (product == null) {
            Box(
                Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) { CircularProgressIndicator() }
        } else {
            Column(
                Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // HERO: Artículo grande
                ElevatedCard(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Artículo", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
                        SelectionContainer {
                            Text(
                                text = product.Articulo.ifBlank { "—" },
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }

                // KPIs + Barcodes (todas las repeticiones)
                ElevatedCard(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.QrCode, contentDescription = null)
                            Spacer(Modifier.width(10.dp))
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text("Códigos de barras", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)

                                val allBarcodes = product.CodigosBarra.csvToList()
                                    .ifEmpty { listOf(product.CodigoBarra).filter { it.isNotBlank() } }

                                if (allBarcodes.isEmpty()) {
                                    Text("Sin código", style = MaterialTheme.typography.titleMedium)
                                } else {
                                    FlowRow(
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        verticalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        allBarcodes.forEach { code ->
                                            AssistChip(onClick = { /* copiar/compartir si quieres */ }, label = { Text(code) })
                                        }
                                    }
                                }
                            }
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.Category, contentDescription = null)
                            Spacer(Modifier.width(10.dp))
                            Column {
                                Text("Categoría", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
                                Text(product.Categoria.ifBlank { "—" }, style = MaterialTheme.typography.titleMedium)
                            }
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.Inventory2, contentDescription = null)
                            Spacer(Modifier.width(10.dp))
                            Column {
                                Text("Existencia", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
                                Text("${product.Existencia}", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                }

                // Precios 2x2
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    PriceCard("Precio 1", product.Precio1, modifier = Modifier.weight(1f))
                    PriceCard("Precio 2", product.Precio2, modifier = Modifier.weight(1f))
                }
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    PriceCard("Precio 3", product.Precio3, modifier = Modifier.weight(1f))
                    PriceCard("Precio 4", product.Precio4, modifier = Modifier.weight(1f))
                }

                // Info técnica
                ElevatedCard(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text("Información técnica", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Medium)
                        DetailRow("CCProducto", product.CCProducto)
                        DetailRow("Artículo", product.Articulo)
                        DetailRow("Códigos (CSV)", product.CodigosBarra.ifBlank { "—" })
                        DetailRow("Código principal", product.CodigoBarra.ifBlank { "—" })
                        DetailRow("Categoría", product.Categoria.ifBlank { "—" })
                        DetailRow("Existencia", "${product.Existencia}")
                        DetailRow("Precio 1", product.Precio1.asLempira())
                        DetailRow("Precio 2", product.Precio2.asLempira())
                        DetailRow("Precio 3", product.Precio3.asLempira())
                        DetailRow("Precio 4", product.Precio4.asLempira())
                    }
                }

                Spacer(Modifier.height(8.dp))
            }
        }
    }
}

@Composable
private fun PriceCard(title: String, value: Double, modifier: Modifier = Modifier) {
    ElevatedCard(modifier) {
        Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(title, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
            Text(value.asLempira(), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Column(Modifier.fillMaxWidth()) {
        Text(label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
        SelectionContainer {
            Text(
                value.ifBlank { "—" },
                style = MaterialTheme.typography.bodyLarge,  // 👈 aquí estaba el typo
                maxLines = Int.MAX_VALUE,
                overflow = TextOverflow.Visible
            )
        }
    }
}


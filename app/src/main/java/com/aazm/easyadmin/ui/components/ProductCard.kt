package com.aazm.easyadmin.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.aazm.easyadmin.data.db.ProductEntity
import com.aazm.easyadmin.util.asLempira

@Composable
fun ProductCard(
    product: ProductEntity,
    compact: Boolean = false,
    onClick: (() -> Unit)? = null
) {
    if (compact) {
        ProductCardCompact(product, onClick)
    } else {
        ProductCardFull(product, onClick)
    }
}

// Compacta: SOLO Articulo, Existencia, Precio1 (formato Lempira)
@Composable
private fun ProductCardCompact(product: ProductEntity, onClick: (() -> Unit)?) {
    ElevatedCard(
        Modifier
            .fillMaxWidth()
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier)
    ) {
        Column(Modifier.padding(14.dp)) {
            Text(
                product.Articulo,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(Modifier.height(6.dp))
            Text("Existencia: ${product.Existencia}", style = MaterialTheme.typography.bodyMedium)
            Text("Precio1: ${product.Precio1.asLempira()}", style = MaterialTheme.typography.bodyMedium)
        }
    }
}

// Full: muestra todos los precios
@Composable
private fun ProductCardFull(product: ProductEntity, onClick: (() -> Unit)?) {
    ElevatedCard(
        Modifier
            .fillMaxWidth()
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier)
    ) {
        Column(Modifier.padding(14.dp)) {
            Text(
                product.Articulo,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(Modifier.height(4.dp))
            Text("Código: ${product.CodigoBarra}", style = MaterialTheme.typography.bodyMedium)
            Spacer(Modifier.height(8.dp))
            Text("Existencia: ${product.Existencia}", style = MaterialTheme.typography.bodyMedium)
            Spacer(Modifier.height(6.dp))
            Text("Precio1: ${product.Precio1.asLempira()}", style = MaterialTheme.typography.bodyMedium)
            Text("Precio2: ${product.Precio2.asLempira()}", style = MaterialTheme.typography.bodyMedium)
            Text("Precio3: ${product.Precio3.asLempira()}", style = MaterialTheme.typography.bodyMedium)
            Text("Precio4: ${product.Precio4.asLempira()}", style = MaterialTheme.typography.bodyMedium)
            Spacer(Modifier.height(6.dp))
            Text("Categoría: ${product.Categoria}", style = MaterialTheme.typography.bodyMedium)
        }
    }
}

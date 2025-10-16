package com.aazm.easyadmin.ui.screens.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.min


import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.foundation.BorderStroke

import androidx.compose.ui.graphics.vector.ImageVector

import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow

import androidx.compose.ui.unit.dp
import androidx.compose.ui.draw.alpha


import androidx.compose.ui.draw.blur


data class HomeItem(
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val gradient: List<Color>,
    val onClick: () -> Unit
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeRoute(
    onOpenInventory: () -> Unit,
    onOpenSales: () -> Unit,
    onOpenCount: () -> Unit,
    onOpenPrintCfg: () -> Unit,
    onOpenInvoice: () -> Unit
) {
    val c = MaterialTheme.colorScheme
    val items = listOf(
        HomeItem(
            "Inventario", "Ver y buscar productos",
            Icons.Rounded.Inventory2, listOf(c.primary, c.tertiary), onOpenInventory
        ),
        HomeItem(
            "Ventas", "Registrar ventas",
            Icons.Rounded.PointOfSale, listOf(Color(0xFF00B09B), Color(0xFF96C93D)), onOpenSales
        ),
        HomeItem(
            "Conteo Productos", "Toma física",
            Icons.Rounded.FactCheck, listOf(Color(0xFFFF512F), Color(0xFFF09819)), onOpenCount
        ),
        HomeItem(
            "Config. de Impresión", "Bluetooth / tamaño",
            Icons.Rounded.Print, listOf(Color(0xFF7F7FD5), Color(0xFF86A8E7), Color(0xFF91EAE4)), onOpenPrintCfg
        ),
        HomeItem(
            "Crear Factura", "Nueva venta",
            Icons.Rounded.ReceiptLong, listOf(Color(0xFFEE7752), Color(0xFFE73C7E)), onOpenInvoice
        )
    )

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("EasyVentas", fontWeight = FontWeight.SemiBold) }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(listOf(
                    MaterialTheme.colorScheme.surface, MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
                )))
                .padding(padding)
        ) {
            // Fondo moderno con blobs animados
            AnimatedBlobsBackground()

            // Contenido
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 160.dp),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                items(items) { item ->
                    GlassTile(item)
                }
            }
        }
    }
}

/* ----------------------------- Fondo animado ------------------------------ */

@Composable
private fun AnimatedBlobsBackground() {
    val c = MaterialTheme.colorScheme
    val infinite = rememberInfiniteTransition(label = "bg")
    val anim1 by infinite.animateFloat(
        initialValue = -100f, targetValue = 100f,
        animationSpec = infiniteRepeatable(animation = tween(9000, easing = LinearEasing), repeatMode = RepeatMode.Reverse),
        label = "b1"
    )
    val anim2 by infinite.animateFloat(
        initialValue = 120f, targetValue = -120f,
        animationSpec = infiniteRepeatable(animation = tween(11000, easing = LinearEasing), repeatMode = RepeatMode.Reverse),
        label = "b2"
    )
    val anim3 by infinite.animateFloat(
        initialValue = -60f, targetValue = 60f,
        animationSpec = infiniteRepeatable(animation = tween(8000, easing = LinearEasing), repeatMode = RepeatMode.Reverse),
        label = "b3"
    )

    Canvas(Modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height

        fun blob(center: Offset, radius: Float, color: Color) {
            drawCircle(
                brush = Brush.radialGradient(listOf(color.copy(alpha = 0.35f), Color.Transparent)),
                radius = radius, center = center
            )
        }
        blob(Offset(w * 0.15f + anim1, h * 0.2f), w * 0.35f, c.primary)
        blob(Offset(w * 0.85f + anim2, h * 0.25f), w * 0.32f, c.tertiary)
        blob(Offset(w * 0.5f + anim3, h * 0.85f), w * 0.42f, c.secondary)
    }
}

/* ------------------------------ Tile “glass” ------------------------------ */

@Composable
private fun GlassTile(item: HomeItem) {
    val shape = RoundedCornerShape(24.dp)
    val interaction = remember { MutableInteractionSource() }
    var pressed by remember { mutableStateOf(false) }

    // Animación de “tap”
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.98f else 1f,
        animationSpec = spring(stiffness = Spring.StiffnessLow),
        label = "pressScale"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .graphicsLayer { /* nada de alpha ni blur aquí */ }
    ) {
        // 1) Capa de borde con gradiente y BLUR (detrás de la card)
        Box(
            Modifier
                .matchParentSize()
                .clip(shape)
                .background(Brush.linearGradient(item.gradient))
                .blur(10.dp)                 // <-- blur solo al borde de fondo
                .alpha(0.55f)                // suaviza el glow
        )

        // 2) Card “glass” NÍTIDA encima (sin blur en ancestros)
        Card(
            modifier = Modifier
                .matchParentSize()
                .padding(1.2.dp)             // “grosor” del borde
                .clip(shape)
                .graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                }
                .clickable(
                    interactionSource = interaction,
                    indication = null
                ) {
                    pressed = true
                    item.onClick()
                    pressed = false
                },
            shape = shape,
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.55f)
            ),
            border = BorderStroke(
                width = 1.dp,
                brush = Brush.linearGradient(
                    listOf(
                        Color.White.copy(alpha = 0.6f),
                        Color.White.copy(alpha = 0.25f)
                    )
                )
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            TileContent(item)  // ← contenido nítido
        }
    }
}


@Composable
private fun TileContent(item: HomeItem) {
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Ícono adaptativo, pero con límites seguros
        val side = androidx.compose.ui.unit.min(maxWidth, maxHeight)
        val iconSize = (side * 0.36f).coerceIn(52.dp, 84.dp)

        // Glow del ícono: usamos una capa EXCLUSIVA con blur, nunca sobre el texto
        Box(Modifier.fillMaxSize().padding(bottom = 44.dp)) {
            // Glow detrás
            Icon(
                imageVector = item.icon,
                contentDescription = null,
                tint = item.gradient.first().copy(alpha = 0.5f),
                modifier = Modifier
                    .size(iconSize)
                    .align(Alignment.Center)
                    .blur(18.dp)          // ← blur solo al glow, no al resto
            )
            // Ícono principal nítido
            Icon(
                imageVector = item.icon,
                contentDescription = item.title,
                tint = Color.White,
                modifier = Modifier
                    .size(iconSize)
                    .align(Alignment.Center)
            )
        }

        // Textos nítidos (sin parent blur)
        Column(
            modifier = Modifier.align(Alignment.BottomStart)
        ) {
            Text(
                text = item.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = Color.White,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = item.subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = Color.White.copy(alpha = 0.92f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}


/* ------------------------- util: BoxWithConstraints ------------------------ */

@Composable
private fun BoxWithConstraints(
    modifier: Modifier = Modifier,
    content: @Composable BoxWithConstraintsScope.() -> Unit
) = androidx.compose.foundation.layout.BoxWithConstraints(modifier, content = content)

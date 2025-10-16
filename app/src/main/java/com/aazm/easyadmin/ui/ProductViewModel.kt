package com.aazm.easyadmin.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.aazm.easyadmin.AppModule
import com.aazm.easyadmin.data.db.ProductEntity
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class UiState(
    val isSyncing: Boolean = false,
    val progressPct: Int? = null,     // 0..100 mientras sincroniza; null si no
    val search: String = "",
    val items: List<ProductEntity> = emptyList(),
    val error: String? = null
)

class ProductViewModel(app: Application) : AndroidViewModel(app) {
    private val repo = AppModule.provideProductRepository(app)

    private val _isSyncing = MutableStateFlow(false)
    private val _progress = MutableStateFlow<Int?>(null)
    private val _search = MutableStateFlow("")
    private val _error = MutableStateFlow<String?>(null)

    private val _all = repo.observeProducts().stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList()
    )

    val ui: StateFlow<UiState> = combine(_isSyncing, _progress, _search, _all, _error) {
            syncing, pct, q, list, err ->
        val filtered = if (q.isBlank()) list else list.filter {
            it.Articulo.contains(q, ignoreCase = true) || it.CodigoBarra.contains(q, true)
        }
        UiState(syncing, pct, q, filtered, err)
    }.stateIn(viewModelScope, SharingStarted.Eagerly, UiState())

    fun setSearch(q: String) { _search.value = q }
    fun clearError() { _error.value = null }
    // --- NUEVO: observar un producto por CCProducto
    fun observeProduct(ccProducto: String) =
        AppModule.provideProductRepository(getApplication()).observeById(ccProducto)

    suspend fun findByBarcode(code: String) =
        AppModule.provideProductRepository(getApplication()).findByBarcode(code)

    fun sync() {
        viewModelScope.launch {
            _isSyncing.value = true
            _progress.value = 0
            _error.value = null
            try {
                repo.syncFromRemote { pct -> _progress.value = pct.coerceIn(0, 100) }
            } catch (t: Throwable) {
                _error.value = t.message ?: "Fallo desconocido al sincronizar"
            } finally {
                _isSyncing.value = false
                _progress.value = null
            }
        }
    }
}

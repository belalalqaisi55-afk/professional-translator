package com.example.data.local

import android.content.Context
import android.content.SharedPreferences
import com.example.data.model.OfflineLanguagePack
import com.example.data.model.OfflinePackStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class OfflinePackManager(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences("omni_offline_packs", Context.MODE_PRIVATE)
    private val scope = CoroutineScope(Dispatchers.Main)

    private val _packs = MutableStateFlow(loadPacks())
    val packs: StateFlow<List<OfflineLanguagePack>> = _packs.asStateFlow()

    private fun loadPacks(): List<OfflineLanguagePack> {
        return OfflineLanguagePack.DEFAULT_PACKS.map { pack ->
            val isDownloaded = prefs.getBoolean("pack_${pack.code}", pack.status == OfflinePackStatus.DOWNLOADED)
            pack.copy(
                status = if (isDownloaded) OfflinePackStatus.DOWNLOADED else OfflinePackStatus.NOT_DOWNLOADED,
                progress = if (isDownloaded) 1f else 0f
            )
        }
    }

    fun isPackDownloaded(code: String): Boolean {
        if (code == "auto") return true
        val pack = _packs.value.find { it.code.equals(code, ignoreCase = true) }
        return pack?.status == OfflinePackStatus.DOWNLOADED
    }

    fun downloadPack(code: String) {
        val currentPacks = _packs.value.toMutableList()
        val index = currentPacks.indexOfFirst { it.code.equals(code, ignoreCase = true) }
        if (index == -1) return

        val pack = currentPacks[index]
        if (pack.status == OfflinePackStatus.DOWNLOADING || pack.status == OfflinePackStatus.DOWNLOADED) return

        // Set downloading state
        currentPacks[index] = pack.copy(status = OfflinePackStatus.DOWNLOADING, progress = 0.05f)
        _packs.value = currentPacks

        scope.launch {
            // Simulate realistic progressive chunk downloads for the on-device ML model
            for (step in 1..10) {
                delay(300)
                val progress = step / 10f
                val list = _packs.value.toMutableList()
                val idx = list.indexOfFirst { it.code.equals(code, ignoreCase = true) }
                if (idx != -1) {
                    list[idx] = list[idx].copy(
                        status = if (progress >= 1f) OfflinePackStatus.DOWNLOADED else OfflinePackStatus.DOWNLOADING,
                        progress = progress
                    )
                    _packs.value = list
                }
            }

            // Save to prefs
            prefs.edit().putBoolean("pack_$code", true).apply()
        }
    }

    fun deletePack(code: String) {
        // We keep Arabic and English always or allow re-download
        prefs.edit().putBoolean("pack_$code", false).apply()
        val list = _packs.value.toMutableList()
        val idx = list.indexOfFirst { it.code.equals(code, ignoreCase = true) }
        if (idx != -1) {
            list[idx] = list[idx].copy(status = OfflinePackStatus.NOT_DOWNLOADED, progress = 0f)
            _packs.value = list
        }
    }
}

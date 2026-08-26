package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.local.TranslationEntity
import com.example.data.remote.FirebaseRepository
import com.example.data.remote.UserProfile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = FirebaseRepository(application)
    private val database = AppDatabase.getDatabase(application)
    private val translationDao = database.translationDao()

    val currentUser: StateFlow<UserProfile?> = repository.currentUser

    private val _isAuthenticating = MutableStateFlow(false)
    val isAuthenticating: StateFlow<Boolean> = _isAuthenticating.asStateFlow()

    private val _syncStatus = MutableStateFlow<String?>(null)
    val syncStatus: StateFlow<String?> = _syncStatus.asStateFlow()

    fun signInWithGoogle() {
        _isAuthenticating.value = true
        _syncStatus.value = null
        viewModelScope.launch {
            try {
                val result = repository.signInWithGoogle()
                if (result.isSuccess) {
                    _syncStatus.value = "تم تسجيل الدخول بنجاح ومزامنة البيانات مع السحابة"
                    syncCloudData()
                } else {
                    _syncStatus.value = "تعذر تسجيل الدخول، تم تفعيل وضع عدم الاتصال"
                }
            } catch (e: Exception) {
                _syncStatus.value = "خطأ: ${e.message}"
            } finally {
                _isAuthenticating.value = false
            }
        }
    }

    fun signOut() {
        viewModelScope.launch {
            repository.signOut()
            _syncStatus.value = "تم تسجيل الخروج بنجاح"
        }
    }

    fun syncCloudData() {
        viewModelScope.launch {
            try {
                val cloudItemsResult = repository.getCloudTranslations()
                val cloudItems = cloudItemsResult.getOrDefault(emptyList())
                for (item in cloudItems) {
                    translationDao.insert(item)
                }
                _syncStatus.value = "تمت مزامنة ${cloudItems.size} عنصراً من السحابة بنجاح"
            } catch (e: Exception) {
                _syncStatus.value = "فشلت المزامنة السحابية"
            }
        }
    }
}

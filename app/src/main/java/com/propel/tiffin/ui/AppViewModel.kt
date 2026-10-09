package com.propel.tiffin.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.propel.tiffin.data.AppPreferences
import com.propel.tiffin.di.AppContainer
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AppViewModel(
    private val prefs: AppPreferences
) : ViewModel() {

    val isPaid: StateFlow<Boolean> = prefs.isPaid
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    private val _thirdLaunchPaywall = Channel<Unit>(Channel.BUFFERED)
    val thirdLaunchPaywall = _thirdLaunchPaywall.receiveAsFlow()

    init {
        viewModelScope.launch {
            val paid = prefs.isPaid.first()
            val count = prefs.incrementLaunchCount()
            if (!paid && count == 3) {
                _thirdLaunchPaywall.send(Unit)
            }
        }
    }
}

class AppViewModelFactory(
    private val container: AppContainer
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return AppViewModel(container.appPreferences) as T
    }
}

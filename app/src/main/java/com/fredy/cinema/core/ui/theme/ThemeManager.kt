package com.fredy.cinema.core.ui.theme

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ThemeManager @Inject constructor() {
    private val _isDarkTheme = MutableStateFlow<Boolean?>(null) // null means follow system
    val isDarkTheme = _isDarkTheme.asStateFlow()

    fun setDarkTheme(isDark: Boolean?) {
        _isDarkTheme.value = isDark
    }
}
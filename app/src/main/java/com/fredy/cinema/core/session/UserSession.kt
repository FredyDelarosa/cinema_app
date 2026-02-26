package com.fredy.cinema.core.session

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserSession @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val prefs = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
    
    // Generamos un ID de sesión cada vez que la aplicación se instancia (o se inyecta por primera vez)
    // para cumplir con "cada nueva conexión debe contarse como un nuevo usuario"
    private val currentSessionUserId = UUID.randomUUID().toString()

    fun getUserId(): String {
        return currentSessionUserId
    }
}
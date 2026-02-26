package com.fredy.cinema.data.datasource.remote.websocket

import com.fredy.cinema.core.di.IoDispatcher
import com.fredy.cinema.core.di.CinemaApiUrl
import com.fredy.cinema.data.datasource.remote.websocket.models.ConnectionState
import com.fredy.cinema.data.datasource.remote.websocket.models.WebSocketMessage
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import okhttp3.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WebSocketManager @Inject constructor(
    private val client: OkHttpClient,
    private val gson: Gson,
    @CinemaApiUrl private val baseUrl: String,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) {
    private val _messages = MutableSharedFlow<WebSocketMessage>(
        extraBufferCapacity = 100,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val messages: SharedFlow<WebSocketMessage> = _messages.asSharedFlow()

    private val _connectionState = MutableStateFlow(ConnectionState.DISCONNECTED)
    val connectionState: StateFlow<ConnectionState> = _connectionState.asStateFlow()

    private var webSocket: WebSocket? = null

    private val listener = object : WebSocketListener() {
        override fun onOpen(webSocket: WebSocket, response: Response) {
            _connectionState.value = ConnectionState.CONNECTED
        }

        override fun onMessage(webSocket: WebSocket, text: String) {
            try {
                val message = gson.fromJson(text, WebSocketMessage::class.java)
                CoroutineScope(ioDispatcher).launch {
                    _messages.emit(message)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
            _connectionState.value = ConnectionState.DISCONNECTED
        }

        override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
            _connectionState.value = ConnectionState.ERROR
        }
    }

    fun connect(roomId: String, userId: String) {
        disconnect()
        // Convert http://10.0.2.2:8080/api/v1/ to ws://10.0.2.2:8080/ws
        val wsBaseUrl = baseUrl.replace("http", "ws").replace("/api/v1/", "/ws")
        val url = "$wsBaseUrl?roomId=$roomId&userId=$userId"
        val request = Request.Builder().url(url).build()
        webSocket = client.newWebSocket(request, listener)
        _connectionState.value = ConnectionState.CONNECTING
    }

    fun disconnect() {
        webSocket?.close(1000, "Normal closure")
        webSocket = null
        _connectionState.value = ConnectionState.DISCONNECTED
    }
}
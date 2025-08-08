// In player/MediaControllerManager.kt
package com.rahul.auricmusic.player

import android.content.ComponentName
import android.content.Context
import androidx.core.content.ContextCompat
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.ListenableFuture
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object MediaControllerManager {

    private val _mediaController = MutableStateFlow<MediaController?>(null)
    val mediaController: StateFlow<MediaController?> = _mediaController.asStateFlow()

    private var controllerFuture: ListenableFuture<MediaController>? = null

    fun connect(context: Context) {
        if (controllerFuture != null) return // Already connecting or connected

        val sessionToken = SessionToken(context, ComponentName(context, AuricMediaService::class.java))
        controllerFuture = MediaController.Builder(context, sessionToken).buildAsync()
        controllerFuture?.addListener(
            {
                _mediaController.value = controllerFuture?.get()
            },
            ContextCompat.getMainExecutor(context)
        )
    }

    fun release() {
        controllerFuture?.let {
            MediaController.releaseFuture(it)
            _mediaController.value = null
            controllerFuture = null
        }
    }
}
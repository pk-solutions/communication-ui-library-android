package com.azure.android.communication.ui.calling.handlers

import android.content.Context
import android.content.DialogInterface
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.view.KeyEvent
import com.azure.android.communication.ui.calling.logger.Logger
import com.azure.android.communication.ui.calling.models.CallCompositeLocalOptions
import com.azure.android.communication.ui.calling.presentation.CallCompositeActivity
import com.azure.android.communication.ui.calling.presentation.fragment.calling.CallingFragment
import com.azure.android.communication.ui.calling.presentation.fragment.setup.SetupFragment
import com.azure.android.communication.ui.calling.presentation.manager.AudioSessionManager
import com.azure.android.communication.ui.calling.presentation.navigation.BackNavigation

internal class CustomKeysHandler(
    private val localOptions: CallCompositeLocalOptions?,
    private val audioSessionManager: AudioSessionManager,
    private val logger: Logger,
) {
    private enum class KeyKind {
        BACK,
        CONFIRM,
        UP,
        DOWN,
        PTT,
        VOLUME_UP,
        VOLUME_DOWN,
    }

    private var lastKey = -1

    fun dispatchKeyEvent(activity: CallCompositeActivity, event: KeyEvent?): Boolean {
        // No custom keys mapped.
        if (localOptions == null) return false

        // Key event lacks a code.
        val keyCode = event?.keyCode
        if (keyCode?.let { it > 0 } != true) return false

        // Get custom kind, or don't handle the code.
        val kind = getKind(keyCode) ?: return false

        if (event.action == KeyEvent.ACTION_DOWN && lastKey != keyCode) {
            lastKey = keyCode // Prevent duplicate events if continuously held down.

            doVibrate(activity, kind)

            // Custom key down behaviors
            when (kind) {
                KeyKind.PTT -> {
                    val fragment = activity.supportFragmentManager.fragments.firstOrNull()
                    (fragment as? CallingFragment)?.clickMicOn()
                }
                else -> {}
            }

        } else if (event.action == KeyEvent.ACTION_UP) {
            lastKey = -1

            // Custom key up behaviors
            val fragment = activity.supportFragmentManager.fragments.firstOrNull()
            when (kind) {
                KeyKind.BACK -> (fragment as? BackNavigation)?.onBackPressed()
                KeyKind.CONFIRM -> {
                    if (fragment is CallingFragment)
                        fragment.switchLocalCamera()
                    else if (fragment is SetupFragment)
                        fragment.attemptJoinCall()
                }
                KeyKind.UP, KeyKind.DOWN -> logger.debug("UP and DOWN not implemented") // TODO
                KeyKind.PTT -> (fragment as? CallingFragment)?.clickMicOff()
                KeyKind.VOLUME_UP, KeyKind.VOLUME_DOWN -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        localOptions.volumeChanger?.let {
                            audioSessionManager.adjustVolume(it, kind == KeyKind.VOLUME_UP)
                        }
                    }
                }
            }
        }

        return true
    }

    fun onDialogKey(dialog: DialogInterface, keyCode: Int, event: KeyEvent): Boolean {
        val kind = getKind(keyCode) ?: return false

        if (event.action == KeyEvent.ACTION_UP) {
            when (kind) {
                KeyKind.BACK -> dialog.dismiss()
                else -> {}
            }
        }

        return true
    }

    private fun getKind(keyCode: Int): KeyKind? {
        if (localOptions == null) return null

        return when {
            localOptions.backKeys?.contains(keyCode) == true -> KeyKind.BACK
            localOptions.confirmKeys?.contains(keyCode) == true -> KeyKind.CONFIRM
            localOptions.upKeys?.contains(keyCode) == true -> KeyKind.UP
            localOptions.downKeys?.contains(keyCode) == true -> KeyKind.DOWN
            localOptions.pttKeys?.contains(keyCode) == true -> KeyKind.PTT
            localOptions.volumeUpKeys?.contains(keyCode) == true -> KeyKind.VOLUME_UP
            localOptions.volumeDownKeys?.contains(keyCode) == true -> KeyKind.VOLUME_DOWN
            else -> null
        }
    }

    private fun doVibrate(context: Context, kind: KeyKind) {
        if (shouldVibrate(kind)) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val v = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
                v.defaultVibrator.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE))
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                @Suppress("DEPRECATION")
                val v = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                v.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                val v = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                @Suppress("DEPRECATION")
                v.vibrate(100)
            }
        }
    }

    private fun shouldVibrate(kind: KeyKind): Boolean =
        kind in listOf(KeyKind.BACK, KeyKind.CONFIRM, KeyKind.UP, KeyKind.DOWN, KeyKind.PTT)
}

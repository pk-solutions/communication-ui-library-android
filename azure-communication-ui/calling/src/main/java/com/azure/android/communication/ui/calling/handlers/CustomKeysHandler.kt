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

    private var lastKey = -1
    fun dispatchKeyEvent(activity: CallCompositeActivity, event: KeyEvent?): Boolean? {
        if (localOptions == null) return null

        val keyCode = event?.keyCode
        if (keyCode?.let { it > 0 } != true) return null

        val isBackKey = localOptions.backKeys?.contains(keyCode) == true
        val isConfirmKey = localOptions.confirmKeys?.contains(keyCode) == true
        val isUpKey = localOptions.upKeys?.contains(keyCode) == true
        val isDownKey = localOptions.downKeys?.contains(keyCode) == true
        val isPttKey = localOptions.pttKeys?.contains(keyCode) == true
        val isVolumeUpKey = localOptions.volumeUpKeys?.contains(keyCode) == true
        val isVolumeDownKey = localOptions.volumeDownKeys?.contains(keyCode) == true

        if (event.action == KeyEvent.ACTION_DOWN && lastKey != keyCode) {
            lastKey = keyCode // Prevent duplicate vibrates if continuously held down.

            // Vibrate on key down for some custom keys
            if (isBackKey || isConfirmKey || isUpKey || isDownKey || isPttKey) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    val v = activity.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
                    v.defaultVibrator.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE))
                } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    @Suppress("DEPRECATION")
                    val v = activity.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                    v.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE))
                } else {
                    @Suppress("DEPRECATION")
                    val v = activity.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                    @Suppress("DEPRECATION")
                    v.vibrate(100)
                }
            }

            // Custom key down behavior
            if (isPttKey) {
                val fragment = activity.supportFragmentManager.fragments.firstOrNull()
                (fragment as? CallingFragment)?.clickMicOn()
            }

            return true

        } else if (event.action == KeyEvent.ACTION_UP) {
            lastKey = -1

            // Custom key up behavior
            val fragment = activity.supportFragmentManager.fragments.firstOrNull()
            if (isBackKey) {
                (fragment as? BackNavigation)?.onBackPressed()
            } else if (isConfirmKey) {
                if (fragment is CallingFragment)
                    fragment.switchLocalCamera()
                else if (fragment is SetupFragment)
                    fragment.attemptJoinCall()
            } else if (isUpKey || isDownKey) {
                // TODO
            } else if (isPttKey) {
                (fragment as? CallingFragment)?.clickMicOff()
            } else if (isVolumeUpKey || isVolumeDownKey) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    localOptions.volumeChanger?.let {
                        audioSessionManager.adjustVolume(it, isVolumeUpKey)
                    }
                }
            }

            return true
        }

        return null
    }

    fun onDialogKey(dialog: DialogInterface, keyCode: Int, event: KeyEvent): Boolean {
        logger.info("Dialog key $keyCode was pressed from ${dialog.hashCode()} with event ${event.action}!")
        return true
    }
}

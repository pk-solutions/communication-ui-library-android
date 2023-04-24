package com.azure.android.communication.ui.calling.presentation.fragment.calling.participantmenu

import com.azure.android.communication.ui.calling.presentation.fragment.calling.controlbar.ControlBarViewModel
import com.azure.android.communication.ui.calling.redux.action.Action
import com.azure.android.communication.ui.calling.redux.action.LocalParticipantAction
import com.azure.android.communication.ui.calling.redux.state.CameraState
import com.azure.android.communication.ui.calling.redux.state.PermissionState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

internal class ParticipantMenuViewModel(private val dispatch: (Action) -> Unit) {

    private val displayStateFlow = MutableStateFlow(false)
    private lateinit var cameraStateFlow: MutableStateFlow<ControlBarViewModel.CameraModel>
    private lateinit var xlBottomDrawerStateFlow: MutableStateFlow<Boolean>

    fun init(
        permissionState: PermissionState,
        cameraState: CameraState,
        isXlBottomDrawer: Boolean,
    ) {
        cameraStateFlow = MutableStateFlow(
            ControlBarViewModel.CameraModel(
                permissionState.cameraPermissionState,
                cameraState
            )
        )

        xlBottomDrawerStateFlow = MutableStateFlow(isXlBottomDrawer)
    }

    fun update(
        permissionState: PermissionState,
        cameraState: CameraState,
    ) {
        cameraStateFlow.value = ControlBarViewModel.CameraModel(permissionState.cameraPermissionState, cameraState)
    }

    fun getDisplayStateFlow(): StateFlow<Boolean> = displayStateFlow
    fun getCameraStateFlow(): StateFlow<ControlBarViewModel.CameraModel> = cameraStateFlow
    fun getXlBottomDrawerStateFlow(): StateFlow<Boolean> = xlBottomDrawerStateFlow

    fun display() {
        displayStateFlow.value = true
    }

    fun close() {
        displayStateFlow.value = false
    }

    fun turnCameraOn() {
        dispatchAction(action = LocalParticipantAction.CameraOnRequested())
    }

    fun turnCameraOff() {
        dispatchAction(action = LocalParticipantAction.CameraOffTriggered())
    }

    private fun dispatchAction(action: Action) {
        dispatch(action)
    }
}

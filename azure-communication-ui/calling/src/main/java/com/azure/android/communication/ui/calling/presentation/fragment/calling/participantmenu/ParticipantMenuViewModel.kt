package com.azure.android.communication.ui.calling.presentation.fragment.calling.participantmenu

import com.azure.android.communication.ui.calling.presentation.fragment.calling.controlbar.ControlBarViewModel
import com.azure.android.communication.ui.calling.redux.action.Action
import com.azure.android.communication.ui.calling.redux.action.LocalParticipantAction
import com.azure.android.communication.ui.calling.redux.state.CameraState
import com.azure.android.communication.ui.calling.redux.state.PermissionState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

internal class ParticipantMenuViewModel(private val dispatch: (Action) -> Unit) {

    private val displayStateFlow = MutableStateFlow(DisplayParticipantMenuModel(false, null))
    private lateinit var cameraStateFlow: MutableStateFlow<ControlBarViewModel.CameraModel>

    fun init(
        permissionState: PermissionState,
        cameraState: CameraState,
    ) {
        cameraStateFlow = MutableStateFlow(
            ControlBarViewModel.CameraModel(
                permissionState.cameraPermissionState,
                cameraState
            )
        )
    }

    fun update(
        permissionState: PermissionState,
        cameraState: CameraState,
    ) {
        cameraStateFlow.value = ControlBarViewModel.CameraModel(permissionState.cameraPermissionState, cameraState)
    }

    fun getDisplayStateFlow(): StateFlow<DisplayParticipantMenuModel> = displayStateFlow
    fun getCameraStateFlow(): StateFlow<ControlBarViewModel.CameraModel> = cameraStateFlow

    /**
     * Displays the menu for the local participant.
     */
    fun displayLocal() {
        displayStateFlow.value = DisplayParticipantMenuModel(true, null)
    }

    /**
     * Displays the menu for a remote participant
     *
     * @param id The remote participant's id, see [com.azure.android.communication.ui.calling.service.ParticipantIdentifierHelper]
     */
    fun displayRemote(id: String) {
        displayStateFlow.value = DisplayParticipantMenuModel(true, id)
    }

    fun close() {
        displayStateFlow.value = DisplayParticipantMenuModel(false, null)
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

    internal data class DisplayParticipantMenuModel(
        val show: Boolean,
        val remoteParticipantId: String?,
    )
}

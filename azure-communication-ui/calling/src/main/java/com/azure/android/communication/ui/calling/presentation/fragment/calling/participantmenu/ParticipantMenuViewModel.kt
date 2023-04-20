package com.azure.android.communication.ui.calling.presentation.fragment.calling.participantmenu

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

internal class ParticipantMenuViewModel {

    private val displayStateFlow = MutableStateFlow(false)

    fun getDisplayStateFlow(): StateFlow<Boolean> = displayStateFlow

    fun display() {
        displayStateFlow.value = true
    }

    fun close() {
        displayStateFlow.value = false
    }

}
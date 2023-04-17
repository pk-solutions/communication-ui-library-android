// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.configuration.events

import com.azure.android.communication.ui.calling.CallCompositeEventHandler
import com.azure.android.communication.ui.calling.models.CallCompositeErrorEvent
import com.azure.android.communication.ui.calling.models.CallCompositeExitEvent
import com.azure.android.communication.ui.calling.models.CallCompositeRemoteParticipantJoinedEvent

internal class CallCompositeEventsHandler {
    private val errorHandlers = mutableSetOf<CallCompositeEventHandler<CallCompositeErrorEvent>>()
    private val remoteParticipantJoinedHandlers =
        mutableSetOf<CallCompositeEventHandler<CallCompositeRemoteParticipantJoinedEvent>>()
    private val onExitHandlers = mutableSetOf<CallCompositeEventHandler<CallCompositeExitEvent>>()

    fun getOnErrorHandlers() = errorHandlers.asIterable()

    fun addOnErrorEventHandler(errorHandler: CallCompositeEventHandler<CallCompositeErrorEvent>) =
        errorHandlers.add(errorHandler)

    fun removeOnErrorEventHandler(errorHandler: CallCompositeEventHandler<CallCompositeErrorEvent>) =
        errorHandlers.remove(errorHandler)

    fun getOnRemoteParticipantJoinedHandlers() = remoteParticipantJoinedHandlers.asIterable()

    fun addOnRemoteParticipantJoinedEventHandler(handler: CallCompositeEventHandler<CallCompositeRemoteParticipantJoinedEvent>) =
        remoteParticipantJoinedHandlers.add(handler)

    fun removeOnRemoteParticipantJoinedEventHandler(handler: CallCompositeEventHandler<CallCompositeRemoteParticipantJoinedEvent>) =
        remoteParticipantJoinedHandlers.remove(handler)

    fun getOnExitEventHandlers() = onExitHandlers.asIterable()

    fun addOnExitEventHandler(handler: CallCompositeEventHandler<CallCompositeExitEvent>) =
        onExitHandlers.add(handler)

    fun removeOnExitEventHandler(handler: CallCompositeEventHandler<CallCompositeExitEvent>) =
        onExitHandlers.remove(handler)
}

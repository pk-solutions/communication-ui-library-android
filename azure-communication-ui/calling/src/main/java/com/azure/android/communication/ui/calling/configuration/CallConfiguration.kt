// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.configuration

import com.azure.android.communication.ui.calling.models.CallCompositeCallFactory

/*
internal enum class CallType {
    GROUP_CALL,
    TEAMS_MEETING,
}
 */

internal data class CallConfiguration(
    val callFactory: CallCompositeCallFactory,
    val displayName: String,
) {
}

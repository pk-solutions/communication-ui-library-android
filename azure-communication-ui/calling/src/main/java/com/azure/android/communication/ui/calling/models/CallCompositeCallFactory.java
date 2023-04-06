// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.models;

import android.content.Context;

import com.azure.android.communication.calling.AudioOptions;
import com.azure.android.communication.calling.Call;
import com.azure.android.communication.calling.DeviceManager;
import com.azure.android.communication.calling.VideoOptions;

import java9.util.concurrent.CompletableFuture;

/**
 * Factory for providing exactly what the call composite needs in the CallingSDKWrapper.
 */
public interface CallCompositeCallFactory {
    /**
     * Accepts, joins, or starts a call for the call composite.
     * This method is only called once.
     * @return The active call.
     */
    CompletableFuture<Call> getCall(VideoOptions videoOptions, AudioOptions audioOptions);

    CompletableFuture<DeviceManager> getDeviceManager(Context context);
}

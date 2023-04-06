// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.models;

import com.azure.android.communication.ui.calling.CallComposite;

/**
 * CallCompositeRemoteOptions for CallComposite.launch.
 *
 * @see CallComposite
 */
public final class CallCompositeRemoteOptions {
    private final CallCompositeCallFactory callFactory;
    private final String displayName;

    public CallCompositeRemoteOptions(final CallCompositeCallFactory callFactory, final String displayName) {
        this.callFactory = callFactory;
        this.displayName = displayName;
    }

    public CallCompositeCallFactory getCallFactory() {
        return callFactory;
    }

    public String getDisplayName() {
        return displayName;
    }
}

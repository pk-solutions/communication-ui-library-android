// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.utilities

import android.graphics.drawable.Drawable
import android.view.View
import com.azure.android.communication.ui.calling.models.CallCompositeParticipantViewData

internal enum class BottomCellItemType {
    BottomMenuAction,
    BottomMenuTitle
}

internal data class BottomCellItem(
    var icon: Drawable?, // The always-present icon for the item.
    var title: String?,
    var contentDescription: String?,
    var accessoryImage: Drawable?, // The image shown if enabled is true.
    var accessoryColor: Int?,
    var accessoryImageDescription: String?,
    var enabled: Boolean?, // NOTE: this only determines if the accessory image is VISIBLE and NOT if the item is clickable. e.g. for a list of audio devices, this shows the checkmark for the selected device.
    var participantViewData: CallCompositeParticipantViewData?, // If icon is null, then the avatar bitmap is used instead.
    var isOnHold: Boolean, // If true, "on hold" is displayed for this item (e.g. in a participant list, if this particular user is on hold).
    val itemType: BottomCellItemType = BottomCellItemType.BottomMenuAction,
    var onClickAction: ((View) -> Unit)?, // The behavior when clicked. If null, then the item is NOT clickable.
)

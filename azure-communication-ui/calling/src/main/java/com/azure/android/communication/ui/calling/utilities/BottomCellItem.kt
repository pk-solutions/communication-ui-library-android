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
    /**
     * The always-present icon for the item.
     */
    var icon: Drawable?,
    /**
     * The display title. Should be unique.
     */
    var title: String?,
    /**
     * The accessibility description for this item.
     */
    var contentDescription: String?,

    /**
     * An image shown if [accessoryVisible] is true.
     */
    var accessoryImage: Drawable?,
    /**
     * The accessory image's color.
     */
    var accessoryColor: Int?,
    /**
     * The accessibility description for the accessory image.
     */
    var accessoryImageDescription: String?,
    /**
     * If true, then the accessibility image will be shown.
     * e.g. a checkbox in a list of audio devices.
     */
    var accessoryVisible: Boolean?,

    /**
     * If [icon] is null, then the avatar bitmap of this property is displayed instead.
     */
    var participantViewData: CallCompositeParticipantViewData?,
    /**
     * If true, "On Hold" is displayed for this item.
     * e.g. in a participant list, if this particular user is on hold.
     */
    var isOnHold: Boolean,

    /**
     * The item type.
     */
    val itemType: BottomCellItemType = BottomCellItemType.BottomMenuAction,
    /**
     * The behavior when clicked.
     * If null, then then this item is NOT clickable.
     */
    var onClickAction: ((View) -> Unit)?,
)

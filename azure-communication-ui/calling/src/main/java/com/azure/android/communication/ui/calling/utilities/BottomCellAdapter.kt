// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.utilities

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.azure.android.communication.ui.R

internal class BottomCellAdapter(
    private val isXlTiles: Boolean = false,
) : RecyclerView.Adapter<BottomCellViewHolder>() {
    private var bottomCellItems: List<BottomCellItem> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BottomCellViewHolder {
        val bottomCellType = BottomCellItemType.values()[viewType]
        val inflater = LayoutInflater.from(parent.context)
        return when (bottomCellType) {
            BottomCellItemType.BottomMenuAction -> {
                val view = if (isXlTiles)
                    inflater.inflate(R.layout.azure_communication_ui_calling_bottom_drawer_xl_tile, parent, false)
                else
                    inflater.inflate(R.layout.azure_communication_ui_calling_bottom_drawer_cell, parent, false)
                if (isAndroidTV(parent.context) || isXlTiles) {
                    view.setOnFocusChangeListener { v, hasFocus ->
                        if (hasFocus) {
                            v.setBackgroundColor(v.context.resources.getColor(if (isXlTiles) R.color.azure_communication_ui_calling_color_primary else R.color.azure_communication_ui_calling_color_focus_tint))
                        } else {
                            v.setBackgroundColor(if (isXlTiles) v.context.resources.getColor(R.color.azure_communication_ui_calling_color_surface) else Color.TRANSPARENT)
                        }
                    }
                }
                BottomCellActionViewHolder(view)
            }
            BottomCellItemType.BottomMenuTitle -> {
                val view = inflater.inflate(R.layout.azure_communication_ui_calling_bottom_drawer_title_cell, parent, false)
                BottomCellViewHolder(view)
            }
        }
    }

    override fun onBindViewHolder(holder: BottomCellViewHolder, position: Int) {
        val item: BottomCellItem = bottomCellItems[position]
        holder.setCellData(item)
    }

    override fun getItemCount() = bottomCellItems.size

    fun setBottomCellItems(bottomCellItems: List<BottomCellItem>) {
        this.bottomCellItems = bottomCellItems
    }

    fun enableBottomCellItem(bottomCellItemName: String) {
        for (bottomCellItem in bottomCellItems) {
            bottomCellItem.enabled = bottomCellItem.title == bottomCellItemName
        }
        super.notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int) = bottomCellItems[position].itemType.ordinal
}

package com.azure.android.communication.ui.calling.presentation.fragment.calling.participantmenu

import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.widget.RelativeLayout
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.azure.android.communication.ui.R
import com.azure.android.communication.ui.calling.utilities.BottomCellAdapter
import com.azure.android.communication.ui.calling.utilities.BottomCellItem
import com.microsoft.fluentui.drawer.DrawerDialog
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@SuppressLint("ViewConstructor")
internal class ParticipantMenuView(
    context: Context,
    private val viewModel: ParticipantMenuViewModel,
) : RelativeLayout(context) {

    private var participantMenuTable: RecyclerView
    private lateinit var participantMenuDrawer: DrawerDialog
    private lateinit var bottomCellAdapter: BottomCellAdapter

    init {
        inflate(context, R.layout.azure_communication_ui_calling_listview, this)
        participantMenuTable = findViewById(R.id.bottom_drawer_table)
        this.setBackgroundResource(R.color.azure_communication_ui_calling_color_bottom_drawer_background)
    }

    fun start(
        viewLifecycleOwner: LifecycleOwner,
        onKeyListener: DialogInterface.OnKeyListener,
    ) {
        initializeDrawer(onKeyListener)
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.getDisplayStateFlow().collect {
                if (it) {
                    participantMenuDrawer.show()
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.getCameraStateFlow().collect {
                bottomCellAdapter.enableBottomCellItem(context.getString(R.string.azure_communication_ui_calling_view_button_toggle_video_accessibility_label))
            }
        }
    }

    fun stop() {
        bottomCellAdapter.setBottomCellItems(mutableListOf())
        participantMenuTable.layoutManager = null
        participantMenuDrawer.dismiss()
        participantMenuDrawer.dismissDialog()
        this.removeAllViews()
    }

    private fun initializeDrawer(onKeyListener: DialogInterface.OnKeyListener) {
        participantMenuDrawer = DrawerDialog(context, DrawerDialog.BehaviorType.BOTTOM)
        participantMenuDrawer.setContentView(this)
        participantMenuDrawer.setOnDismissListener {
            viewModel.close()
        }
        participantMenuDrawer.setOnKeyListener(onKeyListener)

        bottomCellAdapter = BottomCellAdapter()
        bottomCellAdapter.setBottomCellItems(bottomCellItems)
        participantMenuTable.adapter = bottomCellAdapter
        participantMenuTable.layoutManager = LinearLayoutManager(context)
    }

    private val bottomCellItems: List<BottomCellItem>
        get() {
            val bottomCellItems = listOf(
                BottomCellItem(
                    icon = ContextCompat.getDrawable(
                        context,
                        R.drawable.azure_communication_ui_calling_toggle_selector_camera_for_call
                    ),
                    title = context.getString(R.string.azure_communication_ui_calling_view_button_toggle_video_accessibility_label),
                    contentDescription = null,
                    accessoryImage = null,
                    accessoryColor = null,
                    accessoryImageDescription = context.getString(R.string.azure_communication_ui_calling_view_button_toggle_video_accessibility_label),
                    enabled = false,
                    participantViewData = null,
                    isOnHold = false,
                ) {
                    participantMenuDrawer.dismiss()
                    if (it.isEnabled) {
                        viewModel.turnCameraOff()
                    } else {
                        viewModel.turnCameraOn()
                    }
                },
            )
            // TODO: need to rework the drawer to optionally fill the whole screen. see TODO in CustomKeysHandler.
            // TODO: need to either split this into Local and non-local menus, or use a toggle
            // TODO: need to add remove-from-meeting button when editing a remote user
            // TODO: spotlight user? beta feature.
            // TODO: consider reworking from "toggle video" to dynamic text like ControlBarView

            return bottomCellItems
        }

}

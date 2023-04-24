package com.azure.android.communication.ui.calling.presentation.fragment.calling.participantmenu

import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.graphics.drawable.StateListDrawable
import android.widget.RelativeLayout
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.azure.android.communication.ui.R
import com.azure.android.communication.ui.calling.presentation.fragment.calling.controlbar.ControlBarViewModel
import com.azure.android.communication.ui.calling.redux.state.CameraOperationalStatus
import com.azure.android.communication.ui.calling.redux.state.PermissionStatus
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
        // TODO: need to rework this inflate to optionally fill the whole screen. see TODO in CustomKeysHandler.
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
                updateCamera(it)
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
        updateCamera(viewModel.getCameraStateFlow().value)
        participantMenuTable.adapter = bottomCellAdapter
        participantMenuTable.layoutManager = LinearLayoutManager(context)
    }

    // TODO: need to either split this into Local and non-local menus, or use a toggle
    private fun createCameraToggleCellItem(isCameraOn: Boolean, isClickable: Boolean): BottomCellItem {
        val title = if (isCameraOn)
            context.getString(R.string.azure_communication_ui_calling_setup_view_button_video_off)
        else
            context.getString(R.string.azure_communication_ui_calling_setup_view_button_video_on)

        val icon = ContextCompat.getDrawable(
            context,
            R.drawable.azure_communication_ui_calling_toggle_selector_camera_for_call,
        )
        // TODO: this doesn't work
        val iconStates = mutableListOf<Int>()
        iconStates.add((if (isCameraOn) -1 else 1) * android.R.attr.state_selected)
        iconStates.add((if (isClickable) 1 else -1) * android.R.attr.state_enabled)
        icon?.state = iconStates.toIntArray()

        val item = BottomCellItem(
            icon = icon,
            title = title,
            contentDescription = null,
            accessoryImage = null,
            accessoryColor = null,
            accessoryImageDescription = title,
            enabled = false,
            participantViewData = null,
            isOnHold = false,
            onClickAction = null,
        )

        if (isClickable) {
            item.onClickAction = {
                participantMenuDrawer.dismiss()
                if (isCameraOn) {
                    viewModel.turnCameraOff()
                } else {
                    viewModel.turnCameraOn()
                }
            }
        }

        return item
    }

    private fun updateCamera(cameraState: ControlBarViewModel.CameraModel) {
        val shouldBeEnabled = (cameraState.cameraPermissionState != PermissionStatus.DENIED)

        val newCameraToggle = when (cameraState.cameraState.operation) {
            CameraOperationalStatus.ON -> {
                createCameraToggleCellItem(true, shouldBeEnabled)
            }
            CameraOperationalStatus.OFF -> {
                createCameraToggleCellItem(false, shouldBeEnabled)
            }
            else -> {
                // disable button
                createCameraToggleCellItem(isCameraOn = false, isClickable = false)
            }
        }

        // TODO: for now we're just replacing the entire list of items. need to centralize when more buttons are added.
        // rebind the list of items
        bottomCellAdapter = BottomCellAdapter()
        bottomCellAdapter.setBottomCellItems(listOf(
            newCameraToggle,
            // TODO: spotlight user? beta feature.
            // TODO: need to add remove-from-meeting button when editing a remote user
        ))
        participantMenuTable.adapter = bottomCellAdapter
    }

}

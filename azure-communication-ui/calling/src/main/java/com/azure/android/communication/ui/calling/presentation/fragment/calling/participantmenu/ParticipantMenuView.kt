package com.azure.android.communication.ui.calling.presentation.fragment.calling.participantmenu

import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.widget.RelativeLayout
import androidx.core.content.ContextCompat
import androidx.core.view.updateLayoutParams
import androidx.core.view.updatePadding
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
import com.google.android.flexbox.AlignItems
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.microsoft.fluentui.drawer.DrawerDialog
import com.microsoft.fluentui.util.activity
import com.microsoft.fluentui.util.displaySize
import com.microsoft.fluentui.util.statusBarHeight
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
    private var isXlBottomDrawer = false

    init {
        inflate(context, R.layout.azure_communication_ui_calling_listview, this)
        participantMenuTable = findViewById(R.id.bottom_drawer_table)
        this.setBackgroundResource(R.color.azure_communication_ui_calling_color_bottom_drawer_background)
    }

    fun start(
        viewLifecycleOwner: LifecycleOwner,
        onKeyListener: DialogInterface.OnKeyListener,
        isXlBottomDrawer: Boolean,
    ) {
        this.isXlBottomDrawer = isXlBottomDrawer

        initializeDrawer(onKeyListener)

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.getDisplayStateFlow().collect {
                if (it.show) {
                    updateItems(it.remoteParticipantId, viewModel.getCameraStateFlow().value)
                    participantMenuDrawer.show()
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.getCameraStateFlow().collect {
                updateItems(viewModel.getDisplayStateFlow().value.remoteParticipantId, it)
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

    private fun initializeDrawer(
        onKeyListener: DialogInterface.OnKeyListener,
    ) {
        val dimValue = if (isXlBottomDrawer) 0f else 0.5f

        participantMenuDrawer = DrawerDialog(context, DrawerDialog.BehaviorType.BOTTOM, dimValue)
        participantMenuDrawer.setContentView(this)
        participantMenuDrawer.setOnDismissListener {
            viewModel.close()
        }
        participantMenuDrawer.setOnKeyListener(onKeyListener)

        bottomCellAdapter = BottomCellAdapter(isXlBottomDrawer)
        bottomCellAdapter.setBottomCellItems(emptyList())
        participantMenuTable.adapter = bottomCellAdapter

        if (isXlBottomDrawer) {
            participantMenuTable.layoutManager = FlexboxLayoutManager(context).apply {
                flexDirection = FlexDirection.ROW
                flexWrap = FlexWrap.WRAP
                justifyContent = JustifyContent.SPACE_EVENLY
                alignItems = AlignItems.CENTER
            }
            participantMenuTable.updatePadding(top = 30)
            participantMenuTable.updateLayoutParams {
                val displayHeight = context.displaySize.y
                var topMargin = context.statusBarHeight

                val actionBar = context.activity?.supportActionBar
                if (actionBar != null)
                    topMargin += actionBar.height

                height = displayHeight - topMargin
            }
        } else {
            participantMenuTable.layoutManager = LinearLayoutManager(context)
        }
    }

    private fun updateItems(remoteParticipantId: String?, cameraState: ControlBarViewModel.CameraModel) {
        bottomCellAdapter = BottomCellAdapter(isXlBottomDrawer)

        val items = mutableListOf<BottomCellItem>()
        if (remoteParticipantId == null)
            items.add(getCameraToggle(cameraState))

        // TODO: spotlight user? seems to be a beta/JS-only feature right now.
        // TODO: remove-from-meeting button

        bottomCellAdapter.setBottomCellItems(items)
        participantMenuTable.adapter = bottomCellAdapter
    }

    private fun getCameraToggle(cameraState: ControlBarViewModel.CameraModel): BottomCellItem {
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

        return newCameraToggle
    }

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
            accessoryVisible = false,
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

}

// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.presentation.fragment.common.audiodevicelist

import android.content.Context
import android.content.DialogInterface
import android.widget.RelativeLayout
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.azure.android.communication.ui.R
import com.azure.android.communication.ui.calling.redux.state.AudioDeviceSelectionStatus
import com.azure.android.communication.ui.calling.redux.state.AudioState
import com.azure.android.communication.ui.calling.utilities.BottomCellAdapter
import com.azure.android.communication.ui.calling.utilities.BottomCellItem
import com.azure.android.communication.ui.calling.utilities.isAndroidTV
import com.microsoft.fluentui.drawer.DrawerDialog
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

internal class AudioDeviceListView(
    private val viewModel: AudioDeviceListViewModel,
    context: Context,
) : RelativeLayout(context) {

    private var deviceTable: RecyclerView
    private lateinit var audioDeviceDrawer: DrawerDialog
    private lateinit var bottomCellAdapter: BottomCellAdapter

    init {
        inflate(context, R.layout.azure_communication_ui_calling_listview, this)
        deviceTable = findViewById(R.id.bottom_drawer_table)
        this.setBackgroundResource(R.color.azure_communication_ui_calling_color_bottom_drawer_background)
    }

    fun start(
        viewLifecycleOwner: LifecycleOwner,
        onKeyListener: DialogInterface.OnKeyListener,
    ) {
        initializeAudioDeviceDrawer(onKeyListener)
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.audioStateFlow.collect {
                updateSelectedAudioDevice(it)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.displayAudioDeviceSelectionMenuStateFlow.collect {
                if (it) {
                    showAudioDeviceSelectionMenu()
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.audioStateFlow.collect {
                // / rebind the list of items
                bottomCellAdapter = BottomCellAdapter()
                bottomCellAdapter.setBottomCellItems(bottomCellItems)
                deviceTable.adapter = bottomCellAdapter
            }
        }
    }

    fun stop() {
        bottomCellAdapter.setBottomCellItems(mutableListOf())
        deviceTable.layoutManager = null
        audioDeviceDrawer.dismiss()
        audioDeviceDrawer.dismissDialog()
        this.removeAllViews()
    }

    private fun showAudioDeviceSelectionMenu() {
        audioDeviceDrawer.show()
    }

    private fun initializeAudioDeviceDrawer(onKeyListener: DialogInterface.OnKeyListener) {
        audioDeviceDrawer = DrawerDialog(context, DrawerDialog.BehaviorType.BOTTOM)
        audioDeviceDrawer.setContentView(this)
        audioDeviceDrawer.setOnDismissListener {
            viewModel.closeAudioDeviceSelectionMenu()
        }
        audioDeviceDrawer.setOnKeyListener(onKeyListener)

        bottomCellAdapter = BottomCellAdapter()
        bottomCellAdapter.setBottomCellItems(bottomCellItems)
        deviceTable.adapter = bottomCellAdapter
        deviceTable.layoutManager = LinearLayoutManager(context)
    }

    private val bottomCellItems: List<BottomCellItem>
        get() {
            val initialDevice = viewModel.audioStateFlow.value.device
            val bottomCellItems = mutableListOf<BottomCellItem>()

            if (!isAndroidTV(context)) {
                // Receiver (default)
                bottomCellItems.add(
                    BottomCellItem(
                        ContextCompat.getDrawable(
                            context,
                            R.drawable.azure_communication_ui_calling_ic_fluent_speaker_2_24_regular_composite_button_filled
                        ),
                        when (viewModel.audioStateFlow.value.isHeadphonePlugged) {
                            true -> context.getString(R.string.azure_communication_ui_calling_audio_device_drawer_headphone)
                            false -> context.getString(R.string.azure_communication_ui_calling_audio_device_drawer_android)
                        },
                        null,
                        ContextCompat.getDrawable(
                            context,
                            R.drawable.ms_ic_checkmark_24_filled
                        ),
                        null,
                        context.getString(R.string.azure_communication_ui_calling_setup_view_audio_device_selected_accessibility_label),
                        accessoryVisible = initialDevice == AudioDeviceSelectionStatus.RECEIVER_SELECTED,
                        null,
                        false,
                    ) {
                        viewModel.switchAudioDevice(AudioDeviceSelectionStatus.RECEIVER_REQUESTED)
                        audioDeviceDrawer.dismiss()
                    }
                )
            }

            bottomCellItems.add(
                BottomCellItem(
                    ContextCompat.getDrawable(
                        context,
                        R.drawable.azure_communication_ui_calling_ic_fluent_speaker_2_24_filled_composite_button_enabled
                    ),
                    context.getString(R.string.azure_communication_ui_calling_audio_device_drawer_speaker),
                    null,
                    ContextCompat.getDrawable(
                        context,
                        R.drawable.ms_ic_checkmark_24_filled
                    ),
                    null,
                    context.getString(R.string.azure_communication_ui_calling_setup_view_audio_device_selected_accessibility_label),
                    accessoryVisible = initialDevice == AudioDeviceSelectionStatus.SPEAKER_SELECTED,
                    null,
                    false,
                ) {
                    viewModel.switchAudioDevice(AudioDeviceSelectionStatus.SPEAKER_REQUESTED)
                    audioDeviceDrawer.dismiss()
                }
            )

            if (viewModel.audioStateFlow.value.bluetoothState.available) {
                // Remove the first item (Receiver)
                bottomCellItems.removeAt(0)
                bottomCellItems.add(
                    BottomCellItem(
                        ContextCompat.getDrawable(
                            context,
                            R.drawable.azure_communication_ui_calling_ic_fluent_speaker_bluetooth_24_regular
                        ),
                        viewModel.audioStateFlow.value.bluetoothState.deviceName,
                        null,
                        ContextCompat.getDrawable(
                            context,
                            R.drawable.ms_ic_checkmark_24_filled
                        ),

                        null,
                        context.getString(R.string.azure_communication_ui_calling_setup_view_audio_device_selected_accessibility_label),
                        accessoryVisible = initialDevice == AudioDeviceSelectionStatus.BLUETOOTH_SCO_SELECTED,
                        null,
                        false,
                    ) {
                        viewModel.switchAudioDevice(AudioDeviceSelectionStatus.BLUETOOTH_SCO_REQUESTED)
                        audioDeviceDrawer.dismiss()
                    }
                )
            }
            return bottomCellItems
        }

    private fun updateSelectedAudioDevice(audioState: AudioState) {
        if (this::bottomCellAdapter.isInitialized) {
            bottomCellAdapter.enableBottomCellItem(getDeviceTypeName(audioState))
            announceForAccessibility(
                context.getString(
                    R.string.azure_communication_ui_calling_selected_audio_device_announcement,
                    getDeviceTypeName(audioState)
                )
            )
        }
    }

    private fun getDeviceTypeName(audioState: AudioState): String {
        return when (audioState.device) {
            AudioDeviceSelectionStatus.RECEIVER_REQUESTED, AudioDeviceSelectionStatus.RECEIVER_SELECTED ->
                context.getString(R.string.azure_communication_ui_calling_audio_device_drawer_android)
            AudioDeviceSelectionStatus.SPEAKER_REQUESTED, AudioDeviceSelectionStatus.SPEAKER_SELECTED ->
                context.getString(R.string.azure_communication_ui_calling_audio_device_drawer_speaker)
            AudioDeviceSelectionStatus.BLUETOOTH_SCO_SELECTED, AudioDeviceSelectionStatus.BLUETOOTH_SCO_REQUESTED ->
                audioState.bluetoothState.deviceName
        }
    }
}

// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.presentation.fragment.setup

import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.TextUtils
import android.text.style.ForegroundColorSpan
import android.util.LayoutDirection
import android.view.View
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.azure.android.communication.ui.R
import com.azure.android.communication.ui.calling.presentation.DependencyInjectionContainerHolder
import com.azure.android.communication.ui.calling.presentation.fragment.common.audiodevicelist.AudioDeviceListView
import com.azure.android.communication.ui.calling.presentation.fragment.setup.components.ErrorInfoView
import com.azure.android.communication.ui.calling.presentation.fragment.setup.components.JoinCallButtonHolderView
import com.azure.android.communication.ui.calling.presentation.fragment.setup.components.PermissionWarningView
import com.azure.android.communication.ui.calling.presentation.fragment.setup.components.PreviewAreaView
import com.azure.android.communication.ui.calling.presentation.fragment.setup.components.SetupControlBarView
import com.azure.android.communication.ui.calling.presentation.fragment.setup.components.SetupGradientView
import com.azure.android.communication.ui.calling.presentation.fragment.setup.components.SetupParticipantAvatarView
import com.azure.android.communication.ui.calling.presentation.navigation.BackNavigation

internal class SetupFragment :
    Fragment(R.layout.azure_communication_ui_calling_fragment_setup), BackNavigation {

    // Get the DI Container, which gives us what we need for this fragment (dependencies)
    private val holder: DependencyInjectionContainerHolder by activityViewModels()

    private lateinit var warningsView: PermissionWarningView
    private lateinit var setupControlsView: SetupControlBarView
    private lateinit var participantAvatarView: SetupParticipantAvatarView
    private lateinit var localParticipantRendererView: PreviewAreaView
    private lateinit var audioDeviceListView: AudioDeviceListView
    private lateinit var setupGradientView: SetupGradientView
    private lateinit var errorInfoView: ErrorInfoView
    private lateinit var setupJoinCallButtonHolderView: JoinCallButtonHolderView

    private val videoViewManager get() = holder.container.videoViewManager
    private val avatarViewManager get() = holder.container.avatarViewManager
    private val networkManager get() = holder.container.networkManager
    private val viewModel get() = holder.setupViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.init(viewLifecycleOwner.lifecycleScope)

        setActionBarTitle()

        setupGradientView = view.findViewById(R.id.azure_communication_ui_setup_gradient)
        setupGradientView.start(viewLifecycleOwner, viewModel.setupGradientViewModel)

        setupJoinCallButtonHolderView =
            view.findViewById(R.id.azure_communication_ui_setup_join_call_holder)
        setupJoinCallButtonHolderView.start(
            viewLifecycleOwner,
            viewModel.joinCallButtonHolderViewModel,
            networkManager
        )

        participantAvatarView = view.findViewById(R.id.azure_communication_ui_setup_default_avatar)
        participantAvatarView.start(
            viewLifecycleOwner,
            viewModel.participantAvatarViewModel,
            avatarViewManager.callCompositeLocalOptions?.participantViewData,
        )

        warningsView = view.findViewById(R.id.azure_communication_ui_setup_permission_info)
        warningsView.start(
            viewLifecycleOwner,
            viewModel.warningsViewModel,
        )

        localParticipantRendererView =
            view.findViewById(R.id.azure_communication_ui_setup_local_video_holder)
        localParticipantRendererView.start(
            viewLifecycleOwner,
            viewModel.localParticipantRendererViewModel,
            videoViewManager,
        )

        audioDeviceListView =
            AudioDeviceListView(viewModel.audioDeviceListViewModel, this.requireContext())
        audioDeviceListView.layoutDirection =
            activity?.window?.decorView?.layoutDirection ?: LayoutDirection.LOCALE
        audioDeviceListView.start(
            viewLifecycleOwner,
            holder.container.customKeysHandler::onDialogKey,
        )

        setupControlsView = view.findViewById(R.id.azure_communication_ui_setup_buttons)
        setupControlsView.start(
            viewLifecycleOwner,
            viewModel.setupControlBarViewModel,
        )

        errorInfoView = ErrorInfoView(view)
        errorInfoView.start(viewLifecycleOwner, viewModel.errorInfoViewModel)

        viewModel.setupCall()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (this::audioDeviceListView.isInitialized) audioDeviceListView.stop()
        if (this::errorInfoView.isInitialized) errorInfoView.stop()
    }

    override fun onBackPressed() {
        viewModel.exitComposite()
    }

    fun attemptJoinCall() {
        setupJoinCallButtonHolderView.attemptJoinCall()
    }

    private val callCompositeActivity
        get() = (activity as AppCompatActivity)

    private fun setActionBarTitle() {
        // TODO: CallingFragment has similar code. should combine.

        val localOptions = holder.container.configuration.callCompositeLocalOptions
        val titleSpan = if (!TextUtils.isEmpty(localOptions?.setupScreenViewData?.title)) {
            SpannableString(localOptions?.setupScreenViewData?.title)
        } else {
            SpannableString(getString(R.string.azure_communication_ui_calling_call_setup_action_bar_title))
        }

        val titleView = callCompositeActivity.findViewById<TextView>(R.id.toolbar_title)
        titleView.text = titleSpan

        // Only set the subtitle if the title has also been set
        if (!TextUtils.isEmpty(localOptions?.setupScreenViewData?.subtitle)) {
            if (!TextUtils.isEmpty(localOptions?.setupScreenViewData?.title)) {
                val subtitleSpan = SpannableString(localOptions?.setupScreenViewData?.subtitle)
                val subtitleView = callCompositeActivity.findViewById<TextView>(R.id.toolbar_subtitle)
                subtitleView.visibility = View.VISIBLE
                subtitleView.text = subtitleSpan
            } else {
                holder.container.logger.error(
                    "Provided setupScreenViewData has subtitle, but no title provided. In this case subtitle is not displayed."
                )
            }
        }
    }
}

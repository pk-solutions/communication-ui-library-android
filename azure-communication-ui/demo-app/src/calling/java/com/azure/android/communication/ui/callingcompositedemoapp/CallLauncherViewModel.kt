// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.callingcompositedemoapp

import android.content.Context
import androidx.lifecycle.ViewModel
import com.azure.android.communication.calling.*
import com.azure.android.communication.common.CommunicationIdentifier
import com.azure.android.communication.common.CommunicationTokenCredential
import com.azure.android.communication.common.CommunicationTokenRefreshOptions
import com.azure.android.communication.ui.calling.CallComposite
import com.azure.android.communication.ui.calling.CallCompositeBuilder
import com.azure.android.communication.ui.calling.models.CallCompositeCallFactory
import com.azure.android.communication.ui.calling.models.CallCompositeCallHistoryRecord
import com.azure.android.communication.ui.calling.models.CallCompositeLocalOptions
import com.azure.android.communication.ui.calling.models.CallCompositeLocalizationOptions
import com.azure.android.communication.ui.calling.models.CallCompositeRemoteOptions
import com.azure.android.communication.ui.calling.models.CallCompositeSetupScreenViewData
import com.azure.android.communication.ui.callingcompositedemoapp.features.AdditionalFeatures
import com.azure.android.communication.ui.callingcompositedemoapp.features.SettingsFeatures
import java9.util.concurrent.CompletableFuture
import java.util.UUID

class CallLauncherViewModel : ViewModel() {

    companion object {
        val callClient = CallClient()
        var callAgent: CallAgent? = null

        var callComposite: CallComposite? = null
        var incomingCall: IncomingCall? = null
    }

    fun startCallAgent(
        context: Context,
        acsToken: String,
        displayName: String,
        incomingCallHandler: (ic: IncomingCall) -> Unit,
    ) {
        if (callAgent == null) {
            val communicationTokenRefreshOptions =
                CommunicationTokenRefreshOptions({ acsToken }, false)
            val communicationTokenCredential =
                CommunicationTokenCredential(communicationTokenRefreshOptions)

            callAgent = callClient.createCallAgent(
                context,
                communicationTokenCredential,
                CallAgentOptions().setDisplayName(displayName)
            ).get()

            callAgent!!.addOnIncomingCallListener { ic ->
                incomingCall = ic
                incomingCallHandler(ic)
            }
        }
    }

    fun stopCallAgent() {
        callAgent?.dispose()
        callAgent = null
    }

    fun launch(
        context: Context,
        displayName: String,
        groupId: UUID?,
        meetingLink: String?,
        participants: List<CommunicationIdentifier>?,
        acceptIncomingCall: Boolean,
    ) {
        val callComposite = createCallComposite(context)
        callComposite.addOnErrorEventHandler(CallLauncherActivityErrorHandler(context, callComposite))

        if (SettingsFeatures.getRemoteParticipantPersonaInjectionSelection()) {
            callComposite.addOnRemoteParticipantJoinedEventHandler(
                RemoteParticipantJoinedHandler(callComposite, context)
            )
        }

        if (callAgent == null) {
            return
        }
        val activeCallAgent = callAgent!!

        val callFactory = object: CallCompositeCallFactory {
            override fun getCall(
                context: Context?,
                videoOptions: VideoOptions?,
                audioOptions: AudioOptions?
            ): CompletableFuture<Call> {

                val joinCallOptions = JoinCallOptions()
                    .setAudioOptions(audioOptions)
                    .setVideoOptions(videoOptions)

                return if (acceptIncomingCall) {
                    if (incomingCall == null)
                        throw Exception("Cannot accept null incoming call")
                    incomingCall!!.accept(
                        context,
                        AcceptCallOptions()
                            .setVideoOptions(videoOptions))
                } else if (groupId != null)
                    CompletableFuture.completedFuture(
                        activeCallAgent.join(
                            context,
                            GroupCallLocator(groupId),
                            joinCallOptions
                        )
                    )
                else if (meetingLink != null)
                    CompletableFuture.completedFuture(
                        activeCallAgent.join(
                            context,
                            TeamsMeetingLinkLocator(meetingLink),
                            joinCallOptions
                        )
                    )
                else
                    CompletableFuture.completedFuture(
                        activeCallAgent.startCall(
                            context, participants, StartCallOptions()
                                .setAudioOptions(audioOptions)
                                .setVideoOptions(videoOptions)
                        )
                    )
            }

            override fun getDeviceManager(context: Context?): CompletableFuture<DeviceManager> {
                return callClient.getDeviceManager(context)
            }

        }

        val remoteOptions =
            CallCompositeRemoteOptions(callFactory, displayName)

        val localOptions = CallCompositeLocalOptions()
            .setParticipantViewData(SettingsFeatures.getParticipantViewData(context.applicationContext))
            .setSetupScreenViewData(
                CallCompositeSetupScreenViewData()
                    .setTitle(SettingsFeatures.getTitle())
                    .setSubtitle(SettingsFeatures.getSubtitle())
            )
            .setSkipSetupScreen(SettingsFeatures.getSkipSetupScreenFeatureOption())
            .setCameraOn(SettingsFeatures.getCameraOnByDefaultOption())
            .setMicrophoneOn(SettingsFeatures.getMicOnByDefaultOption())

        callComposite.launch(context, remoteOptions, localOptions)
    }

    fun getCallHistory(context: Context): List<CallCompositeCallHistoryRecord> {
        return (callComposite ?: createCallComposite(context)).getDebugInfo(context).callHistoryRecords
    }

    private fun createCallComposite(context: Context): CallComposite {
        SettingsFeatures.initialize(context.applicationContext)

        val selectedLanguage = SettingsFeatures.language()
        val locale = selectedLanguage?.let { SettingsFeatures.locale(it) }

        val callCompositeBuilder = CallCompositeBuilder()
            .localization(CallCompositeLocalizationOptions(locale!!, SettingsFeatures.getLayoutDirection()))

        if (AdditionalFeatures.secondaryThemeFeature.active)
            callCompositeBuilder.theme(R.style.MyCompany_Theme_Calling)

        val callComposite = callCompositeBuilder.build()

        // For test purposes we will keep a static ref to CallComposite
        CallLauncherViewModel.callComposite = callComposite
        return callComposite
    }
}

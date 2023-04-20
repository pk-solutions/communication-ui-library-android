// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.android.communication.ui.calling.models;

import android.media.AudioManager;

import com.azure.android.communication.ui.calling.CallComposite;

import java.util.Set;
import java.util.function.BiConsumer;

/**
 * {@link CallCompositeLocalOptions} for {@link CallComposite#launch}.
 *
 * <p>
 *     Local Options for the Call Composite. These options are not shared with the server and impact local views only.
 *     E.g. The Local Participant Name if it differs from the display name you'd like to share with the server.
 * </p>
 * <pre>
 *
 * &#47;&#47; Initialize the call composite builder
 * final CallCompositeBuilder builder = new CallCompositeBuilder&#40;&#41;;
 *
 * &#47;&#47; Build the call composite
 * CallComposite callComposite = builder.build&#40;&#41;;
 *
 * &#47;&#47; Build the CallCompositeLocalOptions with {@link CallCompositeParticipantViewData}
 * CallCompositeLocalOptions localOptions = new CallCompositeLocalOptions(
 *     new CallCompositeParticipantViewData&#40;...&#41);
 *
 * &#47;&#47; Launch call
 * callComposite.launch&#40;.., .., localOptions&#41
 * </pre>
 *
 * @see CallComposite
 */
public final class CallCompositeLocalOptions {
    private CallCompositeParticipantViewData participantViewData = null;
    private CallCompositeSetupScreenViewData setupScreenViewData = null;
    private boolean cameraOn = false;
    private boolean microphoneOn = false;
    private boolean skipSetupScreen = false;
    private boolean lockRotation = false;
    private boolean confirmExit = true;
    private boolean detachControlButtons = false;
    private boolean hideFloatingHeader = false;
    private boolean ignoreProximitySensor = false;
    private boolean noBannerLink = false;
    private boolean enableParticipantMenuDrawer = false;
    private Set<Integer> backKeys = null;
    private Set<Integer> confirmKeys = null;
    private Set<Integer> upKeys = null;
    private Set<Integer> downKeys = null;
    private Set<Integer> pttKeys = null;
    private Set<Integer> volumeUpKeys = null;
    private Set<Integer> volumeDownKeys = null;
    private BiConsumer<AudioManager, Boolean> volumeChanger = null;

    /**
     * Create LocalSettings.
     *
     * @param participantViewData The {@link CallCompositeParticipantViewData};
     * @see CallCompositeParticipantViewData
     */
    public CallCompositeLocalOptions(final CallCompositeParticipantViewData participantViewData) {
        this.participantViewData = participantViewData;
    }

    /**
     * Create an empty {@link CallCompositeLocalOptions} object and assign using setters.
     */
    public CallCompositeLocalOptions() { }

    /**
     * Get the {@link CallCompositeParticipantViewData}.
     *
     * @return The {@link CallCompositeParticipantViewData} that is currently set.
     */
    public CallCompositeParticipantViewData getParticipantViewData() {
        return participantViewData;
    }

    /**
     * Set a {@link CallCompositeParticipantViewData} to be used.
     * @param participantViewData The participant view data object to be used.
     * @return The current {@link CallCompositeLocalOptions} object for Fluent use.
     */
    public CallCompositeLocalOptions setParticipantViewData(
            final CallCompositeParticipantViewData participantViewData) {
        this.participantViewData = participantViewData;
        return this;
    }

    /**
     * Get the {@link CallCompositeSetupScreenViewData}.
     * @return The {@link CallCompositeSetupScreenViewData} that is currently set.
     */
    public CallCompositeSetupScreenViewData getSetupScreenViewData() {
        return setupScreenViewData;
    }

    /**
     * Set a {@link CallCompositeSetupScreenViewData} to be used.
     * @param setupScreenViewData The setup screen view data object to be used.
     * @return The current {@link CallCompositeLocalOptions} object for Fluent use.
     */
    public CallCompositeLocalOptions setSetupScreenViewData(
            final CallCompositeSetupScreenViewData setupScreenViewData) {
        this.setupScreenViewData = setupScreenViewData;
        return this;
    }

    /**
     * Get the boolean value for skip setup screen.
     * @return The boolean that is currently set.
     */
    public boolean isSkipSetupScreen() {
        return this.skipSetupScreen;
    }

    /**
     * Set a boolean to be used.
     * @param skipSetupScreen The boolean value to be used.
     * @return The current {@link CallCompositeLocalOptions} object for Fluent use.
     */
    public CallCompositeLocalOptions setSkipSetupScreen(final boolean skipSetupScreen) {
        this.skipSetupScreen = skipSetupScreen;
        return this;
    }

    public boolean isLockRotation() {
        return this.lockRotation;
    }

    public CallCompositeLocalOptions setLockRotation(final boolean lockRotation) {
        this.lockRotation = lockRotation;
        return this;
    }

    /**
     * Get the initial camera configuration boolean value.
     * @return The boolean that is currently set.
     */
    public boolean isCameraOn() {
        return this.cameraOn;
    }

    /**
     * Set a boolean to be used.
     * @param cameraOn The boolean value to be used for initial camera configuration.
     * @return The current {@link CallCompositeLocalOptions} object for Fluent use.
     */
    public CallCompositeLocalOptions setCameraOn(
            final boolean cameraOn
    ) {
        this.cameraOn = cameraOn;
        return this;
    }

    /**
     * Get the initial microphone configuration boolean value.
     * @return The boolean that is currently set.
     */
    public boolean isMicrophoneOn() {
        return this.microphoneOn;
    }

    /**
     * Set a boolean to be used.
     * @param microphoneOn The boolean value to be used for initial microphone configuration.
     * @return The current {@link CallCompositeLocalOptions} object for Fluent use.
     */
    public CallCompositeLocalOptions setMicrophoneOn(
            final boolean microphoneOn
    ) {
        this.microphoneOn = microphoneOn;
        return this;
    }

    public boolean isConfirmExit() {
        return confirmExit;
    }
    public CallCompositeLocalOptions setConfirmExit(
            final boolean confirmExit
    ) {
        this.confirmExit = confirmExit;
        return this;
    }

    public boolean isDetachControlButtons() {
        return this.detachControlButtons;
    }
    public CallCompositeLocalOptions setDetachControlButtons(
            final boolean detachControlButtons
    ) {
        this.detachControlButtons = detachControlButtons;
        return this;
    }

    public boolean isHideFloatingHeader() {
        return this.hideFloatingHeader;
    }
    public CallCompositeLocalOptions setHideFloatingHeader(
            final boolean hideFloatingHeader
    ) {
        this.hideFloatingHeader = hideFloatingHeader;
        return this;
    }

    public boolean isIgnoreProximitySensor() {
        return this.ignoreProximitySensor;
    }
    public CallCompositeLocalOptions setIgnoreProximitySensor(
            final boolean ignoreProximitySensor
    ) {
        this.ignoreProximitySensor = ignoreProximitySensor;
        return this;
    }

    public boolean isNoBannerLink() {
        return this.noBannerLink;
    }
    public CallCompositeLocalOptions setNoBannerLink(
            final boolean noBannerLink
    ) {
        this.noBannerLink = noBannerLink;
        return this;
    }

    public boolean isEnableParticipantMenuDrawer() {
        return this.enableParticipantMenuDrawer;
    }
    public CallCompositeLocalOptions setEnableParticipantMenuDrawer(
            final boolean enableParticipantMenuDrawer
    ) {
        this.enableParticipantMenuDrawer = enableParticipantMenuDrawer;
        return this;
    }

    public Set<Integer> getBackKeys() {
        return this.backKeys;
    }
    public CallCompositeLocalOptions setBackKeys(
            final Set<Integer> backKeys
    ) {
        this.backKeys = backKeys;
        return this;
    }

    public Set<Integer> getConfirmKeys() {
        return this.confirmKeys;
    }
    public CallCompositeLocalOptions setConfirmKeys(
            final Set<Integer> confirmKeys
    ) {
        this.confirmKeys = confirmKeys;
        return this;
    }

    public Set<Integer> getUpKeys() {
        return this.upKeys;
    }
    public CallCompositeLocalOptions setUpKeys(
            final Set<Integer> upKeys
    ) {
        this.upKeys = upKeys;
        return this;
    }

    public Set<Integer> getDownKeys() {
        return this.downKeys;
    }
    public CallCompositeLocalOptions setDownKeys(
            final Set<Integer> downKeys
    ) {
        this.downKeys = downKeys;
        return this;
    }

    public Set<Integer> getPttKeys() {
        return pttKeys;
    }
    public CallCompositeLocalOptions setPttKeys(
            final Set<Integer> pttKeys
    ) {
        this.pttKeys = pttKeys;
        return this;
    }

    public Set<Integer> getVolumeUpKeys() {
        return volumeUpKeys;
    }
    public CallCompositeLocalOptions setVolumeUpKeys(
            final Set<Integer> volumeUpKeys
    ) {
        this.volumeUpKeys = volumeUpKeys;
        return this;
    }

    public Set<Integer> getVolumeDownKeys() {
        return volumeDownKeys;
    }
    public CallCompositeLocalOptions setVolumeDownKeys(
            final Set<Integer> volumeDownKeys
    ) {
        this.volumeDownKeys = volumeDownKeys;
        return this;
    }

    public BiConsumer<AudioManager, Boolean> getVolumeChanger() {
        return volumeChanger;
    }
    public CallCompositeLocalOptions setVolumeChanger(
            final BiConsumer<AudioManager, Boolean> volumeChanger
    ) {
        this.volumeChanger = volumeChanger;
        return this;
    }
}

package com.azure.android.communication.ui.calling.presentation.fragment.calling.participantmenu

import android.content.Context
import android.widget.RelativeLayout
import androidx.lifecycle.LifecycleOwner

internal class ParticipantMenuView(
    context: Context,
    private val viewModel: ParticipantMenuViewModel,
) : RelativeLayout(context) {

    init {

    }

    fun start(viewLifecycleOwner: LifecycleOwner) {
    }

    fun stop() {
        this.removeAllViews()
    }

}

package com.azure.android.communication.ui.calling.presentation.fragment.calling.participantmenu

import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.widget.RelativeLayout
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
            val bottomCellItems = mutableListOf<BottomCellItem>()

            // TODO: populate with options. See MoreCallOptionsListView and LeaveConfirmView
            // TODO: add a new toggle to change the list view into a large button view.  then confirm exit, more options, and this menu will all have the same look-and-feel.  need to possibly rework just the listview layout and make the drawer go all the way to the top.

            return bottomCellItems
        }

}

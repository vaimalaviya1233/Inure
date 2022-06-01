package app.simple.inure.popups.viewers

import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import app.simple.inure.R
import app.simple.inure.adapters.popups.AdapterGraphicsFilter
import app.simple.inure.decorations.overscroll.CustomVerticalRecyclerView
import app.simple.inure.extensions.popup.BasePopupWindow
import app.simple.inure.extensions.popup.PopupLinearLayout
import app.simple.inure.util.StatusBarHeight
import kotlin.math.roundToInt

class PopupGraphicsFilter(anchor: View) : BasePopupWindow() {

    private var recyclerView: CustomVerticalRecyclerView

    init {
        val contentView = LayoutInflater.from(anchor.context).inflate(R.layout.popup_graphics_filter, PopupLinearLayout(anchor.context))

        recyclerView = contentView.findViewById(R.id.graphics_filter_recycler_view)
        recyclerView.adapter = AdapterGraphicsFilter()

        if (StatusBarHeight.isLandscape(contentView.context)) {
            val params: ViewGroup.LayoutParams = recyclerView.layoutParams
            params.height = (StatusBarHeight.getDisplayHeight(contentView.context) * 0.85).roundToInt()
            recyclerView.layoutParams = params
        }

        setContentView(contentView)
        init()
        showAsDropDown(anchor, (-width / 1.4).roundToInt(), height / 16, Gravity.NO_GRAVITY)
    }
}
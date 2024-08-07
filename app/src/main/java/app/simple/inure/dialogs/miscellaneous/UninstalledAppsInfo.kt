package app.simple.inure.dialogs.miscellaneous

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import app.simple.inure.R
import app.simple.inure.decorations.ripple.DynamicRippleTextView
import app.simple.inure.extensions.fragments.ScopedDialogFragment

class UninstalledAppsInfo : ScopedDialogFragment() {

    private lateinit var ok: DynamicRippleTextView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.dialog_info_uninstalled, container, false)

        ok = view.findViewById(R.id.ok)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ok.setOnClickListener {
            dismiss()
        }
    }

    companion object {
        fun newInstance(): UninstalledAppsInfo {
            val args = Bundle()
            val fragment = UninstalledAppsInfo()
            fragment.arguments = args
            return fragment
        }

        const val TAG = "UninstalledAppsInfo"
    }
}

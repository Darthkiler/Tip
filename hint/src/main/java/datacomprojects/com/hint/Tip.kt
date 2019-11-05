package datacomprojects.com.hint

import android.content.Context
import android.view.View

class Tip(var hintID: String, var customHint: TipView, view: View?, list: TipsList?) {
    var isShow = false

    init {
        if (view != null)
            this.customHint.attachToView(view)
    }

    fun showIfNeed(context: Context): Boolean {
        if (!TipsSharedPreferencesUtils.getInstance(context).getBoolean(hintID, false)) {
            customHint.show()
            isShow = true
            return true
        }
        return false
    }

    fun wasShowed(context: Context): Boolean {
        return TipsSharedPreferencesUtils.getInstance(context).getBoolean(hintID, false)
    }

    fun hide(context: Context) {
        TipsSharedPreferencesUtils.getInstance(context).putBoolean(hintID, true).apply()
        customHint.hide()
        isShow = false
    }

    fun reset(context: Context) {
        TipsSharedPreferencesUtils.getInstance(context).putBoolean(hintID, false).apply()
    }
}

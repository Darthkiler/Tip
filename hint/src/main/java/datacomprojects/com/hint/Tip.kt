package datacomprojects.com.hint

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.View

class Tip @JvmOverloads constructor(var hintID: String, var customHint: TipView, view: View?, closeDrawable: Drawable? = null) {
    var isShow = false

    init {
        if (view != null)
            this.customHint.attachToView(view)
        closeDrawable?.let { customHint.setCloseImage(it) }
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

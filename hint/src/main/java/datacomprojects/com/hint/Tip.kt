package datacomprojects.com.hint

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.View

class Tip @JvmOverloads constructor(var hintID: String, var customHint: TipView, view: View?, closeDrawable: Drawable? = null) {
    var isShow = false

    init {
        view?.let { this.customHint.attachToView(it) }
        closeDrawable?.let { customHint.setCloseImage(it) }
    }

    fun showIfNeed(context: Context, onShowed: () -> Unit) {
        if (!TipsSharedPreferencesUtils.getInstance(context).getBoolean(hintID, false)) {
            customHint.show(onShowed)
            isShow = true
        }
    }

    fun wasShowed(context: Context): Boolean {
        return TipsSharedPreferencesUtils.getInstance(context).getBoolean(hintID, false)
    }

    fun hide(context: Context, onAnimationEnd: (() -> Unit)? = null) {
        TipsSharedPreferencesUtils.getInstance(context).putBoolean(hintID, true).apply()
        customHint.hide(onAnimationEnd)
        isShow = false
    }

    fun reset(context: Context) {
        TipsSharedPreferencesUtils.getInstance(context).putBoolean(hintID, false).apply()
    }
}

package datacomprojects.com.hint

import android.content.Context

import java.util.ArrayList

class TipsList(private val context: Context) {

    private var tipArrayList = ArrayList<Tip>()

    fun addTipToArray(tip: Tip) {
        tipArrayList.add(tip)
    }

    fun showNext() {
        for (tip in tipArrayList) {
            if (!tip.wasShowed(context)) {
                tip.showIfNeed(context) {
                    tip.hide(context) {
                        showNext()
                    }
                }
                break
            }
        }
    }

    fun allWasShowed(): Boolean {
        for (tip in tipArrayList)
            if (!tip.wasShowed(context))
                return false
        return true
    }

    fun dismissAll() {
        for (tip in tipArrayList)
            tip.hide(context)
    }

    fun addAll() {
        for (tip in tipArrayList)
            tip.reset(context)
    }

    fun skip(onDismiss: ((String) -> Unit)? = null) {
        for (tip in tipArrayList)
            if (tip.isShow) {
                tip.hide(context) {
                    onDismiss?.let { it(tip.hintID) }
                }
                break
            }
    }

}

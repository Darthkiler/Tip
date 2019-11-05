package datacomprojects.com.hint

import android.content.Context

import java.util.ArrayList

import datacomprojects.com.hint.callbacks.TipNeedToDismissTipInterface
import datacomprojects.com.hint.callbacks.TipShowCallback

class TipsList(private val context: Context) {

    var tipArrayList = ArrayList<Tip>()

    private var hintShowedCallback: TipShowCallback? = null

    fun setHintShowedCallback(hintShowedCallback: TipShowCallback) {
        this.hintShowedCallback = hintShowedCallback
    }

    fun addTipToArray(tip: Tip) {
        tipArrayList.add(tip)
    }

    fun showNext(tipNeedToDismissTipInterface: TipNeedToDismissTipInterface) {
        for (tip in tipArrayList) {
            if (tip.showIfNeed(context)) {
                tip.customHint.tipNeedToDismissTipInterface = tipNeedToDismissTipInterface
                if (hintShowedCallback != null)
                    hintShowedCallback!!.onShow(tip.hintID)
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

    fun dismissCurrent() {
        for (tip in tipArrayList)
            if (tip.isShow) {
                tip.hide(context)
                if (hintShowedCallback != null)
                    hintShowedCallback!!.onDismiss(tip.hintID)
                break
            }
    }

}

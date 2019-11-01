package datacomprojects.com.hint;

import android.content.Context;

import java.util.ArrayList;

import datacomprojects.com.hint.callbacks.TipNeedToDismissTipInterface;
import datacomprojects.com.hint.callbacks.TipShowCallback;

public class TipsList {

    private Context context;

    public TipsList(Context context) {
        this.context = context;
    }

    public ArrayList<Tip> tipArrayList = new ArrayList<>();

    public void setHintShowedCallback(TipShowCallback hintShowedCallback) {
        this.hintShowedCallback = hintShowedCallback;
    }

    private TipShowCallback hintShowedCallback;

    public void showNext(TipNeedToDismissTipInterface tipNeedToDismissTipInterface) {
        for (Tip tip :
                tipArrayList) {
                if(tip.showIfNeed(context)) {
                    tip.customHint.setTipNeedToDismissTipInterface(tipNeedToDismissTipInterface);
                    if(hintShowedCallback!=null)
                        hintShowedCallback.onShow(tip.hintID);
                    break;
                }
        }
    }

    public boolean allWasShowed() {
        for(Tip tip: tipArrayList)
            if(!tip.wasShowed(context))
                return false;
        return true;
    }

    public void dismissAll() {
        for(Tip tip : tipArrayList)
            tip.hide(context);
    }

    public void addAll() {
        for(Tip tip : tipArrayList)
            tip.reset(context);
    }

    public void dismissCurrent() {
        for(Tip tip : tipArrayList)
            if(tip.isShow) {
                tip.hide(context);
                if(hintShowedCallback!=null)
                    hintShowedCallback.onDismiss(tip.hintID);
                break;
            }
    }

}

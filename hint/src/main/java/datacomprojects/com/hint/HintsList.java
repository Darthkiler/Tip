package datacomprojects.com.hint;

import android.content.Context;

import java.util.ArrayList;

import datacomprojects.com.hint.callbacks.HintNeedToDismissHintInterface;
import datacomprojects.com.hint.callbacks.HintShowCallback;

public class HintsList {

    private Context context;

    public HintsList(Context context) {
        this.context = context;
    }

    public ArrayList<Hint> hintArrayList = new ArrayList<>();

    public void setHintShowedCallback(HintShowCallback hintShowedCallback) {
        this.hintShowedCallback = hintShowedCallback;
    }

    private HintShowCallback hintShowedCallback;

    public void showNext(HintNeedToDismissHintInterface hintNeedToDismissHintInterface) {
        for (Hint hint:
             hintArrayList) {
                if(hint.showIfNeed(context)) {
                    hint.customHint.setHintNeedToDismissHintInterface(hintNeedToDismissHintInterface);
                    if(hintShowedCallback!=null)
                        hintShowedCallback.onShow(hint.hintID);
                    break;
                }
        }
    }

    public void dismissAll() {
        for(Hint hint:hintArrayList)
            hint.hide(context);
    }

    public void addAll() {
        for(Hint hint:hintArrayList)
            hint.reset(context);
    }

    public void dismissCurrent() {
        for(Hint hint:hintArrayList)
            if(hint.isShow) {
                hint.hide(context);
                if(hintShowedCallback!=null)
                    hintShowedCallback.onDismiss(hint.hintID);
                break;
            }
    }

}

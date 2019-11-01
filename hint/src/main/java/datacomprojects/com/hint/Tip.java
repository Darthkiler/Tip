package datacomprojects.com.hint;

import android.content.Context;
import android.view.View;

import androidx.annotation.Nullable;

public class Tip {
    public boolean isShow = false;
    public String hintID;
    public TipView customHint;

    public Tip(String hintID, TipView customHint, @Nullable View view, @Nullable TipsList list) {
        this.hintID = hintID;
        this.customHint = customHint;
        if(view!=null)
            this.customHint.attachToView(view);
    }

    public boolean showIfNeed(Context context) {
        if(!TipsSharedPreferencesUtils.getInstance(context).getBoolean(hintID,false)) {
            customHint.show();
            isShow = true;
            return true;
        }
        return false;
    }

    public boolean wasShowed(Context context) {
        return TipsSharedPreferencesUtils.getInstance(context).getBoolean(hintID,false);
    }

    public void hide(Context context) {
        TipsSharedPreferencesUtils.getInstance(context).putBoolean(hintID,true).apply();
        customHint.hide();
        isShow = false;
    }

    public void reset(Context context) {
        TipsSharedPreferencesUtils.getInstance(context).putBoolean(hintID,false).apply();
    }
}

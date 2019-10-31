package datacomprojects.com.hint;

import android.content.Context;
import android.view.View;

import androidx.annotation.Nullable;

public class Hint {
    public boolean isShow = false;
    public String hintID;
    public TipView customHint;

    public Hint(String hintID, TipView customHint,@Nullable View view, @Nullable HintsList list) {
        this.hintID = hintID;
        this.customHint = customHint;
        if(view!=null)
            this.customHint.attachToView(view);
    }

    public boolean showIfNeed(Context c) {
        if(!TipsSharedPreferencesUtils.getInstance(c).getBoolean(hintID,false)) {
            customHint.show();
            isShow = true;
            return true;
        }
        return false;
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

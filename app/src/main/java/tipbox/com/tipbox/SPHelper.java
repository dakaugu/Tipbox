package tipbox.com.tipbox;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by dakaugu on 2/1/16.
 */
public class SPHelper {

    protected SharedPreferences mSharedPreferences;
    private static final String TIP_SHARED_PREFERENCES = "TipAssist-Pref";
    private static final String SAVED_HEIGHT = "saved height";

    public SPHelper(Context context) {
        mSharedPreferences = context.getSharedPreferences(TIP_SHARED_PREFERENCES, Context.MODE_PRIVATE);
    }

    public void setSavedHeight(int height) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putInt(SAVED_HEIGHT, height);
        editor.apply();
    }

    public int getHeight() {
        return mSharedPreferences.getInt(SAVED_HEIGHT, 0);
    }
}

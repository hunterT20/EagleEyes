package vn.dmcl.eagleeyes.helper;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import static android.content.ContentValues.TAG;

public class InputHelper {

    public static void hideKeypad(Activity activity) {
        try {
            InputMethodManager inputMethodManager = (InputMethodManager) activity
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
            if (inputMethodManager != null && inputMethodManager.isAcceptingText()) {
                inputMethodManager.toggleSoftInput(0, InputMethodManager.HIDE_IMPLICIT_ONLY);
            }
        } catch (Exception e) {
            Log.e(TAG, "hideKeypad: " + e);
        }
    }

    public static void hideKeypadS(Activity activity, TextView tv) {
        try {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            assert imm != null;
            imm.hideSoftInputFromWindow(tv.getWindowToken(), 0);
        } catch (Exception e) {
            Log.e(TAG, "hideKeypadS: " + e);
        }
    }

    public static void showKeypad(Activity activity, EditText et) {
        et.requestFocus();
        InputMethodManager inputMethodManager = (InputMethodManager) activity
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        assert inputMethodManager != null;
        inputMethodManager.showSoftInput(et, InputMethodManager.SHOW_IMPLICIT);

        if (!inputMethodManager.isAcceptingText()) {
            showKeypadFocre(activity, et);
        }
    }

    private static void showKeypadFocre(Activity activity, EditText et) {
        et.requestFocus();
        InputMethodManager inputMethodManager = ((InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE));
        assert inputMethodManager != null;
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
    }
}
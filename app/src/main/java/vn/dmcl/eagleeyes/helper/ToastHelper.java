package vn.dmcl.eagleeyes.helper;

import android.annotation.SuppressLint;
import android.content.Context;
import android.widget.Toast;

public class ToastHelper {

    @SuppressLint("StaticFieldLeak")
    private static Context context;

    public static void initContext(Context _context) {
        context = _context;
    }

    public static void showShortToast(String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public static void showLongToast(String message) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }
}

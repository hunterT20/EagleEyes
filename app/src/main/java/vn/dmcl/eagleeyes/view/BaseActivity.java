package vn.dmcl.eagleeyes.view;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import vn.dmcl.eagleeyes.R;

@SuppressLint("Registered")
public class BaseActivity extends AppCompatActivity {

    @SuppressLint("StaticFieldLeak")
    private static BaseActivity currentActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        currentActivity = this;
    }

    public static BaseActivity getCurrentActivity() {
        return currentActivity;
    }
}

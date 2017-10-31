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
        setFullScreen();
        //getWindow().getAttributes().windowAnimations = R.style.Fade;
        //overridePendingTransition(R.anim.anim_slide_in, R.anim.anim_slide_out);
    }

    @Override
    protected void onResume() {
        super.onResume();
        currentActivity = this;
    }

    public void setFullScreen() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    public static BaseActivity getCurrentActivity() {
        return currentActivity;
    }


    View mCustomView;
    TextView tv_title;

    @SuppressLint("InflateParams")
    public void initActionBar(String title) {
        ActionBar mActionBar = getSupportActionBar();
        assert mActionBar != null;
        mActionBar.setDisplayShowHomeEnabled(false);
        mActionBar.setDisplayShowTitleEnabled(false);
        LayoutInflater mInflater = LayoutInflater.from(this);
        mCustomView = mInflater.inflate(R.layout.actionbar_title, null);
        mActionBar.setCustomView(mCustomView);
        mActionBar.setDisplayShowCustomEnabled(true);
        mActionBar.setDisplayShowCustomEnabled(true);

        tv_title = (TextView) mCustomView.findViewById(R.id.tv_title);
        tv_title.setText(title);
    }

    /*public void initActionBar() {
        ActionBar mActionBar = getSupportActionBar();
        mActionBar.setDisplayShowHomeEnabled(false);
        mActionBar.setDisplayShowTitleEnabled(false);
        LayoutInflater mInflater = LayoutInflater.from(this);
        mCustomView = mInflater.inflate(R.layout.actionbar, null);
        mActionBar.setCustomView(mCustomView);
        mActionBar.setDisplayShowCustomEnabled(true);

        tv_bt_search = (ImageView) mCustomView.findViewById(tfl.com.vn.teenidol.R.id.tv_bt_search);
        tv_title = (TextView) mCustomView.findViewById(tfl.com.vn.teenidol.R.id.tv_title);
        //TextView tv_bt_search = (TextView) mCustomView.findViewById(R.id.tv_bt_search);
        iv_title = (ImageView) mCustomView.findViewById(tfl.com.vn.teenidol.R.id.iv_title);
        tv_bt_user = (ImageView) mCustomView.findViewById(tfl.com.vn.teenidol.R.id.tv_bt_user);

        tv_title.setVisibility(View.GONE);
        tv_title.setText(getTitle());
        tv_bt_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TeenIdolActivity.this, SearchActivity.class);
                startActivity(intent);
            }
        });
        tv_bt_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (UserAccountHelper.getInstance().checkUserLogin(getCurrentActivity())) {
                    //DialogDeclare dialogDeclare = DialogDeclare.getInstance(getCurrentActivity(), false);
                    //dialogDeclare.show();
                    showProfileActivity();
                }
            }
        });
    }*/

}

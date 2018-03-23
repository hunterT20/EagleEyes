package vn.dmcl.eagleeyes.view.login;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.fabric.sdk.android.Fabric;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import vn.dmcl.eagleeyes.R;
import vn.dmcl.eagleeyes.common.AppConst;
import vn.dmcl.eagleeyes.data.dto.Config;
import vn.dmcl.eagleeyes.data.dto.ApiListResult;
import vn.dmcl.eagleeyes.data.dto.ApiResult;
import vn.dmcl.eagleeyes.data.dto.Session;
import vn.dmcl.eagleeyes.data.remote.ApiUtils;
import vn.dmcl.eagleeyes.helper.DialogHelper;
import vn.dmcl.eagleeyes.helper.NetworkHelper;
import vn.dmcl.eagleeyes.helper.TelephonyInfoHelper;
import vn.dmcl.eagleeyes.helper.ToastHelper;
import vn.dmcl.eagleeyes.helper.UserAccountHelper;
import vn.dmcl.eagleeyes.view.map.MainMapActivity;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";

    @BindView(R.id.et_password)     EditText et_password;
    @BindView(R.id.et_phone)        EditText et_phone;
    @BindView(R.id.v_loading)       RelativeLayout v_loading;
    @BindView(R.id.txtv_Version)    TextView txtv_Version;
    @BindView(R.id.Login_Main)      LinearLayout Login_Main;
    @BindView(R.id.Login_Session)   LinearLayout Login_Session;
    @BindView(R.id.txtv_PhoneNumber)    TextView txtv_PhoneNumber;
    @BindView(R.id.txtv_TypeUser)       TextView txtv_TypeUser;

    private TelephonyInfoHelper telephonyInfo;
    private boolean doubleBackToExitPressedOnce = false;
    private CompositeDisposable disposable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setFullScreen();
        setContentView(R.layout.activity_login);
        final Fabric fabric = new Fabric.Builder(this)
                .kits(new Crashlytics())
                .debuggable(true)
                .build();
        Fabric.with(fabric);
        ButterKnife.bind(this);

        disposable = new CompositeDisposable();

        PackageInfo packageInfo;
        try {
            packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            txtv_Version.setText(String.format("%s %s", "Version", packageInfo.versionName));
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        if (checkPermission()) {
            telephonyInfo = TelephonyInfoHelper.getInstance(this);
            setViewData();
        }

        if (checkLocation() && checkInternet()) {
            checkToken();
        }

        LoadingConfig();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (checkLocation() && checkInternet()) {
            checkToken();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        disposable.clear();
    }

    @OnClick(R.id.tv_login)
    public void onLoginClick() {
        v_loading.setVisibility(View.VISIBLE);

        HashMap<String,String> param = new HashMap<>();
        param.put("phoneNumber", et_phone.getText().toString());
        param.put("otp", et_password.getText().toString());

        Observable<ApiResult<Session>> login = ApiUtils.getAPIBase().login(param);
        Disposable disposableLogin =
                login.subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableObserver<ApiResult<Session>>() {
                            @Override
                            public void onNext(ApiResult<Session> result) {
                                if (result.isResult()) {
                                    Intent intent = new Intent(LoginActivity.this, MainMapActivity.class);

                                    UserAccountHelper.getIntance().setSecureKey(result.getData().getKey());
                                    UserAccountHelper.getIntance().setUserType(result.getData().getUserType());
                                    UserAccountHelper.getIntance().setPhoneNumber(et_phone.getText().toString());

                                    if (result.getData().getUserType() == AppConst.UserType.FLYER){
                                        intent.putExtra("IsFlyer", true);
                                    }else {
                                        intent.putExtra("IsFlyer", false);
                                    }

                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    ToastHelper.showShortToast(result.getMessage());
                                    UserAccountHelper.getIntance().setSecureKey("");
                                }
                            }

                            @Override
                            public void onError(Throwable e) {
                                Log.e(TAG, "Login Error: " + e.getMessage());
                                UserAccountHelper.getIntance().setSecureKey("");
                            }

                            @Override
                            public void onComplete() {
                                v_loading.setVisibility(View.GONE);
                            }
                        });
        disposable.add(disposableLogin);
    }

    @OnClick(R.id.btn_Login_Session)
    public void onLoginSessionClick(){
        checkLogKey();
    }

    @OnClick(R.id.btn_Login_Other)
    public void onLoginOtherClick(){
        Login_Session.setVisibility(View.GONE);
        Login_Main.setVisibility(View.VISIBLE);
    }

    @OnClick(R.id.txtv_Login_Session)
    public void onLoginSessionShowClick(){
        checkToken();
    }

    private void setViewData() {
        if (telephonyInfo != null) {
            String phone = telephonyInfo.getNumberPhone(this);
            if (phone != null && !phone.contentEquals("0") && !phone.contentEquals("")) {
                et_phone.setText(phone);
            }
        }
    }

    private void checkToken() {
        if (!UserAccountHelper.getIntance().getSecureKey().contentEquals("")) {
            new Handler().postDelayed(this::LoginWithSession, 500);
        }
    }

    public boolean checkPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.READ_PHONE_STATE,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.CAMERA
            }, AppConst.PerLocaRequestCode);

        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == AppConst.PerLocaRequestCode) {
            if (permissions[0] == Manifest.permission.READ_PHONE_STATE &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                telephonyInfo = TelephonyInfoHelper.getInstance(this);
                setViewData();
            } else {
                checkPermission();
            }
        }
    }

    /**
     * function checkLogKey kiểm tra trong Sharepreference có Key không
     * Nếu có thì kiểm tra xem key đó có tồn tại trên server
     * Nếu có thì Đăng nhập vào activity Map
     */
    private void checkLogKey() {
        v_loading.setVisibility(View.VISIBLE);

        HashMap<String, String> param = new HashMap<>();
        param.put("key", UserAccountHelper.getIntance().getSecureKey());

        Observable<ApiResult<Session>> checkKey = ApiUtils.getAPIBase().checkKey(param);
        Disposable disposableCheckKey =
                checkKey.subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableObserver<ApiResult<Session>>() {
                            @Override
                            public void onNext(ApiResult<Session> result) {
                                if (result.isResult()) {
                                    Intent intent = new Intent(LoginActivity.this, MainMapActivity.class);
                                    if (UserAccountHelper.getIntance().getUserType() == AppConst.UserType.FLYER) {
                                        intent.putExtra("IsFlyer", true);
                                    }else{
                                        intent.putExtra("IsFlyer", false);
                                    }
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    ToastHelper.showShortToast("Đăng nhập thất bại!\nLỗi: " + result.getMessage());
                                    UserAccountHelper.getIntance().setSecureKey("");
                                    Login_Main.setVisibility(View.VISIBLE);
                                    Login_Session.setVisibility(View.GONE);
                                }
                            }

                            @Override
                            public void onError(Throwable e) {
                                ToastHelper.showShortToast("Đăng nhập thất bại!\nLỗi: " + e.getMessage());
                                UserAccountHelper.getIntance().setSecureKey("");
                            }

                            @Override
                            public void onComplete() {
                                v_loading.setVisibility(View.GONE);
                            }
                        });
        disposable.add(disposableCheckKey);
    }

    private void LoginWithSession(){
        Login_Main.setVisibility(View.GONE);
        Login_Session.setVisibility(View.VISIBLE);

        txtv_PhoneNumber.setText(String.format("Phone: %s", UserAccountHelper.getIntance().getPhoneNumber()));
        txtv_TypeUser.setText(UserAccountHelper.getIntance().getUserType() == AppConst.UserType.FLYER ? "Type: Flyer" : "Type: DCheck");
    }

    public boolean checkLocation() {
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;

        try {
            assert lm != null;
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {
            Log.e(TAG, "checkLocation: " + ex);
        }

        if (!gps_enabled) {
            DialogHelper.showAlertDialog(this, "Mở định vị", "Vui lòng mở định vị để bắt đầu!", "Mở GPS", new DialogHelper.onListenerOneButtonClick() {
                @Override
                public void onButtonClick() {
                    Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(myIntent);
                }
            }, false);
        } else {
            return true;
        }
        return false;
    }

    private boolean checkInternet() {
        if (!NetworkHelper.checkNetworkConnection()) {
            DialogHelper.showAlertDialog(this, "Mở mạng", "Vui lòng mở mạng để bắt đầu!", "Mở Wifi", new DialogHelper.onListenerOneButtonClick() {
                @Override
                public void onButtonClick() {
                    startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                }
            }, false);
        } else {
            return true;
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        ToastHelper.showShortToast("Please click BACK again to exit");

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }

    private void LoadingConfig() {
        if (UserAccountHelper.getIntance().getSecureKey().equals("")) return;

        HashMap<String, Object> param = new HashMap<>();
        param.put("pageIndex", 0);
        param.put("pageSize", 99);
        param.put("key", "");

        Observable<ApiListResult<Config>> getConfig = ApiUtils.getAPIBase().getConfig(param);
        Disposable disposableGetConfig =
                getConfig.subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableObserver<ApiListResult<Config>>() {
                            @Override
                            public void onNext(ApiListResult<Config> result) {
                                if (result.Result) {
                                    if (result.Items != null)
                                        UserAccountHelper.getIntance().updateConfig(result.Items);
                                }
                            }

                            @Override
                            public void onError(Throwable e) {
                                Toast.makeText(LoginActivity.this, "Xảy ra lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onComplete() {

                            }
                        });
        disposable.add(disposableGetConfig);
    }

    public void setFullScreen() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
            Window w = getWindow(); // in Activity's onCreate() for instance
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        } else {
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
    }
}

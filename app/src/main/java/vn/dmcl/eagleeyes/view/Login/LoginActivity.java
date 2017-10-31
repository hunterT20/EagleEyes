package vn.dmcl.eagleeyes.view.Login;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
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
import android.widget.RelativeLayout;

import com.google.gson.reflect.TypeToken;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import vn.dmcl.eagleeyes.R;
import vn.dmcl.eagleeyes.common.AppConst;
import vn.dmcl.eagleeyes.common.FunctionConst;
import vn.dmcl.eagleeyes.dto.ConfigDTO;
import vn.dmcl.eagleeyes.dto.PageResultDTO;
import vn.dmcl.eagleeyes.dto.ResultDTO;
import vn.dmcl.eagleeyes.dto.SessionDTO;
import vn.dmcl.eagleeyes.helper.DataServiceProvider;
import vn.dmcl.eagleeyes.helper.DialogHelper;
import vn.dmcl.eagleeyes.helper.JsonHelper;
import vn.dmcl.eagleeyes.helper.NetworkHelper;
import vn.dmcl.eagleeyes.helper.TelephonyInfoHelper;
import vn.dmcl.eagleeyes.helper.ToastHelper;
import vn.dmcl.eagleeyes.helper.UserAccountHelper;
import vn.dmcl.eagleeyes.view.Map.MainMapActivity;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";

    @BindView(R.id.et_password)
    EditText et_password;
    @BindView(R.id.et_phone)
    EditText et_phone;
    @BindView(R.id.v_loading)
    RelativeLayout v_loading;
    TelephonyInfoHelper telephonyInfo;
    boolean doubleBackToExitPressedOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setFullScreen();
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        if (checkLocation() && checkInternet()){
            autoLogin();
        }
        if (checkPermission()) {
            telephonyInfo = TelephonyInfoHelper.getInstance(this);
            setViewData();
        }
        LoadingConfig();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (checkLocation() && checkInternet()){
            autoLogin();
        }
    }

    @OnClick(R.id.tv_login)
    public void onLoginClick() {
        v_loading.setVisibility(View.VISIBLE);
        if (UserAccountHelper.getIntance().getSecureKey().contentEquals("")) {
            DataServiceProvider<ResultDTO<SessionDTO>> Login = new DataServiceProvider<>(new TypeToken<ResultDTO<SessionDTO>>() {
            }.getType());
            Login.setListener(new DataServiceProvider.OnListenerReponse<ResultDTO<SessionDTO>>() {
                @Override
                public void onSuccess(ResultDTO<SessionDTO> responseData) {
                    if (responseData.isResult()) {
                        Intent intent = new Intent(LoginActivity.this, MainMapActivity.class);
                        UserAccountHelper.getIntance().setSecureKey(responseData.getData().getKey());
                        UserAccountHelper.getIntance().setUserType(responseData.getData().getUserType());
                        if (responseData.getData().getUserType() == AppConst.UserType.FLYER)
                            intent.putExtra("IsFlyer", true);
                        else intent.putExtra("IsFlyer", false);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
                    } else{
                        ToastHelper.showShortToast("Đăng nhập thất bại!\nLỗi: " + responseData.getMessage());
                        UserAccountHelper.getIntance().setSecureKey("");
                    }
                    v_loading.setVisibility(View.GONE);
                }

                @Override
                public void onFailure(String errorMessage) {
                    ToastHelper.showShortToast("Đăng nhập thất bại!\nLỗi: " + errorMessage);
                    UserAccountHelper.getIntance().setSecureKey("");
                    v_loading.setVisibility(View.GONE);
                }
            });
            Login.getData(FunctionConst.Login, AppConst.AsyncMethod.POST, JsonHelper.getIntance().Login(et_phone.getText().toString(), et_password.getText().toString()));
        }
    }

    private void setViewData() {
        if (telephonyInfo != null) {
            String phone = telephonyInfo.getNumberPhone(this);
            if (phone != null && !phone.contentEquals("0") && !phone.contentEquals("")) {
                et_phone.setText(phone);
            }
        }
    }

    private void autoLogin() {
        if (!UserAccountHelper.getIntance().getSecureKey().contentEquals("")) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (!UserAccountHelper.getIntance().getSecureKey().contentEquals("")) {
                        checkLogKey();
                    }
                }
            }, 500);
        }
    }

    public boolean checkPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE}, AppConst.PerLocaRequestCode);

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
        DataServiceProvider<ResultDTO<SessionDTO>> CheckKey = new DataServiceProvider<>(new TypeToken<ResultDTO<SessionDTO>>() {
        }.getType());
        CheckKey.setListener(new DataServiceProvider.OnListenerReponse<ResultDTO<SessionDTO>>() {
            @Override
            public void onSuccess(ResultDTO<SessionDTO> responseData) {
                if (responseData.isResult()) {
                    Intent intent = new Intent(LoginActivity.this, MainMapActivity.class);
                    if (UserAccountHelper.getIntance().getUserType() == AppConst.UserType.FLYER)
                        intent.putExtra("IsFlyer", true);
                    else intent.putExtra("IsFlyer", false);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                } else{
                    ToastHelper.showShortToast("Đăng nhập thất bại!\nLỗi: " + responseData.getMessage());
                    UserAccountHelper.getIntance().setSecureKey("");
                }
                v_loading.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(String errorMessage) {
                ToastHelper.showShortToast("Đăng nhập thất bại!\nLỗi: " + errorMessage);
                UserAccountHelper.getIntance().setSecureKey("");
                v_loading.setVisibility(View.GONE);
            }
        });
        CheckKey.getData(FunctionConst.CheckKey, AppConst.AsyncMethod.GET,
                JsonHelper.getIntance().CheckKey(UserAccountHelper.getIntance().getSecureKey()));
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
        }else {
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
        }else {
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
        if (UserAccountHelper.getIntance().getSecureKey().equals(""))
            return;
        DataServiceProvider<PageResultDTO<ConfigDTO>> GetListConfig = new DataServiceProvider<>(new TypeToken<PageResultDTO<ConfigDTO>>() {
        }.getType());
        GetListConfig.setListener(new DataServiceProvider.OnListenerReponse<PageResultDTO<ConfigDTO>>() {
            @Override
            public void onSuccess(PageResultDTO<ConfigDTO> responseData) {
                if (responseData.Result) {
                    if (responseData.Items != null)
                        UserAccountHelper.getIntance().updateConfig(responseData.Items);
                }
            }

            @Override
            public void onFailure(String errorMessage) {
                ToastHelper.showShortToast(errorMessage);
            }
        });
        GetListConfig.getData(FunctionConst.GetListConfig,
                AppConst.AsyncMethod.GET,
                JsonHelper.getIntance().GetListConfig(0, 99, ""));
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

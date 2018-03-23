package vn.dmcl.eagleeyes.customView;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import vn.dmcl.eagleeyes.R;
import vn.dmcl.eagleeyes.data.model.Photo;
import vn.dmcl.eagleeyes.data.model.ApiResult;
import vn.dmcl.eagleeyes.data.remote.ApiUtils;
import vn.dmcl.eagleeyes.helper.ToastHelper;
import vn.dmcl.eagleeyes.helper.UserAccountHelper;
import vn.dmcl.eagleeyes.data.service.LocationService;

import static android.content.ContentValues.TAG;

public class DialogPhoto extends Dialog implements View.OnClickListener {

    private FragmentActivity activity;
    private boolean fullScreen;
    private RelativeLayout rl_dismiss;
    private TextView tv_bt_ok, tv_bt_cancel;

    private TextView tv_title;
    private ImageView iv_photo, iv_close;
    private EditText et_description;
    private RelativeLayout v_loading;
    private LocationService locationService;
    private Bitmap bitmap;

    public static DialogPhoto getInstance(FragmentActivity a, boolean isFullScreen) {
        return new DialogPhoto(a, isFullScreen);
    }

    private DialogPhoto(FragmentActivity a, boolean isFullScreen) {
        super(a);
        activity = a;
        fullScreen = isFullScreen;
        eventListener = null;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        if (fullScreen)
            if (getWindow() != null)
                getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                        WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.dialog_photo);
    }

    @Override
    public void show() {
        super.show();
        if (getWindow() == null) return;
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.MATCH_PARENT;
        getWindow().setAttributes(params);
        // getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        findViews();
        initDialogEvent();
        setViewData();
    }

    private void findViews() {
        rl_dismiss = findViewById(R.id.rl_dismiss);
        tv_title = findViewById(R.id.tv_title);
        tv_bt_ok = findViewById(R.id.tv_ok);
        tv_bt_cancel = findViewById(R.id.tv_cancel);
        et_description = findViewById(R.id.et_description);
        iv_photo = findViewById(R.id.iv_photo);
        iv_close = findViewById(R.id.iv_close);
        v_loading = findViewById(R.id.v_loading);
    }

    private void setViewData() {
    }

    public void setImageData(LocationService locationService, Bitmap bitmap) {
        iv_photo.setImageBitmap(bitmap);
        this.locationService = locationService;
        this.bitmap = bitmap;
    }

    private void initDialogEvent() {
        rl_dismiss.setOnClickListener(this);
        tv_bt_ok.setOnClickListener(this);
        tv_bt_cancel.setOnClickListener(this);
        iv_close.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_dismiss:
            case R.id.iv_close:
                dismiss();
                break;
            case R.id.tv_cancel:
                dismiss();
                break;
            case R.id.tv_ok:
                if (eventListener != null)
                    eventListener.onOk();
                SendPhoto();
                break;
            default:
                break;
        }
    }

    private void SendPhoto() {
        try {
            if (et_description.getText().toString().contentEquals("")) {
                ToastHelper.showShortToast("Vui lòng điền chú thích trước khi thêm ảnh");
                return;
            }

            v_loading.setVisibility(View.VISIBLE);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
            byte[] byteArray = byteArrayOutputStream.toByteArray();
            String encoded = Base64.encodeToString(byteArray, Base64.DEFAULT);

            HashMap<String, Object> param = new HashMap<>();
            param.put("logId", UserAccountHelper.getIntance().getLogId());
            param.put("lat", locationService.getLastLocation().getLatitude());
            param.put("lng", locationService.getLastLocation().getLongitude());
            param.put("base64Photo", encoded);
            param.put("description", et_description.getText().toString());
            param.put("key", UserAccountHelper.getIntance().getSecureKey());

            Observable<ApiResult<Photo>> uploadPhoto = ApiUtils.getAPIBase().upLoadPhoto(param);
            uploadPhoto.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(new DisposableObserver<ApiResult<Photo>>() {
                        @Override
                        public void onNext(ApiResult<Photo> result) {
                            if (result.isResult()) {
                                ToastHelper.showShortToast("Thêm ảnh thành công!");
                                dismiss();
                            } else ToastHelper.showShortToast(result.getMessage());
                        }

                        @Override
                        public void onError(Throwable e) {
                            Log.e(TAG, "onError: " + e.getMessage());
                            ToastHelper.showShortToast("Xảy ra lỗi: " + e.getMessage());
                        }

                        @Override
                        public void onComplete() {
                            v_loading.setVisibility(View.GONE);
                        }
                    });

        } catch (Exception ex) {
            ToastHelper.showShortToast("Có lỗi: " + ex.getMessage());
            v_loading.setVisibility(View.GONE);
        }
    }


    public interface IDidalogEvent {
        void onOk();
    }

    private IDidalogEvent eventListener;

    public void setIDidalogEventListener(IDidalogEvent listener) {
        eventListener = listener;
    }
}

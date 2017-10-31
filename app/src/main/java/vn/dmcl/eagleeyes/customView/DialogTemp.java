package vn.dmcl.eagleeyes.customView;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Base64;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;

import java.io.ByteArrayOutputStream;

import vn.dmcl.eagleeyes.R;
import vn.dmcl.eagleeyes.common.AppConst;
import vn.dmcl.eagleeyes.common.FunctionConst;
import vn.dmcl.eagleeyes.dto.DCheckLogDTO;
import vn.dmcl.eagleeyes.dto.ResultDTO;
import vn.dmcl.eagleeyes.helper.DataServiceProvider;
import vn.dmcl.eagleeyes.helper.JsonHelper;
import vn.dmcl.eagleeyes.helper.ToastHelper;
import vn.dmcl.eagleeyes.helper.UserAccountHelper;
import vn.dmcl.eagleeyes.service.LocationService;

public class DialogTemp extends Dialog implements View.OnClickListener {

    private FragmentActivity activity;
    private boolean fullScreen;
    private RelativeLayout rl_dismiss;
    private TextView tv_bt_ok, tv_bt_cancel;

    private TextView tv_title;
    private ImageView iv_photo, iv_close;
    private EditText et_description;
    private RelativeLayout v_loading;
    private LocationService locationService;

    public static DialogTemp getInstance(FragmentActivity a, boolean isFullScreen) {
        return new DialogTemp(a, isFullScreen);
    }

    private DialogTemp(FragmentActivity a, boolean isFullScreen) {
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

        findViews();
        initDialogEvent();
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
    }

    private void setViewData() {
    }

    public void setImageData(LocationService locationService, Bitmap bitmap) {
        iv_photo.setImageBitmap(bitmap);
        this.locationService = locationService;
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
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            Bitmap bitmap = ((BitmapDrawable) iv_photo.getDrawable()).getBitmap();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
            byte[] byteArray = byteArrayOutputStream.toByteArray();
            String encoded = Base64.encodeToString(byteArray, Base64.DEFAULT);
            DataServiceProvider<ResultDTO<DCheckLogDTO>> UploadLocationPhoto = new DataServiceProvider<>(new TypeToken<ResultDTO<DCheckLogDTO>>() {
            }.getType());
            UploadLocationPhoto.setListener(new DataServiceProvider.OnListenerReponse<ResultDTO<DCheckLogDTO>>() {
                @Override
                public void onSuccess(ResultDTO<DCheckLogDTO> responseData) {

                }

                @Override
                public void onFailure(String errorMessage) {

                }
            });
            UploadLocationPhoto.getData(FunctionConst.UploadLocationPhoto, AppConst.AsyncMethod.GET,
                    JsonHelper.getIntance().UploadLocationPhoto(UserAccountHelper.getIntance().getLogId(),
                            locationService.getLastLocation().getLatitude(),
                            locationService.getLastLocation().getLongitude(),
                            encoded, et_description.getText().toString(),
                            UserAccountHelper.getIntance().getSecureKey()));
        } catch (Exception ex) {
            ToastHelper.showShortToast("Có lỗi: " + ex.getMessage());
        }
    }


    public interface IDidalogEvent {
        void onOk();
    }

    private DialogPhoto.IDidalogEvent eventListener;

    public void setIDidalogEventListener(DialogPhoto.IDidalogEvent listener) {
        eventListener = listener;
    }
}

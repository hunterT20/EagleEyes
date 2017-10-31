package vn.dmcl.eagleeyes.helper;

import android.content.Intent;
import android.provider.Settings;

import com.google.gson.Gson;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;

import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.entity.ByteArrayEntity;
import vn.dmcl.eagleeyes.common.AppConst;
import vn.dmcl.eagleeyes.dto.DataObjectSend;
import vn.dmcl.eagleeyes.dto.ResultDTO;
import vn.dmcl.eagleeyes.view.BaseActivity;
import vn.dmcl.eagleeyes.view.BaseApplication;
import vn.dmcl.eagleeyes.view.Login.LoginActivity;

public class DataServiceProvider<T> {

    private Type myType = null;
    private String functionName = "";
    private AppConst.AsyncMethod method = AppConst.AsyncMethod.GET;
    private HttpEntity entity = null;
    private UrlGenerationHelper UrlHelper = new UrlGenerationHelper();
    private JSONObject dataObject;

    public boolean isLoading() {
        return isLoading;
    }

    private boolean isLoading = false;

    public DataServiceProvider(Type type) {
        myType = type;
    }

    private void generateParameter(JSONObject trjsonObject) {
    }

    public void getData(String functionKey, AppConst.AsyncMethod mMethod, JSONObject jsonObject, boolean... isCheckInternet) {
        if (isCheckInternet.length == 0)
            if (!checkInternetConnection())
                return;
        getData(functionKey, mMethod, jsonObject);
    }

    public void getData(String functionKey, AppConst.AsyncMethod method, JSONObject jsonObject) {
        isLoading = true;
        functionName = functionKey;
        UrlHelper.functionName = functionKey;
        if (dataObject == null)
            dataObject = jsonObject;

        if (method == AppConst.AsyncMethod.POST) {
            method = AppConst.AsyncMethod.POST;
            try {
                entity = new ByteArrayEntity(jsonObject.toString().getBytes("UTF-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        } else {
            UrlHelper.setMap(jsonObject);
            generateParameter(jsonObject);
        }

        HttpAsyncClientHelper.getIntance().execute(UrlHelper.generateUrl(), method, entity, new HttpAsyncClientHelper.AsyncListenerResponse() {
            @Override
            public void onSuccess(String responseData) {
                isLoading = false;
                String strEncode = JsonHelper.fixStringToJson(responseData.substring(1,
                        responseData.length() - 1));
                Gson gson = new Gson();

                T temp = null;
                try {
                    temp = gson.fromJson(strEncode, myType);
                } catch (Exception ex) {
                    if (listener != null)
                        listener.onFailure(ex.getMessage());
                    reducePageIndex();
                    return;
                }
                if (listener != null) {
                    try {
                        if (temp instanceof ResultDTO)
                            if (((ResultDTO) temp).getCode() == AppConst.SessionEnded) {
                                UserAccountHelper.getIntance().setSecureKey("");
                                DialogHelper.showAlertDialog(BaseActivity.getCurrentActivity(), "Cảnh báo", "Bạn đã hết phiên đăng nhập. Vui lòng đăng nhập lại!", "OK", new DialogHelper.onListenerOneButtonClick() {
                                    @Override
                                    public void onButtonClick() {
                                        Intent intent = new Intent(BaseActivity.getCurrentActivity(), LoginActivity.class);
                                        BaseActivity.getCurrentActivity().finish();
                                        BaseActivity.getCurrentActivity().startActivity(intent);
                                    }
                                });
                            }

                    } catch (Exception ignored) {
                    }
                    listener.onSuccess(temp);
                    checkResultError(strEncode);
                }


            }

            @Override
            public void onFailure(String errorMessage) {
                if (listener != null) {
                    listener.onFailure(errorMessage);
                    isLoading = false;
                }
                reducePageIndex();
            }
        });
    }

    public void postData(String functionKey, AppConst.AsyncMethod mMethod, DataObjectSend data) {
        if (!checkInternetConnection())
            return;
        isLoading = true;
        //UrlHelper.ClearData();
        functionName = functionKey;
        UrlHelper.functionName = functionKey;
        //UrlHelper.ClearData();

        if (mMethod == AppConst.AsyncMethod.POST) {
            method = AppConst.AsyncMethod.POST;
            try {
                entity = new ByteArrayEntity(new Gson().toJson(data).getBytes("UTF-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            //entity = new cz.msebera.android.httpclient.entity.StringEntity(jsonObject.toString(), "UTF-8");
        } else {
            //UrlHelper.setMap(jsonObject);
            //generateParameter(jsonObject);
        }


        HttpAsyncClientHelper.getIntance().execute(UrlHelper.generateUrl(), method, entity, new HttpAsyncClientHelper.AsyncListenerResponse() {
            @Override
            public void onSuccess(String responseData) {
                try {
                    isLoading = false;
                    String strEncode = JsonHelper.fixStringToJson(responseData.substring(1,
                            responseData.length() - 1));
                    Gson gson = new Gson();


                    T temp = gson.fromJson(strEncode, myType);
                    if (listener != null) {
                        try {
                            /*if(temp instanceof Result)
                                if(((Result)temp).getErrorCode() == 7)
                                {
                                    LoginManager.getInstance().logOut();
                                    UserAccountHelper.getInstance().setUserID(-1);
                                    BusHelper.getInstance().setKeySecure(null);
                                    UserAccountHelper.getInstance().clearAll();
                                }
                            if(temp instanceof PageResult)
                                if(((PageResult)temp).getErrorCode() == 7)
                                {
                                    LoginManager.getInstance().logOut();
                                    UserAccountHelper.getInstance().setUserID(-1);
                                    BusHelper.getInstance().setKeySecure(null);
                                    UserAccountHelper.getInstance().clearAll();
                                }*/
                        } catch (Exception ignored) {
                        }
                        listener.onSuccess(temp);
                        checkResultError(strEncode);
                    }

                } catch (Exception ex) {
                    if (listener != null)
                        listener.onFailure(ex.getMessage());
                    reducePageIndex();
                }
            }

            @Override
            public void onFailure(String errorMessage) {
                if (listener != null) {
                    listener.onFailure(errorMessage);
                    isLoading = false;
                }
                reducePageIndex();
            }
        });
    }

    private void reducePageIndex() {}

    private void checkResultError(String result) {}

    private boolean checkInternetConnection() {
        if (!NetworkHelper.checkNetworkConnection()) {
            DialogHelper.showAlertDialog(BaseActivity.getCurrentActivity(), "Không thể kết nối tới máy chủ", "Vui lòng kiểm tra kết nối internet!", "Hủy", "Mở kết nối", new DialogHelper.onListenerTwoButtonClick() {
                @Override
                public void onLeftButtonClick() {
                    checkInternetConnection();
                }

                @Override
                public void onRightButtonClick() {
                    BaseApplication.getContext().startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                }
            });
            return false;
        }
        return true;
    }

    public interface OnListenerReponse<T> {
        void onSuccess(T responseData);

        void onFailure(String errorMessage);
    }

    public void setListener(OnListenerReponse<T> listener) {
        this.listener = listener;
    }

    private OnListenerReponse<T> listener;
}

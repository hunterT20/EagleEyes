package vn.dmcl.eagleeyes.helper;

import android.annotation.SuppressLint;
import android.content.Context;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import java.io.UnsupportedEncodingException;

import cz.msebera.android.httpclient.HttpEntity;
import vn.dmcl.eagleeyes.common.AppConst;

class HttpAsyncClientHelper {
    @SuppressLint("StaticFieldLeak")
    private static Context context;
    @SuppressLint("StaticFieldLeak")
    private static HttpAsyncClientHelper _HttpAsyncClientHelper;

    static HttpAsyncClientHelper getIntance() {
        //if(_HttpAsyncClientHelper == null)
        _HttpAsyncClientHelper = new HttpAsyncClientHelper();
        return _HttpAsyncClientHelper;
    }

    private AsyncHttpClient asyncHttpClient;

    private HttpAsyncClientHelper() {
        asyncHttpClient = new AsyncHttpClient();
        asyncHttpClient.setTimeout(5 * 1000);
        //asyncHttpClient.setMaxRetriesAndTimeout(10,60);
    }

    void execute(String url, AppConst.AsyncMethod method, HttpEntity httpEntity, AsyncListenerResponse asyncListenerResponse) {
        listener = asyncListenerResponse;
        switch (method) {
            case GET:
                asyncHttpClient.get(url, new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {
                        if (listener != null) {
                            try {
                                listener.onSuccess(new String(responseBody, "UTF-8"));
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {
                        if (listener != null) {
                            if (error.getMessage() == null || error.getMessage().equals(""))
                                listener.onFailure("Time out");
                            else
                                listener.onFailure(error.getMessage());
                        }
                    }
                });
                break;
            case POST:
                asyncHttpClient.post(context, url, httpEntity, "application/json", new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {
                        if (listener != null) {
                            try {
                                listener.onSuccess(new String(responseBody, "UTF-8"));
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {
                        if (listener != null) {
                            if (error.getMessage() == null || error.getMessage().equals(""))
                                listener.onFailure("Time out");
                            else
                                listener.onFailure(error.getMessage());
                        }
                    }

                });
                break;
        }
    }
    /*public void post(String url, ByteArrayEntity byteArrayEntity, AsyncListenerResponse asyncListenerResponse) {
        listener = asyncListenerResponse;
        asyncHttpClient.post(context, url, byteArrayEntity, "application/json", new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {
                if (listener != null) {
                    try {
                        listener.onSuccess(new String(responseBody, "UTF-8"));
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {
                if (listener != null) {
                    if (error.getMessage() == null || error.getMessage().equals(""))
                        listener.onFailure("Time out");
                    else
                        listener.onFailure(error.getMessage());
                }
            }
        });
    }*/

    public interface AsyncListenerResponse {
        void onSuccess(String responseData);

        void onFailure(String errorMessage);
    }

    private AsyncListenerResponse listener;
}

package vn.dmcl.eagleeyes.data.remote;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.net.CookieManager;
import java.net.CookiePolicy;
import java.util.concurrent.TimeUnit;

import okhttp3.JavaNetCookieJar;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

class RetrofitClient {
    private static Retrofit retrofitBase = null;
    private static Retrofit retrofitMap = null;

    private static HttpLoggingInterceptor loggingInterceptor = null;
    private static CookieManager cookieManager = null;
    private static OkHttpClient.Builder client = null;

    private static Gson gson = new GsonBuilder()
            .setLenient()
            .create();

    private static void initHttpLogging(){
        if (loggingInterceptor == null){
            loggingInterceptor = new HttpLoggingInterceptor();
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        }
    }

    private static void initCookieManager(){
        if (cookieManager == null){
            cookieManager = new CookieManager();
            cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
        }
    }

    private static void initClient(){
        if (client == null){
            client = new OkHttpClient.Builder()
                    .connectTimeout(1, TimeUnit.MINUTES)
                    .writeTimeout(1, TimeUnit.MINUTES)
                    .readTimeout(1, TimeUnit.MINUTES)
                    .cookieJar(new JavaNetCookieJar(cookieManager))
                    .addInterceptor(loggingInterceptor);
        }
    }

    static Retrofit getClientBase(String baseUrl) {
        if (retrofitBase == null) {
            initHttpLogging();
            initCookieManager();
            initClient();
            retrofitBase = new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .client(client.build())
                    .build();
        }
        return retrofitBase;
    }

    static Retrofit getClientMap(String mapUrl) {
        if (retrofitMap == null) {
            initHttpLogging();
            initCookieManager();
            initClient();
            retrofitMap = new Retrofit.Builder()
                    .baseUrl(mapUrl)
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .client(client.build())
                    .build();
        }
        return retrofitMap;
    }
}

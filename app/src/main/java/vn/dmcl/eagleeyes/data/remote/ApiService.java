package vn.dmcl.eagleeyes.data.remote;

import java.util.HashMap;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import vn.dmcl.eagleeyes.data.model.ApiListResult;
import vn.dmcl.eagleeyes.data.model.ApiResult;
import vn.dmcl.eagleeyes.data.model.AreaFlyer;
import vn.dmcl.eagleeyes.data.model.Config;
import vn.dmcl.eagleeyes.data.model.DCheckManage;
import vn.dmcl.eagleeyes.data.model.FlyerLog;
import vn.dmcl.eagleeyes.data.model.Photo;
import vn.dmcl.eagleeyes.data.model.Session;

public interface ApiService {
    @POST("CheckKey")
    Observable<ApiResult<Session>> checkKey(@Body HashMap<String, String> param);

    @POST("Login")
    Observable<ApiResult<Session>> login(@Body HashMap<String,String> param);

    @POST("GetListConfig")
    Observable<ApiListResult<Config>> getConfig(@Body HashMap<String, Object> param);

    @POST("GetListArea")
    Observable<ApiResult<AreaFlyer>> getListArea(@Body HashMap<String,String> param);

    @GET("sendlocation")
    Observable<Object> sentRealTimeLocation(
            @Query("id") String id,
            @Query("lat") Double lat,
            @Query("log") Double log,
            @Query("name") String name,
            @Query("phone") String phone,
            @Query("branch") String branch
    );

    @POST("SendLocation")
    Observable<ApiResult<FlyerLog>> sendLocation(@Body HashMap<String, Object> param);

    @POST("StartLog")
    Observable<ApiResult<FlyerLog>> startLog(@Body HashMap<String, Object> param);

    @POST("StopLog")
    Observable<ApiResult<FlyerLog>> stopLog(@Body HashMap<String, Object> param);

    @POST("GetListUser")
    Observable<ApiResult<DCheckManage>> getListUser(@Body HashMap<String, Object> param);

    @POST("GetListLocation")
    Observable<ApiResult<FlyerLog>> getListLocation(@Body HashMap<String, Object> param);

    @POST("UploadLocationPhoto")
    Observable<ApiResult<Photo>> upLoadPhoto(@Body HashMap<String, Object> param);
}

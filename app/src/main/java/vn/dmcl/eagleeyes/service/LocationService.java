package vn.dmcl.eagleeyes.service;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.format.Time;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.gson.reflect.TypeToken;

import vn.dmcl.eagleeyes.common.AppConst;
import vn.dmcl.eagleeyes.common.FunctionConst;
import vn.dmcl.eagleeyes.dto.FlyerLogDTO;
import vn.dmcl.eagleeyes.dto.ResultDTO;
import vn.dmcl.eagleeyes.helper.DataServiceProvider;
import vn.dmcl.eagleeyes.helper.DialogHelper;
import vn.dmcl.eagleeyes.helper.JsonHelper;
import vn.dmcl.eagleeyes.helper.ToastHelper;
import vn.dmcl.eagleeyes.helper.UserAccountHelper;
import vn.dmcl.eagleeyes.view.BaseActivity;

public class LocationService extends Service implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {
    private static final String TAG = "BOOMBOOMTESTGPS";
    Time sentTime;
    boolean isStartRecord = false;
    private double currentDistance = 0;

    private static final long UPDATE_INTERVAL = 5000;
    private static final long FASTEST_INTERVAL = 5000;
    private static final int REQUEST_LOCATION_PERMISSION = 100;

    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private Location mLastLocation;
    private FusedLocationProviderClient client;


    IBinder mBinder = new LocalBinder();

    public class LocalBinder extends Binder {
        public LocationService getServiceInstance() {
            return LocationService.this;
        }
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return mBinder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //Log.e(TAG, "onStartCommand");
        super.onStartCommand(intent, flags, startId);
        // giup service khoi tao lai neu bi giet
        return START_STICKY;
    }

    Location prevLocation;
    Handler mainHander;

    // tinh khoang cach giua thay doi
    // cap nhat thoi gian cho
    @SuppressLint("HandlerLeak")
    public void calcuDistance() {
        mainHander = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                try {
                    sendEmptyMessageDelayed(0, 5000);
                    if (!isStartRecord) {
                        return;
                    }
                    if (sentTime == null) {
                        sentTime = new Time();
                        sentTime.setToNow();
                        currentDistance = 0;
                    } else {
                        Time currentTime = new Time();
                        currentTime.setToNow();
                        if (currentTime.toMillis(false) - sentTime.toMillis(false) > AppConst.MaxTimeUpdateLocation) {
                            // goi du lieu len server
                            sentTime = currentTime;
                            currentDistance = 0;
                            sendLocationToSV();
                        }
                    }
                    if (prevLocation == null) {
                        Location lastLocation = getLastLocation();
                        prevLocation = new Location("dummyprovider");
                        prevLocation.setLatitude(lastLocation.getLatitude());
                        prevLocation.setLongitude(lastLocation.getLongitude());
                    } else {
                        Location lastLocation = getLastLocation();
                        if (lastLocation != null) {
                            if (lastLocation.getLatitude() == 0 && lastLocation.getLongitude() == 0) {
                                DialogHelper.showAlertDialog(BaseActivity.getCurrentActivity(), "Mở định vị", "Vui lòng mở định vị để bắt đầu!", "Mở GPS", new DialogHelper.onListenerOneButtonClick() {
                                    @Override
                                    public void onButtonClick() {
                                        Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                        myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(myIntent);
                                    }
                                }, false);
                            }
                            currentDistance += lastLocation.distanceTo(prevLocation);
                            if (currentDistance > AppConst.MaxDistances) {
                                // goi du lieu len server
                                // cap nhat lai vi tri va thoi gian cuoi cung
                                sentTime.setToNow();
                                sendLocationToSV();
                                currentDistance = 0;

                            } else {
                                Log.e("CON", "Vi tri thay doi! " + lastLocation.getLatitude() + "-" + lastLocation.getLongitude());
                                Log.e("CON", "Khoang cach: " + currentDistance);
                            }
                            prevLocation.setLatitude(lastLocation.getLatitude());
                            prevLocation.setLongitude(lastLocation.getLongitude());
                        }
                    }
                } catch (Exception ex) {
                    Log.e(TAG, "handleMessage: " + ex);
                }

            }
        };
        mainHander.sendEmptyMessageDelayed(0, 5000);
    }

    public void sendLocationToSV() {
        DataServiceProvider<ResultDTO<FlyerLogDTO>> SendLocation = new DataServiceProvider<>(new TypeToken<ResultDTO<FlyerLogDTO>>() {
        }.getType());
        SendLocation.setListener(new DataServiceProvider.OnListenerReponse<ResultDTO<FlyerLogDTO>>() {
            @Override
            public void onSuccess(ResultDTO<FlyerLogDTO> responseData) {
                if (!responseData.isResult())
                    ToastHelper.showShortToast("Lỗi: " + responseData.getMessage());
            }

            @Override
            public void onFailure(String errorMessage) {
                ToastHelper.showShortToast(errorMessage);
            }
        });
        SendLocation.getData(FunctionConst.SendLocation, AppConst.AsyncMethod.GET,
                JsonHelper.getIntance().SendLocation(UserAccountHelper.getIntance().getLogId(),
                        UserAccountHelper.getIntance().getSecureKey(),
                        getLastLocation().getLatitude(),
                        getLastLocation().getLongitude()
                ));

    }

    // tinh vi tri cuoi cung thay doi cua GPS hay mang
    public Location getLastLocation() {
        return mLastLocation;
    }

    public void startRecord() {
        if (sentTime == null) {
            sentTime = new Time();
            sentTime.setToNow();
        }
        isStartRecord = true;
        sendLocationToSV();
    }

    public void stopRecord() {
        if (sentTime == null) {
            sentTime = new Time();
            sentTime.setToNow();
        }
        isStartRecord = false;
    }

    private boolean isPlayServicesAvailable() {
        return GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(BaseActivity.getCurrentActivity())
                == ConnectionResult.SUCCESS;
    }

    private boolean isGpsOn() {
        LocationManager manager = (LocationManager) BaseActivity.getCurrentActivity().getSystemService(LOCATION_SERVICE);
        assert manager != null;
        return manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    private void setUpLocationClientIfNeeded() {
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(BaseActivity.getCurrentActivity())
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        if (client == null){
            client = LocationServices.getFusedLocationProviderClient(BaseActivity.getCurrentActivity());
        }
    }

    private void buildLocationRequest() {
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
    }

    private LocationCallback callback = new LocationCallback(){
        @Override
        public void onLocationResult(LocationResult locationResult) {
            super.onLocationResult(locationResult);
            mLastLocation = locationResult.getLastLocation();
        }
    };

    protected void startLocationUpdates() {
        if (ContextCompat.checkSelfPermission(BaseActivity.getCurrentActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(BaseActivity.getCurrentActivity(),
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        client.requestLocationUpdates(mLocationRequest,callback,null);
    }

    protected void stopLocationUpdates() {
        client.removeLocationUpdates(callback);
    }

    @Override
    public void onConnected(Bundle bundle) {
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(BaseActivity.getCurrentActivity(),
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        client.getLastLocation().addOnSuccessListener(BaseActivity.getCurrentActivity(), location -> {
            if (location != null) {
                if (!isGpsOn()){
                    Toast.makeText(BaseActivity.getCurrentActivity(), "Bạn chưa mở GPS! Mở GPS để xác định địa điểm chính xác!", Toast.LENGTH_SHORT).show();
                    return;
                }
                mLastLocation = location;
                startLocationUpdates();
                calcuDistance();
            }
        });
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e(TAG, "onConnectionFailed: " + connectionResult.getErrorMessage());
    }

    @Override
    public void onCreate() {
        if (isPlayServicesAvailable()) {
            setUpLocationClientIfNeeded();
            buildLocationRequest();
        }

        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mGoogleApiClient != null
                && mGoogleApiClient.isConnected()) {
            stopLocationUpdates();
            mGoogleApiClient.disconnect();
            mGoogleApiClient = null;
        }
    }
}

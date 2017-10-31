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
import android.support.v4.app.ActivityCompat;
import android.text.format.Time;
import android.util.Log;

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

public class LocationService extends Service {
    private static final String TAG = "BOOMBOOMTESTGPS";
    private LocationManager mLocationManager = null;
    private static final int LOCATION_INTERVAL = 500;
    private static final float LOCATION_DISTANCE = 15f;
    Time sentTime;
    boolean isStartRecord = false;
    private double currentDistance = 0;

    IBinder mBinder = new LocalBinder();

    public class LocalBinder extends Binder {
        public LocationService getServiceInstance() {
            return LocationService.this;
        }
    }

    public LocaListenner mListenner;

    private class LocationListener implements android.location.LocationListener {
        Location mLastLocation;

        LocationListener(String provider) {
            //Log.e(TAG, "LocationListener " + provider);
            mLastLocation = new Location(provider);
        }

        @Override
        public void onLocationChanged(Location location) {
            Log.e(TAG, "onLocationChanged: " + location);
            mLastLocation.set(location);
            if (mListenner != null)
                mListenner.onLocationChanged(location);
        }

        @Override
        public void onProviderDisabled(String provider) {
            //Log.e(TAG, "onProviderDisabled: " + provider);
        }

        @Override
        public void onProviderEnabled(String provider) {
            //Log.e(TAG, "onProviderEnabled: " + provider);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            //Log.e(TAG, "onStatusChanged: " + provider);
        }
    }

    LocationListener[] mLocationListeners = new LocationListener[]{
            new LocationListener(LocationManager.GPS_PROVIDER),
            new LocationListener(LocationManager.NETWORK_PROVIDER)
    };

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

    @Override
    public void onCreate() {
        Log.e(TAG, "onCreate");
        initializeLocationManager();
        try {
            mLocationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
                    mLocationListeners[1]);
        } catch (SecurityException ex) {
            Log.i(TAG, "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.d(TAG, "network provider does not exist, " + ex.getMessage());
        }
        try {
            mLocationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
                    mLocationListeners[0]);
        } catch (SecurityException ex) {
            Log.i(TAG, "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.d(TAG, "gps provider does not exist " + ex.getMessage());
        }
        calcuDistance();
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
        if (mLocationListeners[0].mLastLocation != null) {
            if (mLocationListeners[0].mLastLocation.getLatitude() != 0 ||
                    mLocationListeners[0].mLastLocation.getLongitude() != 0)
                return mLocationListeners[0].mLastLocation;
            else return mLocationListeners[1].mLastLocation;
        } else return mLocationListeners[1].mLastLocation;
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

    @Override
    public void onDestroy() {
        Log.e(TAG, "onDestroy");
        super.onDestroy();
        if (mLocationManager != null) {
            for (LocationListener mLocationListener : mLocationListeners) {
                try {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    mLocationManager.removeUpdates(mLocationListener);
                } catch (Exception ex) {
                    Log.i(TAG, "fail to remove location listners, ignore", ex);
                }
            }
        }
    }

    public void setLocationListenner(LocaListenner listenner) {
        mListenner = listenner;
    }

    private void initializeLocationManager() {
        Log.e(TAG, "initializeLocationManager");
        if (mLocationManager == null) {
            mLocationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        }
    }

    public interface LocaListenner {
        void onLocationChanged(Location location);
    }
}

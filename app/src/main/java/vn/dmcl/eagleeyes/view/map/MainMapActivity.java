package vn.dmcl.eagleeyes.view.map;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.reflect.TypeToken;
import com.google.maps.android.ui.IconGenerator;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.fabric.sdk.android.Fabric;
import vn.dmcl.eagleeyes.R;
import vn.dmcl.eagleeyes.common.AppConst;
import vn.dmcl.eagleeyes.common.FunctionConst;
import vn.dmcl.eagleeyes.customView.DialogPhoto;
import vn.dmcl.eagleeyes.customView.DrawerFragment;
import vn.dmcl.eagleeyes.dto.AreaDTO;
import vn.dmcl.eagleeyes.dto.AreaFDTO;
import vn.dmcl.eagleeyes.dto.DCheckManageDTO;
import vn.dmcl.eagleeyes.dto.DCheckManageFlyerDTO;
import vn.dmcl.eagleeyes.dto.FlyerLogDTO;
import vn.dmcl.eagleeyes.dto.LocationDTO;
import vn.dmcl.eagleeyes.dto.ResultDTO;
import vn.dmcl.eagleeyes.helper.DataServiceProvider;
import vn.dmcl.eagleeyes.helper.DateTimeHelper;
import vn.dmcl.eagleeyes.helper.JsonHelper;
import vn.dmcl.eagleeyes.helper.MarkerHelper;
import vn.dmcl.eagleeyes.helper.ToastHelper;
import vn.dmcl.eagleeyes.helper.UserAccountHelper;
import vn.dmcl.eagleeyes.service.LocationService;
import vn.dmcl.eagleeyes.view.BaseActivity;
import vn.dmcl.eagleeyes.view.login.LoginActivity;

import static vn.dmcl.eagleeyes.customView.DrawerFragment.MenuListener;
import static vn.dmcl.eagleeyes.customView.DrawerFragment.NavCallback;

public class MainMapActivity extends BaseActivity implements OnMapReadyCallback, NavCallback, MenuListener {
    LocationService locationService;
    GoogleMap googleMap;
    Menu menu;

    @BindView(R.id.drawer)          DrawerLayout drawer;
    @BindView(R.id.v_loading)       RelativeLayout v_loading;
    @BindView(R.id.tv_currentArea)  TextView tv_currentArea;
    @BindView(R.id.ll_marker_des)   LinearLayout ll_marker_des;
    @BindView(R.id.fab_TakePhoto)   FloatingActionButton fab_TakePhoto;
    @BindView(R.id.toolbar)         Toolbar toolbar;

    private ActionBarDrawerToggle mDrawerToggle;
    DrawerFragment drawerFragment;
    Circle currentCircle;
    Marker currentMarker;
    MapFragment mapFragment;

    List<AreaDTO> areaDTOList;
    List<DCheckManageFlyerDTO> dCheckManageFlyerDTOs;
    List<Circle> currentDCircle;
    List<Marker> currentDMarker;
    List<Marker> MarkersText = new ArrayList<>();

    Polyline currentPolyline;
    private int count = 0;
    private boolean doubleBackToExitPressedOnce = false;
    private boolean isStart = false;
    private boolean isFlyer;

    ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceDisconnected(ComponentName name) {
            locationService = null;
        }

        public void onServiceConnected(ComponentName name, IBinder service) {
            LocationService.LocalBinder mLocalBinder = (LocationService.LocalBinder) service;
            locationService = mLocalBinder.getServiceInstance();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_map);
        final Fabric fabric = new Fabric.Builder(this)
                .kits(new Crashlytics())
                .debuggable(true)
                .build();
        Fabric.with(fabric);
        ButterKnife.bind(this);
        MapsInitializer.initialize(getApplicationContext());
        isFlyer = getIntent().getBooleanExtra("IsFlyer", true);

        addViews();
        requestPermission();
        connectoService();

        setupDrawerLayout();
        if (savedInstanceState == null) {
            if (isFlyer)
                LoadFlyerData();
            else {
                currentDMarker = new ArrayList<>();
                currentDCircle = new ArrayList<>();
                LoadDCheckData();
            }
            drawerFragment = DrawerFragment.newInstance(isFlyer);
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.nav_container, drawerFragment, "Drawer")
                    .commit();
            drawerFragment.setMenuListener(this);
        }
    }

    //region Xử lý UI
    private void addViews() {
        mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @OnClick(R.id.fab_TakePhoto)
    public void onTakePhotoClick() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, AppConst.CameraRequestCode);
    }

    private void setupDrawerLayout() {
        // Instantiate the Drawer Toggle
        mDrawerToggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.app_name, R.string.app_name) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu();
                if (getSupportActionBar() == null) return;
                getSupportActionBar().setTitle(getString(R.string.app_name));
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                invalidateOptionsMenu();
                if (getSupportActionBar() == null) return;
                getSupportActionBar().setTitle(getString(R.string.app_name));
            }

            @Override
            public boolean onOptionsItemSelected(MenuItem item) {
                if (item != null && item.getItemId() == R.id.btnMyMenu) {
                    if (!isStart) {
                        startRecordLocation();

                    } else {
                        stopRecordLocation();
                    }
                    return true;
                } else return super.onOptionsItemSelected(item);
            }
        };

        drawer.addDrawerListener(mDrawerToggle);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() == null) return;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu);
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {

        this.googleMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        googleMap.setMyLocationEnabled(true);
        googleMap.getUiSettings().setMapToolbarEnabled(false);

        if (locationService != null) {
            Location last = locationService.getLastLocation();
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(last.getLatitude(), last.getLongitude())));
        } else {
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(10.754252, 106.664683), 15));
        }

        googleMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                return null;
            }

            @SuppressLint("SetTextI18n")
            @Override
            public View getInfoContents(Marker marker) {
                if (marker.getTag() instanceof LocationDTO) {
                    LocationDTO locationDTO = (LocationDTO) marker.getTag();
                    @SuppressLint("InflateParams") View view = getLayoutInflater().inflate(R.layout.map_marker_popupinfo, null);
                    TextView tv_title = view.findViewById(R.id.tv_title);
                    TextView tv_content = view.findViewById(R.id.tv_content);

                    if (currentDMarker.indexOf(marker) == 0)
                        tv_title.setText("Điểm bắt đầu");
                    else if (currentDMarker.indexOf(marker) == currentDMarker.size() - 1)
                        tv_title.setText("Điểm kết thúc");
                    else tv_title.setText("Điểm số " + (currentDMarker.indexOf(marker) + 1));
                    tv_content.setText("Thời gian " + DateTimeHelper.convertSecondToTimeString(locationDTO.getTime()));

                    return view;
                }

                return null;
            }
        });

        googleMap.setOnCameraMoveListener(() -> {
            CameraPosition cameraPosition = googleMap.getCameraPosition();
            int MaxCamera = 19, MinCamera = 17;
            if (cameraPosition.zoom < MinCamera)
                for (int i = 0; i < MarkersText.size(); i++)
                    MarkersText.get(i).setAlpha(0);
            if (cameraPosition.zoom >= MinCamera && cameraPosition.zoom < MaxCamera) {
                float alpha = (cameraPosition.zoom - MinCamera) / (MaxCamera - MinCamera);
                for (int i = 0; i < MarkersText.size(); i++)
                    MarkersText.get(i).setAlpha(alpha);
            } else if (cameraPosition.zoom > MaxCamera)
                for (int i = 0; i < MarkersText.size(); i++)
                    MarkersText.get(i).setAlpha(1);
        });
    }


    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.my_right_side_menu, menu);
        this.menu = menu;
        if (isStart) {
            this.menu.getItem(0).setIcon(R.drawable.ic_stop);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        return mDrawerToggle.onOptionsItemSelected(item) || id == R.id.new_game || super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce || drawer.isDrawerOpen(GravityCompat.START)) {
            if (drawer.isDrawerOpen(GravityCompat.START))
                drawer.closeDrawers();
            else super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        ToastHelper.showShortToast("Nhấn Trở về lần nữa để thoát");

        new Handler().postDelayed(() -> doubleBackToExitPressedOnce = false, 2000);
    }

    @Override
    public void onNavSelected(int position) {
        currentMarker.remove();
        currentCircle.remove();
        currentMarker = googleMap.addMarker(new MarkerOptions()
                .position(new LatLng(drawerFragment.getFlyerAdapter().getItem(position).getLat(), drawerFragment.getFlyerAdapter().getItem(position).getLng()))
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_area))
                .title(drawerFragment.getFlyerAdapter().getItem(position).getName())
        );
        currentCircle = googleMap.addCircle(new CircleOptions()
                .center(new LatLng(drawerFragment.getFlyerAdapter().getItem(position).getLat(), drawerFragment.getFlyerAdapter().getItem(position).getLng()))
                .radius(drawerFragment.getFlyerAdapter().getItem(position).getRadius())
                .strokeColor(Color.argb(99, 37, 144, 255))
                .strokeWidth(4f)
                .fillColor(Color.argb(60, 96, 152, 207)));


        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(drawerFragment.getCurrentArea().getLat(), drawerFragment.getCurrentArea().getLng()), 15));

        drawer.closeDrawers();
    }

    @Override
    public void onNavDCheckSelected(int flyerPosition, int areaPosition) {
        googleMap.clear();
        currentDMarker.clear();
        currentDCircle.clear();
        displayAreasInMap();
        drawer.closeDrawers();
    }


    private void requestPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.CAMERA
            }, AppConst.PerLocaRequestCode);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == AppConst.PerLocaRequestCode) {
            if (permissions[0] == Manifest.permission.ACCESS_FINE_LOCATION &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                googleMap.setMyLocationEnabled(true);
            } else {
                Log.e("ErrorRequest", "onRequestPermissionsResult: " + "Permission was denied. Display an error message.");
            }
        }
    }


    private void connectoService() {
        Intent intent = new Intent(this, LocationService.class);
        bindService(intent, mConnection, BIND_AUTO_CREATE);
    }

    private void startRecordLocation() {
        if (drawerFragment.getCurrentArea() == null) {
            ToastHelper.showShortToast("Vui lòng chọn khu vực trước khi bắt đầu");
            return;
        }
        Location selectArea = new Location("");
        selectArea.setLatitude(drawerFragment.getCurrentArea().getLat());
        selectArea.setLongitude(drawerFragment.getCurrentArea().getLng());
        if (locationService.getLastLocation().distanceTo(selectArea) > AppConst.MaxDistancesApproximately + drawerFragment.getCurrentArea().getRadius()) {
            ToastHelper.showShortToast("Bạn cần đến vùng chỉ định để bắt đầu!");
            return;
        }
        v_loading.setVisibility(View.VISIBLE);
        DataServiceProvider<ResultDTO<FlyerLogDTO>> StartLog = new DataServiceProvider<>(new TypeToken<ResultDTO<FlyerLogDTO>>() {
        }.getType());
        StartLog.setListener(new DataServiceProvider.OnListenerReponse<ResultDTO<FlyerLogDTO>>() {
            @Override
            public void onSuccess(ResultDTO<FlyerLogDTO> responseData) {
                if (responseData.isResult()) {
                    ToastHelper.showShortToast("Bắt đầu ghi nhận!");
                    menu.getItem(0).setIcon(R.drawable.ic_stop);
                    isStart = true;
                    UserAccountHelper.getIntance().setLogId(responseData.getData().getId());
                    locationService.startRecord();
                    drawerFragment.updateCurrentAreaStatus(AppConst.AreaStatus.Started);
                    if (responseData.getData().getArea() != null) {
                        tv_currentArea.setVisibility(View.VISIBLE);
                        tv_currentArea.setText(responseData.getData().getArea().getName());
                    }
                    fab_TakePhoto.setVisibility(View.VISIBLE);
                } else
                    ToastHelper.showShortToast("Bắt đầu thất bại. Lỗi: " + responseData.getMessage());
                v_loading.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(String errorMessage) {
                ToastHelper.showShortToast("Bắt đầu thất bại. Lỗi: " + errorMessage);
                v_loading.setVisibility(View.GONE);
            }
        });
        long UserID = -1;
        if (!isFlyer) UserID = dCheckManageFlyerDTOs.get(0).getFlyerId();
        StartLog.getData(FunctionConst.StartLog, AppConst.AsyncMethod.GET,
                JsonHelper.getIntance().StartLog(UserID, drawerFragment.getCurrentArea().getId(),
                        locationService.getLastLocation().getLatitude(),
                        locationService.getLastLocation().getLongitude(),
                        UserAccountHelper.getIntance().getSecureKey()));
    }

    private void stopRecordLocation() {
        v_loading.setVisibility(View.VISIBLE);
        DataServiceProvider<ResultDTO<FlyerLogDTO>> StopLog = new DataServiceProvider<>(new TypeToken<ResultDTO<FlyerLogDTO>>() {
        }.getType());
        StopLog.setListener(new DataServiceProvider.OnListenerReponse<ResultDTO<FlyerLogDTO>>() {
            @Override
            public void onSuccess(ResultDTO<FlyerLogDTO> responseData) {
                if (responseData.isResult()) {
                    ToastHelper.showShortToast("Kết thúc ghi nhận!");
                    menu.getItem(0).setIcon(R.drawable.ic_start);
                    isStart = false;
                    UserAccountHelper.getIntance().setLogId("");
                    locationService.stopRecord();
                    drawerFragment.updateCurrentAreaStatus(AppConst.AreaStatus.Ended);
                    if (drawerFragment.isCompleteAllArea(isFlyer)) {
                        UserAccountHelper.getIntance().setSecureKey("");
                        startActivity(new Intent(MainMapActivity.this, LoginActivity.class));
                        finish();
                    }
                    tv_currentArea.setVisibility(View.GONE);
                    fab_TakePhoto.setVisibility(View.GONE);
                } else ToastHelper.showShortToast(responseData.getMessage());
                v_loading.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(String errorMessage) {
                ToastHelper.showShortToast(errorMessage);
                v_loading.setVisibility(View.GONE);
            }
        });
        StopLog.getData(FunctionConst.StopLog, AppConst.AsyncMethod.GET,
                JsonHelper.getIntance().StopLog(UserAccountHelper.getIntance().getLogId(),
                        locationService.getLastLocation().getLatitude(),
                        locationService.getLastLocation().getLongitude(),
                        UserAccountHelper.getIntance().getSecureKey()));
    }


    private void displayAreasInMap() {
        final AreaDTO area = drawerFragment.getCurrentArea();
        if (area == null) return;
        if (area.getId() == null) {
            ToastHelper.showShortToast("Bạn đã hoàn thành các khu vực!");
        } else if (area.getStatus() == AppConst.AreaStatus.Started) {
            isStart = true;
            if (menu != null) {
                menu.getItem(0).setIcon(R.drawable.ic_stop);
            }
            new Handler().postDelayed(() -> {
                if (locationService != null)
                    locationService.startRecord();
            }, 1000);
            tv_currentArea.setVisibility(View.VISIBLE);
            tv_currentArea.setText(area.getName());
            fab_TakePhoto.setVisibility(View.VISIBLE);
        }
        if (!isFlyer)
            LoadFlyerArea(area);
        currentMarker = googleMap.addMarker(new MarkerOptions()
                .position(new LatLng(area.getLat(), area.getLng()))
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_area))
                .title(area.getName())
                .snippet("Số lượng: " + area.getCount())
        );
        currentMarker.setTag(area);
        currentCircle = googleMap.addCircle(new CircleOptions()
                .center(new LatLng(area.getLat(), area.getLng()))
                .radius(area.getRadius())
                .strokeColor(Color.argb(99, 37, 144, 255))
                .strokeWidth(4f)
                .fillColor(Color.argb(30, 96, 152, 207)));

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(area.getLat(), area.getLng()), 15));
    }

    private void displayMapPath(final FlyerLogDTO flyerLogDTO) {
        if (flyerLogDTO.getLocation() == null)
            return;
        // Vẽ path của flyer
        PolylineOptions polylineOptions = new PolylineOptions().width(4f).color(Color.argb(255, 18, 103, 255));

        for (LocationDTO item : flyerLogDTO.getLocation())
            polylineOptions.add(new LatLng(item.getLat(), item.getLng()));
        currentPolyline = googleMap.addPolyline(polylineOptions);
        // Vẽ các marker location flyer đã đi qua
        //double scalePercent = 10f;
        IconGenerator iconFactory = new IconGenerator(this);
        iconFactory.setStyle(R.style.iconGenText);
        /*for (int i = 0; i < flyerLogDTO.getLocation().size(); i++)
            if (flyerLogDTO.getLocation().get(i).getTime() > 1000)
                scalePercent = 50f;*/
        for (int i = 0; i < flyerLogDTO.getLocation().size(); i++) {
            String title;
            LocationDTO locationDTO = flyerLogDTO.getLocation().get(i);
            if (i == 0) {
                title = "Điểm bắt đầu";
                currentDMarker.add(googleMap.addMarker(new MarkerOptions()
                        .position(new LatLng(locationDTO.getLat(), locationDTO.getLng()))
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_start))
                        .title(title)
                        .anchor(0.5f, 0.5f)
                        .snippet("Thời gian: " + Math.round(locationDTO.getTime()))));
            } else if (i == flyerLogDTO.getLocation().size() - 1) {
                title = "Điểm kết thúc";
                currentDMarker.add(googleMap.addMarker(new MarkerOptions()
                        .position(new LatLng(locationDTO.getLat(), locationDTO.getLng()))
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_end))
                        .title(title)
                        .anchor(0.5f, 0.5f)
                        .snippet("Thời gian: " + Math.round(locationDTO.getTime()))));
            } else {
                BitmapDescriptor bitmapDescriptor;
                if (locationDTO.getTime() < 10 * 60)
                    bitmapDescriptor = BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_1);
                else if (locationDTO.getTime() < 30 * 60)
                    bitmapDescriptor = BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_2);
                else if (locationDTO.getTime() < 60 * 60)
                    bitmapDescriptor = BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_3);
                else
                    bitmapDescriptor = BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_4);
                title = "Điểm số " + i + 1;
                currentDMarker.add(googleMap.addMarker(new MarkerOptions()
                        .position(new LatLng(locationDTO.getLat(), locationDTO.getLng()))
                        .icon(bitmapDescriptor)
                        .title(title)
                        .anchor(0.5f, 0.5f)
                        .snippet("Thời gian: " + Math.round(locationDTO.getTime()))));
            }
            currentDMarker.get(currentDMarker.size() - 1).setTag(locationDTO);
            currentDMarker.get(currentDMarker.size() - 1).setVisible(false);


            Marker text = googleMap.addMarker(new MarkerOptions().
                    icon(BitmapDescriptorFactory.fromBitmap(iconFactory.makeIcon(DateTimeHelper.convertSecondToTimeString(locationDTO.getTime()))))
                    .position(new LatLng(locationDTO.getLat(), locationDTO.getLng()))
                    .anchor(iconFactory.getAnchorU(), iconFactory.getAnchorV()));
            text.setAlpha(0);
            MarkersText.add(text);
        }
        count = 0;
        beginAnimation();
    }

    private void beginAnimation() {
        if (count == currentDMarker.size())
            return;
        LocationDTO locationDTO = (LocationDTO) currentDMarker.get(count).getTag();
        currentDMarker.get(count).setVisible(true);
        MarkerHelper.animate(googleMap,
                new LatLng(locationDTO != null ? locationDTO.getLat() : 0, locationDTO != null ? locationDTO.getLng() : 0),
                currentDMarker.get(count));
        count++;
        new Handler().postDelayed(() -> beginAnimation(), 100);
    }

    private void LoadFlyerData() {
        DataServiceProvider<ResultDTO<AreaFDTO>> GetListArea = new DataServiceProvider<>(new TypeToken<ResultDTO<AreaFDTO>>() {
        }.getType());
        GetListArea.setListener(new DataServiceProvider.OnListenerReponse<ResultDTO<AreaFDTO>>() {
            @Override
            public void onSuccess(ResultDTO<AreaFDTO> responseData) {
                if (responseData.isResult()) {
                    areaDTOList = responseData.getData().getArea();
                    drawerFragment.updateFlyerData(responseData.getData());
                    displayAreasInMap();
                }
            }

            @Override
            public void onFailure(String errorMessage) {
                ToastHelper.showShortToast("Có lỗi: " + errorMessage);
            }
        });
        GetListArea.getData(FunctionConst.GetListArea, AppConst.AsyncMethod.GET,
                JsonHelper.getIntance().GetListArea(UserAccountHelper.getIntance().getSecureKey()));
    }

    private void LoadDCheckData() {
        v_loading.setVisibility(View.VISIBLE);
        DataServiceProvider<ResultDTO<DCheckManageDTO>> GetListUser = new DataServiceProvider<>(new TypeToken<ResultDTO<DCheckManageDTO>>() {
        }.getType());
        GetListUser.setListener(new DataServiceProvider.OnListenerReponse<ResultDTO<DCheckManageDTO>>() {
            @Override
            public void onSuccess(ResultDTO<DCheckManageDTO> responseData) {
                if (responseData.isResult()) {
                    ll_marker_des.setVisibility(View.VISIBLE);
                    if (responseData.getData().getFlyer().size() != 0) {
                        dCheckManageFlyerDTOs = responseData.getData().getFlyer();
                        drawerFragment.updateDCheckData(responseData.getData().getFlyer());
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                displayAreasInMap();
                            }
                        }, 500);
                    }
                } else ToastHelper.showShortToast(responseData.getMessage());
                v_loading.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(String errorMessage) {
                ToastHelper.showShortToast(errorMessage);
                v_loading.setVisibility(View.GONE);
            }
        });
        GetListUser.getData(FunctionConst.GetListUser, AppConst.AsyncMethod.GET,
                JsonHelper.getIntance().GetListUser(UserAccountHelper.getIntance().getSecureKey()));

    }

    private void LoadFlyerArea(AreaDTO areaDTO) {
        v_loading.setVisibility(View.VISIBLE);
        DataServiceProvider<ResultDTO<FlyerLogDTO>> GetListLocation = new DataServiceProvider<>(new TypeToken<ResultDTO<FlyerLogDTO>>() {
        }.getType());
        GetListLocation.setListener(new DataServiceProvider.OnListenerReponse<ResultDTO<FlyerLogDTO>>() {
            @Override
            public void onSuccess(ResultDTO<FlyerLogDTO> responseData) {
                if (responseData.isResult()) {
                    if (responseData.getData() != null) {
                        displayMapPath(responseData.getData());
                    }
                } else ToastHelper.showShortToast(responseData.getMessage());
                v_loading.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(String errorMessage) {
                ToastHelper.showShortToast(errorMessage);
                v_loading.setVisibility(View.GONE);
            }
        });
        GetListLocation.getData(FunctionConst.GetListLocation, AppConst.AsyncMethod.GET,
                JsonHelper.getIntance().GetListLocation(areaDTO.getId(), UserAccountHelper.getIntance().getSecureKey(), dCheckManageFlyerDTOs.get(0).getFlyerId()));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == AppConst.CameraRequestCode && resultCode == Activity.RESULT_OK) {
            try {
                Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                DialogPhoto dialogPhoto = DialogPhoto.getInstance(this, true);
                dialogPhoto.show();
                dialogPhoto.setImageData(locationService, bitmap);

            } catch (Exception ex) {
                Log.e("MainMapActivity", "onActivityResult: " + ex);
            }

            super.onActivityResult(requestCode, resultCode, data);
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        if (isStart)
            sendNotification("Ứng dụng đang cập nhật vị trí của bạn!");
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isStart) {
            NotificationManager nMgr = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
            assert nMgr != null;
            nMgr.cancel(1001);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isStart) {
            NotificationManager nMgr = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
            assert nMgr != null;
            nMgr.cancel(1001);
        }
    }

    private void sendNotification(String message) {
        Intent intent = new Intent(this, MainMapActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.setAction("ACTION" + System.currentTimeMillis());
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_area))
                .setSmallIcon(R.drawable.ic_area, 100)
                .setContentTitle("EagleEye")
                .setContentText(message)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        assert notificationManager != null;
        notificationManager.notify(1001, notificationBuilder.build());
    }

    @Override
    public void onRefreshList() {
        googleMap.clear();
        currentDMarker.clear();
        currentDCircle.clear();
        if (isFlyer)
            LoadFlyerData();
        else LoadDCheckData();

    }
}
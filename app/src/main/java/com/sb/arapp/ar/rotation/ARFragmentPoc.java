package com.sb.arapp.ar.rotation;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.opengl.Matrix;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.makeramen.roundedimageview.RoundedImageView;
import com.sb.arapp.BuildConfig;
import com.sb.arapp.R;
import com.sb.arapp.Util.Constants;
import com.sb.arapp.ar.Elevation;
import com.sb.arapp.ar.RestInterface;

import com.sb.arapp.ar.database.DatabaseHandler;
import com.sb.arapp.model.elevation.ElevationModel;
import com.sb.arapp.networkRetrofit.PoiResponse;
import com.sb.arapp.networkRetrofit.RetrofitInterface;
import com.sb.arapp.networkRetrofit.poi.Result;
import com.sb.arapp.radar.GenRadarManager;
import com.sb.arapp.radar.GenRadarPoint;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.content.Context.LOCATION_SERVICE;
import static android.content.Context.SENSOR_SERVICE;


public class ARFragmentPoc extends Fragment implements SensorEventListener, View.OnClickListener {

    private static final String TAG = ARFragmentPoc.class.getSimpleName();
    private static ARFragmentPoc instance = null;
    DatabaseHandler db;
    LocationManager locationManager;
    private static final int REQUEST_CAMERA_PERMISSIONS_CODE = 11;
    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;
    private static final int REQUEST_CHECK_SETTINGS = 0x1;
    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000 * 20; //5 minutes refresh

    //private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 1000 * 20; //5 minutes refresh

    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = UPDATE_INTERVAL_IN_MILLISECONDS / 2; // 30 seconds refresh

    private final static String KEY_REQUESTING_LOCATION_UPDATES = "requesting-location-updates";
    private final static String KEY_LOCATION = "location";
    private final static String KEY_LAST_UPDATED_TIME_STRING = "last-updated-time-string";

    int showDist;
    String categoryID, categoryName;

    RelativeLayout infl;
    TextView txtFilterDist;
    ProgressBar progressBar;
    SurfaceView surfaceView;
    FrameLayout cameraContainerLayout;

    List<Result> mPlacesModelList1;
    List<Result>  mStringFilterList1;

    boolean useOrientation;
    Sensor compass;
    Camera camera;

    AROverlayView arOverlayView;

    ARCamera arCamera;
    SensorManager sensorManager;
    private FusedLocationProviderClient mFusedLocationClient;
    private SettingsClient mSettingsClient;
    private LocationRequest mLocationRequest;
    private LocationSettingsRequest mLocationSettingsRequest;
    private LocationCallback mLocationCallback;
    private Location mCurrentLocation;
    private Boolean mRequestingLocationUpdates;
    private String mLastUpdateTime;
    private View view;
    Geocoder geocoder;
    List<Address> addresses;

    private FusedLocationProviderClient mFusedLocationClient1;
    private SettingsClient mSettingsClient1;
    private LocationRequest mLocationRequest1;
    private LocationSettingsRequest mLocationSettingsRequest1;
    private LocationCallback mLocationCallback1;
    private Location mCurrentLocation1;



    public static String EXTRA_ADDRESS = "address";

    private List<GenRadarPoint> genRadarPoints;
    private GenRadarManager mGenRadarManager;
    private List<GenRadarPoint> mGenRadarPoints;
    private GenRadarPoint mCentralGenRadarPoint;

    private static List<GenRadarPoint> genRadarPointss = new ArrayList<>();

    FragmentManager fm;

    private String mCategory = "";

    private ImageView ivBack;

    private boolean viewCreate = false;

    private float[] mGravity;
    private float[] mMagnetic;


    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instance = this;
    }

    public static ARFragmentPoc getInstance() {
        return instance;
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        if (!viewCreate) {


            view = inflater.inflate(R.layout.ar_activity_main, container, false);

            fm = getActivity().getSupportFragmentManager();

            db = new DatabaseHandler(getActivity());

            infl = view.findViewById(R.id.infl);

            txtFilterDist = (TextView) view.findViewById(R.id.txtDistanceFilter);

            progressBar = (ProgressBar) view.findViewById(R.id.progressBar);

            surfaceView = (SurfaceView) view.findViewById(R.id.surface_view);

            cameraContainerLayout = (FrameLayout) view.findViewById(R.id.camera_container_layout);


            ivBack = (ImageView) view.findViewById(R.id.ivBack);

            ivBack.setVisibility(View.GONE);


            loadDialogCalibration();

            useOrientation = false;

            sensorManager = (SensorManager) getActivity().getSystemService(SENSOR_SERVICE);

            compass = sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);

            ivBack.setOnClickListener(this);

            categoryID = "all_place";
            categoryName = "AR Location";


            arOverlayView = new AROverlayView(getActivity(), infl, showDist);

            txtFilterDist.setText(Integer.toString(showDist) + " Km");

            mRequestingLocationUpdates = false;
            mLastUpdateTime = "";

            updateValuesFromBundle(savedInstanceState);

            mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
            mSettingsClient = LocationServices.getSettingsClient(getActivity());

            mFusedLocationClient1 = LocationServices.getFusedLocationProviderClient(getActivity());
            mSettingsClient1 = LocationServices.getSettingsClient(getActivity());

            createLocationCallback();
            createLocationRequest();

            createLocationCallback1();
            createLocationRequest1();

            buildLocationSettingsRequest();

            buildLocationSettingsRequest1();

            requestCameraPermission();
            registerSensors();
            initAROverlayView();

            if (!mRequestingLocationUpdates) {
                mRequestingLocationUpdates = true;
                startLocationUpdates();


            }

            startLocationUpdates1();


            viewCreate = true;
            return view;
        } else {
            return view;
        }

    }

    void Poi_list_call(final Location mLastLocation){
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(getResources().getString(R.string.directions_base_url))
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RetrofitInterface apiService =
                retrofit.create(RetrofitInterface.class);

        final Call<PoiResponse> call = apiService.listPOI(
                String.valueOf(mLastLocation.getLatitude())+","+
                        String.valueOf(mLastLocation.getLongitude()),800,
                getResources().getString(R.string.google_maps_key));

        call.enqueue(new retrofit2.Callback<PoiResponse>() {
            @Override
            public void onResponse(Call<PoiResponse> call,
                                   retrofit2.Response<PoiResponse> response) {

              List<Result> poiResult0=response.body().getResults();

              List<Result> poiResult = new ArrayList<Result>();

              String type = "";

              for (int i = 0; i < poiResult0.size(); i++) {
                  type = poiResult0.get(i).getTypes().get(0);
                    if ((type.equals(getResources().getString(R.string.point_of_interest)))) {

                        poiResult.add(poiResult0.get(i));

                    }

              }

              mPlacesModelList1.clear();
              mStringFilterList1.clear();
                LayoutInflater layoutInflator = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                RelativeLayout rel_leftright = view.findViewById(R.id.rel_leftright);
                ImageView img_left = view.findViewById(R.id.left_side);
                ImageView img_right = view.findViewById(R.id.right_side);
                ArrayList<View> views = new ArrayList<>();
                infl.removeAllViews();
                views.clear();

                int rule = 0;

                for (Result renyooLetsList : poiResult) {

                    View view = layoutInflator.inflate(R.layout.ar_view_poc, infl, false);
                    TextView text = view.findViewById(R.id.t);
                    TextView distance = view.findViewById(R.id.distance);
                    RoundedImageView icon = view.findViewById(R.id.icon);
                    TextView tv_cat = view.findViewById(R.id.tv_cat);

                    icon.setImageResource(R.drawable.ic_launcher_foreground);


                    float distM = 0;

                    String loc = "" + renyooLetsList.getGeometry().getLocation().getLat() + "," +
                            renyooLetsList.getGeometry().getLocation().getLng();

                    double melevation = 560;
                    melevation = getElevation(loc);

                    renyooLetsList.setElevation(melevation);

                    if (mLastLocation.getLatitude() != 0 || mLastLocation.getLongitude() != 0) {
                        distM = Config.distanceFrom(mLastLocation.getLatitude(),
                                mLastLocation.getLongitude(),
                                renyooLetsList.getGeometry().getLocation().getLat(),
                                renyooLetsList.getGeometry().getLocation().getLng(),
                                melevation,
                                mLastLocation.getAltitude());
                    }

                    double distKM = (double) distM /1000;
                    double rndoff = round(distKM,2);
                    distance.setText(""+rndoff +"km away");

                    text.setText(renyooLetsList.getName());

                    tv_cat.setText(renyooLetsList.getTypes().get(0));

                    view.setLayoutParams(new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT));


                    view.setVisibility(View.GONE);
                    view.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            Toast.makeText(getActivity(),"click",Toast.LENGTH_LONG).show();
                        }
                    });




                    infl.addView(view);
                    views.add(view);
                    mPlacesModelList1.add(renyooLetsList);
                }


                if (arOverlayView != null) {
                    arOverlayView.updateCurrentLocation(mLastLocation);
                    arOverlayView.updateFilter(infl,rel_leftright, mPlacesModelList1, showDist, false);
                    arOverlayView.getViews(views, img_left, img_right);
                }


                // radar view
                initGenRadar();

                progressBar.setVisibility(View.GONE);

            }

            @Override
            public void onFailure(Call<PoiResponse> call, Throwable t) {
               // poi_browser_progress.setVisibility(View.GONE);
            }
        });

    }


  public double getElevation(String location) {
        final double[] mElevation = {0};
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(getResources().getString(R.string.directions_base_url))
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RetrofitInterface apiService = retrofit.create(RetrofitInterface.class);

       Call<ElevationModel> call = apiService.getElevationData(location,
                getResources().getString(R.string.google_maps_key));

        call.enqueue(new retrofit2.Callback<ElevationModel>() {
            @Override
            public void onResponse(Call<ElevationModel> call, retrofit2.Response<ElevationModel> response) {

                Log.e("VF", "ELEVATION: " + response.body().getResults().get(0).getElevation());

                mElevation[0] = response.body().getResults().get(0).getElevation();


            }

            @Override
            public void onFailure(Call<ElevationModel> call, Throwable t) {
                Log.e("VF", "ELEVATION: Failed");
                mElevation[0] = 0;
            }
        });

        return mElevation[0];
    }
   

    @Override
    public void onStop() {
        releaseCamera();
        sensorManager.unregisterListener(this);
        super.onStop();
    }

    public void requestCameraPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                getActivity().checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            this.requestPermissions(new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSIONS_CODE);
        } else {
            initARCameraView();
        }
    }

    public void initAROverlayView() {
        if (arOverlayView.getParent() != null) {
            ((ViewGroup) arOverlayView.getParent()).removeView(arOverlayView);
        }
        cameraContainerLayout.addView(arOverlayView);
    }

    public void initARCameraView() {
        reloadSurfaceView();

        if (arCamera == null) {
            arCamera = new ARCamera(getActivity(), surfaceView);
        }
        if (arCamera.getParent() != null) {
            ((ViewGroup) arCamera.getParent()).removeView(arCamera);
        }
        cameraContainerLayout.addView(arCamera);
        arCamera.setKeepScreenOn(true);
        initCamera();
    }

    private void initCamera() {
        int numCams = Camera.getNumberOfCameras();
        if (numCams > 0) {
            try {
                camera = Camera.open();
                // camera.startPreview();
                arCamera.setCamera(camera);
            } catch (RuntimeException ex) {
                Toast.makeText(getActivity(), "no camera found", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void reloadSurfaceView() {
        if (surfaceView.getParent() != null) {
            ((ViewGroup) surfaceView.getParent()).removeView(surfaceView);
        }

        cameraContainerLayout.addView(surfaceView);
    }

    private void releaseCamera() {
        try {
            if (camera != null) {
                try {
                    camera.setPreviewCallback(null);
                    camera.stopPreview();
                    arCamera.setCamera(null);
                } catch (Exception ignore) {
                }
                try {
                    camera.release();
                } catch (Exception ignore) {
                }
                camera = null;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void registerSensors() {
      
        sensorManager.registerListener(this,
                sensorManager.getDefaultSensor(checkSensor()),
                SensorManager.SENSOR_DELAY_NORMAL);

        sensorManager.registerListener(this, compass,
                SensorManager.SENSOR_DELAY_UI);

    }

    int checkSensor() {
        PackageManager packageManager = getActivity().getPackageManager();
        boolean gyroExists = packageManager.hasSystemFeature(PackageManager.FEATURE_SENSOR_GYROSCOPE);
        int sens;
        // Log.e("VF","GYRO: "+gyroExists);
        if (gyroExists) {
            sens = Sensor.TYPE_ROTATION_VECTOR;
        } else {
            sens = Sensor.TYPE_ORIENTATION;
        }
        return sens;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        if (event.sensor.getType() == checkSensor()) {
            float[] rotationMatrixFromVector = new float[16];
            float[] projectionMatrix = new float[16];
            float[] rotatedProjectionMatrix = new float[16];

            SensorManager.getRotationMatrixFromVector(rotationMatrixFromVector, event.values);

            if (arCamera != null) {
                projectionMatrix = arCamera.getProjectionMatrix();
            }

            float aX= event.values[0];
            float aY= event.values[1];
            //aZ= event.values[2];
            double angle = Math.atan2(aX, aY)/(Math.PI/180);

            Matrix.multiplyMM(rotatedProjectionMatrix, 0, projectionMatrix, 0, rotationMatrixFromVector, 0);
            this.arOverlayView.updateRotatedProjectionMatrix(rotatedProjectionMatrix);
            this.arOverlayView.getAngle(angle);
        }


    }

    private float getDirection() {

        float[] temp = new float[9];
        float[] R = new float[9];
        //Load rotation matrix into R
        SensorManager.getRotationMatrix(temp, null,
                mGravity, mMagnetic);

        //Remap to camera's point-of-view
        SensorManager.remapCoordinateSystem(temp,
                SensorManager.AXIS_X,
                SensorManager.AXIS_Z, R);

        //Return the orientation values
        float[] values = new float[3];
        SensorManager.getOrientation(R, values);

        //Convert to degrees
        for (int i=0; i < values.length; i++) {
            Double degrees = (values[i] * 180) / Math.PI;
            values[i] = degrees.floatValue();
        }

        return values[0];

    }

    void loadDialogCalibration() {

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
        //do nothing

        Log.e("onAccuracyChanged", "accuracy:" + i);
   /*     if (i == SENSOR_STATUS_ACCURACY_LOW) {
            Log.e("onAccuracyChanged", "Low:Need Calibration");
            calibDialog.show();
        } else if (i == SENSOR_STATUS_UNRELIABLE) {
            Log.e("onAccuracyChanged", "Unreliable:Need Calibration");
            calibDialog.show();
        } else if (i == SENSOR_STATUS_ACCURACY_MEDIUM) {
            Log.e("onAccuracyChanged", "Medium:Need Calibration");
            calibDialog.show();
        } else if (i == SENSOR_STATUS_ACCURACY_HIGH) {
            if (calibDialog.isShowing()) {
                calibDialog.dismiss();
            }

            //calibDialog.show();
        }*/
    }

    private void updateValuesFromBundle(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            if (savedInstanceState.keySet().contains(KEY_REQUESTING_LOCATION_UPDATES)) {
                mRequestingLocationUpdates = savedInstanceState.getBoolean(
                        KEY_REQUESTING_LOCATION_UPDATES);
            }

            if (savedInstanceState.keySet().contains(KEY_LOCATION)) {
                mCurrentLocation = savedInstanceState.getParcelable(KEY_LOCATION);
            }

            if (savedInstanceState.keySet().contains(KEY_LAST_UPDATED_TIME_STRING)) {
                Log.d(TAG, "mLastUpdateTime:" + savedInstanceState.getString(KEY_LAST_UPDATED_TIME_STRING));
                mLastUpdateTime = savedInstanceState.getString(KEY_LAST_UPDATED_TIME_STRING);
            }
            updateUI();
        }
    }

    private void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setSmallestDisplacement(100);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    private void createLocationRequest1() {


        long INTERVAL = 1000 * 2;
        long FASTEST_INTERVAL = 1000 * 1;


        mLocationRequest1 = new LocationRequest();
        mLocationRequest1.setInterval(INTERVAL);
        mLocationRequest1.setFastestInterval(FASTEST_INTERVAL);
        // mLocationRequest1.setSmallestDisplacement(100);
        mLocationRequest1.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }


    @SuppressLint("MissingPermission")
    public void getLastLocation() {
        // Get last known recent location using new Google Play Services SDK (v11+)
        //  FusedLocationProviderClient locationClient = getFusedLocationProviderClient(this);

        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // GPS location can be null if GPS is switched off
                        if (location != null) {
                            Log.e("VF", "LOCATION CHANGE: " + location.getLatitude());
                            // onLocationChanged(location);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("MapDemoActivity", "Error trying to get last GPS location");
                        e.printStackTrace();
                    }
                });
    }

    private void createLocationCallback() {
        Log.e("VF", "LOCATION CALLBACK");
        mLocationCallback = new LocationCallback() {
            // Log.e("VF","LOCATION CALLING");

            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);


                mCurrentLocation = locationResult.getLastLocation();
                Log.d(TAG, "mLastUpdateTime:" + DateFormat.getTimeInstance().format(new Date()));
                mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
                updateLocationUI();

                Log.e("VF", "LOCATION RESULT " + mCurrentLocation.getSpeed());


            }

            @Override
            public void onLocationAvailability(LocationAvailability locationAvailability) {
                super.onLocationAvailability(locationAvailability);
                Log.e("VF", "LOCATION AVAIL: " + locationAvailability.isLocationAvailable());
            }

        };
    }

    private void createLocationCallback1() {
        Log.e("VF", "LOCATION CALLBACK");
        mLocationCallback1 = new LocationCallback() {
            // Log.e("VF","LOCATION CALLING");

            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);


                mCurrentLocation1 = locationResult.getLastLocation();
                Log.d(TAG, "mLastUpdateTime:" + DateFormat.getTimeInstance().format(new Date()));




            }

            @Override
            public void onLocationAvailability(LocationAvailability locationAvailability) {
                super.onLocationAvailability(locationAvailability);
                Log.e("VF", "LOCATION AVAIL: " + locationAvailability.isLocationAvailable());
            }

        };
    }


    private void buildLocationSettingsRequest() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        mLocationSettingsRequest = builder.build();
    }

    private void buildLocationSettingsRequest1() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest1);
        mLocationSettingsRequest1 = builder.build();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        Log.i(TAG, "User agreed to make required location settings changes.");
                        // Nothing to do. startLocationupdates() gets called in onResume again.
                        break;
                    case Activity.RESULT_CANCELED:
                        Log.i(TAG, "User chose not to make required location settings changes.");
                        mRequestingLocationUpdates = false;
                        updateUI();
                        break;
                }
                break;
        }
    }

    private void startLocationUpdates() {
        mSettingsClient.checkLocationSettings(mLocationSettingsRequest)
                .addOnSuccessListener(getActivity(), new OnSuccessListener<LocationSettingsResponse>() {
                    @SuppressLint("MissingPermission")
                    @Override
                    public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                        Log.i(TAG, "All location settings are satisfied.");
                        mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                                mLocationCallback, Looper.myLooper());

                        updateUI();
                    }
                })
                .addOnFailureListener(getActivity(), new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        int statusCode = ((ApiException) e).getStatusCode();
                        switch (statusCode) {
                            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                Log.i(TAG, "Location settings are not satisfied. Attempting to upgrade " +
                                        "location settings ");
                                try {
                                    ResolvableApiException rae = (ResolvableApiException) e;
                                    rae.startResolutionForResult(getActivity(), REQUEST_CHECK_SETTINGS);
                                } catch (IntentSender.SendIntentException sie) {
                                    Log.i(TAG, "PendingIntent unable to execute request.");
                                }
                                break;
                            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                String errorMessage = "Location settings are inadequate, and cannot be " +
                                        "fixed here. Fix in Settings.";
                                Log.e(TAG, errorMessage);
                                Toast.makeText(getActivity(), errorMessage, Toast.LENGTH_LONG).show();
                                mRequestingLocationUpdates = false;
                        }

                        updateUI();
                    }
                });

        // getLastLocation();


    }

    private void startLocationUpdates1() {
        mSettingsClient1.checkLocationSettings(mLocationSettingsRequest1)
                .addOnSuccessListener(getActivity(), new OnSuccessListener<LocationSettingsResponse>() {
                    @SuppressLint("MissingPermission")
                    @Override
                    public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                        Log.i(TAG, "All location settings are satisfied.");
                        mFusedLocationClient1.requestLocationUpdates(mLocationRequest1,
                                mLocationCallback1, Looper.myLooper());

                        //updateUI();
                    }
                })
                .addOnFailureListener(getActivity(), new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        int statusCode = ((ApiException) e).getStatusCode();
                        switch (statusCode) {
                            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                Log.i(TAG, "Location settings are not satisfied. Attempting to upgrade " +
                                        "location settings ");
                                try {
                                    ResolvableApiException rae = (ResolvableApiException) e;
                                    rae.startResolutionForResult(getActivity(), REQUEST_CHECK_SETTINGS);
                                } catch (IntentSender.SendIntentException sie) {
                                    Log.i(TAG, "PendingIntent unable to execute request.");
                                }
                                break;
                            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                String errorMessage = "Location settings are inadequate, and cannot be " +
                                        "fixed here. Fix in Settings.";
                                Log.e(TAG, errorMessage);
                                Toast.makeText(getActivity(), errorMessage, Toast.LENGTH_LONG).show();
                                // mRequestingLocationUpdates = false;
                        }

                        //updateUI();
                    }
                });

        // getLastLocation();


    }

    private void updateUI() {
        Log.e("VF", "UPDATE UI");
        //setButtonsEnabledState();
        updateLocationUI();
    }

    public void updateLocationUI() {
        if (mCurrentLocation != null) {
            if (!mCurrentLocation.hasAltitude()) {
                if (Config.checkConnection(getActivity())) {
                    RestAdapter adapter = new RestAdapter.Builder().
                            setEndpoint(Constants.GMAPS_ELEVATION_API_URL).build();
                    RestInterface restInterface = adapter.create(RestInterface.class);
                    restInterface.getElevation(mCurrentLocation.getLatitude() + "," + mCurrentLocation.getLongitude(),
                            Constants.SENSOR,
                            Constants.DIRECTION_API_KEY, new Callback<Elevation>() {
                                @Override
                                public void success(Elevation model, Response response) {
                                    if (model.getStatus().equalsIgnoreCase("ok")) {
                                        update(model.getResults().get(0).getElevation());
                                    } else {
                                        update(0.0);
                                    }
                                }

                                @Override
                                public void failure(RetrofitError error) {
                                    Log.e("ERROR GET ELEV", ":" + error.getMessage());
                                    update(0.0);
                                }
                            });
                } else {
                    update(0.0);
                }
            } else {
                update(mCurrentLocation.getAltitude());
            }

            Log.e("My Location", "ALT:" + mCurrentLocation.getAltitude() + ", ACC:" + mCurrentLocation.getAccuracy() + ", PROV:" + mCurrentLocation.getProvider());

        } else {
            Log.e("VF", "Location NULL");
            // Toast.makeText(getActivity(),"Cannot retrieve device location...please try restarting your device",
            //       Toast.LENGTH_LONG).show();

        }
    }

    void update(Double alt) {
        mCurrentLocation.setAltitude(alt);

        mPlacesModelList1 = new ArrayList<>();
        mStringFilterList1 = new ArrayList<>();



        if (mCurrentLocation != null) {
            Log.e("VF", "CURR: " + mCurrentLocation.getLatitude());
            Toast.makeText(getActivity(), "Location detected", Toast.LENGTH_LONG).show();
           // loadAllPlaces(mCurrentLocation, mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());

            Poi_list_call(mCurrentLocation);

        } else {
            Log.e("VF", "CURR: NULL");
            Toast.makeText(getActivity(), "Location null", Toast.LENGTH_LONG).show();

        }
      


    }

    private void stopLocationUpdates() {
        if (!mRequestingLocationUpdates) {
            Log.d(TAG, "stopLocationUpdates: updates never requested, no-op.");
            return;
        }

        mFusedLocationClient.removeLocationUpdates(mLocationCallback)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        mRequestingLocationUpdates = false;
                        //setButtonsEnabledState();
                    }
                });
    }

    private void stopLocationUpdates1() {
     /*   if (!mRequestingLocationUpdates) {
            Log.d(TAG, "stopLocationUpdates: updates never requested, no-op.");
            return;
        }*/

        mFusedLocationClient1.removeLocationUpdates(mLocationCallback1)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        //mRequestingLocationUpdates = false;
                        //setButtonsEnabledState();
                    }
                });
    }


    @Override
    public void onResume() {
        super.onResume();
        registerSensors();
        initCamera();
        if (mRequestingLocationUpdates && checkPermissions()) {
            startLocationUpdates();
        } else if (!checkPermissions()) {
            requestPermissions();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        releaseCamera();
        stopLocationUpdates();
        stopLocationUpdates1();
    }

    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putBoolean(KEY_REQUESTING_LOCATION_UPDATES, mRequestingLocationUpdates);
        savedInstanceState.putParcelable(KEY_LOCATION, mCurrentLocation);
        savedInstanceState.putString(KEY_LAST_UPDATED_TIME_STRING, mLastUpdateTime);
        super.onSaveInstanceState(savedInstanceState);
    }

    private void showSnackbar(final int mainTextStringId, final int actionStringId,
                              View.OnClickListener listener) {
        Snackbar.make(
                view.findViewById(android.R.id.content),
                getString(mainTextStringId),
                Snackbar.LENGTH_INDEFINITE)
                .setAction(getString(actionStringId), listener).show();
    }

    private boolean checkPermissions() {
        int permissionState = ActivityCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION);
        return permissionState == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissions() {
        boolean shouldProvideRationale =
                ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                        Manifest.permission.ACCESS_FINE_LOCATION);

        if (shouldProvideRationale) {
            Log.i(TAG, "Displaying permission rationale to provide additional context.");
            showSnackbar(R.string.permission_rationale,
                    android.R.string.ok, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // Request permission
                            ActivityCompat.requestPermissions(getActivity(),
                                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                    REQUEST_PERMISSIONS_REQUEST_CODE);
                        }
                    });
        } else {
            Log.i(TAG, "Requesting permission");
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_PERMISSIONS_REQUEST_CODE);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        Log.i(TAG, "onRequestPermissionResult");
        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length <= 0) {

                Log.i(TAG, "User interaction was cancelled.");
            } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (mRequestingLocationUpdates) {
                    Log.i(TAG, "Permission granted, updates requested, starting location updates");
                    startLocationUpdates();
                }
            } else {
                showSnackbar(R.string.permission_denied_explanation,
                        R.string.settings, new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                // Build intent that displays the App settings screen.
                                Intent intent = new Intent();
                                intent.setAction(
                                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package",
                                        BuildConfig.APPLICATION_ID, null);
                                intent.setData(uri);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                        });
            }
        }
    }




    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                //   onBackPressed();
                return true;

            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }




    //This method leads you to the alert dialog box.
    void checkGps() {
        locationManager = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);

        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {


            showGPSDisabledAlertToUser();
        }
    }

    //This method configures the Alert Dialog box.
    private void showGPSDisabledAlertToUser() {

        Toast.makeText(getActivity(), "GPS is not enabled", Toast.LENGTH_LONG).show();

    }

    private void initGenRadar() {
        // Generate the Radar Points
        mGenRadarPoints = generateGenRadarPoints();

        // Create the central Radar Point which will be used by GenRadar API
        mCentralGenRadarPoint = new GenRadarPoint("Center Point",
                mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude(),
                0, 0, 3.2f, Color.TRANSPARENT);
        // Initialize the GenRadarManager
        // The last two params are same as declared width and height of container and top parent layout in xml layout file

        if (mGenRadarManager != null) {
            //  mGenRadarManager.initAndUpdateRadarWithPoints(mCentralGenRadarPoint, mGenRadarPoints);
            mGenRadarManager.updateRadarWithPoints(mGenRadarPoints);

        } else {
            mGenRadarManager = new GenRadarManager(getActivity(), (LinearLayout)
                    view.findViewById(R.id.container), 100, 100);
            // Let the GenRadarManager to do the radar-initialization process
            mGenRadarManager.initAndUpdateRadarWithPoints(mCentralGenRadarPoint, mGenRadarPoints);
        }

    }



    private List<GenRadarPoint> generateGenRadarPoints() {
        float radius = 3.2f;


        genRadarPointss.clear();


        for (Result poi : mPlacesModelList1) {

            genRadarPointss.add(new GenRadarPoint(poi.getId(), poi.getGeometry().getLocation().getLat(),
                    poi.getGeometry().getLocation().getLng(), 0, 0, radius, Color.BLUE));

        }

        genRadarPoints = genRadarPointss;
        return genRadarPoints;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ivBack:
                getActivity().onBackPressed();
                break;



            default:
                break;
        }
    }



    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }





}

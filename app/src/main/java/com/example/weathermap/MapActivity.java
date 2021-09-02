package com.example.weathermap;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {


    private RequestQueue mQueue;

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        Toast.makeText(this,"Map is Ready", Toast.LENGTH_SHORT).show();
        gMap = googleMap;
        mQueue = Volley.newRequestQueue(this);

        if (mLocationPermissionGranted){
            getDeviceLocation();
            jsonParse();
            init();

            gMap.setMyLocationEnabled(true);
            gMap.getUiSettings().setMyLocationButtonEnabled(false);
            gMap.setContentDescription("Temp = 2");
            gMap.setBuildingsEnabled(true);
            gMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(@NonNull LatLng latLng) {
                Toast.makeText(MapActivity.this, "Weather Info",Toast.LENGTH_LONG).show();
                String seachString = mSearchText.getText().toString();
                Geocoder geocoder = new Geocoder(MapActivity.this);
                List<Address> list = new ArrayList<>();
                try {
                    list = geocoder.getFromLocationName(seachString, 1);

                }catch (IOException e){
                    Log.d(TAG, "geoLocate: IOException "+ e.getMessage());

                }
                if (list.size()>0) {
                    Address address = list.get(0);

                    Log.d(TAG, "geoLocate: Found location " + address.toString());
                    //Toast.makeText(this, address.toString(), Toast.LENGTH_SHORT).show();
                    moveCamera(new LatLng(address.getLatitude(), address.getLongitude()),
                            "Lat " + address.getLatitude() + "\n" +
                                    "Long " + address.getLongitude());
                }
            }




            });

            }


        }



    private static final String TAG = "MapActivity";

    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private static final float DEFAULT_ZOOM = 15;

    //widgets
    private EditText mSearchText;
    private ImageView mGps;
    private ImageView mInfo;
    //vars
    private Boolean mLocationPermissionGranted = false;
    private GoogleMap gMap;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        mSearchText = findViewById(R.id.input_search);
        mGps = findViewById(R.id.ic_gps);
        mInfo = findViewById(R.id.ic_info);

        getLocationPermission();
        getDeviceLocation();



    }

    private void init(){
        Log.d(TAG, "init: Initializing");

        mSearchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_SEARCH
                        || actionId ==EditorInfo.IME_ACTION_DONE
                        || event.getAction() == KeyEvent.ACTION_DOWN
                        || event.getAction() == KeyEvent.KEYCODE_ENTER){

                    //execute our method for searching
                    geoLocate();

                }return false;
            }
        });
        mInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: On info widget click");
                jsonParse();
                Log.d(TAG, "onClick: On info widget click success");

            }
        });

        mGps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: Click GPS icon to Reset Position");
                getDeviceLocation();
            }
        });
        hideSoftKeyboard();
    }
    private void geoLocate(){
        Log.d(TAG, "geoLocate: Geolocating");
        String seachString = mSearchText.getText().toString();
        Geocoder geocoder = new Geocoder(MapActivity.this);
        List<Address> list = new ArrayList<>();
        try {
            list = geocoder.getFromLocationName(seachString, 1);

        }catch (IOException e){
            Log.d(TAG, "geoLocate: IOException "+ e.getMessage());

        }
        if (list.size()>0){
            Address address = list.get(0);

            Log.d(TAG, "geoLocate: Found location " + address.toString());
            //Toast.makeText(this, address.toString(), Toast.LENGTH_SHORT).show();
            moveCamera(new LatLng(address.getLatitude(),address.getLongitude()),
                    "Latitude" + address.getLatitude()+"\n"+
                            "Longitude " + address.getLongitude());

        }

        hideSoftKeyboard();
    }

    private void jsonParse(){
        //Parse API (JSON FORMAT)

        String url = "api.openweathermap.org/data/2.5/weather?q=London,uk&APPID=2e29188ca139841dc42c071c3973e714";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray jsonArray = response.getJSONArray("temp");
                            for (int i = 0; i < jsonArray.length(); i++){
                                JSONObject  temps = jsonArray.getJSONObject(i);

                                double daY = temps.getDouble("day");
                               // double miN = temps.getDouble("min");
                                //double maX = temps.getDouble("max");
                                //double nighT = temps.getDouble("night");
                                //double evE = temps.getDouble("eve");
                                //double morN = temps.getDouble("morn");
                                //Toast Message Upon Success
                                Toast.makeText(MapActivity.this,  daY+""
                                        ,Toast.LENGTH_LONG).show();

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        mQueue.add(request);
    }

    private void getDeviceLocation(){
        Log.d(TAG, "getDeviceLocation: Getting Device's Current Location");

        FusedLocationProviderClient mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        try {
            if (mLocationPermissionGranted) {

                final Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if(task.isSuccessful()){
                            Log.d(TAG, "onComplete: Found Location");
                            Location currentLocation = (Location) task.getResult();
                            Double longi = currentLocation.getLongitude();
                            Double lati = currentLocation.getLatitude();
                            if(longi == null){
                                return;}
                            else if(lati == null){
                                return;}
                            else{
                                moveCamera(new LatLng(lati, longi),
                                    currentLocation.toString());}

                        }else {
                            Log.d(TAG, "onComplete: Current Location is NULL");
                        }
                    }
                });
            }

        }catch (SecurityException e){
            Log.d(TAG, "getDeviceLocation: Security Exception");
        }

    }
    
    
    private void moveCamera(LatLng latLng, String title){
        Log.d(TAG, "moveCamera: Moving the camera to: lat: " + latLng.latitude + ", lng: " + latLng.longitude);
        gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, MapActivity.DEFAULT_ZOOM));

        MarkerOptions options = new MarkerOptions()
                .position(latLng)
                .title(title);
            gMap.addMarker((options));

        hideSoftKeyboard();
    }


    //Initialize Google Maps
    private void initMap(){
        Log.d(TAG, "initMap: Initializing Map");

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(MapActivity.this);
    }

    //Get the required location Permissions
    private void getLocationPermission(){
        Log.d(TAG, "getLocationPermission: Getting location permission ");
        String[] permissions = new String[0];
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q
        ) {
            permissions = new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                                   Manifest.permission.ACCESS_COARSE_LOCATION,
                                    Manifest.permission.ACCESS_BACKGROUND_LOCATION};
        }
        if (ContextCompat.checkSelfPermission((this.getApplicationContext()),
                FINE_LOCATION)== PackageManager.PERMISSION_GRANTED){
            if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                    COURSE_LOCATION )== PackageManager.PERMISSION_GRANTED) {
                if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                        COURSE_LOCATION )== PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;

                    initMap();}
            }else {
                ActivityCompat.requestPermissions(this, permissions,
                        LOCATION_PERMISSION_REQUEST_CODE);
            }}
        else {
                ActivityCompat.requestPermissions(this, permissions,
                        LOCATION_PERMISSION_REQUEST_CODE);
            }        
            
        
    }

    //Request Permission Results
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult: called");
        mLocationPermissionGranted = false;
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0) {
                for (int grantResult : grantResults) {
                    if (grantResult != PackageManager.PERMISSION_GRANTED) {
                        mLocationPermissionGranted = false;
                        Log.d(TAG, "onRequestPermissionsResult: Permission failed");
                        return;
                    }
                }
                Log.d(TAG, "onRequestPermissionsResult: Permission granted");
                mLocationPermissionGranted = true;
                //initialize our map
                initMap();
            }
        }
        }

    private void hideSoftKeyboard(){
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }
}


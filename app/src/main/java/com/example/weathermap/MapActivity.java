package com.example.weathermap;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private TextView mTextViewResult;
    private RequestQueue mQueue;

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        Toast.makeText(this,"Map is Ready", Toast.LENGTH_SHORT).show();
        gMap = googleMap;
        mQueue = Volley.newRequestQueue(this);

        if (mLocationPermissionGranted){
            getDeviceLocation();
            gMap.setMyLocationEnabled(true);
            gMap.getUiSettings().setMyLocationButtonEnabled(true);
            gMap.setContentDescription("Temp = 2");
            gMap.setBuildingsEnabled(true);
            gMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(@NonNull LatLng latLng) {
                Toast.makeText(MapActivity.this, "Weather Info",Toast.LENGTH_LONG).show();

                String url = "api.openweathermap.org/data/2.5/forecast/daily?lat="+ latLng.latitude +"&lon"+ latLng.longitude
                        + "&cnt=1&appid=2e29188ca139841dc42c071c3973e714";
                JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    JSONArray jsonArray = response.getJSONArray("temp");
                                    for (int i = 0; i < jsonArray.length(); i++){
                                        JSONObject  temps = jsonArray.getJSONObject(i);

                                        double daY = temps.getDouble("day");
                                        double miN = temps.getDouble("min");
                                        double maX = temps.getDouble("max");
                                        double nighT = temps.getDouble("night");
                                        double evE = temps.getDouble("eve");
                                        double morN = temps.getDouble("morn");
                                        //Toast.makeText(MapActivity.this,""+daY+"\n",Toast.LENGTH_LONG).show();


                                    };
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




            });

            }


        }



    private static final String TAG = "MapActivity";

    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private static final float DEFAULT_ZOOM = 15;

    //vars
    private Boolean mLocationPermissionGranted = false;
    private GoogleMap gMap;
    private FusedLocationProviderClient  mFusedLocationProviderClient;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);


        getLocationPermission();
        getDeviceLocation();



    }

    private void jsonParse(LatLng latLng){
        //Parse API (JSON FORMAT)

        String url = "api.openweathermap.org/data/2.5/forecast/daily?lat="+ latLng.latitude +"&lon"+ latLng.longitude
                + "&cnt="+1+"&appid=2e29188ca139841dc42c071c3973e714";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray jsonArray = response.getJSONArray("temp");
                            for (int i = 0; i < jsonArray.length(); i++){
                                JSONObject  temps = jsonArray.getJSONObject(i);

                                double daY = temps.getDouble("day");
                                double miN = temps.getDouble("min");
                                double maX = temps.getDouble("max");
                                double nighT = temps.getDouble("night");
                                double evE = temps.getDouble("eve");
                                double morN = temps.getDouble("morn");
                                Toast.makeText(MapActivity.this,  daY +"\n"+  miN +"\n"
                                                +  maX +"\n"+ nighT +"\n"+  evE +"\n"+  morN
                                        ,Toast.LENGTH_LONG).show();

                            };
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

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        try {
            if (mLocationPermissionGranted) {

                final Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if(task.isSuccessful()){
                            Log.d(TAG, "onComplete: Found Location");
                            Location currentLocation = (Location) task.getResult();
                            moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()),
                                    DEFAULT_ZOOM);

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
    private void moveCamera(LatLng latLng, float zoom){
        Log.d(TAG, "moveCamera: Moving the camera to: lat: " + latLng.latitude + ", lng: " + latLng.longitude);
        gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,zoom));
    }

    //Initialize Google Maps
    private void initMap(){
        Log.d(TAG, "initMap: Initializing Map");

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(MapActivity.this);
    }

    //Get the required location Permissions
    private void getLocationPermission(){
        Log.d(TAG, "getLocationPermission: Getting location permission ");
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,
                               Manifest.permission.ACCESS_COARSE_LOCATION};
        if (ContextCompat.checkSelfPermission((this.getApplicationContext()),
                FINE_LOCATION)== PackageManager.PERMISSION_GRANTED){
            if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                    COURSE_LOCATION )== PackageManager.PERMISSION_GRANTED) {
                mLocationPermissionGranted = true;
                initMap();
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
        switch (requestCode){
            case LOCATION_PERMISSION_REQUEST_CODE:{
                if(grantResults.length > 0){
                    for (int i = 0; i < grantResults.length; i++){
                        if(grantResults[i] != PackageManager.PERMISSION_GRANTED){
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
        }

    
}


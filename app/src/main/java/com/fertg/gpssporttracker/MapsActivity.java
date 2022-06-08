package com.fertg.gpssporttracker;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationListener;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;

import com.google.android.gms.location.LocationRequest;

import android.location.LocationManager;

import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.location.LocationResult;
import com.fertg.gpssporttracker.databinding.ActivityMapsBinding;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private SupportMapFragment mapFragment;
    private GeofireProvider mGeofireProvider;
    private final static int LOCATION_REQUEST_CODE = 1;
    private FirebaseAuth auth;
    private DatabaseReference mDatabase;
    private LocationRequest mLocationRequest;
    private String  KeyEvent="";
    private LatLng mCurrentLatLong;

    private Marker mMarker;

    private FusedLocationProviderClient mFusedLocation;
    LocationCallback mLocationCall = new LocationCallback() {
        @Override
        public void onLocationResult(@NonNull LocationResult locationResult) {
            auth = FirebaseAuth.getInstance();
            mDatabase = FirebaseDatabase.getInstance().getReference();
            mDatabase.child("Users").child(auth.getUid().toString()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        String nombre;
                        nombre = snapshot.child("name").getValue().toString();
                        for (Location location : locationResult.getLocations()) {
                            mCurrentLatLong = new LatLng(location.getLatitude(),location.getLongitude());
                            if (getApplicationContext() != null) {
                                if (mMarker != null) {
                                    mMarker.remove();
                                }
                                mMarker = mMap.addMarker(new MarkerOptions().position(
                                        new LatLng(location.getLatitude(), location.getLongitude()
                                        )).title(nombre).icon(BitmapDescriptorFactory.fromResource(R.drawable.runicoico)));
                                //obtenemos la localizacion del usuario en tiempo real
                                mMap.moveCamera(CameraUpdateFactory.newCameraPosition(
                                        new CameraPosition.Builder()
                                                .target(new LatLng(location.getLatitude(), location.getLongitude()))
                                                .zoom(15f)
                                                .build()
                                ));
                                updateLocation();
                            }
                        }

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }
    };

    private ActivityMapsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Intent intent = getIntent();
        KeyEvent = intent.getStringExtra(MenuPrincipalActivity.KEY_EVENT);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mFusedLocation = LocationServices.getFusedLocationProviderClient(this);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        mGeofireProvider = new GeofireProvider();
    }
    private void updateLocation(){
        if(existSesion() && mCurrentLatLong != null){
            mGeofireProvider.saveLocation(FirebaseAuth.getInstance().getCurrentUser().getUid().toString(),mCurrentLatLong);
        }
    }

public boolean existSesion(){
        boolean exist =false;
            if(auth.getCurrentUser() != null){
            exist=true;
        }
        return true;
}
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.setMyLocationEnabled(true);
        mLocationRequest = LocationRequest.create()
                .setInterval(1000)
                .setFastestInterval(1000)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setSmallestDisplacement(5);
        startLocation();
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                    mFusedLocation.requestLocationUpdates(mLocationRequest, mLocationCall, Looper.myLooper());
                    mMap.setMyLocationEnabled(true);
                }

            }
        }
    }


    private void startLocation() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mFusedLocation.requestLocationUpdates(mLocationRequest, mLocationCall, Looper.myLooper());
                mMap.setMyLocationEnabled(true);
            } else {
                checkLocationPermissions();
            }
        } else {
            mFusedLocation.requestLocationUpdates(mLocationRequest, mLocationCall, Looper.myLooper());
            mMap.setMyLocationEnabled(true);
        }
    }


    private void checkLocationPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                new AlertDialog.Builder(this).setTitle("Debes permitir los permisos para continuar").setMessage("Esta aplicacion requiere los permisos de ubicación para poder utilizarse").setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        ActivityCompat.requestPermissions(MapsActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
                    }
                }).create().show();
            } else {
                ActivityCompat.requestPermissions(MapsActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
            }
        }
    }
}
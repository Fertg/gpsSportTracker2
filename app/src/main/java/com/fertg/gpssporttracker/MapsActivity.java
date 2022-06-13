package com.fertg.gpssporttracker;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationListener;

import android.Manifest;
import android.app.ActivityOptions;
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
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.location.LocationResult;
import com.fertg.gpssporttracker.databinding.ActivityMapsBinding;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
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
//clase que carga el mapa para compartir ubicacion
public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Button btndisc;
    private SupportMapFragment mapFragment;
    private GeofireProvider mGeofireProvider;
    private final static int LOCATION_REQUEST_CODE = 1;
    private FirebaseAuth auth;
    private DatabaseReference mDatabase;
    private LocationRequest mLocationRequest;
    private String  KeyEvent="";
    private LatLng mCurrentLatLong;
    private MapView mMapView;
    private Marker mMarker;
    //API DE GOOGLE
    private static final String MAPVIEW_BUNDLE_KEY = "AIzaSyAycNx8SvegwhjRFU_nsZbyx-lkRLAyz4E";
    //atributo clase google para obtener geolocalizacion
    private FusedLocationProviderClient mFusedLocation;
    //metodo que funciona cuando la ubicación cambia con funcion lambda
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
                            //borramos el marcador cuando nos movemos
                            if (getApplicationContext() != null) {
                                if (mMarker != null) {
                                    mMarker.remove();
                                }
                                //volvemos a pintar el marcador
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
                                //actualizamos localizacion
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
        btndisc=findViewById(R.id.btnDisconect);
        KeyEvent = intent.getStringExtra(MenuPrincipalActivity.KEY_EVENT);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mFusedLocation = LocationServices.getFusedLocationProviderClient(this);
        //SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
          //      .findFragmentById(R.id.map);
        //mapFragment.getMapAsync(this);
        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY);
        }
        mMapView = (MapView) findViewById(R.id.mapView);
        mMapView.onCreate(mapViewBundle);

        mMapView.getMapAsync(this);

        mGeofireProvider = new GeofireProvider();

        btndisc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mFusedLocation.removeLocationUpdates(mLocationCall);
                mGeofireProvider.removeLocation(FirebaseAuth.getInstance().getCurrentUser().getUid().toString());
                Intent in = new Intent(MapsActivity.this, MenuPrincipalActivity.class);
                startActivity(in, ActivityOptions.makeSceneTransitionAnimation(MapsActivity.this).toBundle());
                Toast.makeText(MapsActivity.this, "Desconectado", Toast.LENGTH_SHORT).show();
            }
        });

    }
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        Bundle mapViewBundle = outState.getBundle(MAPVIEW_BUNDLE_KEY);
        if (mapViewBundle == null) {
            mapViewBundle = new Bundle();
            outState.putBundle(MAPVIEW_BUNDLE_KEY, mapViewBundle);
        }

        mMapView.onSaveInstanceState(mapViewBundle);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mMapView.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mMapView.onStop();
    }

    //save en el geoprovider
    private void updateLocation(){
        if(existSesion() && mCurrentLatLong != null){
            mGeofireProvider.saveLocation(FirebaseAuth.getInstance().getCurrentUser().getUid().toString(),mCurrentLatLong);
        }
    }
    @Override
    protected void onPause() {
        mMapView.onPause();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        mMapView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }
public boolean existSesion(){
        boolean exist =false;
            if(auth.getCurrentUser() != null){
            exist=true;
        }
        return true;
}

//cuando el mapa está activo, tiene permisos creamos la primera ubicacion e invocamos al starLocation
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

//chekeamos permisos y actualizamos la localización. Ya comenzará a actualizarse en 2º plano cada vez que nos movamos hasta detenerla
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

//Check de permisos, en algunos androids con SDK superior a 21 puede no funcionar la ubicacion en 2º plano
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
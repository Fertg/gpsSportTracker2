package com.fertg.gpssporttracker;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.fertg.gpssporttracker.databinding.ActivityMapsViewsBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class MapsViewsActivity extends FragmentActivity implements OnMapReadyCallback {

    private ActivityMapsViewsBinding binding;
    private GoogleMap mMap;
    private SupportMapFragment mapFragment;
    private GeofireProvider mGeofireProvider;
    private final static int LOCATION_REQUEST_CODE = 1;
    private FirebaseAuth auth;
    private DatabaseReference mDatabase;
    private LocationRequest mLocationRequest;
    private String keyEvent = "";
    private LatLng mCurrentLatLong;
    private List<Marker> mRunnerMarkers = new ArrayList<>();
    String ubicacion="";
    private Marker mMarker;
    private RunnerProvider runnerProvider;
 private int mCounter=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        keyEvent = intent.getStringExtra(MenuPrincipalActivity.KEY_EVENT);
        mGeofireProvider = new GeofireProvider();
        binding = ActivityMapsViewsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        auth = FirebaseAuth.getInstance();

        runnerProvider = new RunnerProvider();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mCurrentLatLong =  buscar(keyEvent);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        getActiveRunner();

    }

    private void getActiveRunner() {
        LatLng runnerLatLng=buscar(keyEvent);
        mGeofireProvider.getActiveRunners(runnerLatLng).addGeoQueryEventListener(
                new GeoQueryEventListener() {
                    @Override
                    public void onKeyEntered(String key, GeoLocation location) {
                        //añadir marcadores de los corredores que están en el evento
                        for (Marker mMarker : mRunnerMarkers) {
                            if (mMarker.getTag() != null) {
                                if (mMarker.getTag().equals(key)) {
                                    return;
                                }
                            }
                        }
                        mDatabase.child("Users").child(key).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    String nombre;
                                    nombre = snapshot.child("name").getValue().toString();
                                    LatLng runLat = new LatLng(location.latitude, location.longitude);
                                    Marker mMarker = mMap.addMarker(new MarkerOptions().position(runLat).title(nombre).icon(BitmapDescriptorFactory.fromResource(R.drawable.location)));
                                    mMarker.setTag(key);
                                    Log.e("nombre corredor: ", nombre) ;
                                    getRunnersInfo();
                                    mRunnerMarkers.add(mMarker);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                    }



                    @Override
                    public void onKeyExited(String key) {
                        for (Marker mMarker : mRunnerMarkers) {
                            if (mMarker.getTag() != null) {
                                if (mMarker.getTag().equals(key)) {
                                    mMarker.remove();
                                    mRunnerMarkers.remove(mMarker);
                                    return;
                                }
                            }
                        }
                    }

                    @Override
                    public void onKeyMoved(String key, GeoLocation location) {
                        // Actualizar la posicione de cada conductor
                        for (Marker mMarker : mRunnerMarkers) {
                            if (mMarker.getTag() != null) {
                                if (mMarker.getTag().equals(key)) {
                                    mMarker.setPosition(new LatLng(location.latitude, location.longitude));
                                    return;
                                }
                            }
                        }

                    }

                    @Override
                    public void onGeoQueryReady() {

                    }

                    @Override
                    public void onGeoQueryError(DatabaseError error) {

                    }
                });
    }
    public LatLng buscar(String keyEvent) {

        String addressStr = keyEvent;
        double latitude, longtitude;
        LatLng latLongEv = null;
        Geocoder geoCoder = new Geocoder(this);
        try {
            List<Address> addresses = geoCoder.getFromLocationName(addressStr, 1);
            if (addresses.size() > 0) {

                latitude = addresses.get(0).getLatitude();
                longtitude = addresses.get(0).getLongitude();
                latLongEv = new LatLng(latitude, longtitude);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return latLongEv;
    }
    private void getRunnersInfo() {
        mCounter= 0;
        for(Marker marker:mRunnerMarkers){
            runnerProvider.getRunner(marker.getTag().toString()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    mCounter= mCounter + 1;
                    if(snapshot.exists()) {
                        if(snapshot.hasChild("name")){
                            String name = snapshot.child("name").getValue().toString();
                            marker.setTitle(name);
                        }

                        if(mCounter== mRunnerMarkers.size()){
                            mMap.setInfoWindowAdapter(new PopupAdapter(MapsViewsActivity.this,getLayoutInflater()));

                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }


    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng ubi = buscar(keyEvent);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(
                new CameraPosition.Builder()
                        .target(ubi)
                        .zoom(14f)
                        .build()));

    }


}
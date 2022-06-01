package com.fertg.gpssporttracker;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class GeofireProvider {
    private DatabaseReference mDatabase;
    private GeoFire mGeofire;

    public GeofireProvider() {
       mDatabase = FirebaseDatabase.getInstance().getReference().child("active_runers");
       mGeofire = new GeoFire(mDatabase);




    }

    public void saveLocation(String id, LatLng latlong){
        mGeofire.setLocation(id, new GeoLocation(latlong.latitude,latlong.longitude));
    }

    public void removeLocation(String id){
        mGeofire.removeLocation(id);

    }



    public GeoQuery getActiveRunners(LatLng latlng){
        GeoQuery geoQuery = mGeofire.queryAtLocation(new GeoLocation(latlng.latitude, latlng.longitude),20);
        geoQuery.removeAllListeners();
        return geoQuery;
    }
}

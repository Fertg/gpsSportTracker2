package com.fertg.gpssporttracker;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
//Clase para obtener/guardar la ubicacion de los corredores activos
public class GeofireProvider {
    private DatabaseReference mDatabase;
    private GeoFire mGeofire;
//obtengo la instancia
    public GeofireProvider() {
       mDatabase = FirebaseDatabase.getInstance().getReference().child("active_runers");
       mGeofire = new GeoFire(mDatabase);
    }
//guardo la localizacion con un id longitud y latitud
    public void saveLocation(String id, LatLng latlong){
        mGeofire.setLocation(id, new GeoLocation(latlong.latitude,latlong.longitude));
    }
//metodo para borrar el marcador cada vez que se mueve
    public void removeLocation(String id){
        mGeofire.removeLocation(id);
    }
//obtener los corredores activos
    public GeoQuery getActiveRunners(LatLng latlng){
        //mediante una querydeLocalizacion le paso la ubicacion de la persona que visualiza longitud y latitud en un radio de 20km
        //TODO en un futuro permitir aumentar radio desde ventana
        GeoQuery geoQuery = mGeofire.queryAtLocation(new GeoLocation(latlng.latitude, latlng.longitude),20);
        geoQuery.removeAllListeners();
        return geoQuery;
    }
}

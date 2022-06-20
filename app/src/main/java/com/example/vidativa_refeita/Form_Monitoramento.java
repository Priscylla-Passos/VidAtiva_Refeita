package com.example.vidativa_refeita;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.loader.app.LoaderManager;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.vidativa_refeita.databinding.ActivityFormMonitoramentoBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class Form_Monitoramento extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityFormMonitoramentoBinding binding;

    //Atributos para GPS
    private static final int REQUEST_LOCATION_UPDATES = 2;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private LocationRequest mLocationRequest;
    private LocationCallback mLocationCallback;

    //Atributos Marcador
    private Marker userMarker;

    //Atributos distancia, tempo
    Location currentPosition, lastPosition;
    boolean firstFix = true;
    double distanciaAcumulada;
    long initialTime, currentTime, elapseTime;

    //Firebase
    FirebaseFirestore bd_configuracao = FirebaseFirestore.getInstance();
    private String configuracao;
    private String tipo_mapa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityFormMonitoramentoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        startLoction();
        initialTime = System.currentTimeMillis();
    }

    private void startLoction() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)==
         PackageManager.PERMISSION_GRANTED){
            mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
            mLocationRequest = LocationRequest.create();
            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            mLocationRequest.setInterval(5*1000);
            mLocationRequest.setFastestInterval(1*1000);

            mLocationCallback = new LocationCallback(){
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    super.onLocationResult(locationResult);
                    Location location = locationResult.getLastLocation();
                    updateMapPosition(location);
                }
            };
            mFusedLocationProviderClient.requestLocationUpdates(mLocationRequest, mLocationCallback, null);
        }else{
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION_UPDATES);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_LOCATION_UPDATES){
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                startLoction();
            }else{
                Toast.makeText(this, "Sem Permissão para mostrar atualizações da sua Localização", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mFusedLocationProviderClient != null)
            mFusedLocationProviderClient.removeLocationUpdates(mLocationCallback);
    }

    //atualiza posição do Mapa
    public void updateMapPosition(Location location){
        currentTime = System.currentTimeMillis();
        elapseTime = currentTime-initialTime;
        if (firstFix){
            firstFix = false;
            currentPosition=lastPosition=location;
            distanciaAcumulada = 0;
        }else{
            lastPosition = currentPosition;
            currentPosition = location;
            distanciaAcumulada += currentPosition.distanceTo(lastPosition);

        }
        //colocar aqui a distancia acumulada em metros
        //colocar aqui o temp transcorrido elapsedTime/1000
        LatLng userPosition = new LatLng(location.getLatitude(), location.getLongitude());
        if (mMap != null){
            if (userMarker == null){
                userMarker = mMap.addMarker(new MarkerOptions().position(userPosition));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userPosition, 15f));
            }else {
                userMarker.setPosition(userPosition);
                mMap.moveCamera(CameraUpdateFactory.newLatLng(userPosition));
            }

        }

    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

       configuracao = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DocumentReference documentReference = bd_configuracao.collection("Configuracao").document(configuracao);

        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                if (documentSnapshot != null){
                    tipo_mapa = documentSnapshot.getString("Tipo do Mapa");
                    if ("Satelite".equalsIgnoreCase(tipo_mapa)){
                        mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                    }else if ("Vetorial".equalsIgnoreCase(tipo_mapa)){
                        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                    }
                }
            }
        });

    }
}
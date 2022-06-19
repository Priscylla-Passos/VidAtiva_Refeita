package com.example.vidativa_refeita;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;


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
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import com.example.vidativa_refeita.databinding.ActivityMapsMonitoramentoBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;


public class Form_MapsMonitoramento extends FragmentActivity implements OnMapReadyCallback{
    //mapa
    private GoogleMap mMap;
    private ActivityMapsMonitoramentoBinding binding;
    private UiSettings settingsUi;


    //Firebase
    FirebaseFirestore bd_configuracao = FirebaseFirestore.getInstance();
    private String configuracao;

    private String tipo_mapa;

    //Atributos permissão
    private static final int REQUEST_LOCATION_UPDATES = 2;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private LocationRequest mLocationRequest;
    private LocationCallback mLocationCallback;

    //Atributo Marcador
    private Marker userMarker;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsMonitoramentoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        startLocation();
    }

    private void startLocation() {
        if (ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED){
            mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
            mLocationRequest = LocationRequest.create();
            mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
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
            //Solicite a Permissão
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
            REQUEST_LOCATION_UPDATES);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_LOCATION_UPDATES){
            if(grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                startLocation();
            }else{
                Toast.makeText(this, "Sem permissão para mostrar atualizações", Toast.LENGTH_SHORT).show();
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

    private void updateMapPosition(Location location) {
        if (mMap != null){
            LatLng userPosition = new LatLng(location.getLatitude(), location.getLongitude());
            userMarker = mMap.addMarker(new MarkerOptions().position(userPosition).title("inicio do Treino"));
            CameraPosition cameraPosition = new CameraPosition.Builder().zoom(55).target(userPosition).build();
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }

    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        UiSettings settingsUi = mMap.getUiSettings();






        configuracao = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DocumentReference documentReference = bd_configuracao.collection("Configuracao").document(configuracao);

        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {

                if (documentSnapshot != null) {

                    tipo_mapa = documentSnapshot.getString("Tipo do Mapa");

                    if ("Satelite".equalsIgnoreCase(tipo_mapa)){
                        mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                    }else if ("Vetorial".equalsIgnoreCase(tipo_mapa)){
                        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                    }

                }
            }
        });


       //Configuração dos elelmentos da interfaca gráfica
        settingsUi.setAllGesturesEnabled(true);
        settingsUi.setCompassEnabled(true);



    }




}
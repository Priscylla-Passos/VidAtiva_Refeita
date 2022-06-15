package com.example.vidativa_refeita;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import androidx.annotation.UiContext;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;


import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
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

import java.util.ArrayList;


public class Form_MapsMonitoramento extends FragmentActivity implements OnMapReadyCallback{
    //mapa
    private GoogleMap mMap;
    private ActivityMapsMonitoramentoBinding binding;
    private UiSettings settingsUi;


    //Firebase
    FirebaseFirestore bd_configuracao = FirebaseFirestore.getInstance();
    private String configuracao;

    private String exercicio;
    private String orientacao_mapa;
    private String tipo_mapa;
    private String unidade_velocidade;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsMonitoramentoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


    }



    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        UiSettings settingsUi = mMap.getUiSettings();

        LatLng jardimdealah = new LatLng(-12.996735, -38.442789);

        mMap.addMarker(new MarkerOptions().position(jardimdealah).title("Octávio Mangabeirah"));
        CameraPosition cameraPosition = new CameraPosition.Builder().zoom(55).target(jardimdealah).build();

        configuracao = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DocumentReference documentReference = bd_configuracao.collection("Configuracao").document(configuracao);

        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {

                if (documentSnapshot != null) {

                    tipo_mapa = documentSnapshot.getString("Tipo do Mapa");

                    if ("vetorial".equalsIgnoreCase(tipo_mapa)){
                        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                    }else {
                        mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                    }

                }
            }
        });


       //Configuração dos elelmentos da interfaca gráfica
        settingsUi.setAllGesturesEnabled(true);
        settingsUi.setCompassEnabled(true);

        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

    }




}
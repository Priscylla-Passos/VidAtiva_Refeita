package com.example.vidativa_refeita;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import android.os.Bundle;
import android.util.Log;

import com.example.vidativa_refeita.databinding.ActivityFormHistoricoBinding;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;


import java.text.DecimalFormat;
import java.text.Format;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.List;

public class Form_Historico extends FragmentActivity implements OnMapReadyCallback {
    private GoogleMap mMap;
    private ActivityFormHistoricoBinding binding;

    //Atributos Firebase
    FirebaseFirestore bd_historico = FirebaseFirestore.getInstance();
    private String historico, historicoID;
    private String distanciaTotal;
    private String tempo;
    private String velocidademedia;
    private String calorias;
    private String unidadeVelocidade;

    //atributo para trajetoria
    private List<HashMap<String, Double>> trajetoria;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityFormHistoricoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // carregar trajetoria do banco;
        carregarHistorico();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    private void carregarHistorico() {
        historico = FirebaseAuth.getInstance().getCurrentUser().getUid();

        DocumentReference documentReference = bd_historico.collection("Historico").document(historico);
        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if(value != null) {
                    distanciaTotal = value.getString("Distancia Total");
                    tempo = value.getString("Tempo");
                    velocidademedia = value.getString("Velocidade Média");

                    calorias = value.getString("Calorias");
                    trajetoria = (List<HashMap<String, Double>>) value.get("Coordenadas");

                    binding.txtDistanciaValue.setText( distanciaTotal );
                    binding.textTime.setText(tempo);
                    unidadeVelocidade = value.getString("Unidade de Velocidade");
                    binding.textVelMediaValue.setText( velocidademedia );

                    binding.textGastoValue
                            .setText(calorias);

                }
            }
        });

    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;


      //  mMap.addPolyline(new PolylineOptions().addAll());
    }
}
package com.example.vidativa_refeita;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.Chronometer;
import android.widget.CompoundButton;
import android.widget.TextView;
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
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.vidativa_refeita.databinding.ActivityFormMonitoramentoBinding;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.ArrayList;
import java.util.List;

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
    private LatLng userPosition;
    private BitmapDescriptor icon;
    private Polyline polyline;
    private List<LatLng> list;

    // Cronometro
    private Chronometer chronometer;
    private boolean isPlaying = false;
    private long PauseOffSet = 0;
    private boolean clicked = false;

    //Atributos distancia, tempo
    Location currentPosition, lastPosition;
    boolean firstFix = true;
    double distanciaAcumulada;
    long initialTime, currentTime, elapseTime;

    //Firebase
    FirebaseFirestore bd_configuracao = FirebaseFirestore.getInstance();
    private String configuracao;
    private String tipo_mapa, exercicio, orientacao_mapa, unidade_velocidade;

    //Componentes
    private TextView text_velocidade,text_distancia;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityFormMonitoramentoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        starComponents();
        getInformationBd();

        // Cronometro
        iniCronometro();
        resetComponents();

        startLocalization();
        initialTime = System.currentTimeMillis();

    }

    private void startLocalization() {
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
                startLocalization();
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

    if (clicked == true) {
        if (firstFix) {
            firstFix = false;
            currentPosition = lastPosition = location;
            distanciaAcumulada = 0;
        } else {
            lastPosition = currentPosition;
            currentPosition = location;
            distanciaAcumulada += currentPosition.distanceTo(lastPosition);
            speedTime();
        }

    }



        userPosition = new LatLng(location.getLatitude(), location.getLongitude());
        if (mMap != null){
            if (userMarker == null){
                userMarker = mMap.addMarker(new MarkerOptions().position(userPosition).icon(icon));
                 positionCamera();
            }else {
                userMarker.setPosition(userPosition);
                list.add(userPosition);
                drawRoute ();
                mMap.moveCamera(CameraUpdateFactory.newLatLng(userPosition));
            }

        }

    }

    private void positionCamera() {
        if ("Vetorial".equalsIgnoreCase(tipo_mapa)) {
           mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userPosition, 55f));
        }else if ("Satelite".equalsIgnoreCase(tipo_mapa)){
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userPosition, 80f));
        }

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
         mMap = googleMap;
         list = new ArrayList<LatLng>();



    }
    private  void getInformationBd(){
        configuracao = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DocumentReference documentReference = bd_configuracao.collection("Configuracao").document(configuracao);

        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                if (documentSnapshot != null){
                    tipo_mapa = documentSnapshot.getString("Tipo do Mapa");
                    exercicio = documentSnapshot.getString("Exercicio");
                    orientacao_mapa = documentSnapshot.getString("Orientação do Mapa");
                    unidade_velocidade = documentSnapshot.getString("Unidade de Velocidade");

                    if ("Satelite".equalsIgnoreCase(tipo_mapa)){
                        mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                    }else if ("Vetorial".equalsIgnoreCase(tipo_mapa)){
                        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                    }
                    if ("Bicicleta".equalsIgnoreCase(exercicio)){
                        icon = BitmapDescriptorFactory.fromResource(R.drawable.ic_bike);
                       // binding.icBike.setVisibility(View.VISIBLE);
                    }else if ("Caminhada".equalsIgnoreCase(exercicio)){
                       icon = BitmapDescriptorFactory.fromResource(R.drawable.ic_caminhada);
                     //   binding.icCaminhada.setVisibility(View.VISIBLE);
                    }else if ("Corrida".equalsIgnoreCase(exercicio)){
                        icon = BitmapDescriptorFactory.fromResource(R.drawable.ic_corrida);
                     //   binding.icCorrida.setVisibility(View.VISIBLE);
                    }
                }
            }
        });

    }
    private void starComponents(){
       text_velocidade = findViewById(R.id.velocidade_value);
       text_distancia = findViewById(R.id.distancia_value);


    }

 private void iniCronometro(){

     binding.btParar.setEnabled(false);
     binding.btParar.setText("Zerar");


     try {

         binding.btIniciar.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
             @Override
             public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                 binding.btParar.setEnabled(false);

                 if(b){
                     binding.cronometroValue.setBase(SystemClock.elapsedRealtime()- PauseOffSet);
                     binding.cronometroValue.start();
                     clicked = true;

                     // Mudar background para uma experiência melhor para o usuário
                     // binding.btIniciar.setBackgroundResource(R.drawable.background_toggle_stop);
                     //binding.btIniciar.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_baseline_pause_24, 0,0,0);
                     isPlaying = true;
                     speedTime();
                 }else{
                     binding.cronometroValue.stop();
                     PauseOffSet = SystemClock.elapsedRealtime()- binding.cronometroValue.getBase();
                     clicked = false;
                     // Mudar background para uma experiência melhor para o usuário
                     // binding.btIniciar.setBackgroundResource(R.drawable.background_toggle_play);
                     //binding.btIniciar.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_play, 0,0,0);
                     binding.btIniciar.setCompoundDrawablePadding(10);
                     isPlaying = false;
                     binding.btParar.setEnabled(true);

                 }
             }
         });

         binding.btParar.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 binding.cronometroValue.setBase(SystemClock.elapsedRealtime());
                 binding.btIniciar.setBackgroundResource(R.drawable.background_toggle);
                 PauseOffSet = 0;
                 binding.cronometroValue.stop();
                 isPlaying = false;
                 resetComponents();
                 binding.velocidadeValue.setText("0.0");
                 binding.distanciaValue.setText("0.0");
             }
         });

     }catch (Exception e){
         Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT)
                 .show();
     }

 }

    public void resetComponents(){
        binding.btIniciar.setText("Iniciar");
        binding.btIniciar.setTextOn("Parar");
        binding.btIniciar.setTextOff("Iniciar");
    }

 public void drawRoute (){


        PolylineOptions po;
        if (polyline == null){
            po = new PolylineOptions();
            for(int i = 0, tam = list.size(); i<tam; i++){
                po.add(list.get(i));
            }
            po.color(Color.RED);
            polyline = mMap.addPolyline(po);
        }else{
            polyline.setPoints(list);
        }
 }

 private void speedTime(){

        if ("m/s".equalsIgnoreCase(unidade_velocidade)){
            // distancia acumulada em metros
            text_distancia.setText(String.format("%.2f", distanciaAcumulada));
            binding.distanciaSimbol.setText("metros");
            //temp transcorrido elapsedTime/1000
            text_velocidade.setText(""+elapseTime/1000);
            binding.velocidadeSimbol.setText("Segundos");
        }else if ("km/h".equalsIgnoreCase(unidade_velocidade)){
            text_distancia.setText(String.format("%.2f",distanciaAcumulada/1000));
            binding.distanciaSimbol.setText("Km");
            text_velocidade.setText(String.format("%.2f", elapseTime*3.6));
            binding.velocidadeSimbol.setText("Km/h");
        }



 }

}
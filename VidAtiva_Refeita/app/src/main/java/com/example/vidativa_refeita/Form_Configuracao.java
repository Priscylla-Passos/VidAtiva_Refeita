package com.example.vidativa_refeita;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;


import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;


import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.HashMap;
import java.util.Map;

public class Form_Configuracao extends AppCompatActivity {

    private RadioButton caminhada, corrida, bicicleta, km, ms;
    private RadioButton nenhuma, north, course, vetorial, satelite;
    private final String text_caminhada = "Caminhada";
    private final String text_corrida = "Corrida";
    private final String text_bicicleta = "Bicicleta";
    private final String text_km = "Km/h";
    private final String text_ms = "m/s";
    private final String text_nenhuma = "Nenhuma";
    private final String text_north = "North Up";
    private final String text_course = "Courseh Up";
    private final String text_vetorial = "Vetorial";
    private final String text_satelite = "Satelite";
    private String exercicio, velocidade, orientacao, tipo;


    private Button bt_salvar, bt_limpar;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String configuracaoID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_configuracao);

        getSupportActionBar().hide();

        IniciarComponents();

    }

    //recuperar dados
    @Override
    protected void onStart() {
        super.onStart();

    configuracaoID = FirebaseAuth.getInstance().getCurrentUser().getUid();
    DocumentReference documentReference = db.collection("Configuracao").document(configuracaoID);
    documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
        @Override
        public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
            if (documentSnapshot != null){
                exercicio = documentSnapshot.getString("Exercicio");
                velocidade = documentSnapshot.getString("Unidade de Velocidade");
                orientacao = documentSnapshot.getString("Orientação do Mapa");
                tipo = documentSnapshot.getString("Tipo do Mapa");

                if (exercicio.equals(text_caminhada)){
                    caminhada.setChecked(true);
                }else if (exercicio.equals(text_corrida)){
                    corrida.setChecked(true);
                }else if (exercicio.equals(text_bicicleta)) {
                    bicicleta.setChecked(true);
                }

                if (velocidade.equals(text_km)) {
                    km.setChecked(true);
                }else if (velocidade.equals(text_ms)){
                    ms.setChecked(true);
                }

                if (orientacao.equals(text_nenhuma)){
                    nenhuma.setChecked(true);
                }else if (orientacao.equals(text_north)){
                    north.setChecked(true);
                }else if (orientacao.equals(text_course)){
                    course.setChecked(true);
                }

                if (tipo.equals(text_vetorial)){
                    vetorial.setChecked(true);
                }else if (tipo.equals(text_satelite)){
                    satelite.setChecked(true);
                }
            }
        }
    });


        //bt_salvar
        bt_salvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FirebaseFirestore db = FirebaseFirestore.getInstance();

                Map<String, Object> exercicio = new HashMap<>();
                if (caminhada.isChecked()) {
                    exercicio.put("Exercicio", text_caminhada);
                } else if (corrida.isChecked()) {
                    exercicio.put("Exercicio", text_corrida);
                } else if (bicicleta.isChecked()) {
                    exercicio.put("Exercicio", text_bicicleta);
                }

                if (km.isChecked()) {
                    exercicio.put("Unidade de Velocidade", text_km);
                } else if (ms.isChecked()) {
                    exercicio.put("Unidade de Velocidade", text_ms);
                }

                if (nenhuma.isChecked()) {
                    exercicio.put("Orientação do Mapa", text_nenhuma);
                } else if (north.isChecked()) {
                    exercicio.put("Orientação do Mapa", text_north);
                } else if (course.isChecked()) {
                    exercicio.put("Orientação do Mapa", text_course);
                }

                if (vetorial.isChecked()) {
                    exercicio.put("Tipo do Mapa", text_vetorial);
                } else if (satelite.isChecked()) {
                    exercicio.put("Tipo do Mapa", text_satelite);
                }

                configuracaoID = FirebaseAuth.getInstance().getCurrentUser().getUid();

                DocumentReference documentReference = db.collection("Configuracao").document(configuracaoID);
                documentReference.set(exercicio).addOnSuccessListener(new OnSuccessListener<Void>() {
                    public void onSuccess(Void unused) {
                        Log.d("bd", "Sucesso ao salvar os dados");
                    }
                })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d("bd_e", "Erro ao Salvar os Dados" + e.toString());
                            }
                        });
            }
        });

        bt_limpar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FirebaseFirestore db = FirebaseFirestore.getInstance();

                Map<String, Object> exercicio = new HashMap<>();
                if (caminhada.isChecked()) {
                    exercicio.put("Exercicio", "");
                    caminhada.setChecked(false);
                } else if (corrida.isChecked()) {
                    exercicio.put("Exercicio", " ");
                    corrida.setChecked(false);
                } else if (bicicleta.isChecked()) {
                    exercicio.put("Exercicio", " ");
                    bicicleta.setChecked(false);
                }

                if (km.isChecked()) {
                    exercicio.put("Unidade de Velocidade", " ");
                    km.setChecked(false);
                } else if (ms.isChecked()) {
                    exercicio.put("Unidade de Velocidade", " ");
                    ms.setChecked(false);
                }

                if (nenhuma.isChecked()) {
                    exercicio.put("Orientação do Mapa", " ");
                    nenhuma.setChecked(false);
                } else if (north.isChecked()) {
                    exercicio.put("Orientação do Mapa", " ");
                    north.setChecked(false);
                } else if (course.isChecked()) {
                    exercicio.put("Orientação do Mapa", " ");
                    course.setChecked(false);
                }

                if (vetorial.isChecked()) {
                    exercicio.put("Tipo do Mapa", " ");
                    vetorial.setChecked(false);
                } else if (satelite.isChecked()) {
                    exercicio.put("Tipo do Mapa", " ");
                    satelite.setChecked(false);
                }

                configuracaoID = FirebaseAuth.getInstance().getCurrentUser().getUid();

                DocumentReference documentReference = db.collection("Configuracao").document(configuracaoID);
                documentReference.set(exercicio).addOnSuccessListener(new OnSuccessListener<Void>() {
                    public void onSuccess(Void unused) {
                        Log.d("bd", "Sucesso ao salvar os dados");
                    }
                })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d("bd_e", "Erro ao Salvar os Dados" + e.toString());
                            }
                        });

            }
        });

    }


    private void IniciarComponents() {

        caminhada = findViewById(R.id.rb_caminhada);
        corrida = findViewById(R.id.rb_corrida);
        bicicleta = findViewById(R.id.rb_bicicleta);
        km = findViewById(R.id.rb_km);
        ms = findViewById(R.id.rb_metro);
        nenhuma = findViewById(R.id.rb_nenhuma);
        north = findViewById(R.id.rb_north);
        course = findViewById(R.id.rb_course);
        vetorial = findViewById(R.id.rb_vetorial);
        satelite = findViewById(R.id.rb_satelite);
        bt_salvar = findViewById(R.id.bt_salvar_preferencias);
        bt_limpar = findViewById(R.id.bt_limpar_preferencias);


    }

}

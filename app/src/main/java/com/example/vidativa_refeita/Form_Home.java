package com.example.vidativa_refeita;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;


public class Form_Home extends AppCompatActivity {
    private CardView cardView1, cardView2, cardView3, cardView4;
    FloatingActionButton exitFab, aboutFab, languageFab;
    ExtendedFloatingActionButton addFab;
    Boolean isAllFABVisible;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_home);

       getSupportActionBar().hide();

        IniciarComponentes();

        cardView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Form_Home.this, Form_Perfil.class);
                startActivity(intent);

            }
        });

        cardView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Form_Home.this, Form_Configuracao.class);
                startActivity(intent);

            }
        });

        cardView3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent (Form_Home.this, Form_MapsMonitoramento.class);
                startActivity(intent);
            }
        });

        cardView4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Form_Home.this, Form_Historico.class);
                startActivity(intent);
            }
        });

        addFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isAllFABVisible){
                    cardView4.setVisibility(View.INVISIBLE);
                    exitFab.show();
                    aboutFab.show();
                    languageFab.show();

                    addFab.extend();
                    isAllFABVisible = true;
                }else{
                    cardView4.setVisibility(View.VISIBLE);
                    exitFab.hide();
                    aboutFab.hide();
                    languageFab.hide();

                    addFab.shrink();

                    isAllFABVisible = false;
                }
            }
        });
        exitFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(Form_Home.this, MainActivity.class);
                startActivity(intent);
                finish();

            }
        });
        aboutFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent  intent = new Intent(Form_Home.this, Form_Sobre.class);
                startActivity(intent);


            }
        });
        languageFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }



    private void IniciarComponentes(){
        cardView1 = findViewById(R.id.profile);
        cardView2 = findViewById(R.id.settings);
        cardView3 = findViewById(R.id.monitor_exercises);
        cardView4 = findViewById(R.id.historic);
        addFab = findViewById(R.id.add_fab);
        exitFab = findViewById(R.id.exit_fab);
        aboutFab = findViewById(R.id.about_fab);
        languageFab = findViewById(R.id.language_fab);

        exitFab.setVisibility(View.GONE);
        aboutFab.setVisibility(View.GONE);
        languageFab.setVisibility(View.GONE);


        isAllFABVisible = false;
        addFab.shrink();;

    }

}


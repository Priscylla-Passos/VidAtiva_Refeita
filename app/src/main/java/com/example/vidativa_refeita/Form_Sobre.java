package com.example.vidativa_refeita;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class Form_Sobre extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_sobre);

        getSupportActionBar().hide();
    }
}
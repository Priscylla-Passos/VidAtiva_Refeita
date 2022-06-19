package com.example.vidativa_refeita;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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

public class Form_Perfil extends AppCompatActivity {
    private EditText edit_nome, edit_dt_nascimento, edit_sexo, edit_altura, edit_peso;
    private Button bt_salvar;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String usuarioID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_perfil);

        getSupportActionBar().hide();
        
        IniciarComponentes();
    }

    @Override
    protected void onStart() {
        super.onStart();

        String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        usuarioID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DocumentReference documentReference = db.collection("Usuarios").document(usuarioID);
        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                if(documentSnapshot != null){
                    edit_nome.setText(documentSnapshot.getString("Nome"));
                    edit_nome.setFocusable(false);
                    edit_dt_nascimento.setText(documentSnapshot.getString("Data de Nascimento"));
                    edit_sexo.setText(documentSnapshot.getString("Sexo"));
                    edit_altura.setText(documentSnapshot.getString("Altura"));
                    edit_peso.setText(documentSnapshot.getString("Peso"));
                }
            }
        });
            bt_salvar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String nome = edit_nome.getText().toString();
                    String dt_nascimento = edit_dt_nascimento.getText().toString();
                    String sexo = edit_sexo.getText().toString();
                    String altura = edit_altura.getText().toString();
                    String peso = edit_peso.getText().toString();

                    FirebaseFirestore db = FirebaseFirestore.getInstance();

                    Map<String, Object> usuarios = new HashMap<>();
                    usuarios.put("Nome", nome);
                    usuarios.put("Data de Nascimento", dt_nascimento);
                    usuarios.put("Sexo", sexo);
                    usuarios.put("Altura", altura);
                    usuarios.put("Peso", peso);

                    usuarioID = FirebaseAuth.getInstance().getCurrentUser().getUid();

                    DocumentReference documentReference1 = db.collection("Usuarios").document(usuarioID);
                    documentReference1.set(usuarios).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d("db", "Sucesso ao salvar dados");
                            Toast.makeText(Form_Perfil.this, "Salvo Com Sucesso!", Toast.LENGTH_SHORT).show();}
                    })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d("db_error", "Erro ao salvar dados" + e.toString());
                                    Toast.makeText(Form_Perfil.this, "Erro Ao salvar. Tente Novamente", Toast.LENGTH_SHORT).show();
                                }
                            });


                }
            });

    }




    private void IniciarComponentes() {
        edit_nome = findViewById(R.id.edit_nome);
        edit_dt_nascimento = findViewById(R.id.edit_dt_nascimento);
        edit_sexo = findViewById(R.id.edit_sexo);
        edit_altura = findViewById(R.id.edit_altura);
        edit_peso = findViewById(R.id.edit_peso);
        bt_salvar = findViewById(R.id.bt_salvar);

    }
}
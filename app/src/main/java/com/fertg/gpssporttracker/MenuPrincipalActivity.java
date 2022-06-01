package com.fertg.gpssporttracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.fertg.gpssporttracker.ModeloDatos.Evento;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class MenuPrincipalActivity extends AppCompatActivity {
    public static final String KEY_EVENT ="KEY_EVENT";
    private Button share,limpiar,create,misEvents,seguir;
    private TextView nombreL,typeRol;
    private FirebaseAuth auth;
    private DatabaseReference mDatabase;
    private TextView codigoEvento,locaEvento;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_principal);
        nombreL=findViewById(R.id.tvNombre);
        share=findViewById(R.id.btn_share);
        create=findViewById(R.id.btn_Crear);
        limpiar=findViewById(R.id.btn_limp);
        seguir=findViewById(R.id.btn_verEventoT);
        misEvents=findViewById(R.id.btn_eventosCreados);
        typeRol=findViewById(R.id.textVrol);
        codigoEvento=findViewById(R.id.input_codigo);
        locaEvento=findViewById(R.id.input_loca);
        auth = FirebaseAuth.getInstance();
        create.setVisibility(View.INVISIBLE);
        misEvents.setVisibility(View.INVISIBLE);
        mDatabase = FirebaseDatabase.getInstance().getReference();

        chekType();

 seguir.setOnClickListener(new View.OnClickListener() {
     @Override
     public void onClick(View view) {
         List<String> listaCode= new ArrayList<>();
         String localidadE=locaEvento.getText().toString();
         mDatabase.child("Events").addValueEventListener(new ValueEventListener() {
             @Override
             public void onDataChange(@NonNull DataSnapshot snapshot) {
                 for (DataSnapshot objSnap : snapshot.getChildren()) {
                     String code2 = objSnap.child("lugarEvento").getValue().toString();
                     listaCode.add(code2);

                     if(localidadE.equalsIgnoreCase(code2)){
                         Intent in = new Intent(MenuPrincipalActivity.this, MapsViewsActivity.class);
                         in.putExtra(KEY_EVENT, localidadE);
                         startActivity(in, ActivityOptions.makeSceneTransitionAnimation(MenuPrincipalActivity.this).toBundle());
                                            }}
             }

             @Override
             public void onCancelled(@NonNull DatabaseError error) {
             }
         });
         if(!codigoEvento.getText().toString().isEmpty()){
             Toast.makeText(MenuPrincipalActivity.this, "Código evento vacío.", Toast.LENGTH_SHORT).show();
         }}
 });



        limpiar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                limpiar.setHint("Introduzca el código del evento ");
            }
        });
    share.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            List<String> listaCode= new ArrayList<>();
            mDatabase.child("Events").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot objSnap : snapshot.getChildren()) {
                        String code2 = objSnap.child("codigoEvento").getValue().toString();
                        listaCode.add(code2);

                        if(Objects.equals(codigoEvento.getText().toString().toUpperCase(Locale.ROOT), code2)){
                            Intent in = new Intent(MenuPrincipalActivity.this, MapsActivity.class);
                            in.putExtra(KEY_EVENT, codigoEvento.getText().toString());
                            startActivity(in, ActivityOptions.makeSceneTransitionAnimation(MenuPrincipalActivity.this).toBundle());

                        }}
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
           if(!codigoEvento.getText().toString().isEmpty()){
            Toast.makeText(MenuPrincipalActivity.this, "Código evento vacío.", Toast.LENGTH_SHORT).show();
        }}
    });

        misEvents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in = new Intent(MenuPrincipalActivity.this, misEventosActivity.class);
                startActivity(in, ActivityOptions.makeSceneTransitionAnimation(MenuPrincipalActivity.this).toBundle());
            }
        });

        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in = new Intent(MenuPrincipalActivity.this, EventCreateActivity.class);
                startActivity(in, ActivityOptions.makeSceneTransitionAnimation(MenuPrincipalActivity.this).toBundle());
            }
        });

    }

    public void chekType(){
        if(auth!=null){
        mDatabase.child("Users").child(auth.getUid().toString()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String nombre;
                    nombre = snapshot.child("name").getValue().toString();
                    nombreL.setText(nombre.toString());
                    String type = snapshot.child("type").getValue().toString();


                    if (type.equals("1")) {
                        create.setVisibility(View.VISIBLE);
                        misEvents.setVisibility(View.VISIBLE);
                        share.setEnabled(false);
                        typeRol.setText("Organizador de Eventos");
                        ;
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



    }else{
            Toast.makeText(this, "MODO INVITADO", Toast.LENGTH_SHORT).show();
    }
    }
}
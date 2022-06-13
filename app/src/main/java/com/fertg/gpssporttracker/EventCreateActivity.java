package com.fertg.gpssporttracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.fertg.gpssporttracker.ModeloDatos.Corredor;
import com.fertg.gpssporttracker.ModeloDatos.Evento;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
//Clase para crear eventos
public class EventCreateActivity extends AppCompatActivity {
    private Button geneCode, clearCode, btCreate,btMod;
    private TextView code;
    private TextView nombreEvent, localidadEvent, modalidEvent, numeEvent, fechaEvent;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private String codeBBD = "";
   private Spinner spinnerEventos;
    @Override


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_create);
        spinnerEventos=findViewById(R.id.spinnerEvento);

        //GENERAR CODIGO
        code = findViewById(R.id.codigoGenerated);
        geneCode = findViewById(R.id.btn_generateCode);
        clearCode = findViewById(R.id.btn_clearCreate);
        btCreate = findViewById(R.id.btn_create);
        btMod = findViewById(R.id.btn_modi);
        btCreate.setEnabled(false);
        int i = 6;
        //RECUPERAR DATOS
        nombreEvent = findViewById(R.id.ev_inputNombre);
        localidadEvent = findViewById(R.id.ev_inputLoc);
        modalidEvent = findViewById(R.id.ev_inputMod);
        numeEvent = findViewById(R.id.ev_inputNum);
        fechaEvent = findViewById(R.id.ev_inputFecha);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        FirebaseUser user = mAuth.getCurrentUser();

        //LISTENER BOTONES
        btMod.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadCodes();
                btCreate.setText("Actualizar Evento");
            }
        });
        btCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final int[] flag = {0};
//referencias en la bbdd con el nodo evento
                mDatabase.child("Events").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//añadir valor con el keyEvent como PK
                        for( final DataSnapshot snapshot1:  snapshot.getChildren()){
                            mDatabase.child("Events").child(snapshot1.getKey()).addValueEventListener(new ValueEventListener() {

                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    String codeBDD="";
                                    Evento event = snapshot1.getValue(Evento.class);
                                     codeBDD= event.getCodigoEvento();
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
//obtener los campos de los inputs
                        } if(flag[0] ==0){
                            String nombreE = nombreEvent.getText().toString();
                            String locaE = localidadEvent.getText().toString();
                            String modE = modalidEvent.getText().toString();
                            String numbE = numeEvent.getText().toString();
                            String dateE = fechaEvent.getText().toString();
                            String uSerUII = mAuth.getCurrentUser().getUid().toString();
                            Evento eventoDatos = new Evento();
                            cargarDatos(nombreE, locaE, modE, numbE, dateE, uSerUII);
                            Log.e("Evento creado", flag[0] +"");
                            btCreate.setEnabled(false);

                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });

// btn generar codigo
        geneCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                codeBBD = generateCode(i).toUpperCase(Locale.ROOT).toString();
                code.setText(codeBBD);
                btCreate.setText("Crear Evento");
                geneCode.setEnabled(false);
            }
        });
        //limpiar codigo
        clearCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                code.setText("Código del evento");
                geneCode.setEnabled(true);
            }
        });

    }
//cargar los datos en la bbdd
    private void cargarDatos(String nombreE, String locaE, String modE, String numbE, String dateE, String uSerUII) {

        if ( !nombreE.isEmpty() ||  !locaE.isEmpty() ||  !modE.isEmpty() ||  !numbE.isEmpty() ||  !dateE.isEmpty() ) {
            Map<String, Object> datosEvento = new HashMap<>();

        datosEvento.put("codigoEvento", codeBBD);
        datosEvento.put("nombreEvento", nombreE);
        datosEvento.put("lugarEvento", locaE);
        datosEvento.put("Modalidad", modE);
        datosEvento.put("numeroCorredores", numbE);
        datosEvento.put("fechaEvento", dateE);
        datosEvento.put("uSerUII", uSerUII);
        mDatabase.child("Events").child(codeBBD).setValue(datosEvento);
        Toast.makeText(EventCreateActivity.this, "Evento Creado", Toast.LENGTH_SHORT).show();
        limpiarC();
        }else{
            Toast.makeText(EventCreateActivity.this, "Campos vacios", Toast.LENGTH_SHORT).show();
        }
    }

    public void loadCodes(){
         List<Evento> listEvento = new ArrayList<>();
        mDatabase.child("Events").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    for (DataSnapshot objSnap: snapshot.getChildren()) {
//Control de recuperar solo los del usuario logueado
                            if (objSnap.child("uSerUII").getValue().toString().equalsIgnoreCase(mAuth.getUid())){
                        String code = objSnap.getKey();
                        String nombreEvento= objSnap.child("nombreEvento").getValue().toString();
                        String lugarEvento= objSnap.child("lugarEvento").getValue().toString();
                        String modalidad= objSnap.child("Modalidad").getValue().toString();
                        String numeroCorredores= objSnap.child("numeroCorredores").getValue().toString();
                        String fechaEvento= objSnap.child("fechaEvento").getValue().toString();
                        listEvento.add(new Evento( nombreEvento,  lugarEvento,  modalidad,  numeroCorredores, code,  fechaEvento));
                            }}

                    ArrayAdapter<Evento> arrayAdapter = new ArrayAdapter<>(EventCreateActivity.this, android.R.layout.simple_dropdown_item_1line,listEvento);
               spinnerEventos.setAdapter(arrayAdapter);
                    spinnerEventos.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                       nombreEvent.setText(listEvento.get(i).getNombreEvento().toString());
                       localidadEvent.setText(listEvento.get(i).getLugarEvento().toString());
                       modalidEvent.setText(listEvento.get(i).getModalidad().toString());
                       numeEvent.setText(listEvento.get(i).getNumeroCorredores().toString());
                       fechaEvent.setText(listEvento.get(i).getFechaEvento().toString());
                        btCreate.setEnabled(true);
                            codeBBD = listEvento.get(i).getCodigoEvento().toString();
                            code.setText(codeBBD);
                            btMod.setEnabled(false);
                        }
                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {

                        }
                    });

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    //Generar codigo RAMDOM
    public String generateCode(int i) {

        byte[] bytearray;

        bytearray = new byte[256];

        String mystring;
        StringBuffer thebuffer;
        String theAlphaNumericS;

        new Random().nextBytes(bytearray);

        mystring
                = new String(bytearray, Charset.forName("UTF-8"));

        thebuffer = new StringBuffer();

        //remove all spacial char
        theAlphaNumericS
                = mystring
                .replaceAll("[^A-Z0-9]", "");

        //random selection
        for (int m = 0; m < theAlphaNumericS.length(); m++) {

            if (Character.isLetter(theAlphaNumericS.charAt(m))
                    && (i > 0)
                    || Character.isDigit(theAlphaNumericS.charAt(m))
                    && (i > 0)) {

                thebuffer.append(theAlphaNumericS.charAt(m));
                i--;
            }
        }
        btCreate.setEnabled(true);
        // the resulting string
        return thebuffer.toString();
    }
//limpiar campos
    private void limpiarC() {
        nombreEvent.setText("");
        localidadEvent.setText("");
        modalidEvent.setText("");
        numeEvent.setText("");
        fechaEvent.setText("");
    }
}

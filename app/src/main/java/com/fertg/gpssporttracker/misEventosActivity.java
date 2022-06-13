package com.fertg.gpssporttracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListAdapter;

import com.fertg.gpssporttracker.ModeloDatos.Evento;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

//Cargar RV con los eventos de cada organizador
public class misEventosActivity extends AppCompatActivity {
    private RecyclerView rv;
    private List<Evento> eventosList;
    private List<Evento> eventosListFind;
    private RecyclerView.Adapter adapter;
    adapterEvents listAdapter;
    private FirebaseAuth mAuth;
    private String user = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mis_eventos);
        rv = findViewById(R.id.rv_eventos);
        rv.setLayoutManager(new LinearLayoutManager(this));
        eventosList = new ArrayList<>();
        //recupero instancia de Firebase y del auth
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getUid();
        //le referenci un adaptador al rv y a una lista
        adapter = new adapterEvents(eventosList);
        rv.setAdapter(adapter);
        database.getReference().child("Events").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                eventosList.removeAll(eventosList);
                //obtengo los datos para añadirlos a la lista de eventosList
                for (DataSnapshot snapshotL : snapshot.getChildren()) {
                    Evento eventosObList = snapshotL.getValue(Evento.class);
                //si existe los añado
                    if (eventosObList.getuSerUII().equals(user)) {
                        eventosList.add(eventosObList);
                    }
                }
                adapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            //al mover borro el evento de la BBDD
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                Evento deleteEvento = eventosList.get(viewHolder.getAdapterPosition());
                int position = viewHolder.getAdapterPosition();
                String key = eventosList.get(position).getCodigoEvento();
                database.getReference().child("Events").child(key).removeValue();
                database.getReference().child("Events").child(key).setValue(null);
                eventosList.remove(viewHolder.getAdapterPosition());
                adapter.notifyItemRemoved(viewHolder.getAdapterPosition());
                Snackbar.make(rv, "¿Borrar?", Snackbar.LENGTH_LONG).setAction("Deshacer", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        eventosList.add(position, deleteEvento);
                        adapter.notifyItemInserted(position);
                    }
                }).show();
            }
        }).attachToRecyclerView(rv);

    }
}
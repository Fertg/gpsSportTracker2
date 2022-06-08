package com.fertg.gpssporttracker;

import com.fertg.gpssporttracker.ModeloDatos.Corredor;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RunnerProvider {

    DatabaseReference mDatabase;

    public RunnerProvider(){
        mDatabase = FirebaseDatabase.getInstance().getReference().child("users");
    }


    public DatabaseReference getRunner(String idRunner){
        return mDatabase.child(idRunner);
    }



}

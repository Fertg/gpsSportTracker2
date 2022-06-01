package com.fertg.gpssporttracker.ModeloDatos;

public class Corredor {
    String id;
    String name;
    String email;
    int type;

    public Corredor() {

    }

    public Corredor(String id, String name, String email, int type) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.type = type;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}

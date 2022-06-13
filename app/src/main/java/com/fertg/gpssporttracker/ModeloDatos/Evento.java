
package com.fertg.gpssporttracker.ModeloDatos;

import com.google.android.material.textfield.TextInputEditText;
//Clase para setear en la base de datos los datos de los eventos
public class Evento {
    @Override
    public String toString() {
        return  codigoEvento ;
    }

    private String nombreEvento;
    private String lugarEvento;
    private String Modalidad;
    private String numeroCorredores;
    private String codigoEvento;
    private String fechaEvento;
    private String uSerUII;

    public String getuSerUII() {
        return uSerUII;
    }

    public void setuSerUII(String uSerUII) {
        this.uSerUII = uSerUII;
    }

    public Evento(String nombreEvento, String lugarEvento, String modalidad, String numeroCorredores, String codigoEvento, String fechaEvento, String uSerUII) {
        this.nombreEvento = nombreEvento;
        this.lugarEvento = lugarEvento;
        Modalidad = modalidad;
        this.numeroCorredores = numeroCorredores;
        this.codigoEvento = codigoEvento;
        this.fechaEvento = fechaEvento;
        this.uSerUII = uSerUII;
    }

    public Evento() {
    }

    public String getFechaEvento() {
        return fechaEvento;
    }

    public void setFechaEvento(String fechaEvento) {
        this.fechaEvento = fechaEvento;
    }

    public Evento(String nombreEvento, String lugarEvento, String modalidad, String numeroCorredores, String codigoEvento, String fechaEvento) {
        this.nombreEvento = nombreEvento;
        this.lugarEvento = lugarEvento;
        Modalidad = modalidad;
        this.numeroCorredores = numeroCorredores;
        this.codigoEvento = codigoEvento;
        this.fechaEvento = fechaEvento;
    }

    public String getNombreEvento() {
        return nombreEvento;
    }

    public void setNombreEvento(String nombreEvento) {
        this.nombreEvento = nombreEvento;
    }

    public String getLugarEvento() {
        return lugarEvento;
    }

    public void setLugarEvento(String lugarEvento) {
        this.lugarEvento = lugarEvento;
    }

    public String getModalidad() {
        return Modalidad;
    }

    public void setModalidad(String modalidad) {
        Modalidad = modalidad;
    }

    public String getNumeroCorredores() {
        return numeroCorredores;
    }

    public void setNumeroCorredores(String numeroCorredores) {
        this.numeroCorredores = numeroCorredores;
    }

    public String getCodigoEvento() {
        return codigoEvento;
    }

    public void setCodigoEvento(String codigoEvento) {
        this.codigoEvento = codigoEvento;
    }
}

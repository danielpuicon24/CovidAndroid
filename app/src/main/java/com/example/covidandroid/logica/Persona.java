package com.example.covidandroid.logica;

import java.util.ArrayList;

public class Persona {
    private String dni;
    private String codigo;
    private String nombre;
    private String apellidoPaterno;
    private String apellidoMaterno;
    private String fechanac;
    private String telefono;
    private String qr;


    public String getQr() { return qr; }

    public void setQr(String qr) { this.qr = qr; }

    public static ArrayList<Persona> listaDNI = new ArrayList<>();

    public String getFechanac() { return fechanac; }

    public void setFechanac(String fechanac) { this.fechanac = fechanac; }

    public String getTelefono() { return telefono; }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getDni() { return dni; }

    public void setDni(String dni) { this.dni = dni; }

    public String getCodigo() { return codigo; }

    public void setCodigo(String codigo) { this.codigo = codigo; }

    public String getNombre() { return nombre; }

    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getApellidoPaterno() {
        return apellidoPaterno;
    }

    public void setApellidoPaterno(String apellidoPaterno) {
        this.apellidoPaterno = apellidoPaterno;
    }

    public String getApellidoMaterno() {
        return apellidoMaterno;
    }

    public void setApellidoMaterno(String apellidoMaterno) {
        this.apellidoMaterno = apellidoMaterno;
    }
}

package com.example.covidandroid.logica;

import org.json.JSONObject;

import java.util.ArrayList;

public class Veterinario {


    public static ArrayList<Veterinario> listaVeterinario = new ArrayList<>();


    private String dni;
    private String nombre;
    private String apellidos;
    private String unidadMedida;
    private String cantidad;
    private String direccion;
    private double latitud;
    private double longitud;
    private String foto;

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getUnidadMedida() {
        return unidadMedida;
    }

    public void setUnidadMedida(String unidadMedida) {
        this.unidadMedida = unidadMedida;
    }

    public String getCantidad() {
        return cantidad;
    }

    public void setCantidad(String cantidad) {
        this.cantidad = cantidad;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public double getLatitud() {
        return latitud;
    }

    public void setLatitud(double latitud) {
        this.latitud = latitud;
    }

    public double getLongitud() {
        return longitud;
    }

    public void setLongitud(double longitud) {
        this.longitud = longitud;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }


    public JSONObject generarJSON(){

        JSONObject jsonObject = new JSONObject();
        try
        {
            jsonObject.put("dni", this.getDni());
            jsonObject.put("nombre", this.getNombre());
            jsonObject.put("apellidos", this.getApellidos());
            jsonObject.put("unidadMedida", this.getUnidadMedida());
            jsonObject.put("cantidad", this.getCantidad());
            jsonObject.put("direccion", this.getDireccion());
            jsonObject.put("latitud", this.getLatitud());
            jsonObject.put("longitud", this.getLongitud());
            jsonObject.put("foto", this.getFoto());
        }catch (Exception e)
        {
            e.printStackTrace();
        }
        return  jsonObject;
    }
}

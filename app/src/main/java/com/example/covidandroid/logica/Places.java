package com.example.covidandroid.logica;

import java.util.ArrayList;

public class Places {
    private int id;
    private String nombre;
    private String descripcion;
    private String categoria;
    private double latitud;
    private double longitud;

    public static ArrayList<Places> listaPlaces = new ArrayList<>();

    public int getId() { return id; }

    public void setId(int id) { this.id = id; }

    public String getNombre() { return nombre; }

    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDescripcion() { return descripcion; }

    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public String getCategoria() { return categoria; }

    public void setCategoria(String categoria) { this.categoria = categoria; }

    public double getLatitud() { return latitud; }

    public void setLatitud(double latitud) { this.latitud = latitud; }

    public double getLongitud() { return longitud; }

    public void setLongitud(double longitud) { this.longitud = longitud; }
}

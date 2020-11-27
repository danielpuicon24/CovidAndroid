package com.example.covidandroid.logica;

import java.util.ArrayList;

public class Cliente {
    private String id;
    private String nombre;

    public static ArrayList<Cliente> listaCliente = new ArrayList<>();



    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
}

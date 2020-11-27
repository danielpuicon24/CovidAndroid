package com.example.covidandroid.logica;

import org.json.JSONObject;

public class Producto {

    private String id;
    private String nombre;
    private double precio;
    private String foto;
    private int cantidad; //guardará la cantidad que se desea vender de cada producto

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

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }


    public JSONObject generarJSON(){

        JSONObject jsonObject = new JSONObject();
        try
        {
            jsonObject.put("id_producto", this.getId());
            jsonObject.put("cantidad", this.getCantidad());
            jsonObject.put("precio_unitario", this.getPrecio());
        }catch (Exception e)
        {
            e.printStackTrace();
        }
        return  jsonObject;
    }
}

package com.VinyApps.SimpleListApp.utilidades;

public class Adaptador {

    private String texto;
    private int id;

    public Adaptador(String texto, int id) {
        this.texto = texto;
        this.id = id;
    }

    public String getTexto() {
        return texto;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


}

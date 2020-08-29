package com.VinyApps.SimpleListApp.utilidades;

public class Utilidades {

    //cONSTANTES CAMPOS TABLA LSITA
    public static final String TABLA_LISTA = "lista";
    public static final String CAMPO_NOMBRE = "nombre";
    public static String CAMPO_OBJETOSDELISTA = "objetosLista";

    public static final String CREAR_TABLA_LISTA = "CREATE TABLE "+TABLA_LISTA+" ("+CAMPO_NOMBRE+" text primary key, objetosLista text)";
}

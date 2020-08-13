package com.example.listappmobile;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.example.listappmobile.utilidades.Utilidades;

class ConexionSQLiteHelper extends SQLiteOpenHelper {


    public ConexionSQLiteHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
            db.execSQL("create table lista(id INTEGER PRIMARY KEY AUTOINCREMENT, nombre text, objetosLista text, cantidadObjetos int)");
            db.execSQL("create table objetos(id INTEGER PRIMARY KEY AUTOINCREMENT, nombre text, FOREIGN KEY(nombre) REFERENCES lista(objetosLista) )");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS lista");
            db.execSQL("DROP TABLE IF EXISTS objetos");
            onCreate(db);
    }
}

package com.VinyApps.SimpleListApp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

class ConexionSQLiteHelper extends SQLiteOpenHelper {


    public ConexionSQLiteHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
            db.execSQL("create table nombreLista(id INTEGER PRIMARY KEY AUTOINCREMENT, nombre text)");

            db.execSQL("create table objetosLista(id integer primary key AUTOINCREMENT not null, id_lista integer, objeto text, foreign key(id_lista) references nombreLista(id))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS nombreLista");
            db.execSQL("DROP TABLE IF EXISTS objetosLista");
            onCreate(db);
    }
}

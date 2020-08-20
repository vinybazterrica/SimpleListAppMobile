package com.example.listappmobile;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.listappmobile.entidades.Lista;
import com.example.listappmobile.utilidades.Utilidades;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ActivityCreaLista extends AppCompatActivity {

    Button btn_Agregar;
    TextView tx_nombreProducto;
    ListView listaView;
    TextView nombreLista;

    List <String> productos = new ArrayList<>();
    ArrayAdapter<String> adapter;

    long idLista;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crea_lista);

        getSupportActionBar().setDisplayShowHomeEnabled(true); //Agrego el icono al ActionBar
        getSupportActionBar().setIcon(R.mipmap.ic_launcher);   //Seteo el icono que quiero en el ActionBar

        //Id de la lista creada anteriormente
        idLista = getIntent().getLongExtra("idLista", -1);

        nombreLista = findViewById(R.id.et_NombreLista);
        btn_Agregar = findViewById(R.id.btn_agregar);
        tx_nombreProducto = findViewById(R.id.tx_nombreProducto);
        listaView = findViewById(R.id.listaView);

        adapter = new ArrayAdapter<>(getApplicationContext(),R.layout.items_lista, productos);
        listaView.setAdapter(adapter);

        ConexionSQLiteHelper conn = new ConexionSQLiteHelper(this, "bd_listas", null, 4);
        SQLiteDatabase db = conn.getWritableDatabase();

        Cursor busca_Nombre_En_BD = db.rawQuery("select nombre from nombreLista where id ="+idLista,null);

        if (busca_Nombre_En_BD != null) {
            busca_Nombre_En_BD.moveToFirst();
            do {
                String nombre_Lista = busca_Nombre_En_BD.getString(0);
                nombreLista.setText(nombre_Lista);
            }while (busca_Nombre_En_BD.moveToNext());
        }
        busca_Nombre_En_BD.close();



        //Funcion que escucha si se pulsa el boton de agregar objeto a la lista
        btn_Agregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Valido que el campo Tx_nombreProducto no este vacio, si esta vacio muestra un TOAST diciendo que ingrese dato
            if(!tx_nombreProducto.getText().toString().isEmpty()) {
                productos.add(tx_nombreProducto.getText().toString());
                adapter.notifyDataSetChanged();
                tx_nombreProducto.setText(null);
                tx_nombreProducto.setHint(R.string.txt_AgregarProductoNombre);
                } else if (tx_nombreProducto.getText().toString().isEmpty()) {
                Toast.makeText(ActivityCreaLista.this, "Ingrese nombre", Toast.LENGTH_SHORT).show();
            }
            }

        });
    }

    public void guardarLista (View v) {

        if(productos.size() != 0) {

            ConexionSQLiteHelper conn = new ConexionSQLiteHelper(this, "bd_listas", null, 4);
            SQLiteDatabase db = conn.getWritableDatabase();

            ContentValues valores = new ContentValues();

            for (int i = 0 ; i < productos.size(); i++){
                valores.put("id_lista",idLista);
                valores.put("objeto",productos.get(i));
                long insert = db.insert("objetosLista", null, valores);
            }

                Toast.makeText(this, "Lista guardada exitosamente", Toast.LENGTH_LONG).show();

                conn.close();

                nombreLista.setText("");
                nombreLista.setHint(R.string.txt_pido_nombre_lista);
                tx_nombreProducto.setText("");
                tx_nombreProducto.setHint(R.string.txt_AgregarProductoNombre);
                productos.clear();

                Intent i = new Intent(this, BuscarListas.class);
                startActivity(i);

        } else {
            Toast.makeText(this,"Debe ingresar nombre de Lista", Toast.LENGTH_SHORT).show();
        }

    }

}

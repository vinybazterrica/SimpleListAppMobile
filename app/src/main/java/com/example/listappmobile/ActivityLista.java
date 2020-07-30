package com.example.listappmobile;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.listappmobile.entidades.Lista;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ActivityLista extends AppCompatActivity {

    private TextView nombre_lista;
    private ListView lista_Objetos_Lista;
    private Button btnEliminar;

    List<String> objetosLista = new ArrayList<>();
    ArrayList<String> convertido = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista);
        getSupportActionBar().setDisplayShowHomeEnabled(true); //Agrego el icono al ActionBar
        getSupportActionBar().setIcon(R.mipmap.ic_launcher);   //Seteo el icono que quiero en el ActionBar

        String nombreListaSeleccionada = getIntent().getStringExtra("ListaSeleccionada");

        nombre_lista = findViewById(R.id.nombreLista);
        lista_Objetos_Lista = findViewById(R.id.listadoDeObjetos);
        btnEliminar = findViewById(R.id.btnEliminar);

        ArrayAdapter<String> adapterLista= new ArrayAdapter<>(getApplicationContext(), R.layout.items_lista, objetosLista); //Antes iba objetosLista
        lista_Objetos_Lista.setAdapter(adapterLista);

        ConexionSQLiteHelper conn = new ConexionSQLiteHelper(getApplicationContext(), "bd_listas", null, 2);
        SQLiteDatabase db = conn.getWritableDatabase();

        nombre_lista.setText(nombreListaSeleccionada);

        Cursor fila = db.rawQuery("select objetosLista from lista where nombre = '"+nombreListaSeleccionada+"'", null);

        String NombreLista;

        if (fila != null && fila.moveToFirst()) {
            do{
                NombreLista = fila.getString(fila.getColumnIndex("objetosLista"));
            }while (fila.moveToNext());
        }

        ArrayList<String> list = new ArrayList<String>();
        Object jsonObject = db.rawQuery("select objetosLista from lista where nombre = '"+nombreListaSeleccionada+"'", null);;
//        JSONArray jsonArray = (JSONArray)jsonObject;
//        if (jsonArray != null) {
//            int len = jsonArray.length();
//            for (int i=0;i<len;i++){
//                try {
//                    objetosLista.add(jsonArray.get(i).toString());
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//        }


        // Toast.makeText(getApplicationContext(), "Listado: "+strings, Toast.LENGTH_LONG).show();


        db.close();
        btnEliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConexionSQLiteHelper conn = new ConexionSQLiteHelper(getApplicationContext(), "bd_listas", null, 2);
                SQLiteDatabase db = conn.getWritableDatabase();
                String nombreListaEliminar = nombre_lista.getText().toString();

                db.execSQL("DELETE from lista where nombre = '"+nombreListaEliminar+"'");

                Intent i = new Intent(getApplicationContext(), BuscarListas.class);
                startActivity(i);

            }
        });


    }
}
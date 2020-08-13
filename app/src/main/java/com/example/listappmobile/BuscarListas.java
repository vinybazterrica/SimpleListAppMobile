package com.example.listappmobile;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.view.View;

import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class BuscarListas extends AppCompatActivity {

    TextView tvBuscador;
    ListView listadoDeListasCreadas;
    Button botonBuscar;

    List <String> nombreListas = new ArrayList<>();

    List <String> nombreListasencontradas = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buscar_listas);
        getSupportActionBar().setDisplayShowHomeEnabled(true); //Agrego el icono al ActionBar
        getSupportActionBar().setIcon(R.mipmap.ic_launcher);   //Seteo el icono que quiero en el ActionBar

        tvBuscador = findViewById(R.id.buscadorDeListas);
        listadoDeListasCreadas = findViewById(R.id.listadoDeListasCreadas);
        botonBuscar = findViewById(R.id.BotonBuscar);

        ConexionSQLiteHelper conn = new ConexionSQLiteHelper(this, "bd_listas", null, 3);
        SQLiteDatabase db = conn.getWritableDatabase();

        Cursor consultaNombre = db.rawQuery("select nombre from lista", null);

        if (consultaNombre.moveToFirst()){
            ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(), R.layout.items_lista, nombreListas);
            listadoDeListasCreadas.setAdapter(adapter);
            do{
                nombreListas.add(consultaNombre.getString(0));
            }while (consultaNombre.moveToNext());

        }

        conn.close();

        //Buscador de listas, el cual busca en la base por like
        botonBuscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConexionSQLiteHelper conn = new ConexionSQLiteHelper(getApplicationContext(), "bd_listas", null, 3);
                SQLiteDatabase db2 = conn.getWritableDatabase();

                String busquedadelista = tvBuscador.getText().toString();

                if (!busquedadelista.isEmpty()) {
                    nombreListas.clear();
                    nombreListasencontradas.clear();

                    Cursor buscadordeNombre = db2.rawQuery("select nombre from lista where nombre like '%" +busquedadelista+"%'", null);

                    if (buscadordeNombre.moveToFirst()) {
                        ArrayAdapter<String> adapterBuscador = new ArrayAdapter<>(getApplicationContext(), R.layout.items_lista, nombreListasencontradas);

                        listadoDeListasCreadas.setAdapter(adapterBuscador);
                        do {
                            nombreListasencontradas.add(buscadordeNombre.getString(0));
                        }while (buscadordeNombre.moveToNext());
                        adapterBuscador.notifyDataSetChanged();
                    }

                    buscadordeNombre.close();

                } else {

                    ConexionSQLiteHelper conn2 = new ConexionSQLiteHelper(getApplicationContext(), "bd_listas", null, 2);
                    SQLiteDatabase db3 = conn2.getWritableDatabase();

                    nombreListas.clear();
                    nombreListasencontradas.clear();
                    Cursor consultaNombre = db3.rawQuery("select nombre from lista", null);

                    if (consultaNombre.moveToFirst()){
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(), R.layout.items_lista, nombreListas);
                        listadoDeListasCreadas.setAdapter(adapter);
                        do{
                            nombreListas.add(consultaNombre.getString(0));
                        }while (consultaNombre.moveToNext());
                    }

                    consultaNombre.close();
                }

                    conn.close();
            }
        });

            listadoDeListasCreadas.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    String nombrelistaSeleccionada = nombreListas.get(position);
                    Toast.makeText(getApplicationContext(),"La lista seleccionada es : "+nombrelistaSeleccionada, Toast.LENGTH_SHORT).show();

                    Intent i = new Intent(getApplicationContext(),ActivityLista.class);
                    i.putExtra("ListaSeleccionada",nombrelistaSeleccionada);
                    startActivity(i);
                }
            });



    }

}
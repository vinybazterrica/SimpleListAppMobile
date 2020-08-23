package com.example.listappmobile;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class CreaNombreLista extends AppCompatActivity {

    TextView nombreLista;
    Button btn_Crear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crea_nombre_lista);
        getSupportActionBar().setDisplayShowHomeEnabled(true); //Agrego el icono al ActionBar
        getSupportActionBar().setIcon(R.mipmap.ic_launcher);   //Seteo el icono que quiero en el ActionBar

        nombreLista = findViewById(R.id.ED_Pido_Nombre_Lista);
        btn_Crear = findViewById(R.id.btn_crearLista);

        btn_Crear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String nombre_lista_creada = nombreLista.getText().toString();

                if (!nombre_lista_creada.isEmpty()){

                    ConexionSQLiteHelper conn = new ConexionSQLiteHelper(getApplicationContext(), "bd_listas", null, 4);
                    SQLiteDatabase db = conn.getWritableDatabase();

                    ContentValues valores = new ContentValues();

                    valores.put("nombre",nombre_lista_creada);

                    Long IDguardado = db.insert("nombreLista",null,valores);

                    Toast.makeText(getApplicationContext(), "Lista guardada exitosamente, id: " + IDguardado.toString(), Toast.LENGTH_LONG).show();

                    Intent i = new Intent(getApplicationContext(),ActivityCreaLista.class);
                    i.putExtra("idLista", IDguardado);
                    finish();
                    startActivity(i);

                    conn.close();

                } else {
                    Toast.makeText(getApplicationContext(),"Debe ingresar nombre de Lista", Toast.LENGTH_SHORT).show();
                }


            }
        });

    }
}
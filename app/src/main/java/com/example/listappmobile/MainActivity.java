package com.example.listappmobile;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().setDisplayShowHomeEnabled(true); //Agrego el icono al ActionBar
        getSupportActionBar().setIcon(R.mipmap.ic_launcher);    //Seteo el icono que quiero en el ActionBar




    }

    //funcion para las opciones overflow
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.overflow, menu);
        return true;
    }

    //funcion para los botones de las opciones OverFlow
    public boolean onOptionsItemSelected (MenuItem item){
        int id = item.getItemId();

        if (id == R.id.versionApp){
            Toast.makeText(this, R.string.version_app, Toast.LENGTH_SHORT).show();
        }

        return super.onOptionsItemSelected(item);
    }

    //Funcion para ir a la pantalla de CREAR LISTA
    public void CrearLista (View v){

        Intent i = new Intent(this,CreaNombreLista.class);
        startActivity(i);

    }

    public void BuscarLista (View v){
        Intent i = new Intent(this,BuscarListas.class);
        startActivity(i);
    }



}

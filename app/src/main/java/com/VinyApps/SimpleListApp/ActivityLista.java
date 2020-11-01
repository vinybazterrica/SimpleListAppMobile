package com.VinyApps.SimpleListApp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.VinyApps.SimpleListApp.utilidades.ModificarLista;

import java.util.ArrayList;
import java.util.List;

public class ActivityLista extends AppCompatActivity {

    private TextView nombre_lista;
    private ListView lista_Objetos_Lista;
    private Button btnEliminar, btnModificar;

    List<String> objetosLista = new ArrayList<>();
    List<Integer> idsObjetos = new ArrayList<>();
    ArrayAdapter<String> adapterLista;

    //Constantes de SQLite:
    String nombre_bdd = "bd_listas";
    String bdd_Busca_Nombre_en_nombreLista_id ="SELECT nombre from nombreLista where id =";
    String bdd_Busca_id_objeto_en_objetosLista_por_id ="SELECT id,objeto FROM objetosLista WHERE id_lista = ";
    String bdd_Elimina_objetosLista_por_id = "DELETE from objetosLista where id =";
    String bdd_Elimina_nombreLista_por_id = "DELETE from nombreLista where id =";
    String bdd_Elimina_objetosLista_por_idLista = "DELETE FROM objetosLista where id_lista =";

    //Constantes normales:
    String mensaje_elimina_item = "¿Desea eliminar el item seleccionado?";
    String mensaje_Elimina_Lista = "¿Desea Eliminar la Lista?";
    String id_borrado = "Id borrado : ";
    String consulta_Eliminar_item = "¿Eliminar Item?";
    String consulta_Eliminar_Lista = "¿Eliminar Lista?";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista);
        getSupportActionBar().setDisplayShowHomeEnabled(true); //Agrego el icono al ActionBar
        getSupportActionBar().setIcon(R.mipmap.ic_launcher);   //Seteo el icono que quiero en el ActionBar


        final long idLista = getIntent().getLongExtra("idLista",-1);

        nombre_lista = findViewById(R.id.nombreLista);
        lista_Objetos_Lista = findViewById(R.id.listadoDeObjetos);
        btnEliminar = findViewById(R.id.btnEliminar);
        btnModificar = findViewById(R.id.btn_modificar_lista);

        adapterLista = new ArrayAdapter<>(getApplicationContext(), R.layout.items_lista, objetosLista); //Antes iba objetosLista
        lista_Objetos_Lista.setAdapter(adapterLista);

        ConexionSQLiteHelper conn = new ConexionSQLiteHelper(getApplicationContext(), nombre_bdd, null, 4);
        SQLiteDatabase db = conn.getWritableDatabase();

        Cursor buscoNombre = db.rawQuery(bdd_Busca_Nombre_en_nombreLista_id+idLista,null);

        if (buscoNombre != null && buscoNombre.moveToFirst()) {
            do{
                String  NombreLista = buscoNombre.getString(0);
                nombre_lista.setText(NombreLista);
            }while (buscoNombre.moveToNext());
        }

        buscoNombre.close();

        Cursor fila = db.rawQuery(bdd_Busca_id_objeto_en_objetosLista_por_id+idLista, null);


        if (fila != null && fila.moveToFirst()) {
            do{
                int idObjeto = fila.getInt(0);
               String  NombreLista = fila.getString(1);
                idsObjetos.add(idObjeto);
               objetosLista.add(NombreLista);
            }while (fila.moveToNext());
        }


        fila.close();

        lista_Objetos_Lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog.Builder alerta = new AlertDialog.Builder(ActivityLista.this);
                alerta.setMessage(mensaje_elimina_item)
                        .setCancelable(true)
                        .setPositiveButton(Html.fromHtml("Si"), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                ConexionSQLiteHelper conn = new ConexionSQLiteHelper(getApplicationContext(), nombre_bdd, null, 4);
                                SQLiteDatabase db = conn.getWritableDatabase();

                                int id_Objeto = idsObjetos.get(position);

                                db.execSQL(bdd_Elimina_objetosLista_por_id+id_Objeto);

                                objetosLista.remove(position);
                                adapterLista.notifyDataSetChanged();

                                db.close();

                                Toast.makeText(getApplicationContext(),id_borrado+id_Objeto, Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                AlertDialog titulo = alerta.create();
                titulo.setTitle(consulta_Eliminar_item);
                titulo.show();
            }
        });

        db.close();
        btnEliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder preguntaEliminar = new AlertDialog.Builder(ActivityLista.this);

                preguntaEliminar.setMessage(mensaje_Elimina_Lista)
                        .setCancelable(true)
                        .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ConexionSQLiteHelper conn = new ConexionSQLiteHelper(getApplicationContext(), nombre_bdd, null, 4);//
                                SQLiteDatabase db = conn.getWritableDatabase();
                                int idLista_Eliminar = (int) idLista;

                                db.execSQL(bdd_Elimina_nombreLista_por_id+idLista_Eliminar);
                                db.execSQL(bdd_Elimina_objetosLista_por_idLista+idLista_Eliminar);

                                Intent i = new Intent(getApplicationContext(), BuscarListas.class);
                                startActivity(i);
                                db.close();
                                finish();
                            }
                        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                AlertDialog titulo = preguntaEliminar.create();
                preguntaEliminar.setTitle(consulta_Eliminar_Lista);
                titulo.show();
            }
        });

        btnModificar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent enviarAModificar = new Intent(getApplicationContext(), ModificarLista.class);
                enviarAModificar.putExtra("idLista",idLista);
                startActivity(enviarAModificar);
            }
        });

    }
}
package com.VinyApps.SimpleListApp.utilidades;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
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
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.VinyApps.SimpleListApp.ActivityCreaLista;
import com.VinyApps.SimpleListApp.ActivityLista;
import com.VinyApps.SimpleListApp.BuscarListas;
import com.VinyApps.SimpleListApp.ConexionSQLiteHelper;
import com.VinyApps.SimpleListApp.R;

import java.util.ArrayList;
import java.util.List;

public class ModificarLista extends AppCompatActivity {

    private EditText nombre_lista, tx_nombreProducto;
    private ListView lista_Objetos_Lista;
    private Button btnEliminar, btn_Agregar, btn_Actualizar;

    List<String> objetosListaModificar = new ArrayList<>();
    List<Integer> idsObjetosModificar = new ArrayList<>();
    ArrayAdapter<String> adapterListaModificar;

    //Constantes de SQLite:
    String nombre_bdd = "bd_listas";
    String bdd_Busca_Nombre_en_nombreLista_id ="SELECT nombre from nombreLista where id =";
    String bdd_Busca_id_objeto_en_objetosLista_por_id ="SELECT id,objeto FROM objetosLista WHERE id_lista = ";
    String bdd_Elimina_objetosLista_por_id = "DELETE from objetosLista where id =";
    String bdd_Elimina_nombreLista_por_id = "DELETE from nombreLista where id =";
    String bdd_Elimina_objetosLista_por_idLista = "DELETE FROM objetosLista where id_lista =";

    //Constantes normales:
    String mensaje_elimina_item = "¿Desea eliminar el item seleccionado?";
    String id_borrado = "Id borrado : ";
    String consulta_Eliminar_item = "¿Eliminar Item?";
    String mensaje_ingresar_nombre = "Debe ingresar nombre de lista";
    String mensaje_Elimina_Lista = "¿Desea Eliminar la Lista?";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modificar_lista);
        getSupportActionBar().setDisplayShowHomeEnabled(true); //Agrego el icono al ActionBar
        getSupportActionBar().setIcon(R.mipmap.ic_launcher);   //Seteo el icono que quiero en el ActionBar

        final long idLista = getIntent().getLongExtra("idLista",-1);

        nombre_lista = findViewById(R.id.et_NombreListaModificar);
        lista_Objetos_Lista = findViewById(R.id.listaViewModificar);
        btnEliminar = findViewById(R.id.btn_EliminarModifica);
        btn_Agregar = findViewById(R.id.btn_agregarModificar);
        tx_nombreProducto = findViewById(R.id.tx_nombreProductoModificar);
        btn_Actualizar = findViewById(R.id.btn_guardar);

        adapterListaModificar = new ArrayAdapter<>(getApplicationContext(), R.layout.items_lista, objetosListaModificar); //Antes iba objetosLista
        lista_Objetos_Lista.setAdapter(adapterListaModificar);

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
                idsObjetosModificar.add(idObjeto);
                objetosListaModificar.add(NombreLista);
            }while (fila.moveToNext());
        }

        fila.close();

        lista_Objetos_Lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog.Builder alerta = new AlertDialog.Builder(ModificarLista.this);
                alerta.setMessage(mensaje_elimina_item)
                        .setCancelable(true)
                        .setPositiveButton(Html.fromHtml("Si"), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                ConexionSQLiteHelper conn = new ConexionSQLiteHelper(getApplicationContext(), nombre_bdd, null, 4);
                                SQLiteDatabase db = conn.getWritableDatabase();

                                int id_Objeto = idsObjetosModificar.get(position);

                                db.execSQL(bdd_Elimina_objetosLista_por_id+id_Objeto);

                                objetosListaModificar.remove(position);
                                adapterListaModificar.notifyDataSetChanged();

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

                AlertDialog.Builder AlertaEliminarLista = new AlertDialog.Builder(ModificarLista.this);
                AlertaEliminarLista.setCancelable(true)
                        .setPositiveButton("si", new DialogInterface.OnClickListener() {
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
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                AlertDialog tituloELiminarLista = AlertaEliminarLista.create();
                tituloELiminarLista.setTitle(mensaje_Elimina_Lista);
                tituloELiminarLista.show();
            }
        });



        btn_Agregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Valido que el campo Tx_nombreProducto no este vacio, si esta vacio muestra un TOAST diciendo que ingrese dato
                if(!tx_nombreProducto.getText().toString().isEmpty()) {
                    objetosListaModificar.add(tx_nombreProducto.getText().toString());
                    adapterListaModificar.notifyDataSetChanged();
                    tx_nombreProducto.setText(null);
                    tx_nombreProducto.setHint(R.string.txt_AgregarProductoNombre);
                } else if (tx_nombreProducto.getText().toString().isEmpty()) {
                    Toast.makeText(ModificarLista.this, "Ingrese nombre", Toast.LENGTH_SHORT).show();
                }
            }

        });

        btn_Actualizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nombreDeLista = nombre_lista.getText().toString();
                if (!nombreDeLista.isEmpty()) {
                    ConexionSQLiteHelper conn = new ConexionSQLiteHelper(getApplicationContext(), nombre_bdd, null, 4);//
                    SQLiteDatabase db = conn.getWritableDatabase();
                    int idLista_Eliminar = (int) idLista;

                    db.execSQL(bdd_Elimina_objetosLista_por_idLista + idLista_Eliminar);

                    ContentValues valores = new ContentValues();
                    ContentValues valorTitulo = new ContentValues();
                    valorTitulo.put("nombre", String.valueOf(nombre_lista));

                    for (int i = 0; i < objetosListaModificar.size(); i++) {
                        valores.put("id_lista", idLista);
                        valores.put("objeto", objetosListaModificar.get(i));
                        valores.put("estadoObjeto",0);
                        long insert = db.insert("objetosLista", null, valores);
                    }

                    db.execSQL("UPDATE nombreLista set nombre = '" + nombreDeLista + "'" + " WHERE id = " + idLista);

                    Intent i = new Intent(getApplicationContext(), BuscarListas.class);
                    startActivity(i);
                    db.close();
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(),mensaje_ingresar_nombre,Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
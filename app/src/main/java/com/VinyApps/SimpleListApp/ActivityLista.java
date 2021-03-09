package com.VinyApps.SimpleListApp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.VinyApps.SimpleListApp.utilidades.ModificarLista;

import java.util.ArrayList;
import java.util.List;

public class ActivityLista extends AppCompatActivity {

    private TextView nombre_lista, contadorProgreso;
    private ListView lista_Objetos_Lista, lista_objetos_completados;
    private Button btnEliminar, btnModificar;
    private ProgressBar progresoItems;

    List<String> objetosLista = new ArrayList<>();
    List<Integer> idsObjetos = new ArrayList<>();
    List<String> objetosCompletados = new ArrayList<>();
    ArrayAdapter<String> adapterLista;
    ArrayAdapter<String> adapterListaCompletos;

    Handler handler = new Handler();

    //Constantes de SQLite:
    String nombre_bdd = "bd_listas";
    String bdd_Busca_Nombre_en_nombreLista_id = "SELECT nombre from nombreLista where id =";
    String bdd_Busca_id_objeto_en_objetosLista_por_id = "SELECT id,objeto FROM objetosLista WHERE id_lista = ";
    String bdd_Elimina_objetosLista_por_id = "DELETE from objetosLista where id =";
    String bdd_Elimina_nombreLista_por_id = "DELETE from nombreLista where id =";
    String bdd_Elimina_objetosLista_por_idLista = "DELETE FROM objetosLista where id_lista =";

    //Constantes normales:
    String mensaje_elimina_item = "¿Desea eliminar el item  o marcarlo como 'Completado'?";
    String mensaje_Elimina_Lista = "¿Desea Eliminar la Lista?";
    String id_borrado = "Id borrado : ";
    String consulta_Eliminar_item = "¿Eliminar Item?";
    String consulta_Eliminar_Lista = "¿Eliminar Lista?";
    String consulta_volver_Pendiente = "¿Desea marcar como 'No Completado'?";
    String volver_Pendiente = "Eliminara el item del listado de Completados";
    String ya_Existente_En_Completados = "Objeto seleccionado ya completado.";

    //Variables:
    int progreso = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista);
        getSupportActionBar().setDisplayShowHomeEnabled(true); //Agrego el icono al ActionBar
        getSupportActionBar().setIcon(R.mipmap.ic_launcher);   //Seteo el icono que quiero en el ActionBar

        final long idLista = getIntent().getLongExtra("idLista", -1);

        nombre_lista = findViewById(R.id.nombreLista);
        lista_Objetos_Lista = findViewById(R.id.listadoDeObjetos);
        lista_objetos_completados= findViewById(R.id.ListaItemsCompletados);
        btnEliminar = findViewById(R.id.btnEliminar);
        btnModificar = findViewById(R.id.btn_modificar_lista);
        progresoItems = findViewById(R.id.BarraProgreso);
        contadorProgreso = findViewById(R.id.contador);

        adapterLista = new ArrayAdapter<>(getApplicationContext(), R.layout.items_lista, objetosLista); //Antes iba objetosLista
        adapterListaCompletos = new ArrayAdapter<>(getApplication(), R.layout.items_lista_completados,objetosCompletados);

        lista_Objetos_Lista.setAdapter(adapterLista);
        lista_objetos_completados.setAdapter(adapterListaCompletos);

        ConexionSQLiteHelper conn = new ConexionSQLiteHelper(getApplicationContext(), nombre_bdd, null, 4);
        SQLiteDatabase db = conn.getWritableDatabase();

        Cursor buscoNombre = db.rawQuery(bdd_Busca_Nombre_en_nombreLista_id + idLista, null);

        if (buscoNombre != null && buscoNombre.moveToFirst()) {
            do {
                String NombreLista = buscoNombre.getString(0);
                nombre_lista.setText(NombreLista);
            } while (buscoNombre.moveToNext());
        }

        buscoNombre.close();

        Cursor fila = db.rawQuery(bdd_Busca_id_objeto_en_objetosLista_por_id + idLista, null);

        if (fila != null && fila.moveToFirst()) {
            do {
                int idObjeto = fila.getInt(0);
                String NombreLista = fila.getString(1);
                idsObjetos.add(idObjeto);
                objetosLista.add(NombreLista);
            } while (fila.moveToNext());
        }

        Cursor filaCompletado = db.rawQuery(bdd_Busca_id_objeto_en_objetosLista_por_id + idLista + " AND estadoObjeto = 1", null);

        if (filaCompletado != null && filaCompletado.moveToFirst()) {
            do {
                int idObjeto = filaCompletado.getInt(0);
                String NombreLista = filaCompletado.getString(1);
                idsObjetos.add(idObjeto);
                objetosCompletados.add(NombreLista);
                progreso++;
            } while (filaCompletado.moveToNext());
        }

        String ContadorTotal = progreso + "/" + objetosLista.size();
        contadorProgreso.setText(ContadorTotal);
        progresoItems.setMax(objetosLista.size());
        progresoItems.setProgress(progreso);

        fila.close();
        filaCompletado.close();

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

                                db.execSQL(bdd_Elimina_objetosLista_por_id + id_Objeto);

                                objetosLista.remove(position);
                                adapterLista.notifyDataSetChanged();
                                if(progreso != 0){
                                    progreso = progreso - 1;
                                }

                                progresoItems.setProgress(progreso);
                                contadorProgreso.setText(progreso + "/" + objetosLista.size());

                                db.close();

                                Toast.makeText(getApplicationContext(), id_borrado + id_Objeto, Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton("Completado", new DialogInterface.OnClickListener() { //Envia a la lista "Completados" al item seleccionado
                                @Override
                                public void onClick (DialogInterface dialog,int which){
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                     final int id_Objeto = idsObjetos.get(position);
                                     final int  estadoObjeto = BuscoEstado(id_Objeto);
                                        if (estadoObjeto == 0) {
                                            if (progreso < objetosLista.size()) {
                                                progreso++;
                                                handler.post(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        progresoItems.setMax(objetosLista.size());
                                                        progresoItems.setProgress(progreso);
                                                        contadorProgreso.setText(progreso + "/" + objetosLista.size());
                                                        if (progreso == objetosLista.size()) {
                                                            progresoItems.setProgress(progreso);
                                                            Toast.makeText(getApplicationContext(), "Lista Completa!", Toast.LENGTH_SHORT).show();
                                                        }
                                                 CambiaEstado(id_Objeto,estadoObjeto);
                                                    }
                                                });
                                                try {
                                                    Thread.sleep(10);
                                                } catch (InterruptedException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        }else{

                                           ActivityLista.this.runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    Toast.makeText(getApplicationContext(),ya_Existente_En_Completados,Toast.LENGTH_SHORT).show();
                                                }
                                            });

                                        }
                                    }
                                }).start();
                                objetosCompletados.add(objetosLista.get(position));
                            }
                      });
                AlertDialog titulo = alerta.create();
                titulo.setTitle(consulta_Eliminar_item);
                titulo.show();
            }
        });

        lista_objetos_completados.setOnItemClickListener(new AdapterView.OnItemClickListener() { //Consulta si vuelve un item a la lista pendientes
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                    final AlertDialog.Builder pendiente = new AlertDialog.Builder(ActivityLista.this);
                    pendiente.setMessage(volver_Pendiente)
                            .setCancelable(true)
                            .setPositiveButton("Si", new DialogInterface.OnClickListener() {    //Elimina el item de la lista
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    progreso--;
                                    progresoItems.setProgress(progreso);
                                    contadorProgreso.setText(progreso + "/" + objetosLista.size());
                                    objetosCompletados.remove(position);
                                    final int id_Objeto = idsObjetos.get(position);
                                    final int  estadoObjeto = BuscoEstado(id_Objeto);
                                    CambiaEstado(id_Objeto,estadoObjeto);

                                }
                            }).setNegativeButton("No", new DialogInterface.OnClickListener() {  //Aca no hace nada
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    AlertDialog titulo = pendiente.create();
                    pendiente.setTitle(consulta_volver_Pendiente);
                    titulo.show();
            }
        });

        btnEliminar.setOnClickListener(new View.OnClickListener() {     //Elimina lista
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder preguntaEliminar = new AlertDialog.Builder(ActivityLista.this);
                preguntaEliminar.setMessage(mensaje_Elimina_Lista)
                        .setCancelable(true)
                        .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ConexionSQLiteHelper conn = new ConexionSQLiteHelper(getApplicationContext(), nombre_bdd, null, 4);
                                SQLiteDatabase db = conn.getWritableDatabase();
                                int idLista_Eliminar = (int) idLista;

                                db.execSQL(bdd_Elimina_nombreLista_por_id + idLista_Eliminar);
                                db.execSQL(bdd_Elimina_objetosLista_por_idLista + idLista_Eliminar);

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

        btnModificar.setOnClickListener(new View.OnClickListener() {    //Envia a la modificacion de lista
            @Override
            public void onClick(View v) {
                Intent enviarAModificar = new Intent(getApplicationContext(), ModificarLista.class);
                enviarAModificar.putExtra("idLista", idLista);
                startActivity(enviarAModificar);
            }
        });
    }
        //Buscador de Estado del objeto....
    public int BuscoEstado (int id_elemento){
        int EstadoDelObjeto = 0;
        ConexionSQLiteHelper conn = new ConexionSQLiteHelper(getApplicationContext(), nombre_bdd, null, 4);
        SQLiteDatabase db = conn.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT estadoObjeto FROM objetosLista WHERE id ="+id_elemento,null);
        if (cursor.moveToFirst()){
            do{
                EstadoDelObjeto = cursor.getInt(0);
            }while (cursor.moveToNext());
        }
            return EstadoDelObjeto;
    }

    public void CambiaEstado (int id_elemento, int EstadoAnterior){
        ConexionSQLiteHelper conn = new ConexionSQLiteHelper(getApplicationContext(), nombre_bdd, null, 4);
        SQLiteDatabase db = conn.getWritableDatabase();
        if (EstadoAnterior == 0) {
            db.execSQL("UPDATE objetosLista SET estadoObjeto =" + 1 + " WHERE id=" + id_elemento);
        } else {
            db.execSQL("UPDATE objetosLista SET estadoObjeto =" + 0 + " WHERE id=" + id_elemento);
        }
    }
}
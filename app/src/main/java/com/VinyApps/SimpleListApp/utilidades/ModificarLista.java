package com.VinyApps.SimpleListApp.utilidades;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

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
    private Button btnEliminar, btn_Agregar;

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
    String id_borrado = "Id borrado : ";
    String consulta_Eliminar_item = "¿Eliminar Item?";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modificar_lista);
        getSupportActionBar().setDisplayShowHomeEnabled(true); //Agrego el icono al ActionBar
        getSupportActionBar().setIcon(R.mipmap.ic_launcher);   //Seteo el icono que quiero en el ActionBar

        final long idLista = getIntent().getLongExtra("idLista",2);

        nombre_lista = findViewById(R.id.et_NombreListaModificar);
        lista_Objetos_Lista = findViewById(R.id.listadoDeObjetos);
        btnEliminar = findViewById(R.id.btnEliminar);
        btn_Agregar = findViewById(R.id.btn_agregarModificar);
        tx_nombreProducto = findViewById(R.id.tx_nombreProductoModificar);


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
                AlertDialog.Builder alerta = new AlertDialog.Builder(ModificarLista.this);
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
        });

        btn_Agregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Valido que el campo Tx_nombreProducto no este vacio, si esta vacio muestra un TOAST diciendo que ingrese dato
                if(!tx_nombreProducto.getText().toString().isEmpty()) {
                    objetosLista.add(tx_nombreProducto.getText().toString());
                    adapterLista.notifyDataSetChanged();
                    tx_nombreProducto.setText(null);
                    tx_nombreProducto.setHint(R.string.txt_AgregarProductoNombre);
                } else if (tx_nombreProducto.getText().toString().isEmpty()) {
                    Toast.makeText(ModificarLista.this, "Ingrese nombre", Toast.LENGTH_SHORT).show();
                }
            }

        });

    }
}
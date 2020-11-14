package com.VinyApps.SimpleListApp.utilidades;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.VinyApps.SimpleListApp.R;

import java.util.List;

class ListaAdaptador extends ArrayAdapter<Adaptador> {

    private List<Adaptador> mList;
    private Context mContext;
    private int resourceL;

    public ListaAdaptador(@NonNull Context context, int resource, List<Adaptador> objects) {
        super(context, resource, objects);
        this.mList= objects;
        this.mContext=context;
        this.resourceL= resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view= convertView;

        if (view == null)
            view= LayoutInflater.from(mContext).inflate(R.layout.items_lista_botones,null);

            Adaptador adaptadorobjeto = mList.get(position);

            TextView nombreObjeto = view.findViewById(R.id.tview);
            nombreObjeto.setText(adaptadorobjeto.getTexto());

            return view;
    }
}

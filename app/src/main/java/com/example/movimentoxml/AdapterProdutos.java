package com.example.movimentoxml;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class AdapterProdutos extends ArrayAdapter<Produtos> {

    private final Context context;
    private final ArrayList<Produtos> elementos;

    public AdapterProdutos(Context context, ArrayList<Produtos> elementos) {
        super(context, R.layout.linha, elementos);
        this.context = context;
        this.elementos = elementos;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.linha, parent, false);
        TextView codigo = (TextView) rowView.findViewById(R.id.codigo);
        TextView descricao = (TextView) rowView.findViewById(R.id.descricao);
        TextView estoque = (TextView) rowView.findViewById(R.id.estoque);
        codigo.setText(String.valueOf(elementos.get(position).getCodigo()));
        descricao.setText(elementos.get(position).getDescricao());
        estoque.setText(String.valueOf(elementos.get(position).getEstoque()));
        return rowView;
    }
}

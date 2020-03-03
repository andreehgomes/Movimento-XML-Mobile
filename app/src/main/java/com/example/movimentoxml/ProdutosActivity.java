package com.example.movimentoxml;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import java.util.ArrayList;

public class ProdutosActivity extends AppCompatActivity {

    private ListView lista;
    private Button botaoFiltrar;
    private EditText textoPesquisa;
    private RadioButton radioCodigo;
    private RadioButton radioDescricao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_produtos);

        lista = (ListView) findViewById(R.id.lvProdutos);
        ArrayAdapter adapter = new AdapterProdutos(this, adicionarProdutos("codigo"));
        lista.setAdapter(adapter);

        botaoFiltrar = (Button) findViewById(R.id.btnFiltrar);
        textoPesquisa = (EditText) findViewById(R.id.edtPesquisa);
        //radioCodigo = (RadioButton) findViewById(R.id.rbCodigo);
        //radioDescricao = (RadioButton) findViewById(R.id.rbDescricao);

        botaoFiltrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(textoPesquisa.getText().toString().isEmpty()){

                    RadioGroup grupo = (RadioGroup) findViewById(R.id.rgFiltro);
                    String pesquisa = textoPesquisa.getText().toString();
                    String botaoRadio;
                    ArrayAdapter adapter;

                    switch ( grupo.getCheckedRadioButtonId() ){
                        case R.id.rbCodigo:
                            botaoRadio = "codigo";
                            adapter = new AdapterProdutos(view.getContext() ,adicionarProdutos(botaoRadio));
                            lista.setAdapter(adapter);
                            break;
                        case R.id.rbDescricao:
                            botaoRadio = "descricaoProd";
                            adapter = new AdapterProdutos(view.getContext() ,adicionarProdutos(botaoRadio));
                            lista.setAdapter(adapter);
                            break;
                        default:
                            alertaConfirmacao("Filtro", "Selecionar CÓDIGO ou DESCRIÇÃO para realizar o filtro. ");
                    }


                }else{
                    RadioGroup grupo = (RadioGroup) findViewById(R.id.rgFiltro);
                    String pesquisa = textoPesquisa.getText().toString();
                    String botaoRadio;
                    ArrayAdapter adapter;

                    switch ( grupo.getCheckedRadioButtonId() ){
                        case R.id.rbCodigo:
                            botaoRadio = "codigo";
                            adapter = new AdapterProdutos(view.getContext() ,adicionarProdutoFiltro(pesquisa, botaoRadio));
                            lista.setAdapter(adapter);
                            break;
                        case R.id.rbDescricao:
                            botaoRadio = "descricaoProd";
                            adapter = new AdapterProdutos(view.getContext() ,adicionarProdutoFiltro(pesquisa, botaoRadio));
                            lista.setAdapter(adapter);
                            break;
                        default:
                            alertaConfirmacao("Filtro", "Selecionar CÓDIGO ou DESCRIÇÃO para realizar o filtro. ");
                    }
                }
            }
        });

    }

    private ArrayList<Produtos> adicionarProdutos(String ordenacao){
        ArrayList<Produtos> produtos = new ArrayList<Produtos>();

        //Criar ou abrir banco de dados
        SQLiteDatabase bancoDados = openOrCreateDatabase("app", MODE_PRIVATE, null);
        Cursor cursor;
        try{
            //Select para puxar os cadastros
            if(ordenacao.equals("codigo")){
                cursor = bancoDados.rawQuery("SELECT * FROM produtos ORDER BY codigo", null);
            }else{
                cursor = bancoDados.rawQuery("SELECT * FROM produtos ORDER BY descricaoProd", null);
            }



            int indiceColunaCodigo = cursor.getColumnIndex("codigo");
            int indiceColunaDescricao = cursor.getColumnIndex("descricaoProd");
            int indiceColunaEstoque = cursor.getColumnIndex("estoque");

            //cursor.moveToFirst();

            for(cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()){
                Produtos p = new Produtos(cursor.getInt(indiceColunaCodigo), cursor.getString(indiceColunaDescricao), cursor.getInt(indiceColunaEstoque));
                produtos.add(p);
            }

            return produtos;

        }catch (Exception e){

        }

        return produtos;
    }

    private ArrayList<Produtos> adicionarProdutoFiltro(String pesquisa, String filtro){
        ArrayList<Produtos> produtos = new ArrayList<Produtos>();

        //Criar ou abrir banco de dados
        SQLiteDatabase bancoDados = openOrCreateDatabase("app", MODE_PRIVATE, null);

        //Select para puxar os cadastros
        Cursor cursor = bancoDados.rawQuery("SELECT * FROM produtos " +
                "WHERE " + filtro + " like '%" + pesquisa + "%'" +
                "ORDER BY " + filtro, null);

        int indiceColunaCodigo = cursor.getColumnIndex("codigo");
        int indiceColunaDescricao = cursor.getColumnIndex("descricaoProd");
        int indiceColunaEstoque = cursor.getColumnIndex("estoque");

        //cursor.moveToFirst();

        for(cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()){
            Produtos p = new Produtos(cursor.getInt(indiceColunaCodigo), cursor.getString(indiceColunaDescricao), cursor.getInt(indiceColunaEstoque));
            produtos.add(p);
        }

        return produtos;
    }

    private void alertaConfirmacao(String titulo, String mensagem){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(titulo);
        builder.setMessage(mensagem);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

}

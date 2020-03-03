package com.example.movimentoxml;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ContagemActivity extends AppCompatActivity {

    private TextView viewCodigo;
    private TextView viewDescricao;
    private TextView viewEstoque;
    private EditText contagem;
    private Button adicionar;
    private Button filtrar;
    private ListView lista;
    private RadioButton filtroCodigo;
    private RadioButton filtroDescricao;
    private EditText textoFiltro;
    private TextView contagemNumero;
    private Button listarContagens;
    private Button listarProdutos;
    private Button limpar;

    private int flag = 0;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contagem);

        contagemNumero = (TextView) findViewById(R.id.txtContagemNumero);

        //Nova contagem ou continuar contagem antiga?
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Contagem.");
        builder.setMessage("Deseja iniciar uma nova contagem ou continuar com a contagem anterior?");

        builder.setPositiveButton("NOVA", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //pegar valores da data pra fazer o codigo da contagem
                Date data = new Date();
                String codCont = new SimpleDateFormat("ddMMyyHHmmss").format(data);
                Toast.makeText(getApplicationContext(), "Contagem nº "+codCont.toString(), Toast.LENGTH_LONG).show();
                contagemNumero.setText(codCont.toString());

                //Criar ou abrir banco de dados
                SQLiteDatabase bancoDados = openOrCreateDatabase("app", MODE_PRIVATE, null);
                //Deletar tabela se ela exisitir para
                bancoDados.execSQL("DROP TABLE IF EXISTS contagem");
                //Criar tabela produtos
                bancoDados.execSQL("CREATE TABLE IF NOT EXISTS contagem(codigoContagem INT(12), codigoProduto INT(10), descricaoProd VARCHAR, estoque INT(10), contagem INT(10))");
                bancoDados.close();
            }
        });

        builder.setNegativeButton("CONTINUAR", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //Criar ou abrir banco de dados
                SQLiteDatabase bancoDados = openOrCreateDatabase("app", MODE_PRIVATE, null);

                Cursor cursor = bancoDados.rawQuery("SELECT codigoContagem FROM contagem", null);
                cursor.moveToFirst();
                int indiceColunaCodigo = cursor.getColumnIndex("codigoContagem");
                contagemNumero.setText(String.valueOf(cursor.getLong(indiceColunaCodigo)));
                bancoDados.close();

                ArrayAdapter adapter = new AdapterContagem(getApplicationContext(), adicionarContagens());
                lista.setAdapter(adapter);

            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();






        viewCodigo = (TextView) findViewById(R.id.txtCodigoV);
        viewDescricao = (TextView) findViewById(R.id.txtDescricaoV);
        viewEstoque = (TextView) findViewById(R.id.txtEstoqueV);
        contagem = (EditText) findViewById(R.id.edtContagem);
        adicionar = (Button) findViewById(R.id.btnAdicionar);
        filtrar = (Button) findViewById(R.id.btnFiltroCont);
        lista = (ListView) findViewById(R.id.lvListaContagem);
        filtroCodigo = (RadioButton) findViewById(R.id.rbCodCont);
        filtroDescricao = (RadioButton) findViewById(R.id.rbDescCont);
        textoFiltro = (EditText) findViewById(R.id.edtPesquisarCont);
        listarContagens = (Button) findViewById(R.id.btnContagens);
        listarProdutos = (Button) findViewById(R.id.btnProdutos);
        limpar = (Button) findViewById(R.id.btnLimpar);

        listarContagens.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayAdapter adapter = new AdapterContagem(view.getContext(), adicionarContagens());
                lista.setAdapter(adapter);
                flag = 1;
            }
        });

        listarProdutos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                RadioGroup grupo = (RadioGroup) findViewById(R.id.rgFiltrarCont);
                String botaoRadio;
                ArrayAdapter adapter;

                switch ( grupo.getCheckedRadioButtonId() ){
                    case R.id.rbCodCont:
                        botaoRadio = "codigo";
                        adapter = new AdapterProdutos(view.getContext() ,adicionarProdutos(botaoRadio));
                        lista.setAdapter(adapter);
                        break;
                    case R.id.rbDescCont:
                        botaoRadio = "descricaoProd";
                        adapter = new AdapterProdutos(view.getContext() ,adicionarProdutos(botaoRadio));
                        lista.setAdapter(adapter);
                        break;
                    default:
                        alertaConfirmacao("Filtro", "Selecionar CÓDIGO ou DESCRIÇÃO para realizar o filtro. ");
                }

                //ArrayAdapter adapter = new AdapterProdutos(view.getContext(), adicionarProdutos());
                //lista.setAdapter(adapter);
                flag = 2;
            }
        });

        filtrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(textoFiltro.getText().toString().isEmpty()){
                    RadioGroup grupo = (RadioGroup) findViewById(R.id.rgFiltrarCont);
                    String botaoRadio;
                    ArrayAdapter adapter;

                    switch ( grupo.getCheckedRadioButtonId() ){
                        case R.id.rbCodCont:
                            botaoRadio = "codigo";
                            adapter = new AdapterProdutos(view.getContext() ,adicionarProdutos(botaoRadio));
                            lista.setAdapter(adapter);
                            break;
                        case R.id.rbDescCont:
                            botaoRadio = "descricaoProd";
                            adapter = new AdapterProdutos(view.getContext() ,adicionarProdutos(botaoRadio));
                            lista.setAdapter(adapter);
                            break;
                        default:
                            alertaConfirmacao("Filtro", "Selecionar CÓDIGO ou DESCRIÇÃO para realizar o filtro. ");
                    }

                }else{
                    RadioGroup grupo = (RadioGroup) findViewById(R.id.rgFiltrarCont);
                    String pesquisa = textoFiltro.getText().toString();
                    String botaoRadio;
                    ArrayAdapter adapter;

                    switch ( grupo.getCheckedRadioButtonId() ){
                        case R.id.rbCodCont:
                            botaoRadio = "codigo";
                            adapter = new AdapterProdutos(view.getContext() ,adicionarProdutoFiltro(pesquisa, botaoRadio));
                            lista.setAdapter(adapter);
                            break;
                        case R.id.rbDescCont:
                            botaoRadio = "descricaoProd";
                            adapter = new AdapterProdutos(view.getContext() ,adicionarProdutoFiltro(pesquisa, botaoRadio));
                            lista.setAdapter(adapter);
                            break;
                        default:
                            alertaConfirmacao("Filtro", "Selecionar CÓDIGO ou DESCRIÇÃO para realizar o filtro. ");
                    }
                }
                textoFiltro.setText("");
                flag = 2;

            }
        });

        lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //TextView texto = (TextView) view;

                if (flag == 2) {
                    TextView codigo = view.findViewById(R.id.codigo);
                    TextView descricao = view.findViewById(R.id.descricao);
                    TextView estoque = view.findViewById(R.id.estoque);

                    viewCodigo.setText(codigo.getText().toString());
                    viewDescricao.setText(descricao.getText().toString());
                    viewEstoque.setText((estoque.getText().toString()));
                }else if(flag == 1){
                    TextView codigo = view.findViewById(R.id.codigo);
                    TextView descricao = view.findViewById(R.id.descricao);
                    TextView estoque = view.findViewById(R.id.estoque);
                    TextView contagens = view.findViewById(R.id.contagem);

                    viewCodigo.setText(codigo.getText().toString());
                    viewDescricao.setText(descricao.getText().toString());
                    viewEstoque.setText((estoque.getText().toString()));
                    contagem.setText(contagens.getText().toString());

                    deletarItemContagem(Integer.parseInt(codigo.getText().toString()));
                    ArrayAdapter adapter = new AdapterContagem(view.getContext(), adicionarContagens());
                    lista.setAdapter(adapter);
                }
            }
        });

        adicionar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if( contagem.getText().toString().isEmpty() ){
                    alertaConfirmacao("Atenção", "Atribuida um valor para o campo CONTAGEM.");
                }else{
                    //Criar ou abrir banco de dados
                    SQLiteDatabase bancoDados = openOrCreateDatabase("app", MODE_PRIVATE, null);

                    bancoDados.execSQL("INSERT INTO contagem (" +
                            "codigoContagem," +
                            "codigoProduto," +
                            "descricaoProd," +
                            "estoque," +
                            "contagem) " +
                            "VALUES " + "(" +
                            Long.parseLong(contagemNumero.getText().toString()) + ", " +
                            Integer.parseInt(viewCodigo.getText().toString()) + ", " +
                            "'"+ viewDescricao.getText() +"', " +
                            Integer.parseInt(viewEstoque.getText().toString()) + ", " +
                            Integer.parseInt(contagem.getText().toString()) + ")");

                    bancoDados.close();

                    viewCodigo.setText("");
                    viewDescricao.setText("");
                    viewEstoque.setText("");
                    contagem.setText("");

                    ArrayAdapter adapter = new AdapterContagem(view.getContext(), adicionarContagens());
                    lista.setAdapter(adapter);

                }
                flag = 1;

            }
        });

        limpar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewCodigo.setText("");
                viewDescricao.setText("");
                viewEstoque.setText("");
                contagem.setText("");
            }
        });

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

    private ArrayList<Contagem> adicionarContagens(){
        ArrayList<Contagem> contagens = new ArrayList<Contagem>();

        //Criar ou abrir banco de dados
        SQLiteDatabase bancoDados = openOrCreateDatabase("app", MODE_PRIVATE, null);

        try{
            //Select para puxar os cadastros
            Cursor cursor = bancoDados.rawQuery("SELECT * FROM contagem ORDER BY codigoProduto", null);

            int indiceColunaCodigoContagem = cursor.getColumnIndex("codigoContagem");
            int indiceColunaCodigo = cursor.getColumnIndex("codigoProduto");
            int indiceColunaDescricao = cursor.getColumnIndex("descricaoProd");
            int indiceColunaEstoque = cursor.getColumnIndex("estoque");
            int indiceColunaContagem = cursor.getColumnIndex("contagem");

            //cursor.moveToFirst();

            for(cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()){
                Contagem c = new Contagem(
                        cursor.getInt(indiceColunaCodigoContagem),
                        cursor.getInt(indiceColunaCodigo),
                        cursor.getString(indiceColunaDescricao),
                        cursor.getInt(indiceColunaEstoque),
                        cursor.getInt(indiceColunaContagem));
                contagens.add(c);
            }

            return contagens;

        }catch (Exception e){

        }

        return contagens;
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

    public void deletarItemContagem(int codigo){

        //abrir banco
        SQLiteDatabase bancoDados = openOrCreateDatabase("app", MODE_PRIVATE, null);
        bancoDados.execSQL("DELETE FROM contagem WHERE codigoProduto = " + codigo);
        bancoDados.close();

    }


}

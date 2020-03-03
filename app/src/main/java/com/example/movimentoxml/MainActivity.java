package com.example.movimentoxml;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;



public class MainActivity extends AppCompatActivity {


    private Button botao;
    private Button produtos;
    private Button contagem;
    private Button exportar;
    private String[] permissoesNecessarias = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        botao = (Button) findViewById(R.id.btnTeste);
        produtos = (Button) findViewById(R.id.btnProdutos);
        contagem = (Button) findViewById(R.id.btnContagem);
        exportar = (Button) findViewById(R.id.btnExportar);

        Permissao.validaPermissoes(1,this, permissoesNecessarias);

        botao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                lerArquivo();
            }
        });

        exportar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    exportarArquivo();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        produtos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, ProdutosActivity.class));
            }
        });

        contagem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, ContagemActivity.class));
            }
        });

    }

    public void exportarArquivo() throws IOException {
        ArrayList<Contagem> cont = new ArrayList<Contagem>();
        FileWriter log;
        File arquivo = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "ArqExport.txt");
        if(arquivo.exists()){
            arquivo.delete();
            log = new FileWriter( new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "ArqExport.txt"));
        }else{
            log = new FileWriter( new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "ArqExport.txt"));
        }

        PrintWriter gravarLog = new PrintWriter(log);
        //Criar ou abrir banco de dados
        SQLiteDatabase bancoDados = openOrCreateDatabase("app", MODE_PRIVATE, null);
        try{
            Cursor cursor = bancoDados.rawQuery("SELECT * FROM contagem", null);
            int indiceColunaCodigo = cursor.getColumnIndex("codigoContagem");
            int indiceColunaCodigoProduto = cursor.getColumnIndex("codigoProduto");
            int indiceColunaDescricaoProd = cursor.getColumnIndex("descricaoProd");
            int indiceColunaEstoque = cursor.getColumnIndex("estoque");
            int indiceColunaContagem = cursor.getColumnIndex("contagem");

            for(cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()){
                Contagem c = new Contagem(cursor.getLong(indiceColunaCodigo),
                        cursor.getInt(indiceColunaCodigoProduto),
                        cursor.getString(indiceColunaDescricaoProd),
                        cursor.getInt(indiceColunaEstoque),
                        cursor.getInt(indiceColunaContagem));

                gravarLog.printf(
                        c.getCodigoContagem() + ";" +
                        c.getCodigo() + ";" +
                        c.getDescricao() + ";" +
                        c.getEstoque() + ";" +
                        c.getContagem() + "\n"
                );
            }
            log.close();
            gravarLog.close();
            bancoDados.close();
            alertaConfirmacao("Arquivo de Exportação",
                    "O arquivo foi exportado com sucesso para dentro da pasta DOWNLOAD do smartphone. ");

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                Intent mediaScanIntent = new Intent(
                        Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                Uri contentUri = Uri.fromFile(arquivo);
                mediaScanIntent.setData(contentUri);
                this.sendBroadcast(mediaScanIntent);
            } else {
                sendBroadcast(new Intent(
                        Intent.ACTION_MEDIA_MOUNTED,
                        Uri.parse("ArqExport.txt://"+ Environment.DIRECTORY_DOWNLOADS)));
            }


        }catch (Exception e){

        }

    }

    public void lerArquivo(){

        try {

            //testando permissão
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("PERMISSÃO");
                builder.setMessage("PERMISSÃO NEGADA");
                builder.show();
            }


            File path = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "ArqImport.txt");
            BufferedReader bufferedReader = new BufferedReader(new FileReader( path));
            String linha;

            //Criar ou abrir banco de dados
            SQLiteDatabase bancoDados = openOrCreateDatabase("app", MODE_PRIVATE, null);
            //Deletar tabela se ela exisitir para
            bancoDados.execSQL("DROP TABLE IF EXISTS produtos");

            while((linha = bufferedReader.readLine()) != null){
                String[] moeda = linha.split(";");

                try {
                    //Criar tabela produtos
                    bancoDados.execSQL("CREATE TABLE IF NOT EXISTS produtos(codigo INT(10), descricaoProd VARCHAR, estoque INT(10))");
                    //inserir informações no banco
                    bancoDados.execSQL("INSERT INTO produtos(codigo, descricaoProd, estoque) " +
                            "VALUES (" +
                            Integer.parseInt(moeda[0]) + ", '" +
                            moeda[1] + "', " +
                            Integer.parseInt(moeda[2]) + ") ");


                }catch (Exception e){
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Importação de arquivo");
                    builder.setMessage("Erro no salvar informações no banco: " + e);
                    builder.show();
                }



            }
            //inputStream.close();
            bufferedReader.close();

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Importação de arquivo");
            builder.setMessage("Importação concluída com sucesso");
            builder.show();

        } catch (IOException e) {
            e.printStackTrace();
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Importação de arquivo");
            builder.setMessage("Erro no importar o arquivo: " + e);
            builder.show();
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Importação de arquivo");
            builder.setMessage("Erro no importar o arquivo: " + e);
            builder.show();
        }
    }

    public void incluirImportacaoo(String cod, String desc, String estoq){

        try {
            //Criar ou abrir banco de dados
            SQLiteDatabase bancoDados = openOrCreateDatabase("app", MODE_PRIVATE, null);
            //Deletar tabela se ela exisitir para
            bancoDados.execSQL("DROP TABLE IF EXISTS produtos");
            //Criar tabela produtos
            bancoDados.execSQL("CREATE TABLE IF NOT EXISTS produtos(codigo INT(10), descricaoProd VARCHAR, estoque INT(10))");
            //inserir informações no banco
            bancoDados.execSQL("INSERT INTO produtos(codigo, descricaoProd, estoque) VALUES (" + cod + ", '" +
                    desc + "'', " +
                    estoq + ") ");


        }catch (Exception e){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Importação de arquivo");
            builder.setMessage("Erro no salvar informações no banco: " + e);
            builder.show();
        }

    }

    public void onPreExecute() {
        ProgressDialog dialog = new ProgressDialog(this);
        dialog.setTitle("Aguarde!!!");
        dialog.setMessage("Realizando a importação do arquivo.");
        dialog.show();
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults){

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        for( int resultado : grantResults){

            if(resultado == PackageManager.PERMISSION_DENIED){
                alertaValidacaoPermissao();
            }

        }

    }

    private void alertaValidacaoPermissao(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Permissões negadas");
        builder.setMessage("Para utilizar esse app, é necessário aceitar as permissões");

        builder.setPositiveButton("CONFIRMAR", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
               finish();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
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

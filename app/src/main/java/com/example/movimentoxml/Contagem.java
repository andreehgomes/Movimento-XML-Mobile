package com.example.movimentoxml;

public class Contagem {

    private long codigoContagem;
    private int codigo;
    private String descricao;
    private int estoque;
    private int contagem;

    public long getCodigoContagem() {
        return codigoContagem;
    }

    public void setCodigoContagem(long codigoContagem) {
        this.codigoContagem = codigoContagem;
    }

    public int getCodigo() {
        return codigo;
    }

    public void setCodigo(int codigo) {
        this.codigo = codigo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public int getEstoque() {
        return estoque;
    }

    public void setEstoque(int estoque) {
        this.estoque = estoque;
    }

    public int getContagem() {
        return contagem;
    }

    public void setContagem(int contagem) {
        this.contagem = contagem;
    }

    public Contagem(long codigoContagem, int codigo, String descricao, int estoque, int contagem) {
        this.codigoContagem = codigoContagem;
        this.codigo = codigo;
        this.descricao = descricao;
        this.estoque = estoque;
        this.contagem = contagem;
    }
}

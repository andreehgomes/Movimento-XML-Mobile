package com.example.movimentoxml;

public class Produtos {

    private int codigo;
    private String descricao;
    private int estoque;

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

    public Produtos(int codigo, String descricao, int estoque) {
        this.codigo = codigo;
        this.descricao = descricao;
        this.estoque = estoque;
    }
}

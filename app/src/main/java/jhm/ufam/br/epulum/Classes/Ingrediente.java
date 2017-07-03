package jhm.ufam.br.epulum.Classes;

import java.io.Serializable;

/**
 * Created by Mateus on 21/06/2017.
 */

public class Ingrediente implements Serializable{
    private int quantidade;
    private String nome;
    private String unidade;

    public Ingrediente(int quantidade, String unidade, String nome) {
        this.quantidade = quantidade;
        this.nome = nome;
        this.unidade = unidade;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(int quantidade) {
        this.quantidade = quantidade;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getUnidade() {
        return unidade;
    }

    public void setUnidade(String unidade) {
        this.unidade = unidade;
    }

    public String toString(){
        return quantidade+" "+unidade+" "+nome;
    }
}

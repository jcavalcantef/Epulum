package jhm.ufam.br.epulum.Classes;

/**
 * Created by hendrio on 18/07/17.
 */

public class Categoria {
    private long _id;
    private String nome;
    private String tipo;

    Categoria(){
        nome = ""; tipo = "";
    }

    Categoria(String nome, String tipo){
        this.nome = nome;
        this.tipo = tipo;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }
}

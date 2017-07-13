package jhm.ufam.br.epulum.Classes;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mateus on 7/13/2017.
 */

public class ListaCompras implements Serializable{
    private List<String> itens;
    private String nome;

    public ListaCompras() {
        itens = new ArrayList<>();
    }

    public void addItem(String item){
        itens.add(item);
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void removeItem(int pos){
        itens.remove(pos);
    }

    public List<String> getItens() {
        return itens;
    }

    public void setItens(List<String> itens) {
        this.itens = itens;
    }

    public void modifyItem(int pos, String novo){
        itens.set(pos, novo);
    }

}

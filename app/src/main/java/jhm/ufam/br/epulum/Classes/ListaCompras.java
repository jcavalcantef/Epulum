package jhm.ufam.br.epulum.Classes;

import android.util.Log;

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

    public ListaCompras(String nome, String itens) {
        this.itens = new ArrayList<>();
        setAllItens(itens);
        this.nome = nome;
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

    public void setAllItens(String all){
        Log.v("ITENS all",all+"%");
        all = all.replaceAll("\\[","").replaceAll("\\]","");
        String[] itensNovos= all.split(",");
        String bleh = "";
        for(int i = 0; i < itensNovos.length ; i++){
            bleh = itensNovos[i];
            Log.v("ITENS bleh",bleh+"%");
            itens.add(bleh.trim());
            //Log.v("receita", bleh.trim());
        }

    }

}

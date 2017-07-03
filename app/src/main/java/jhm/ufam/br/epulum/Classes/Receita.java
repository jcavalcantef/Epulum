package jhm.ufam.br.epulum.Classes;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class Receita implements Serializable{
    private String name;
    private String descricao;
    private int photoId;
    private List<Ingrediente> ingredientes;
    private List<String> passos;

    public Receita(String name, String descricao, int photoId) {
        this.name = name;
        this.descricao = descricao;
        this.photoId = photoId;
        this.ingredientes=new ArrayList<>();
        this.passos=new ArrayList<>();
    }

    public Receita() {
        this.ingredientes=new ArrayList<>();
        this.passos=new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public int getPhotoId() {
        return photoId;
    }

    public void setPhotoId(int photoId) {
        this.photoId = photoId;
    }

    public List<Ingrediente> getIngredientes() {
        return ingredientes;
    }

    public void setIngredientes(List<Ingrediente> ingredientes) {
        this.ingredientes = ingredientes;
    }

    public List<String> getPassos() {
        return passos;
    }

    public void setPassos(List<String> passos) {
        this.passos = passos;
    }

    public void addIngrediente(Ingrediente in){
        ingredientes.add(in);
    }

    public void addPasso(String pass){
        passos.add(pass);
    }

}
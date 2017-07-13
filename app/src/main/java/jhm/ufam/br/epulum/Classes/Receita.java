package jhm.ufam.br.epulum.Classes;

import android.util.Log;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class Receita implements Serializable{
    private long _id;
    private String name;
    private String descricao;
    private int photoId;
    private List<String> ingredientes;
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

    public long get_id() {
        return _id;
    }

    public void set_id(long _id) {
        this._id = _id;
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

    public List<String> getIngredientes() {
        return ingredientes;
    }

    public void setIngredientes(List<String> ingredientes) {
        this.ingredientes = ingredientes;
    }

    public List<String> getPassos() {
        return passos;
    }

    public void setPassos(List<String> passos) {
        this.passos = passos;
    }

    public void addIngrediente(String in){
        ingredientes.add(in);
    }

    public void addPasso(String pass){
        passos.add(pass);
    }

    public void modifyIngrediente(int pos, String novo){
        ingredientes.set(pos, novo);
    }

    public void modifyPasso(int pos, String novo){
        passos.set(pos, novo);
    }

    public void removeIngrediente(int pos){
        ingredientes.remove(pos);
    }

    public void removePasso(int pos){
        passos.remove(pos);
    }

    public void setAllIngredientes(String all){
        all=all.replaceAll("\\[","").replaceAll("\\]","");
        String[] ingredientesNovos= all.split(",");
        String bleh;
        for(int i=0;i<ingredientesNovos.length;i++){
            bleh=ingredientesNovos[i].replaceAll(",", "");
            ingredientes.add(bleh.trim());
            //Log.v("receita", bleh.trim());
        }

    }

    public void setAllPassos(String all){
        all=all.replaceAll("\\[","").replaceAll("\\]","");
        String[] ingredientesNovos= all.split(",");
        String bleh;
        for(int i=0;i<ingredientesNovos.length;i++){
            bleh=ingredientesNovos[i].replaceAll(",", "");
            passos.add(bleh.trim());
            //Log.v("receita", bleh.trim());
        }
    }

}
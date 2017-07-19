package jhm.ufam.br.epulum.Classes;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class Receita implements Serializable{
    private long _id;
    private int idCategoria;
    private int idUsuario;
    private String nome;
    private String tempoPreparo;
    private String descricao;
    private int photoId;
    private String foto;
    private List<String> ingredientes;
    private List<String> passos;

    public Receita(String nome, String descricao, int photoId) {
        this.nome = nome;
        this.descricao = descricao;
        this.photoId = photoId;
        this.ingredientes=new ArrayList<>();
        this.passos=new ArrayList<>();
    }

    public Receita() {
        this.ingredientes=new ArrayList<>();
        this.passos=new ArrayList<>();
    }

    public Receita(String nome, String descricao, int photoId, String ingredientes, String passos){
        this.idCategoria=0;
        this.idUsuario=0;
        this.nome="";
        this.tempoPreparo="";
        this.descricao="";
        this.photoId=0;
        this.foto="";
        this.nome = nome;
        this.descricao = descricao;
        this.photoId = photoId;
        this.ingredientes=new ArrayList<>();
        this.passos=new ArrayList<>();
        this.setAllIngredientes(ingredientes);
        this.setAllPassos(passos);
    }

    public Receita(long _id, int idCategoria, int idUsuario, String nome, String tempoPreparo, String descricao, String foto, String ingredientes, String passos) {
        this._id = _id;
        this.idCategoria = idCategoria;
        this.idUsuario = idUsuario;
        this.nome = nome;
        this.tempoPreparo = tempoPreparo;
        this.descricao = descricao;
        this.foto = foto;
        setAllIngredientes(ingredientes);
        setAllPassos(passos);
    }

    public Receita(JSONObject obj){
        try {
            this.ingredientes=new ArrayList<>();
            this.passos=new ArrayList<>();
            //this._id = Long.parseLong(obj.get("Id").toString());
            this.nome= obj.get("Nome").toString();
            this.descricao= obj.get("Descricao").toString();
            this.idCategoria= Integer.parseInt(obj.get("Idcategoria").toString());
            this.setAllIngredientes(obj.get("Ingredientes").toString());
            this.setAllPassos(obj.get("Passos").toString());
            Log.v("json"," completou receita");

        }catch(JSONException e){
            e.printStackTrace();

        }
    }


    public long get_id() {
        return _id;
    }

    public void set_id(long _id) {
        this._id = _id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
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

    public String getIngredientesString(){
        String s = "[";
        int i = 0;
        for(; i < this.ingredientes.size()-1; i++){
            s = s + ingredientes.get(i) + ", ";
        }
        s = s + ingredientes.get(i) + "]";
        return s;
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
        ingredientes= new ArrayList<>();
        all = all.replaceAll("\\[","").replaceAll("\\]","");
        String[] ingredientesNovos= all.split(",");
        String bleh;
        for(int i = 0; i < ingredientesNovos.length; i++){
            bleh = ingredientesNovos[i].replaceAll(",", "");
            ingredientes.add(bleh.trim());
            //Log.v("receita", bleh.trim());
        }

    }

    public void setAllPassos(String all){
        passos= new ArrayList<>();
        all = all.replaceAll("\\[","").replaceAll("\\]","");
        String[] ingredientesNovos = all.split(",");
        String bleh;
        for(int i = 0; i < ingredientesNovos.length; i++){
            bleh = ingredientesNovos[i].replaceAll(",", "");
            passos.add(bleh.trim());
            //Log.v("receita", bleh.trim());
        }
    }

    public int getIdCategoria() {
        return idCategoria;
    }

    public void setIdCategoria(int idCategoria) {
        this.idCategoria = idCategoria;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getTempoPreparo() {
        return tempoPreparo;
    }

    public void setTempoPreparo(String tempoPreparo) {
        this.tempoPreparo = tempoPreparo;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    @Override
    public String toString(){
        return null;
    }
}
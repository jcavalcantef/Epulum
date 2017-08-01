package jhm.ufam.br.epulum.Classes;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SuppressWarnings("serial")
public class Receita implements Serializable{
    private long _id;
    private long _idcategoria;
    private long _idusuario;
    private String nome;
    private String tempopreparo;
    private String descricao;
    private String foto;
    private String fotoLocal;
    private List<String> ingredientes;
    private List<String> passos;
    private int photoId;

    public static enum categoria{
        DOCE,
        SALGADO,
        MASSA,
        BEBIDA,
        SOPA,
        BOLO,
        TORTA
    };

    ArrayList<String> categorias;

    public Receita(String nome, String descricao, int photoId) {
        this.nome = nome;
        this.descricao = descricao;
        this.photoId = photoId;
        this.ingredientes=new ArrayList<>();
        this.passos=new ArrayList<>();
    }

    public Receita() {
        categorias = new ArrayList<>(Arrays.asList("Doce", "Salgado", "Massa", "Bebida", "Sopa", "Bolo", "Torta"));
        //categorias.add("Doce", "Salgado", "Massa", "Bebida", "Sopa", "Bolo", "Torta");
        this.ingredientes=new ArrayList<>();
        this.passos=new ArrayList<>();
    }

    public Receita(String nome, String descricao, String foto, String ingredientes, String passos){
        this.nome = nome;
        this.descricao = descricao;
        this.foto = foto;
        this.ingredientes = new ArrayList<>();
        this.passos = new ArrayList<>();
        this.setAllIngredientes(ingredientes);
        this.setAllPassos(passos);
    }

    public Receita(long _idcategoria, long _idusuario, String nome, String tempopreparo, String descricao, String foto, String ingredientes, String passos, int photoId) {
        this._idcategoria = _idcategoria;
        this._idusuario = _idusuario;
        this.nome = nome;
        this.tempopreparo = tempopreparo;
        this.descricao = descricao;
        this.fotoLocal = foto;
        this.ingredientes = new ArrayList<>();
        this.passos = new ArrayList<>();
        this.setAllIngredientes(ingredientes);
        this.setAllPassos(passos);
        this.photoId = photoId;
    }

    public Receita(JSONObject obj){
        try {
            this.ingredientes=new ArrayList<>();
            this.passos=new ArrayList<>();
            //this._id = Long.parseLong(obj.get("Id").toString());
            this.nome= obj.getString("Nome");
            this.descricao= obj.getString("Descricao");
            this._idcategoria= obj.getInt("Idcategoria");
            this.foto = obj.getString("Foto");
            this.setAllIngredientes(obj.getString("Ingredientes"));
            this.setAllPassos(obj.getString("Passos"));
            this.tempopreparo = obj.getString("Tempopreparo");

            Log.v("json"," completou receita");
        }catch(JSONException e){
            e.printStackTrace();

        }
    }

    public ArrayList<String> getCategorias() {
        return categorias;
    }

    public void setCategorias(ArrayList<String> categorias) {
        this.categorias = categorias;
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

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public List<String> getIngredientes() {
        return ingredientes;
    }

    public String getIngredientesString()
    {
        if(ingredientes.size() == 0) return "NULL";
        return ingredientes.toString();
    }

    public void setIngredientes(List<String> ingredientes) {
        this.ingredientes = ingredientes;
    }

    public void setIngredientesString(String ing) {
        setAllIngredientes(ing);
    }

    public List<String> getPassos() {
        return passos;
    }

    public String getPassosString() {
        if(passos.size() == 0) return "NULL";
        return passos.toString();
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

    public long get_idcategoria() {
        return _idcategoria;
    }

    public void set_idcategoria(long _idcategoria) {
        this._idcategoria = _idcategoria;
    }

    public long get_idusuario() {
        return _idusuario;
    }

    public void set_idusuario(long _idusuario) {
        this._idusuario = _idusuario;
    }

    public String getTempopreparo() {
        return tempopreparo;
    }

    public void setTempopreparo(String tempopreparo) {
        this.tempopreparo = tempopreparo;
    }

    public int getPhotoId() {
        return photoId;
    }

    public void setPhotoId(int photoId) {
        this.photoId = photoId;
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

    public String getFotoLocal() {
        return fotoLocal;
    }

    public void setFotoLocal(String fotoLocal) {
        this.fotoLocal = fotoLocal;
    }

    public void setAllIngredientes(String all){
        all = all.replaceAll("\\[","").replaceAll("\\]","");
        String[] ingredientesNovos= all.split(",");
        String bleh = "";
        for(int i = 0; i < ingredientesNovos.length ; i++){
            bleh = ingredientesNovos[i];
            ingredientes.add(bleh.trim());
            //Log.v("receita", bleh.trim());
        }

    }

    public void setAllPassos(String all){
        all = all.replaceAll("\\[","").replaceAll("\\]","");
        String[] passosNovos= all.split(",");
        String bleh = "";
        for(int i = 0; i < passosNovos.length ; i++){
            bleh = passosNovos[i];
            passos.add(bleh.trim());
            //Log.v("receita", bleh.trim());
        }
    }



}
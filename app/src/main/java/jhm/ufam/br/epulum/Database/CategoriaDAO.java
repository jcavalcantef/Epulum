package jhm.ufam.br.epulum.Database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;

import jhm.ufam.br.epulum.Classes.Categoria;

/**
 * Created by hendrio on 18/07/17.
 */

public class CategoriaDAO {
    private SQLiteDatabase bancoDeDados;

    public  CategoriaDAO(Context context){ // se conecta com o banco // Ex: connect();
        this.bancoDeDados = (new MyDBHandler(context)).getWritableDatabase();
        //initializeDatabase();
    }

    public Categoria getCategoria(String nome){
        Categoria c = null;
        String sqlQuery = "SELECT * FROM " + MyDBHandler.TABLE_CATEGORIAS + " WHERE " +
                MyDBHandler.COLUNA_categorianome + " LIKE '" + nome + "%'";
        Cursor cursor = this.bancoDeDados.rawQuery(sqlQuery,null);
        if(cursor.moveToNext()){
            c = new Categoria(cursor.getString(0),cursor.getLong(1));
        }
        cursor.close();
        return c;
    }

    public ArrayList<Categoria> getAllCategorias(){
        ArrayList<Categoria> categorias = new ArrayList<Categoria>();
        String selectQuery = "SELECT * FROM " + MyDBHandler.TABLE_CATEGORIAS;
        Cursor cursor = bancoDeDados.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()){
            do{
                Categoria c = new Categoria(cursor.getString(0),cursor.getLong(1));
                categorias.add(c);
            } while (cursor.moveToNext());
        }
        return categorias;
    }

    public ArrayList<String> getAllCategoriasByNome(){
        ArrayList<String> categorias = new ArrayList<String>();
        String selectQuery = "SELECT * FROM " + MyDBHandler.TABLE_CATEGORIAS;
        Cursor cursor = bancoDeDados.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()){
            do{
                Categoria c = new Categoria(cursor.getString(0),cursor.getLong(1));
                categorias.add(c.getNome());
            } while (cursor.moveToNext());
        }
        return categorias;
    }

    public boolean addCategoria(Categoria c){
        try{
            String sqlCmd = "INSERT INTO " + MyDBHandler.TABLE_CATEGORIAS + " ( " +
                    MyDBHandler.COLUNA_categorianome + ", " +
                    MyDBHandler.COLUNA_categoriatipo + " ) " +
                    " VALUES ('" +
                    c.getNome() + "', '"+
                    c.getTipo() + "' );";
            this.bancoDeDados.execSQL(sqlCmd);
            return true;
        }catch (Exception e){
            Log.e("Erro ao tentar add", e.getMessage());
            return false;
        }
    }

    public boolean updateCategoria(Categoria c, String nome){
        try{
            String sqlCmd = "UPDATE " + MyDBHandler.TABLE_RECEITAS + " SET " +
                    MyDBHandler.COLUNA_categorianome + " ='" + c.getNome() + "', " +
                    MyDBHandler.COLUNA_categoriatipo + " ='" + c.getTipo() + "' " +
                    "WHERE " + MyDBHandler.COLUNA_categorianome + " = '" + nome + "' ;";
            this.bancoDeDados.execSQL(sqlCmd);
            return true;
        }catch (Exception e){
            Log.e("Erro ao tentar editar", e.getMessage());
            return false;
        }
    }

    public boolean removeCategoria(Categoria c){
        try{
            String sqlCmd = "DELETE FROM " + MyDBHandler.TABLE_CATEGORIAS + " WHERE " + MyDBHandler.COLUNA_categorianome + " = '" + c.getNome() + "';";
            this.bancoDeDados.execSQL(sqlCmd);
            return true;
        }catch (Exception e){
            Log.e("Erro ao tentar remover", e.getMessage());
            return false;
        }
    }

    public void initializeDatabase(){
        Categoria massas = new Categoria();
    }
}

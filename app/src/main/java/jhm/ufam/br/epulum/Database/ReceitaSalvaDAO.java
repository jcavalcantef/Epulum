package jhm.ufam.br.epulum.Database;

/**
 * Created by hendrio on 19/07/17.
 */

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;

import jhm.ufam.br.epulum.Classes.Categoria;
import jhm.ufam.br.epulum.Classes.Receita;
import jhm.ufam.br.epulum.R;

public class ReceitaSalvaDAO {
    private SQLiteDatabase bancoDeDados;
    private Context context;

    public  ReceitaSalvaDAO(Context context){ // se conecta com o banco // Ex: connect();
        this.bancoDeDados = (new MyDBHandler(context)).getWritableDatabase();
        this.context = context;
    }
    /*
    *
    * Função getReceita
    *
    * Usando como parâmetro o nome da receita, busca nomes semelhantes no banco de dados e retorna o primeiro resultado encontrado
    * Retorna a classe Receita
    *
    * */
    public Receita getReceita(String nome){
        Receita receita = null;
        String sqlQuery = "SELECT * FROM " + MyDBHandler.TABLE_RECEITAS_SALVAS + " WHERE " +
                MyDBHandler.COLUNA_nome + " LIKE '" + nome + "%'";
        Cursor cursor = this.bancoDeDados.rawQuery(sqlQuery,null);
        if(cursor.moveToNext()){
            receita = new Receita(cursor.getInt(1),cursor.getInt(2),cursor.getString(3), cursor.getString(4), cursor.getString(5), cursor.getString(6), cursor.getString(7), cursor.getString(8), cursor.getInt(9));
        }
        cursor.close();
        return receita;
    }
    /*
    *
    *
    * Função getAllReceitas
    *
    * Utiliza uma query no estilo SELECT * FROM TABELA para, do resultado da query, retornar um
    * ArrayList com todas as receitas encontradas.
    *
    * */
    public ArrayList<Receita> getAllReceitas(){
        ArrayList<Receita> receitas = new ArrayList<Receita>();
        String selectQuery = "SELECT * FROM " + MyDBHandler.TABLE_RECEITAS_SALVAS;
        Cursor cursor = bancoDeDados.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()){
            do{
                Receita r = new Receita(cursor.getInt(1),cursor.getInt(2),
                        cursor.getString(3), cursor.getString(4),
                        cursor.getString(5), cursor.getString(6),
                        cursor.getString(7), cursor.getString(8), cursor.getInt(9));
                receitas.add(r);
            } while (cursor.moveToNext());
        }
        return receitas;
    }
    /*
    *
    * Função getReceitasByUsuario
    *
    * Parecida com getAllReceitas e getReceita, porém pega todos as receitas de um único usuário, utilizando seu id
    *
    * */
    public ArrayList<Receita> getReceitasByUsuario(long idUsuario){
        ArrayList<Receita> receitas = new ArrayList<Receita>();
        String sqlQuery = "SELECT * FROM " + MyDBHandler.TABLE_RECEITAS_SALVAS + " WHERE " +
                MyDBHandler.COLUNA_idusuario + " LIKE '" + idUsuario + "%'";
        Cursor cursor = bancoDeDados.rawQuery(sqlQuery, null);
        if (cursor.moveToFirst()){
            do{
                Receita r = new Receita(cursor.getInt(1),cursor.getInt(2),cursor.getString(3), cursor.getString(4), cursor.getString(5), cursor.getString(6), cursor.getString(7), cursor.getString(8), cursor.getInt(9));
                receitas.add(r);
            } while (cursor.moveToNext());
        }
        return receitas;
    }
    /*
    *
    * Função getReceitasByIdCategoria
    *
    * Parecida com getAllReceitas e getReceita, porém pega todos as receitas de uma categoria, utilizando seu id
    *
    * */
    public ArrayList<Receita> getReceitasByIdCategoria(long idCategoria){
        ArrayList<Receita> receitas = new ArrayList<Receita>();
        String sqlQuery = "SELECT * FROM " + MyDBHandler.TABLE_RECEITAS_SALVAS + " WHERE " +
                MyDBHandler.COLUNA_idcategoria + " LIKE '" + idCategoria + "%'";
        Cursor cursor = bancoDeDados.rawQuery(sqlQuery, null);
        if (cursor.moveToFirst()){
            do{
                Receita r = new Receita(cursor.getInt(1),cursor.getInt(2),cursor.getString(3), cursor.getString(4), cursor.getString(5), cursor.getString(6), cursor.getString(7), cursor.getString(8), cursor.getInt(9));
                receitas.add(r);
            } while (cursor.moveToNext());
        }
        return receitas;
    }
    /*
    *
    * Função getReceitasByNomeCategoria
    *
    * Parecida com getAllReceitas e getReceita, porém pega todos as receitas de uma categoria, utilizando seu nome
    *
    * */
    public ArrayList<Receita> getReceitasByNomeCategoria(String nomeCategoria){
        ArrayList<Receita> receitas = new ArrayList<Receita>();
        CategoriaDAO categoriaDAO = new CategoriaDAO(context);
        Categoria c = categoriaDAO.getCategoria(nomeCategoria);
        String sqlQuery = "SELECT * FROM " + MyDBHandler.TABLE_RECEITAS_SALVAS + " WHERE " +
                MyDBHandler.COLUNA_categoriatipo + " LIKE '" + c.getTipo() + "%'";
        Cursor cursor = bancoDeDados.rawQuery(sqlQuery, null);
        if (cursor.moveToFirst()){
            do{
                Receita r = new Receita(cursor.getInt(1),cursor.getInt(2),cursor.getString(3), cursor.getString(4), cursor.getString(5), cursor.getString(6), cursor.getString(7), cursor.getString(8), cursor.getInt(9));
                receitas.add(r);
            } while (cursor.moveToNext());
        }
        return receitas;
    }
    /*
    *
    *
    * Função addReceita
    *
    * Utilizando um parâmetro Receita, adiciona essa instância no banco de dados e retorna um valor verdadeiro
    * caso tenha adicionado com sucesso, e falso caso contrário.
    *
    * */
    public boolean addReceita(Receita r){
        if(existsReceita(r.getNome()))
            return false;
        try{
            Log.v("Valor Ingredientes", r.getIngredientesString());
            String sqlCmd = "INSERT INTO " + MyDBHandler.TABLE_RECEITAS_SALVAS + " ( " +
                    MyDBHandler.COLUNA_idcategoria + ", " +
                    MyDBHandler.COLUNA_idusuario + ", " +
                    MyDBHandler.COLUNA_nome + ", " +
                    MyDBHandler.COLUNA_tempopreparo + ", " +
                    MyDBHandler.COLUNA_descricao + ", " +
                    MyDBHandler.COLUNA_foto + ", " +
                    MyDBHandler.COLUNA_ingredientes + ", " +
                    MyDBHandler.COLUNA_passos + ", " +
                    MyDBHandler.COLUNA_photoid + " ) " +
                    " VALUES ('" +
                    r.get_idcategoria() + "', '"+
                    r.get_idusuario() + "', '"+
                    r.getNome() + "', '"+
                    r.getTempopreparo() + "', '"+
                    r.getDescricao() + "', '" +
                    r.getFotoLocal() + "', '" +
                    r.getIngredientesString() + "', '" +
                    r.getPassosString() + "', '" +
                    r.getPhotoId() + "' );";
            this.bancoDeDados.execSQL(sqlCmd);
            return true;
        }catch (Exception e){
            Log.e("Erro ao tentar add", e.getMessage());
            return false;
        }
    }
    /*
    *
    *
    * Função removeReceita
    *
    * Utilizando Receita como parâmetro, remove a receita em questão e retorna verdadeiro em caso positivo,
    * falso caso contrário.
    *
    * */
    public boolean removeReceita(String nome){
        if(!existsReceita(nome))
            return false;
        try{
            String sqlCmd = "DELETE FROM " + MyDBHandler.TABLE_RECEITAS_SALVAS + " WHERE " + MyDBHandler.COLUNA_nome + "='" + nome + "';";
            this.bancoDeDados.execSQL(sqlCmd);
            return true;
        }catch (Exception e){
            Log.e("Erro ao tentar remover", e.getMessage());
            return false;
        }
    }
    /*
    *
    *
    * Função updateReceita
    *
    * Utilizando o nome de uma receita e os valores a alterar em Receita, atualiza-se uma instância de Receita
    * no banco de dados, retornando verdadeiro caso haja sucesso e falso caso contrário.
    *
    * */
    public boolean updateReceita(Receita r, String nome){
        try{
            String sqlCmd = "UPDATE " + MyDBHandler.TABLE_RECEITAS_SALVAS + " SET " +
                    MyDBHandler.COLUNA_nome + " ='" + r.getNome() + "', " +
                    MyDBHandler.COLUNA_descricao+ " ='" + r.getDescricao() + "', " +
                    MyDBHandler.COLUNA_foto+ " ='" + r.getFoto() + "', " +
                    MyDBHandler.COLUNA_ingredientes + " ='" + r.getIngredientesString() + "', " +
                    MyDBHandler.COLUNA_passos + " ='" + r.getPassosString() + "' " +
                    "WHERE " + MyDBHandler.COLUNA_nome + " = '" + nome + "' ;";
            this.bancoDeDados.execSQL(sqlCmd);
            return true;
        }catch (Exception e){
            Log.e("Erro ao tentar editar", e.getMessage());
            return false;
        }
    }

    /* Testa se uma receita já existe no banco de dados com o mesmo nome*/
    public boolean existsReceita(String nome){
        return (getReceita(nome)!=null);
    }

    public void close(){
        MyDBHandler.closeDatabase(bancoDeDados);
    }

}
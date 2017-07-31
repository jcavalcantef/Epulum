package jhm.ufam.br.epulum.Database;

/**
 * Created by hendrio on 14/07/17.
 */
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.util.Log;


import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import jhm.ufam.br.epulum.Classes.Categoria;
import jhm.ufam.br.epulum.Classes.ListaCompras;
import jhm.ufam.br.epulum.Classes.Receita;
import jhm.ufam.br.epulum.R;


public class ListaComprasDAO {
    private SQLiteDatabase bancoDeDados;
    private Context context;

    public  ListaComprasDAO(Context context){ // se conecta com o banco // Ex: connect();
        this.bancoDeDados = (new MyDBHandler(context)).getWritableDatabase();
        this.context = context;
        initializeDatabase();
    }
    /*
    *
    * Função getReceita
    *
    * Usando como parâmetro o nome da receita, busca nomes semelhantes no banco de dados e retorna o primeiro resultado encontrado
    * Retorna a classe Receita
    *
    * */
    public ListaCompras getListaCompras(String nome){
        if(!isOpen()){
            open();
        };
        ListaCompras listaCompras = null;
        String sqlQuery = "SELECT * FROM " + MyDBHandler.TABLE_LISTACOMPRAS + " WHERE " +
                MyDBHandler.COLUNA_nome + " LIKE '" + nome + "%'";
        Cursor cursor = this.bancoDeDados.rawQuery(sqlQuery,null);
        if(cursor.moveToNext()){
            listaCompras = new ListaCompras(cursor.getString(0),cursor.getString(1));
        }
        cursor.close();
        return listaCompras;
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
    public ArrayList<ListaCompras> getAllListasCompras(){
        ArrayList<ListaCompras> listasCompras = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + MyDBHandler.TABLE_LISTACOMPRAS;
        Cursor cursor = bancoDeDados.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()){
            do{
                Log.v("LISTA COMPRA 1",cursor.getString(0)+" "+cursor.getString(1));
                ListaCompras lc = new ListaCompras(cursor.getString(0),cursor.getString(1));
                listasCompras.add(lc);
            } while (cursor.moveToNext());
        }
        return listasCompras;
    }
    /*
    *
    * Função getReceitasByUsuario
    *
    * Parecida com getAllReceitas e getReceita, porém pega todos as receitas de um único usuário, utilizando seu id
    *
    * */
    public ArrayList<ListaCompras> getListaComprasByNome(String nome){
        ArrayList<ListaCompras> listasCompras = new ArrayList<>();
        String sqlQuery = "SELECT * FROM " + MyDBHandler.TABLE_LISTACOMPRAS + " WHERE " +
                MyDBHandler.COLUNA_listanome + " LIKE '" + nome + "%'";
        Cursor cursor = bancoDeDados.rawQuery(sqlQuery, null);
        if (cursor.moveToFirst()){
            do{
                ListaCompras lc = new ListaCompras(cursor.getString(0),cursor.getString(1));
                listasCompras.add(lc);
            } while (cursor.moveToNext());
        }
        return listasCompras;
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
    public boolean addListaCompras(ListaCompras lc){
        if(existsListaCompras(lc.getNome()))
            return false;
        try{
            String sqlCmd = "INSERT INTO " + MyDBHandler.TABLE_LISTACOMPRAS + " ( " +
                    MyDBHandler.COLUNA_listanome + ", " +
                    MyDBHandler.COLUNA_listaitens + ") " +
                    " VALUES ('" +
                    lc.getNome() + "', '"+
                    lc.getItens().toString() + "' );";
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
    public boolean removeListaCompras(String nome){
        if(!existsListaCompras(nome))
            return false;
        try{
            String sqlCmd = "DELETE FROM " + MyDBHandler.TABLE_LISTACOMPRAS + " WHERE " + MyDBHandler.COLUNA_listanome + "='" + nome + "';";
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
    public boolean updateListaCompras(ListaCompras lc, String nome){
        try{
            String sqlCmd = "UPDATE " + MyDBHandler.TABLE_LISTACOMPRAS + " SET " +
                    MyDBHandler.COLUNA_listanome + " ='" + lc.getNome() + "', " +
                    MyDBHandler.COLUNA_listaitens+ " ='" + lc.getItens().toString() + "', " +
                    "WHERE " + MyDBHandler.COLUNA_listanome + " = '" + nome + "' ;";
            this.bancoDeDados.execSQL(sqlCmd);
            return true;
        }catch (Exception e){
            Log.e("Erro ao tentar editar", e.getMessage());
            return false;
        }
    }
    /*
    *
    *
    * Função initializeDatabase
    *
    * Esta função insere vários valores pré-definidos no nosso recém-criado banco de dados, incluindo uma receita de torta,
    * joelho de porco, entre outras
    *
    * */
    public void initializeDatabase() {
        ListaCompras a= new ListaCompras();
        a.setNome("Mercado semanal");
        a.addItem("Batata");
        a.addItem("cebola");
        a.addItem("banana");
        a.addItem("repolho roxo");
        ListaCompras b= new ListaCompras();
        b.setNome("Farmacia");
        b.addItem("tilenol");
        b.addItem("dipirona");
        b.addItem("leite de rosas");
        ListaCompras c= new ListaCompras();
        c.setNome("Churrascada");
        c.addItem("Batata palha");
        c.addItem("Picanha");
        c.addItem("Maminha");
        c.addItem("Amendoim");
        addListaCompras(a);
        addListaCompras(b);
        addListaCompras(c);


    }

    /* Testa se uma receita já existe no banco de dados com o mesmo nome*/
    public boolean existsListaCompras(String nome){
        return (getListaCompras(nome)!=null);
    }

    public void close(){
        MyDBHandler.closeDatabase(bancoDeDados);
    }

    public void open(){
        bancoDeDados = (new MyDBHandler(context)).getWritableDatabase();
    }

    public boolean isOpen(){
        return MyDBHandler.isOpen(bancoDeDados);
    }
}
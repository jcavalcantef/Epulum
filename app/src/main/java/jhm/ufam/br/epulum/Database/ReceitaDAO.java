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

import jhm.ufam.br.epulum.Classes.Receita;
import jhm.ufam.br.epulum.R;


public class ReceitaDAO {
    private SQLiteDatabase bancoDeDados;

    public  ReceitaDAO(Context context){ // se conecta com o banco // Ex: connect();
        this.bancoDeDados = (new MyDBHandler(context)).getWritableDatabase();
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
    public Receita getReceita(String nome){
        Receita produto = null;
        String sqlQuery = "SELECT * FROM " + MyDBHandler.TABLE_RECIPES + " WHERE " +
                MyDBHandler.COLUMN_RECIPENAME + " LIKE '" + nome + "%'";
        Cursor cursor = this.bancoDeDados.rawQuery(sqlQuery,null);
        if(cursor.moveToNext()){
            produto = new Receita(cursor.getString(0), cursor.getString(1),cursor.getInt(2),cursor.getString(3), cursor.getString(4));
        }
        cursor.close();
        return produto;
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
        String selectQuery = "SELECT * FROM " + MyDBHandler.TABLE_RECIPES;
        Cursor cursor = bancoDeDados.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()){
            do{
                Receita r = new Receita(cursor.getString(0), cursor.getString(1),cursor.getInt(2),cursor.getString(3), cursor.getString(4));
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
        try{
            String sqlCmd = "INSERT INTO " + MyDBHandler.TABLE_RECIPES + " VALUES ('" +
                    r.getNome() + "', '"+ r.getDescricao() + "', '" +
                    r.getPhotoId() + "', '" + r.getIngredientesString() + "');";
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
    public boolean removeReceita(Receita r){
        try{
            String sqlCmd = "DELETE FROM " + MyDBHandler.TABLE_RECIPES + " WHERE " + MyDBHandler.COLUMN_RECIPENAME + "='" + r.getNome() + "'";
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
            String sqlCmd = "UPDATE " + MyDBHandler.TABLE_RECIPES + " SET " +
                    MyDBHandler.COLUMN_RECIPEDESCRIPTION + " ='" + r.getDescricao() + "', " +
                    MyDBHandler.COLUMN_RECIPEPHOTOID + " ='" + r.getPhotoId() + "', " +
                    MyDBHandler.COLUMN_RECIPEINGREDIENTS + " ='" + r.getIngredientes() + "', " +
                    MyDBHandler.COLUMN_RECIPESTEPS + " ='" + r.getPassos() + "', " +
                    MyDBHandler.COLUMN_RECIPENAME + " ='" + r.getNome() + "' " +
                    "WHERE " + MyDBHandler.COLUMN_RECIPENAME + " ='" + nome + "';";
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
        Receita torta = new Receita("Torta de Maçã", "Uma Torta de Maçã muito gostosa e simples.", R.drawable.torta_de_maca);
        /*torta.addIngrediente("100 gramas de manteiga");
        torta.addIngrediente("2 gemas");
        torta.addIngrediente("4 colheres de açúcar refinado");
        torta.addIngrediente("200 gramas de farinha de trigo");
        torta.addIngrediente("500 ml de leite");
        torta.addIngrediente("1 lata de leite condensado");
        torta.addIngrediente("2 colheres de sopa de amido de milho");
        torta.addIngrediente("3 maçãs");*/
        torta.addPasso("misture a manteiga, as gemas e o açúcar");
        torta.addPasso("Junte a farinha aos poucos, até formar uma massa que não grude nas mãos.");
        torta.addPasso("Forre com a massa uma forma de torta redonda untada levemente com manteiga e fure toda a superfície com um garfo e leve ao forno pré-aquecido em temperatura média ou baixa para a massa dourar, aproximadamente 15 minutos");
        torta.addPasso("numa panela, coloque a água e o açúcar e leve ao fogo");
        torta.addPasso("Ao ferver, junte as fatias de maçãs para cozinhar levemente sem deixar desmanchar, apenas uns 2 minutos");
        torta.addPasso("Retire as maçãs com uma escumadeira e acrescente a gelatina à água que sobrou na panela, mexendo bem");
        torta.addPasso("Deixe esfriar e leve a geladeira por 10 minutos");
        torta.setAllIngredientes("[100 gramas de manteiga, 2 gemas, 4 colheres de açúcar refinado, 200 gramas de farinha de trigo, 500 ml de leite, 1 lata de leite condensado, 2 colheres de sopa de amido de milho, 3 maçãs]");
        this.addReceita(torta);
        //Log.v("receita","começa :"+torta.getIngredientes().toString()+": termina");
        this.addReceita(new Receita("Joelho de Porco", "Joelho de porco com a casca tostada e crocante.", R.drawable.joelho_de_porco));
        this.addReceita(new Receita("Hambúrguer Vegano", "Hambúrguer sem carne para quem quer uma refeição saudável.", R.drawable.hamburguer_vegano));
        this.addReceita(new Receita("Bolinho De Carne Moída", "", R.drawable.bolinho_de_carne_moida));
        this.addReceita(new Receita("Filé À Parmegiana", "", R.drawable.file_parmegiana));
        this.addReceita(new Receita("Costela Na Pressão Com Linguíça", "", R.drawable.costela_na_pressao));
        this.addReceita(new Receita("Camarão com creme de leite", "", R.drawable.camarao_com_creme_de_leite));
        this.addReceita(new Receita("Sopa de abóbora", "", R.drawable.sopa_de_abobora));

    }

}
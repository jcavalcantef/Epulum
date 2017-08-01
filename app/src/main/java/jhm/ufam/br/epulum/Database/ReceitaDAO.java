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
import jhm.ufam.br.epulum.Classes.Receita;
import jhm.ufam.br.epulum.R;


public class ReceitaDAO {
    private SQLiteDatabase bancoDeDados;
    private Context context;

    public  ReceitaDAO(Context context){ // se conecta com o banco // Ex: connect();
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
    public Receita getReceita(String nome){
        if(!isOpen()){
            open();
        };
        Receita receita = null;
        String sqlQuery = "SELECT * FROM " + MyDBHandler.TABLE_RECEITAS + " WHERE " +
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
        ArrayList<Receita> receitas = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + MyDBHandler.TABLE_RECEITAS;
        Cursor cursor = bancoDeDados.rawQuery(selectQuery, null);
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
* Função getReceitasByUsuario
*
* Parecida com getAllReceitas e getReceita, porém pega todos as receitas de um único usuário, utilizando seu id
*
* */
    public ArrayList<Receita> getReceitasByUsuario(long idUsuario){
        ArrayList<Receita> receitas = new ArrayList<>();
        String sqlQuery = "SELECT * FROM " + MyDBHandler.TABLE_RECEITAS + " WHERE " +
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
        String sqlQuery = "SELECT * FROM " + MyDBHandler.TABLE_RECEITAS + " WHERE " +
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
        String sqlQuery = "SELECT * FROM " + MyDBHandler.TABLE_RECEITAS + " WHERE " +
                MyDBHandler.COLUNA_idcategoria + " LIKE '" + c.getTipo() + "%'";
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
            String sqlCmd = "INSERT INTO " + MyDBHandler.TABLE_RECEITAS + " ( " +
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
                    r.getFoto() + "', '" +
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
            String sqlCmd = "DELETE FROM " + MyDBHandler.TABLE_RECEITAS + " WHERE " + MyDBHandler.COLUNA_nome + "='" + nome + "';";
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
            String sqlCmd = "UPDATE " + MyDBHandler.TABLE_RECEITAS + " SET " +
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
        torta.addIngrediente("100 gramas de manteiga");
        torta.addIngrediente("2 gemas");
        torta.addIngrediente("4 colheres de açúcar refinado");
        torta.addIngrediente("200 gramas de farinha de trigo");
        torta.addIngrediente("500 ml de leite");
        torta.addIngrediente("1 lata de leite condensado");
        torta.addIngrediente("2 colheres de sopa de amido de milho");
        torta.addIngrediente("3 maçãs");
        torta.addPasso("misture a manteiga as gemas e o açúcar");
        torta.addPasso("Junte a farinha aos poucos até formar uma massa que não grude nas mãos.");
        torta.addPasso("Forre com a massa uma forma de torta redonda untada levemente com manteiga e fure toda a superfície com um garfo e leve ao forno pré-aquecido em temperatura média ou baixa para a massa dourar aproximadamente 15 minutos");
        torta.addPasso(" coloque a água e o açúcar numa panela e leve ao fogo");
        torta.addPasso("Ao ferver junte as fatias de maçãs para cozinhar levemente sem deixar desmanchar apenas uns 2 minutos");
        torta.addPasso("Retire as maçãs com uma escumadeira e acrescente a gelatina à água que sobrou na panela mexendo bem");
        torta.addPasso("Deixe esfriar e leve a geladeira por 10 minutos");
        torta.set_idusuario(1);
        //torta.setAllIngredientes("[100 gramas de manteiga, 2 gemas, 4 colheres de açúcar refinado, 200 gramas de farinha de trigo, 500 ml de leite, 1 lata de leite condensado, 2 colheres de sopa de amido de milho, 3 maçãs]");
        this.addReceita(torta);
        //Log.v("receita","começa :"+torta.getIngredientes().toString()+": termina");
        if(!(this.addReceita(new Receita("Joelho de Porco", "Joelho de porco com a casca tostada e crocante.", R.drawable.joelho_de_porco)) ))
            Log.v("ERRO RECEITA","AAAAAAAAA");
        this.addReceita(new Receita("Hambúrguer Vegano", "Hambúrguer sem carne para quem quer uma refeição saudável.", R.drawable.hamburguer_vegano));
        this.addReceita(new Receita("Bolinho De Carne Moída", "", R.drawable.bolinho_de_carne_moida));
        this.addReceita(new Receita("Filé À Parmegiana", "", R.drawable.file_parmegiana));
        this.addReceita(new Receita("Costela Na Pressão Com Linguíça", "", R.drawable.costela_na_pressao));
        this.addReceita(new Receita("Camarão com creme de leite", "", R.drawable.camarao_com_creme_de_leite));
        this.addReceita(new Receita("Sopa de abóbora", "", R.drawable.sopa_de_abobora));
    }

/* Testa se uma receita já existe no banco de dados com o mesmo nome*/
    public boolean existsReceita(String nome){
        return (getReceita(nome)!=null);
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
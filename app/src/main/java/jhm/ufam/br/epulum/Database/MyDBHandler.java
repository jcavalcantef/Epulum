package jhm.ufam.br.epulum.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import jhm.ufam.br.epulum.Classes.Receita;

/**
 * Created by hendrio on 12/07/17.
 */

public class MyDBHandler extends SQLiteOpenHelper{

    private static final int DATABASE_VERSION = 2;
    private static final String DATABASE_NAME = "recipesDB.db";
    public static final String TABLE_RECEITAS = "Receitas";
    public static final String TABLE_RECEITAS_SALVAS = "ReceitasSalvas";

    public static final String COLUNA_id="id";
    public static final String COLUNA_idcategoria="idcategoria";
    public static final String COLUNA_idusuario="idusuario";
    public static final String COLUNA_nome="nome";
    public static final String COLUNA_tempopreparo="tempopreparo";
    public static final String COLUNA_descricao="descricao";
    public static final String COLUNA_foto="foto";
    public static final String COLUNA_fotolocal="fotolocal";
    public static final String COLUNA_ingredientes="ingredientes";
    public static final String COLUNA_passos="passos";
    public static final String COLUNA_photoid = "photoid";
    public static final String TABLE_CREATE = "CREATE TABLE IF NOT EXISTS " + TABLE_RECEITAS + " ( " +
            COLUNA_id + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUNA_idcategoria + " INT, " +
            COLUNA_idusuario + " INT, " +
            COLUNA_nome + " TEXT, " +
            COLUNA_tempopreparo + " TEXT, " +
            COLUNA_descricao + " TEXT, " +
            COLUNA_foto + " TEXT, " +
            COLUNA_fotolocal + " TEXT, " +
            COLUNA_ingredientes + " TEXT, " +
            COLUNA_passos + " TEXT," +
            COLUNA_photoid + " INT );";

    public static final String TABLE_CATEGORIAS = "Categorias";
    public static final String COLUNA_categorianome="nome";
    public static final String COLUNA_categoriatipo="tipo";
    public static final String TABLE_CREATE_CATEGORIA = "CREATE TABLE IF NOT EXISTS " + TABLE_CATEGORIAS + " ( " +
            COLUNA_categorianome + " TEXT," +
            COLUNA_categoriatipo + " LONG );";

    public static final String TABLE_CREATE_RECEITAS_SALVAS = "CREATE TABLE IF NOT EXISTS " + TABLE_RECEITAS_SALVAS + " ( " +
            COLUNA_id + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUNA_idcategoria + " INT, " +
            COLUNA_idusuario + " INT, " +
            COLUNA_nome + " TEXT, " +
            COLUNA_tempopreparo + " TEXT, " +
            COLUNA_descricao + " TEXT, " +
            COLUNA_foto + " TEXT, " +
            COLUNA_ingredientes + " TEXT, " +
            COLUNA_passos + " TEXT," +
            COLUNA_photoid + " INT );";

    public static final String TABLE_LISTACOMPRAS = "ListaCompras";
    public static final String COLUNA_listanome="nome";
    public static final String COLUNA_listaitens="itens";
    public static final String TABLE_CREATE_LISTACOMPRAS="CREATE TABLE IF NOT EXISTS " + TABLE_LISTACOMPRAS + " ( " +
            COLUNA_listanome + " TEXT," +
            COLUNA_listaitens + " TEXT );";

    private static final String INSERIR_CATEGORIA_DOCE = "INSERT INTO " + TABLE_CATEGORIAS + "(" +
            COLUNA_categorianome + ", " + COLUNA_categoriatipo+ ")" +
            "VALUES ('Doce', 'Sobremesa');";
    private static final String INSERIR_CATEGORIA_SALGADO = "INSERT INTO " + TABLE_CATEGORIAS + "(" +
            COLUNA_categorianome + ", " + COLUNA_categoriatipo+ ")" +
            "VALUES ('Salgado', 'Refeição');";
    private static final String INSERIR_CATEGORIA_MASSA = "INSERT INTO " + TABLE_CATEGORIAS + "(" +
            COLUNA_categorianome + ", " + COLUNA_categoriatipo+ ")" +
            "VALUES ('Massa', 'Refeição');";
    private static final String INSERIR_CATEGORIA_BEBIDA = "INSERT INTO " + TABLE_CATEGORIAS + "(" +
            COLUNA_categorianome + ", " + COLUNA_categoriatipo+ ")" +
            "VALUES ('Bebida', 'Líquido');";
    private static final String INSERIR_CATEGORIA_SOPA = "INSERT INTO " + TABLE_CATEGORIAS + "(" +
            COLUNA_categorianome + ", " + COLUNA_categoriatipo+ ")" +
            "VALUES ('SOPA', 'Líquido');";
    private static final String INSERIR_CATEGORIA_BOLO = "INSERT INTO " + TABLE_CATEGORIAS + "(" +
            COLUNA_categorianome + ", " + COLUNA_categoriatipo+ ")" +
            "VALUES ('Bolo', 'Sobremesa');";
    private static final String INSERIR_CATEGORIA_TORTA = "INSERT INTO " + TABLE_CATEGORIAS + "(" +
            COLUNA_categorianome + ", " + COLUNA_categoriatipo+ ")" +
            "VALUES ('Torta', 'Sobremesa');";

    private static final String POPULATE_CATEGORIAS = INSERIR_CATEGORIA_DOCE + INSERIR_CATEGORIA_SALGADO +
            INSERIR_CATEGORIA_MASSA + INSERIR_CATEGORIA_BEBIDA + INSERIR_CATEGORIA_SOPA +
            INSERIR_CATEGORIA_BOLO + INSERIR_CATEGORIA_TORTA;

    public MyDBHandler(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
    }

    public MyDBHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            db.execSQL(TABLE_CREATE);
        }catch (Exception e) {
            Log.e("Erro ao criar tabela", e.getMessage());
        }
        try {
            db.execSQL(TABLE_CREATE_CATEGORIA);
        }catch (Exception e) {
            Log.e("Erro ao criar tabela", e.getMessage());
        }
        try {
            db.execSQL(TABLE_CREATE_RECEITAS_SALVAS);
        }catch (Exception e) {
            Log.e("Erro ao criar tabela", e.getMessage());
        }
        try {
            db.execSQL(TABLE_CREATE_LISTACOMPRAS);
        }catch (Exception e) {
            Log.e("Erro ao criar tabela", e.getMessage());
        }
        try{
            db.execSQL(POPULATE_CATEGORIAS);
        }catch (Exception e){
            Log.e("Erro categorias", e.getMessage());
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECEITAS);
        onCreate(db);
    }

    public static void closeDatabase(SQLiteDatabase db){
        db.close();
    }

    public static boolean isOpen(SQLiteDatabase db){
        return db.isOpen();
    }
/*
    public void addProduct(Receita recipe) {

        ContentValues values = new ContentValues();
        values.put(COLUMN_RECIPENAME, recipe.getName());
        values.put(COLUMN_RECIPEDESCRIPTION, recipe.getDescricao());
        values.put(COLUMN_RECIPEPHOTOID, recipe.getPhotoId());
        values.put(COLUMN_RECIPEINGREDIENTS, recipe.getIngredientes().toString());
        values.put(COLUMN_RECIPESTEPS, recipe.getPassos().toString());

        SQLiteDatabase db = this.getWritableDatabase();

        db.insert(TABLE_RECIPES, null, values);
        db.close();
    }

    public Receita findRecipe(String recipename) {
        String query = "Select * FROM " + TABLE_RECIPES + " WHERE " + COLUMN_RECIPENAME + " =  \"" + recipename + "\"";

        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery(query, null);

        Receita recipe = new Receita();

        if (cursor.moveToFirst()) {
            cursor.moveToFirst();
            recipe.set_id(Integer.parseInt(cursor.getString(0)));
            recipe.setName(cursor.getString(1));
            recipe.setDescricao(cursor.getString(2));
            recipe.setPhotoId(Integer.parseInt(cursor.getString(3)));
            recipe.setAllIngredientes(cursor.getString(4));
            recipe.setAllPassos(cursor.getString(5));
            cursor.close();
        } else {
            recipe = null;
        }
        db.close();
        return recipe;
    }

    public boolean deleteRecipe(String recipename) {

        boolean result = false;

        String query = "Select * FROM " + TABLE_RECIPES + " WHERE " + COLUMN_RECIPENAME + " =  \"" + recipename + "\"";

        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery(query, null);

        Receita recipe = new Receita();

        if (cursor.moveToFirst()) {
            recipe.set_id(Integer.parseInt(cursor.getString(0)));
            db.delete(TABLE_RECIPES, COLUMN_ID + " = ?",
                    new String[] { String.valueOf(recipe.get_id()) });
            cursor.close();
            result = true;
        }
        db.close();
        return result;
    } */
}

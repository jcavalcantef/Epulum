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

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "recipesDB.db";
    public static final String TABLE_RECEITAS = "Receitas";

    public static final String COLUNA_id="id";
    public static final String COLUNA_idcategoria="idcategoria";
    public static final String COLUNA_idusuario="idusuario";
    public static final String COLUNA_nome="nome";
    public static final String COLUNA_tempopreparo="tempopreparo";
    public static final String COLUNA_descricao="descricao";
    public static final String COLUNA_foto="foto";
    public static final String COLUNA_ingredientes="ingredientes";
    public static final String COLUNA_passos="passos";
    public static final String COLUNA_photoid = "photoid";
    public static final String TABLE_CREATE = "CREATE TABLE receitas " +
            "( "+COLUNA_id+" INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUNA_idcategoria+" INT, " +
            COLUNA_idusuario+" INT, " +
            COLUNA_nome+" TEXT, " +
            COLUNA_tempopreparo+" TEXT, " +
            COLUNA_descricao+" TEXT, " +
            COLUNA_foto+" TEXT, " +
            COLUNA_ingredientes+" TEXT, " +
            COLUNA_passos+" TEXT," +
            COLUNA_photoid + " INT );";

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
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECEITAS);
        onCreate(db);
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

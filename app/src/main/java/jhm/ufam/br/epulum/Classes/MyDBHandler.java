package jhm.ufam.br.epulum.Classes;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by hendrio on 12/07/17.
 */

public class MyDBHandler extends SQLiteOpenHelper{

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "recipesDB.db";
    private static final String TABLE_RECIPES = "recipes";

    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_RECIPENAME = "recipename";
    public static final String COLUMN_RECIPEDESCRIPTION = "recipedescription";
    public static final String COLUMN_RECIPEPHOTOID = "recipephotoid";
    public static final String COLUMN_RECIPEINGREDIENTS = "recipeingredients";
    public static final String COLUMN_RECIPESTEPS = "recipesteps";


    public MyDBHandler(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_PRODUCTS_TABLE = "CREATE TABLE " +
                TABLE_RECIPES + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY," +
                COLUMN_RECIPENAME + " TEXT," +
                COLUMN_RECIPEDESCRIPTION + " TEXT" +
                COLUMN_RECIPEPHOTOID + " INTEGER" +
                COLUMN_RECIPEINGREDIENTS + "TEXT" +
                COLUMN_RECIPESTEPS + " TEXT" + ")";
        db.execSQL(CREATE_PRODUCTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECIPES);
        onCreate(db);
    }

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
    }
}

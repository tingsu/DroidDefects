/*
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package caldwell.ben.bites;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import java.util.HashMap;
import caldwell.ben.bites.RecipeBook.Ingredients;
import caldwell.ben.bites.RecipeBook.Methods;
import caldwell.ben.bites.RecipeBook.Recipes;

/**
 * Provides access to a database of notes. Each note has a title, the note
 * itself, a creation date and a modified data.
 */
public class RecipeBookProvider extends ContentProvider {

    private static final String TAG = "RecipeBookProvider";

    private static final String DATABASE_NAME = "recipe_book.db";
    private static final int DATABASE_VERSION = 3;
    private static final String RECIPE_TABLE_NAME = "recipes";
    private static final String INGREDIENT_TABLE_NAME = "ingredients";
    private static final String METHOD_TABLE_NAME = "methods";

    private static HashMap<String, String> sRecipesProjectionMap;

    private static final int RECIPES = 1;
    private static final int RECIPE_ID = 2;
    private static final int INGREDIENTS = 3;
    private static final int INGREDIENT_ID = 4;
    private static final int METHODS = 5;
    private static final int METHOD_ID = 6;
    
    private static final UriMatcher sUriMatcher;

    /**
     * This class helps open, create, and upgrade the database file.
     */
    private static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE " + RECIPE_TABLE_NAME + " ("
                    + Recipes._ID + " INTEGER PRIMARY KEY,"
                    + Recipes.TITLE + " TEXT,"
                    + Recipes.AUTHOR + " TEXT,"
                    + Recipes.DESCRIPTION + " TEXT,"
                    + Recipes.CREATED_DATE + " INTEGER,"
                    + Recipes.MODIFIED_DATE + " INTEGER"
                    + ");");
            db.execSQL("CREATE TABLE " + INGREDIENT_TABLE_NAME + " ("
                    + Ingredients._ID + " INTEGER PRIMARY KEY,"
                    + Ingredients.RECIPE + " INTEGER,"
                    + Ingredients.ORDINAL + " INTEGER,"
                    + Ingredients.TEXT + " TEXT,"
                    + Ingredients.STATUS + " INTEGER,"
                    + Ingredients.CREATED_DATE + " INTEGER,"
                    + Ingredients.MODIFIED_DATE + " INTEGER"
                    + ");");
            db.execSQL("CREATE TABLE " + METHOD_TABLE_NAME + " ("
                    + Methods._ID + " INTEGER PRIMARY KEY,"
                    + Methods.RECIPE + " INTEGER,"
                    + Methods.STEP + " INTEGER,"
                    + Methods.TEXT + " TEXT,"
                    + Methods.CREATED_DATE + " INTEGER,"
                    + Methods.MODIFIED_DATE + " INTEGER"
                    + ");");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            if (oldVersion < 3) {
            	//From version 2 to version 3, add authors column to the recipes table
            	db.execSQL("ALTER TABLE " + RECIPE_TABLE_NAME 
            				+ " ADD " + Recipes.AUTHOR + " TEXT;");
            	//From version 2 to version 3, add description column to the recipes table
            	db.execSQL("ALTER TABLE " + RECIPE_TABLE_NAME 
            				+ " ADD " + Recipes.DESCRIPTION + " TEXT;");
            	//From version 2 to version 3, add ordinal column to the ingredients table
            	db.execSQL("ALTER TABLE " + INGREDIENT_TABLE_NAME 
            				+ " ADD " + Ingredients.ORDINAL + " INTEGER;");
            }           
        }
    }

    private DatabaseHelper mOpenHelper;

    @Override
    public boolean onCreate() {
        mOpenHelper = new DatabaseHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
            String sortOrder) {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        String orderBy;
        
        switch (sUriMatcher.match(uri)) {
        case RECIPES:
            qb.setTables(RECIPE_TABLE_NAME);
            qb.setProjectionMap(sRecipesProjectionMap);
         // If no sort order is specified use the default
            if (TextUtils.isEmpty(sortOrder)) {
                orderBy = RecipeBook.Recipes.DEFAULT_SORT_ORDER;
            } else {
                orderBy = sortOrder;
            }
            break;

        case RECIPE_ID:
            qb.setTables(RECIPE_TABLE_NAME);
            qb.setProjectionMap(sRecipesProjectionMap);
            qb.appendWhere(Recipes._ID + "=" + uri.getPathSegments().get(1));
         // If no sort order is specified use the default            
            if (TextUtils.isEmpty(sortOrder)) {
                orderBy = RecipeBook.Recipes.DEFAULT_SORT_ORDER;
            } else {
                orderBy = sortOrder;
            }
            break;

        case INGREDIENTS:
            qb.setTables(INGREDIENT_TABLE_NAME);
            qb.setProjectionMap(sRecipesProjectionMap);
         // If no sort order is specified use the default
            if (TextUtils.isEmpty(sortOrder)) {
                orderBy = RecipeBook.Ingredients.DEFAULT_SORT_ORDER;
            } else {
                orderBy = sortOrder;
            }
            break;

        case INGREDIENT_ID:
            qb.setTables(INGREDIENT_TABLE_NAME);
            qb.setProjectionMap(sRecipesProjectionMap);
            qb.appendWhere(Ingredients._ID + "=" + uri.getPathSegments().get(1));
         // If no sort order is specified use the default
            if (TextUtils.isEmpty(sortOrder)) {
                orderBy = RecipeBook.Ingredients.DEFAULT_SORT_ORDER;
            } else {
                orderBy = sortOrder;
            }
            break;
            
        case METHODS:
            qb.setTables(METHOD_TABLE_NAME);
            qb.setProjectionMap(sRecipesProjectionMap);
         // If no sort order is specified use the default
            if (TextUtils.isEmpty(sortOrder)) {
                orderBy = RecipeBook.Methods.DEFAULT_SORT_ORDER;
            } else {
                orderBy = sortOrder;
            }
            break;

        case METHOD_ID:
            qb.setTables(METHOD_TABLE_NAME);
            qb.setProjectionMap(sRecipesProjectionMap);
            qb.appendWhere(Methods._ID + "=" + uri.getPathSegments().get(1));
         // If no sort order is specified use the default
            if (TextUtils.isEmpty(sortOrder)) {
                orderBy = RecipeBook.Methods.DEFAULT_SORT_ORDER;
            } else {
                orderBy = sortOrder;
            }
            break;
           
        default:
            throw new IllegalArgumentException("Unknown URI " + uri);
        }

        // Get the database and run the query
        SQLiteDatabase db = mOpenHelper.getReadableDatabase();
        Cursor c = qb.query(db, projection, selection, selectionArgs, null, null, orderBy);

        // Tell the cursor what uri to watch, so it knows when its source data changes
        c.setNotificationUri(getContext().getContentResolver(), uri);
        return c;
    }

    @Override
    public String getType(Uri uri) {
        switch (sUriMatcher.match(uri)) {
        case RECIPES:
            return Recipes.CONTENT_TYPE;

        case RECIPE_ID:
            return Recipes.CONTENT_ITEM_TYPE;

        case INGREDIENTS:
            return Ingredients.CONTENT_TYPE;

        case INGREDIENT_ID:
            return Ingredients.CONTENT_ITEM_TYPE;

        case METHODS:
            return Methods.CONTENT_TYPE;

        case METHOD_ID:
            return Methods.CONTENT_ITEM_TYPE;

        default:
            throw new IllegalArgumentException("Unknown URI " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues initialValues) {

        ContentValues values;
        if (initialValues != null) {
            values = new ContentValues(initialValues);
        } else {
            values = new ContentValues();
        }
        Long now = Long.valueOf(System.currentTimeMillis());
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        long rowId = 0;
        
        switch (sUriMatcher.match(uri)) {
        	case RECIPES:
        		// Make sure that the fields are all set
                if (values.containsKey(RecipeBook.Recipes.CREATED_DATE) == false) {
                    values.put(RecipeBook.Recipes.CREATED_DATE, now);
                }

                if (values.containsKey(RecipeBook.Recipes.MODIFIED_DATE) == false) {
                    values.put(RecipeBook.Recipes.MODIFIED_DATE, now);
                }

                if (values.containsKey(RecipeBook.Recipes.TITLE) == false) {
                    Resources r = Resources.getSystem();
                    values.put(RecipeBook.Recipes.TITLE, r.getString(android.R.string.untitled));
                }

                rowId = db.insert(RECIPE_TABLE_NAME, Recipes.TITLE, values);
                if (rowId > 0) {
                    Uri noteUri = ContentUris.withAppendedId(RecipeBook.Recipes.CONTENT_URI, rowId);
                    getContext().getContentResolver().notifyChange(noteUri, null);
                    return noteUri;
                }

                throw new SQLException("Failed to insert row into " + uri);
          
        	case INGREDIENTS:
        		// Make sure that the fields are all set
                if (values.containsKey(RecipeBook.Ingredients.CREATED_DATE) == false) {
                    values.put(RecipeBook.Ingredients.CREATED_DATE, now);
                }

                if (values.containsKey(RecipeBook.Ingredients.MODIFIED_DATE) == false) {
                    values.put(RecipeBook.Ingredients.MODIFIED_DATE, now);
                }
                
                //If there is no ordinal value then increment the highest existing
                if (values.containsKey(RecipeBook.Ingredients.ORDINAL) == false) {
                    int ordinal = 0;
                    Cursor c = db.query(INGREDIENT_TABLE_NAME, new String[]{"max("+Ingredients.ORDINAL+")"}, 
                    		Ingredients.RECIPE + "=" + values.get(Ingredients.RECIPE), null, null, null, null);
                    if(c.moveToFirst()) {
                    	ordinal = c.getInt(0)+1;
                    }
                    values.put(RecipeBook.Ingredients.ORDINAL, ordinal);
                }

                if (values.containsKey(RecipeBook.Ingredients.TEXT) == false) {
                    Resources r = Resources.getSystem();
                    values.put(RecipeBook.Ingredients.TEXT, r.getString(android.R.string.untitled));
                }
                
                if (values.containsKey(RecipeBook.Ingredients.STATUS) == false) {
                    values.put(RecipeBook.Ingredients.STATUS, Ingredients.STATUS_UNCHECKED);
                }
                
                //Don't allow inserting ingredient without a parent recipe
                if (values.containsKey(RecipeBook.Ingredients.RECIPE) == false) {
                	throw new SQLException("Failed to insert row into " + uri);
                }

                rowId = db.insert(INGREDIENT_TABLE_NAME, Ingredients.RECIPE, values);
                if (rowId > 0) {
                    Uri noteUri = ContentUris.withAppendedId(RecipeBook.Ingredients.CONTENT_URI, rowId);
                    getContext().getContentResolver().notifyChange(noteUri, null);
                    return noteUri;
                }

                throw new SQLException("Failed to insert row into " + uri);
          
        	case METHODS:
        		// Make sure that the fields are all set
                if (values.containsKey(RecipeBook.Methods.CREATED_DATE) == false) {
                    values.put(RecipeBook.Methods.CREATED_DATE, now);
                }

                if (values.containsKey(RecipeBook.Methods.MODIFIED_DATE) == false) {
                    values.put(RecipeBook.Methods.MODIFIED_DATE, now);
                }
               
                if (values.containsKey(RecipeBook.Methods.TEXT) == false) {
                    Resources r = Resources.getSystem();
                    values.put(RecipeBook.Methods.TEXT, r.getString(android.R.string.untitled));
                }
                
                if (values.containsKey(RecipeBook.Methods.STEP) == false) {
                	values.put(RecipeBook.Methods.STEP, 0);
                }

                //Don't allow methods without parent recipes
                if (values.containsKey(RecipeBook.Methods.RECIPE) == false) {
                    throw new SQLException("Failed to insert row into " + uri);
                }

                rowId = db.insert(METHOD_TABLE_NAME, Methods.RECIPE, values);
                if (rowId > 0) {
                    Uri noteUri = ContentUris.withAppendedId(RecipeBook.Methods.CONTENT_URI, rowId);
                    getContext().getContentResolver().notifyChange(noteUri, null);
                    return noteUri;
                }

                throw new SQLException("Failed to insert row into " + uri);
            
        	default:
        		throw new IllegalArgumentException("Unknown URI " + uri);

        }
        
    }

    @Override
    public int delete(Uri uri, String where, String[] whereArgs) {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int count;
        switch (sUriMatcher.match(uri)) {
        case RECIPES:
            count = db.delete(RECIPE_TABLE_NAME, where, whereArgs);
            /**
             * Delete all ingredients and methods without a parent recipe in the recipe table
             * This is a continuous cleanup strategy to stop cluttering with orphans
             */
            db.execSQL(getContext().getString(R.string.sql_delete_orphan_ingredients));
            db.execSQL(getContext().getString(R.string.sql_delete_orphan_methods));
            break;

        case RECIPE_ID:
            String recipeId = uri.getPathSegments().get(1);
            count = db.delete(RECIPE_TABLE_NAME, Recipes._ID + "=" + recipeId
                    + (!TextUtils.isEmpty(where) ? " AND (" + where + ')' : ""), whereArgs);
            /**
             * Delete all ingredients and methods without a parent recipe in the recipe table
             * This is a continuous cleanup strategy to stop cluttering with orphans
             */
            db.execSQL(getContext().getString(R.string.sql_delete_orphan_ingredients));
            db.execSQL(getContext().getString(R.string.sql_delete_orphan_methods));
            break;

        case INGREDIENTS:
            count = db.delete(INGREDIENT_TABLE_NAME, where, whereArgs);
            break;

        case INGREDIENT_ID:
            String ingredientId = uri.getPathSegments().get(1);
            count = db.delete(INGREDIENT_TABLE_NAME, Ingredients._ID + "=" + ingredientId
                    + (!TextUtils.isEmpty(where) ? " AND (" + where + ')' : ""), whereArgs);
            break;

        case METHODS:
            count = db.delete(METHOD_TABLE_NAME, where, whereArgs);
            break;

        case METHOD_ID:
            String methodId = uri.getPathSegments().get(1);
            count = db.delete(METHOD_TABLE_NAME, Methods._ID + "=" + methodId
                    + (!TextUtils.isEmpty(where) ? " AND (" + where + ')' : ""), whereArgs);
            break;

        default:
            throw new IllegalArgumentException("Unknown URI " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public int update(Uri uri, ContentValues initialValues, String where, String[] whereArgs) {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int count;
        
        ContentValues values;
        if (initialValues != null) {
            values = new ContentValues(initialValues);
        } else {
            values = new ContentValues();
        }
        Long now = Long.valueOf(System.currentTimeMillis());
                
        switch (sUriMatcher.match(uri)) {
        case RECIPES:
        	if (values.containsKey(RecipeBook.Recipes.MODIFIED_DATE) == false) {
                values.put(RecipeBook.Recipes.MODIFIED_DATE, now);
            }
        	count = db.update(RECIPE_TABLE_NAME, values, where, whereArgs);
            break;

        case RECIPE_ID:
            String noteId = uri.getPathSegments().get(1);
            if (values.containsKey(RecipeBook.Recipes.MODIFIED_DATE) == false) {
                values.put(RecipeBook.Recipes.MODIFIED_DATE, now);
            }
            count = db.update(RECIPE_TABLE_NAME, values, Recipes._ID + "=" + noteId
                    + (!TextUtils.isEmpty(where) ? " AND (" + where + ')' : ""), whereArgs);
            break;

        case INGREDIENTS:
        	if (values.containsKey(RecipeBook.Ingredients.MODIFIED_DATE) == false) {
                values.put(RecipeBook.Ingredients.MODIFIED_DATE, now);
            }
        	count = db.update(INGREDIENT_TABLE_NAME, values, where, whereArgs);
            break;

        case INGREDIENT_ID:
            String ingredientId = uri.getPathSegments().get(1);
            if (values.containsKey(RecipeBook.Ingredients.MODIFIED_DATE) == false) {
                values.put(RecipeBook.Ingredients.MODIFIED_DATE, now);
            }
            count = db.update(INGREDIENT_TABLE_NAME, values, Ingredients._ID + "=" + ingredientId
                    + (!TextUtils.isEmpty(where) ? " AND (" + where + ')' : ""), whereArgs);
            break;

        case METHODS:
        	if (values.containsKey(RecipeBook.Methods.MODIFIED_DATE) == false) {
                values.put(RecipeBook.Methods.MODIFIED_DATE, now);
            }
        	count = db.update(METHOD_TABLE_NAME, values, where, whereArgs);
            break;

        case METHOD_ID:
            String methodId = uri.getPathSegments().get(1);
            if (values.containsKey(RecipeBook.Methods.MODIFIED_DATE) == false) {
                values.put(RecipeBook.Methods.MODIFIED_DATE, now);
            }
            count = db.update(METHOD_TABLE_NAME, values, Methods._ID + "=" + methodId
                    + (!TextUtils.isEmpty(where) ? " AND (" + where + ')' : ""), whereArgs);
            break;

        default:
            throw new IllegalArgumentException("Unknown URI " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    static {
        sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        sUriMatcher.addURI(RecipeBook.AUTHORITY, "recipes/", RECIPES);
        sUriMatcher.addURI(RecipeBook.AUTHORITY, "recipes/#", RECIPE_ID);
        sUriMatcher.addURI(RecipeBook.AUTHORITY, "ingredients/", INGREDIENTS);
        sUriMatcher.addURI(RecipeBook.AUTHORITY, "ingredients/#", INGREDIENT_ID);
        sUriMatcher.addURI(RecipeBook.AUTHORITY, "methods/", METHODS);
        sUriMatcher.addURI(RecipeBook.AUTHORITY, "methods/#", METHOD_ID);
        
        sRecipesProjectionMap = new HashMap<String, String>();
        sRecipesProjectionMap.put(Recipes._ID, Recipes._ID);
        sRecipesProjectionMap.put(Recipes.TITLE, Recipes.TITLE);
        sRecipesProjectionMap.put(Recipes.AUTHOR, Recipes.AUTHOR);
        sRecipesProjectionMap.put(Recipes.DESCRIPTION, Recipes.DESCRIPTION);
        sRecipesProjectionMap.put(Recipes.CREATED_DATE, Recipes.CREATED_DATE);
        sRecipesProjectionMap.put(Recipes.MODIFIED_DATE, Recipes.MODIFIED_DATE);
        sRecipesProjectionMap.put(Ingredients._ID, Ingredients._ID);
        sRecipesProjectionMap.put(Ingredients.RECIPE, Ingredients.RECIPE);
        sRecipesProjectionMap.put(Ingredients.ORDINAL, Ingredients.ORDINAL);
        sRecipesProjectionMap.put(Ingredients.TEXT, Ingredients.TEXT);
        sRecipesProjectionMap.put(Ingredients.STATUS, Ingredients.STATUS);
        sRecipesProjectionMap.put(Ingredients.CREATED_DATE, Ingredients.CREATED_DATE);
        sRecipesProjectionMap.put(Ingredients.MODIFIED_DATE, Ingredients.MODIFIED_DATE);
        sRecipesProjectionMap.put(Methods._ID, Methods._ID);
        sRecipesProjectionMap.put(Methods.RECIPE, Methods.RECIPE);
        sRecipesProjectionMap.put(Methods.STEP, Methods.STEP);
        sRecipesProjectionMap.put(Methods.TEXT, Methods.TEXT);
        sRecipesProjectionMap.put(Methods.CREATED_DATE, Methods.CREATED_DATE);
        sRecipesProjectionMap.put(Methods.MODIFIED_DATE, Methods.MODIFIED_DATE);
    }
}

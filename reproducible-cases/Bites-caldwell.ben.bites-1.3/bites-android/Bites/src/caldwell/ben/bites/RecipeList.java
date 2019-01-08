package caldwell.ben.bites;

/*
 * Send Text message using built in sms message app
 * 
 *  Intent sendIntent = new Intent(Intent.ACTION_VIEW);
 *  sendIntent.putExtra("sms_body", "Content of the SMS goes here...");
 *  sendIntent.setType("vnd.android-dir/mms-sms");
 *  startActivity(sendIntent);
 *  
 *  from http://mobiforge.com/developing/story/sms-messaging-android
*/
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import org.xmlpull.v1.XmlSerializer;
import caldwell.ben.bites.RecipeBook.Ingredients;
import caldwell.ben.bites.RecipeBook.Methods;
import caldwell.ben.bites.RecipeBook.Recipes;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.app.TabActivity;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.util.Xml;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Filterable;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class RecipeList extends ListActivity {
	
	private static final String TAG = "RecipeList";
	// Menu item ids
	public static final int MENU_ITEM_EDIT = Menu.FIRST;
	public static final int MENU_ITEM_DELETE = Menu.FIRST + 1;
    public static final int MENU_ITEM_INSERT = Menu.FIRST + 2;
    public static final int MENU_ITEM_SEND_SMS = Menu.FIRST + 3;
    public static final int MENU_ITEM_PREFERENCES = Menu.FIRST + 4;
    public static final int MENU_ITEM_SEND_EMAIL = Menu.FIRST + 5;
    
    /**
     * Case selections for the type of dialog box displayed
     */
    private static final int DIALOG_EDIT = 1;
    private static final int DIALOG_DELETE = 2;
    private static final int DIALOG_INSERT = 3;
    
	/**
     * The columns we are interested in from the database
     */
    private static final String[] PROJECTION = new String[] {
            Recipes._ID, // 0
            Recipes.TITLE, // 1
            Recipes.AUTHOR, // 2
            Recipes.DESCRIPTION, // 3
    };
    	
    private Cursor mCursor;
    private SharedPreferences mPrefs;
    private Uri mUri;
    
    //Use private members for dialog textview to prevent weird persistence problem
	private EditText mDialogEdit;
	private View mDialogView;
	private TextView mDialogText;
    private TextView mHeader;
	private EditText mDialogAuthor;
	private EditText mDialogDesc;
    
    /**
     * Custom adapter for recipe list.
     * Allows filtering by recipe name.
     * @author Ben
     *
     */
    private class RecipeAdapter extends SimpleCursorAdapter implements Filterable
    {
    	private ContentResolver mContent;   
    	
		public RecipeAdapter(Context context, int layout, Cursor c,
				String[] from, int[] to) {
			super(context, layout, c, from, to);
			mContent = context.getContentResolver();
		}

		@Override
        public Cursor runQueryOnBackgroundThread(CharSequence constraint) {
            if (getFilterQueryProvider() != null) {
                return getFilterQueryProvider().runQuery(constraint);
            }

            StringBuilder buffer = null;
            String[] args = null;
            if (constraint != null) {
                buffer = new StringBuilder();
                buffer.append("UPPER(");
                buffer.append(Recipes.TITLE);
                buffer.append(") GLOB ?");
                args = new String[] { constraint.toString().toUpperCase() + "*" };
            }

            return mContent.query(Recipes.CONTENT_URI, PROJECTION,
                    buffer == null ? null : buffer.toString(), args,
                    null);
        }
    }
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Intent intent = getIntent();
		if (intent.getData() == null) {
            intent.setData(Recipes.CONTENT_URI);
        }
        
        setContentView(R.layout.recipes);
        mHeader = (TextView)findViewById(R.id.recipeheader);
        getListView().setOnCreateContextMenuListener(this);
        
        mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
	}
	
	
	@Override
	protected void onResume() {
		super.onResume();
		
		String sortOrder = mPrefs.getString(getString(R.string.key_recipe_sort), Recipes.DEFAULT_SORT_ORDER);
		try {
			mCursor = managedQuery(Recipes.CONTENT_URI, PROJECTION, null, null,
	                sortOrder);
		} catch (SQLiteException e) {
			//Try a safer query
			mCursor = managedQuery(Recipes.CONTENT_URI, PROJECTION, null, null,
	                Recipes.DEFAULT_SORT_ORDER);
		}

        // Used to map recipe entries from the database to views
        RecipeAdapter adapter = new RecipeAdapter(this, R.layout.recipelist_item, mCursor,
                new String[] { Recipes.TITLE, Recipes.AUTHOR, Recipes.DESCRIPTION }, 
                new int[] { R.id.recipetitle, R.id.author, R.id.description});
        setListAdapter(adapter);
       	mCursor.moveToFirst();
       	if (!mCursor.isBeforeFirst()) {
       		if (Bites.mRecipeId == 0) {
	       		Bites.mRecipeId = mCursor.getLong(mCursor.getColumnIndex(Recipes._ID));
		        Bites.mRecipeName = mCursor.getString(mCursor.getColumnIndex(Recipes.TITLE));
       		}
        } else {
        	Bites.mRecipeId = 0;
        	Bites.mRecipeName = "";             	
        }
        mHeader.setText(Bites.mRecipeName);
	}



	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		
		// Insert a new recipe into the list
        menu.add(0, MENU_ITEM_INSERT, 0, "insert")
                .setShortcut('3', 'a')
                .setIcon(android.R.drawable.ic_menu_add);
        
     // Generate any additional actions that can be performed on the
        // overall list.  In a normal install, there are no additional
        // actions found here, but this allows other applications to extend
        // our menu with their own actions.
        Intent intent = new Intent(null, getIntent().getData());
        intent.addCategory(Intent.CATEGORY_ALTERNATIVE);
        menu.addIntentOptions(Menu.CATEGORY_ALTERNATIVE, 0, 0,
                new ComponentName(this, RecipeList.class), null, intent, 0, null);
       
        menu.add (0, MENU_ITEM_PREFERENCES, 0, R.string.preferences)
        .setIcon(android.R.drawable.ic_menu_preferences);
        
        return true;
	}

	
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case MENU_ITEM_INSERT:
            // Insert a new item
        	showDialog(DIALOG_INSERT);
        	mDialogEdit.setText("");
        	mDialogAuthor.setText("");
        	return true;
	    case MENU_ITEM_PREFERENCES:
        	startActivity(new Intent(this,BitesPreferences.class));
        	return true;
	    }
        return super.onOptionsItemSelected(item);
    }

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		AdapterView.AdapterContextMenuInfo info;
        try {
             info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        } catch (ClassCastException e) {
            Log.e(TAG, "bad menuInfo", e);
            return;
        }
		Cursor cursor = (Cursor)getListAdapter().getItem(info.position);
		if (cursor == null) {
            // For some reason the requested item isn't available, do nothing
            return;
        }
        // Setup the menu header
        menu.setHeaderTitle(cursor.getString(cursor.getColumnIndex(Recipes.TITLE)));
        // Add a menu item to delete the note
        menu.add(0, MENU_ITEM_EDIT, 0, R.string.edit_recipe);
        menu.add(0, MENU_ITEM_DELETE, 0, R.string.delete_recipe);
        menu.add(0, MENU_ITEM_SEND_SMS, 0, R.string.send_recipe_sms);
        menu.add(0, MENU_ITEM_SEND_EMAIL, 0, R.string.send_recipe_email);
	}
	
	

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterView.AdapterContextMenuInfo info;
        try {
             info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        } catch (ClassCastException e) {
            Log.e(TAG, "bad menuInfo", e);
            return false;
        }
        
        Cursor cursor = (Cursor) getListAdapter().getItem(info.position);
        if (cursor == null) {
            // For some reason the requested item isn't available, do nothing
            return false;
        }
        
        Bites.mRecipeId = mCursor.getLong(cursor.getColumnIndex(Recipes._ID));
        Bites.mRecipeName = mCursor.getString(cursor.getColumnIndex(Recipes.TITLE));
        mUri = ContentUris.withAppendedId(getIntent().getData(), Bites.mRecipeId);

        switch (item.getItemId()) {
	        case MENU_ITEM_EDIT: {
                // Edit the ingredient that the context menu is for
	        	showDialog(DIALOG_EDIT);
				mDialogEdit.setText(cursor.getString(cursor.getColumnIndex(Recipes.TITLE)));
				mDialogAuthor.setText(cursor.getString(cursor.getColumnIndex(Recipes.AUTHOR)));
				mDialogDesc.setText(cursor.getString(cursor.getColumnIndex(Recipes.DESCRIPTION)));
                return true;	        	
	        }    
	        case MENU_ITEM_DELETE: {
                // Delete the note that the context menu is for
	        	showDialog(DIALOG_DELETE);
				mDialogText.setText(cursor.getString(cursor.getColumnIndex(Recipes.TITLE)));
                return true;
            }
	        
	        case MENU_ITEM_SEND_SMS: {
	        	//Send the recipe via sms
	        	sendSMSRecipe();
	        	return true;
	        }
	        
	        case MENU_ITEM_SEND_EMAIL: {
	        	//Send the recipe via email attachment
	        	sendEmailRecipe();
	        	return true;
	        }
        }
        return false;
	}
	
	private void sendEmailRecipe() {
		//Get a recipe cursor
		Cursor cRecipe = getContentResolver().query(Uri.withAppendedPath(Recipes.CONTENT_URI, Long.toString(Bites.mRecipeId)), 
													PROJECTION, 
													null, 
													null, 
													null);
		//Get an ingredient cursor
    	Cursor cIngredient = getContentResolver().query(Ingredients.CONTENT_URI, 
    											new String[] {Ingredients._ID, Ingredients.TEXT}, 
    											Ingredients.RECIPE + "=" + Bites.mRecipeId , 
    											null, 
    											null);
    	Cursor cMethod = getContentResolver().query(Methods.CONTENT_URI, 
				new String[] {Methods._ID, Methods.STEP, Methods.TEXT}, 
				Methods.RECIPE + "=" + Bites.mRecipeId , 
				null, 
				null);
		
		//Create the xml string
		XmlSerializer serializer = Xml.newSerializer();
		StringWriter swriter = new StringWriter();
		try {
			serializer.setOutput(swriter);
			serializer.startDocument("UTF-8", true);
			serializer.startTag("", "recipe");
			serializer.attribute("", "name", Bites.mRecipeName);
			cRecipe.moveToFirst();
			serializer.attribute("", "author", cRecipe.getString(cRecipe.getColumnIndex(Recipes.AUTHOR)));
			serializer.startTag("", "description");
			serializer.text(cRecipe.getString(cRecipe.getColumnIndex(Recipes.DESCRIPTION)));
			serializer.endTag("", "description");
			//add ingredients to xml file
	    	cIngredient.moveToFirst();
	    	while (!cIngredient.isLast() && !cIngredient.isNull(0) )
	    	{
	    		serializer.startTag("", "ingredient");
	    		serializer.text(cIngredient.getString(cIngredient.getColumnIndex(Ingredients.TEXT)));
	    		serializer.endTag("", "ingredient");
	    		cIngredient.moveToNext();
	    	}
	    	//add methods to xml file
	    	cMethod.moveToFirst();
	    	while (!cMethod.isLast())
	    	{
	    		serializer.startTag("", "method");
	    		serializer.attribute("", "step", cMethod.getString(cMethod.getColumnIndex(Methods.STEP)));
	    		serializer.text(cMethod.getString(cMethod.getColumnIndex(Methods.TEXT)));
	    		serializer.endTag("", "method");
	    		cMethod.moveToNext();
	    	}
			serializer.endTag("", "recipe");
			serializer.endDocument();
		} catch (Exception e) {
			//Error creating the xml file, show toast and return
			Toast.makeText(this, R.string.xml_create_error, Toast.LENGTH_LONG).show();
			return;
		}
		
		//Store the xml recipe file on the SD card 
		File root = Environment.getExternalStorageDirectory();
		File file = new File(root, Bites.mRecipeName.replace(" ", "_") + ".xml");
		try {
			file.createNewFile();
			FileWriter writer = new FileWriter(file);
			BufferedWriter out = new BufferedWriter(writer);
			out.write(swriter.toString());
			//TODO: write the lines of text to the file
			//close the file writer
			out.close();
		} catch (IOException e) {
			//Error writing the file to the SD card, show toast and abort
			Toast.makeText(this, R.string.sd_write_error, Toast.LENGTH_LONG).show();
			//TODO: delete the file if it was created?
			return;
		}
				
		Intent sendIntent = new Intent(Intent.ACTION_SEND);
		sendIntent.putExtra(Intent.EXTRA_SUBJECT, Bites.mRecipeName); 
		/*sendIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse 
		("file://"+Environment.getExternalStorageDirectory()+"/data.csv"));*/
		sendIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse 
		("file://"+file.getAbsolutePath()));
		sendIntent.setType("text/plain");
		startActivity(sendIntent);
	}

	private void sendSMSRecipe(){
		Intent sendIntent = new Intent(Intent.ACTION_VIEW);
		
		String msg;
		//Get an ingredient cursor
    	Cursor cIngredient = getContentResolver().query(Ingredients.CONTENT_URI, 
    											new String[] {Ingredients._ID, Ingredients.TEXT}, 
    											Ingredients.RECIPE + "=" + Bites.mRecipeId , 
    											null, 
    											null);
    	Cursor cMethod = getContentResolver().query(Methods.CONTENT_URI, 
				new String[] {Methods._ID, Methods.STEP, Methods.TEXT}, 
				Methods.RECIPE + "=" + Bites.mRecipeId , 
				null, 
				null);
    	
    	msg =  "***Bites Recipe***\n";
    	msg = msg + Bites.mRecipeName + "\n";
    	msg = msg + "**Ingredients**\n";
    	//Get ingredients
    	int colIng = cIngredient.getColumnIndex(Ingredients.TEXT);
    	cIngredient.moveToFirst();
    	while (!cIngredient.isLast() && !cIngredient.isNull(0) )
    	{
    		
    		msg = msg + cIngredient.getString(colIng) + "\n";
    		cIngredient.moveToNext();
    	}
    	msg = msg + "**Method**\n";
    	//Get methods
    	int colStep = cMethod.getColumnIndex(Methods.STEP);
    	int colMethod = cMethod.getColumnIndex(Methods.TEXT);
    	cMethod.moveToFirst();
    	while (!cMethod.isLast())
    	{
    		msg = msg + cMethod.getString(colStep) + "." + cMethod.getString(colMethod) + "\n";
    		cMethod.moveToNext();
    	}
    	msg = msg + "***";
    	sendIntent.putExtra("sms_body", msg);
    	sendIntent.setType("vnd.android-dir/mms-sms");
    	startActivity(sendIntent);
	}


	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		selectItem(id);
		//Get the parent tabactivity and set the curren tab to the ingredients tab
		((TabActivity)getParent()).getTabHost().setCurrentTab(1);
	}
	
	private void selectItem(long id) {
		mUri = ContentUris.withAppendedId(getIntent().getData(), id);
		Bites.mRecipeId = id;
		//Get a temp cursor to the uri of the clicked item
		Cursor c = getContentResolver().query(mUri, PROJECTION, null, null, null);
		c.moveToLast();
		Bites.mRecipeName = c.getString(c.getColumnIndex(Recipes.TITLE));
		//Update the header text with the current recipe name
		mHeader.setText(Bites.mRecipeName);
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		LayoutInflater factory = LayoutInflater.from(this);
		switch (id) {
		case DIALOG_INSERT:
			mDialogView = factory.inflate(R.layout.dialog_recipename, null);
			mDialogEdit = (EditText)mDialogView.findViewById(R.id.recipename_edit);
			mDialogAuthor = (EditText)mDialogView.findViewById(R.id.recipeauthor_edit);
			mDialogAuthor.setText("");
			mDialogDesc = (EditText)mDialogView.findViewById(R.id.recipedescription);
			mDialogDesc.setText("");
            return new AlertDialog.Builder(this)
                .setTitle(R.string.recipe_name)
                .setView(mDialogView)
                .setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
                	public void onClick(DialogInterface dialog, int whichButton) {
                    	/* User clicked OK so do some stuff */
                    	ContentValues values = new ContentValues();
                    	values.put(Recipes.TITLE, mDialogEdit.getText().toString());
                    	values.put(Recipes.AUTHOR, mDialogAuthor.getText().toString());
                    	values.put(Recipes.DESCRIPTION, mDialogDesc.getText().toString());
                    	mUri = getContentResolver().insert(Recipes.CONTENT_URI,values);
                    	selectItem(Long.parseLong(mUri.getLastPathSegment()));
                    }
                })
                .setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        /* User clicked cancel so do some stuff */
                    }
                })
                .create();
		case DIALOG_EDIT:
			mDialogView = factory.inflate(R.layout.dialog_recipename, null);
			mDialogEdit = (EditText)mDialogView.findViewById(R.id.recipename_edit);
			mDialogAuthor = (EditText)mDialogView.findViewById(R.id.recipeauthor_edit);
			mDialogDesc = (EditText)mDialogView.findViewById(R.id.recipedescription);
            return new AlertDialog.Builder(this)
                .setTitle(R.string.recipe_name)
                .setView(mDialogView)
                .setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
                	public void onClick(DialogInterface dialog, int whichButton) {
                    	/* User clicked OK so do some stuff */
                    	ContentValues values = new ContentValues();
                    	/*Bites.mRecipeName = mDialogEdit.getText().toString();
                    	mHeader.setText(Bites.mRecipeName);*/
                        values.put(Recipes.TITLE, mDialogEdit.getText().toString());
                        values.put(Recipes.AUTHOR, mDialogAuthor.getText().toString());
                        values.put(Recipes.DESCRIPTION, mDialogDesc.getText().toString());
                        getContentResolver().update(mUri, values, null, null);
                        selectItem(Long.parseLong(mUri.getLastPathSegment()));
                    }
                })
                .setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        /* User clicked cancel so do some stuff */
                    }
                })
                .create();
		case DIALOG_DELETE:
			mDialogView = factory.inflate(R.layout.dialog_confirm, null);
			mDialogText = (TextView)mDialogView.findViewById(R.id.dialog_confirm_prompt);
            return new AlertDialog.Builder(this)
                .setTitle(R.string.delete_recipe)
                .setView(mDialogView)
                .setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
                	public void onClick(DialogInterface dialog, int whichButton) {
                    	/* User clicked OK so do some stuff */
                        getContentResolver().delete(mUri, null, null);
                        //Requery cursor to update with removed row
                        mCursor.requery();
                        if (mCursor.moveToFirst()) 
                        {
                        	selectItem(mCursor.getLong(0));
                        }
                        else //empty list - clear the recipe title header
                        {
                        	Bites.mRecipeId = 0;
                        	Bites.mRecipeName = "";
                        	mHeader.setText(Bites.mRecipeName);
                        }
                    }
                })
                .setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        /* User clicked cancel so do some stuff */
                    }
                })
                .create();
		}
		return null;
	}
}

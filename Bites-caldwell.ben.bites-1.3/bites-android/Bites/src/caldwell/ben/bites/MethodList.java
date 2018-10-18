package caldwell.ben.bites;

import caldwell.ben.bites.RecipeBook.Methods;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.ComponentName;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

public class MethodList extends ListActivity {
	
	private static final String TAG = "MethodList";
	
	// Menu item ids
	public static final int MENU_ITEM_EDIT = Menu.FIRST;
	public static final int MENU_ITEM_DELETE = Menu.FIRST + 1;
    public static final int MENU_ITEM_INSERT = Menu.FIRST + 2;
	
	/**
     * The columns we are interested in from the database
     */
    private static final String[] PROJECTION = new String[] {
            Methods._ID, // 0
            Methods.RECIPE, // 1
            Methods.STEP, // 2
            Methods.TEXT, // 3
    };
    
    /**
     * Case selections for the type of dialog box displayed
     */
    private static final int DIALOG_EDIT = 1;
    private static final int DIALOG_DELETE = 2;
    private static final int DIALOG_INSERT = 3;
    
    /**
     * Column indexes
     */
    private static final int COLUMN_INDEX_ID = 0;
//    private static final int COLUMN_INDEX_RECIPE = 1;
    private static final int COLUMN_INDEX_STEP = 2;
    private static final int COLUMN_INDEX_METHOD = 3;
    
    private Uri mUri;
    
  //Use private members for dialog textview to prevent weird persistence problem
	private EditText mDialogEdit;
	private View mDialogView;
	private TextView mDialogText, mDialogStep;
	private TextView mHeader;

	private Cursor mCursor;	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
			
		Intent intent = getIntent();
        if (intent.getData() == null) {
            intent.setData(Methods.CONTENT_URI);
        }
			
		setContentView(R.layout.methods);
		
		mHeader = (TextView)findViewById(R.id.methodheader);
		
		getListView().setOnCreateContextMenuListener(this);
	}
	
	
	@Override
	protected void onResume() {
		super.onResume();
		
		/**Refresh the cursor using the selected recipe whenever the activity is resumed.
		 * A new recipe can only be selected from the recipelist activity and 
		 * this activity has to be resumed to display again so this should work fine. 
		 */
		mCursor = managedQuery(Methods.CONTENT_URI, PROJECTION,
				Methods.RECIPE + "=" + Bites.mRecipeId, 
				null, Methods.DEFAULT_SORT_ORDER);

		// Used to map notes entries from the database to views
		SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, R.layout.methodlist_item, mCursor,
			new String[] { Methods.STEP, Methods.TEXT}, 
			new int[] { R.id.methodstep, R.id.methodtext});
		setListAdapter(adapter);
		
		//Set the header text to the current recipe name
		mHeader.setText(Bites.mRecipeName);
		
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
        menu.setHeaderTitle(cursor.getString(COLUMN_INDEX_METHOD));
        // Add a menu item to delete the note
        menu.add(0, MENU_ITEM_EDIT, 0, R.string.edit_method);
        menu.add(0, MENU_ITEM_DELETE, 0, R.string.delete_method);
        
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
        
        mUri = ContentUris.withAppendedId(getIntent().getData(), cursor.getLong(COLUMN_INDEX_ID));

        switch (item.getItemId()) {
	        case MENU_ITEM_EDIT: {
                // Edit the ingredient that the context menu is for
	        	showDialog(DIALOG_EDIT);
				mDialogStep.setText(cursor.getString(COLUMN_INDEX_STEP));
				mDialogEdit.setText(cursor.getString(COLUMN_INDEX_METHOD));
                return true;	        	
	        }    
	        case MENU_ITEM_DELETE: {
                // Delete the note that the context menu is for
	        	showDialog(DIALOG_DELETE);
				mDialogText.setText(cursor.getString(COLUMN_INDEX_METHOD));
                return true;
            }
        }
        return false;
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		
		// Insert a new recipe into the list
        menu.add(0, MENU_ITEM_INSERT, 0, "insert")
                .setShortcut('3', 'a')
                .setIcon(android.R.drawable.ic_menu_add);
        /*menu.add(0, MENU_ITEM_EDIT, 0, "edit")
        .setIcon(android.R.drawable.ic_menu_edit);*/
        
     // Generate any additional actions that can be performed on the
        // overall list.  In a normal install, there are no additional
        // actions found here, but this allows other applications to extend
        // our menu with their own actions.
        Intent intent = new Intent(null, getIntent().getData());
        intent.addCategory(Intent.CATEGORY_ALTERNATIVE);
        menu.addIntentOptions(Menu.CATEGORY_ALTERNATIVE, 0, 0,
                new ComponentName(this, MethodList.class), null, intent, 0, null);     
        
        return true;
	}
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case MENU_ITEM_INSERT:
            // Insert a new item
        	showDialog(DIALOG_INSERT);
        	mDialogEdit.setText("");
        	mDialogStep.setText("");
        	break;
	    }
        return super.onOptionsItemSelected(item);
    }
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		mUri = ContentUris.withAppendedId(getIntent().getData(), id);
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		LayoutInflater factory = LayoutInflater.from(this);
		switch (id) {
		case DIALOG_EDIT:
            mDialogView = factory.inflate(R.layout.dialog_method, null);
            mDialogEdit = (EditText)mDialogView.findViewById(R.id.method_edit);
            mDialogStep = (EditText)mDialogView.findViewById(R.id.method_step);
            return new AlertDialog.Builder(this)
                .setTitle(R.string.edit_method)
                .setView(mDialogView)
                .setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
                	public void onClick(DialogInterface dialog, int whichButton) {
                    	/* User clicked OK so do some stuff */
                    	ContentValues values = new ContentValues();
                        values.put(Methods.TEXT, mDialogEdit.getText().toString());
                        /**
                         * Get the step number. 
                         * If step number is not a valid integer then default to 0
                         */
                        int stepNum = 0;
                        try {
                        	stepNum = Integer.valueOf(mDialogStep.getText().toString());
                        }
                        finally {
                        	values.put(Methods.STEP, stepNum);
                        }
                        values.put(Methods.RECIPE, Bites.mRecipeId);
                        getContentResolver().update(mUri, values, null, null);
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
                .setTitle(R.string.delete_method)
                .setView(mDialogView)
                .setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
                	public void onClick(DialogInterface dialog, int whichButton) {
                    	/* User clicked OK so do some stuff */
                		getContentResolver().delete(mUri, null, null);
                	}
                })
                .setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    	
                    }
                })
                .create();
		case DIALOG_INSERT:
            mDialogView = factory.inflate(R.layout.dialog_method, null);
            mDialogEdit = (EditText)mDialogView.findViewById(R.id.method_edit);
            mDialogStep = (EditText)mDialogView.findViewById(R.id.method_step);
            return new AlertDialog.Builder(this)
                .setTitle(R.string.insert_method)
                .setView(mDialogView)
                .setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
                	public void onClick(DialogInterface dialog, int whichButton) {
                    	/* User clicked OK so do some stuff */
                		ContentValues values = new ContentValues();
                		values.put(Methods.RECIPE, Bites.mRecipeId);
                        values.put(Methods.TEXT, mDialogEdit.getText().toString());
                        /**
                         * Get the step number. 
                         * If step number is not a valid integer then default to 0
                         */
                        int stepNum = 0;
                        try {
                        	stepNum = Integer.valueOf(mDialogStep.getText().toString());
                        }
                        finally {
                        	values.put(Methods.STEP, stepNum);
                        }
                        values.put(Methods.RECIPE, Bites.mRecipeId);
                        mUri = getContentResolver().insert(Methods.CONTENT_URI,values);
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

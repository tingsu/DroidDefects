package caldwell.ben.bites;

import java.io.File;
import java.io.InputStream;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Element;
import caldwell.ben.bites.RecipeBook.Ingredients;
import caldwell.ben.bites.RecipeBook.Methods;
import caldwell.ben.bites.RecipeBook.Recipes;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.NotificationManager;
import android.app.TabActivity;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

/**
 * The main activity, operates as a top level tabhost that contains list activities for
 * recipes, ingredients and method steps.
 * 
 * Recipe received notifications send an intent to Bites to add the new recipe when clicked.
 *  
 * @author Ben Caldwell
 *
 */
public class Bites extends TabActivity {
	SmsReceiver sms;
	
	static long mRecipeId;
	static String mRecipeName;
	
	private static final int DIALOG_IMPORT = 1;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        checkRecipeReceived();
        
        final TabHost tabHost = getTabHost();
               
        tabHost.addTab(tabHost.newTabSpec("tab_recipes")
                .setIndicator(getResources().getText(R.string.tab_recipes))
                .setContent(new Intent(this, RecipeList.class)));
        tabHost.addTab(tabHost.newTabSpec("tab_ingredients")
                .setIndicator(getResources().getText(R.string.tab_ingredients))
                .setContent(new Intent(this, IngredientList.class)));
        tabHost.addTab(tabHost.newTabSpec("tab_method")
                .setIndicator(getResources().getText(R.string.tab_method))
                .setContent(new Intent(this, MethodList.class)));  
    }

	/**
	 * Check the intent to see if Bites was started on receiving a recipe
	 * from either an sms or a downloaded file and add to the content provider if it was.
	 */
	private void checkRecipeReceived() {
		if (getIntent().getAction() != null)
        {
	        /**
	         * If Bites was started by clicking on a notification that a recipe was received
	         * load the new recipe into the database and cancel the notification 
	         */
	        if (getIntent().getAction().contentEquals("com.captainfanatic.bites.RECEIVED_RECIPE") 
	        		|| getIntent().getAction().contentEquals(Intent.ACTION_VIEW)) {
	        	showDialog(DIALOG_IMPORT);
			}
        }
	}


	@Override
	protected Dialog onCreateDialog(int id) {
		LayoutInflater factory = LayoutInflater.from(this);
		View mDialogView;
		TextView mDialogText;
		switch (id)	{
		case DIALOG_IMPORT:
			mDialogView = factory.inflate(R.layout.dialog_confirm, null);
			mDialogText = (TextView)mDialogView.findViewById(R.id.dialog_confirm_prompt);
			mDialogText.setText(getImportedRecipeName());
			return new AlertDialog.Builder(this)
            .setTitle(R.string.import_recipe)
            .setView(mDialogView)
            .setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
            	public void onClick(DialogInterface dialog, int whichButton) {
                	/* User clicked OK so do some stuff */
            		importRecipe();
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
	
	/**
	 * getImportedRecipeName
	 * Returns the title of a recipe to be imported from sms or xml file
	 * @return String
	 * @author Ben Caldwell
	 */
	private String getImportedRecipeName() {

		//If the intent has no action then no recipe was imported - return null
		if (getIntent().getAction() == null) {
			return null;
		}

		//Recipe received via sms
        if (getIntent().getAction().contentEquals("com.captainfanatic.bites.RECEIVED_RECIPE"))
        {
    		return getIntent().getStringExtra(SmsReceiver.KEY_RECIPE);
		}
        
		//Recipe received via XML file download
        if (getIntent().getScheme().equals("file")) {
        	String path = getIntent().getData().getPath();
        	File file = new File(path);
			try {
				DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
				Document doc = builder.parse(file);
				Element recipe = doc.getDocumentElement();
				return recipe.getAttribute("name");
			} catch (Exception e) {
				Toast.makeText(this, R.string.xml_create_error, Toast.LENGTH_LONG).show();
				return null;
			}
        }
		
		//Recipe received via XML file as gmail attachment
        if (getIntent().getScheme().equals("content")) {
        	try {
	        	InputStream attachment = getContentResolver().openInputStream(getIntent().getData());
	        	DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
				Document doc = builder.parse(attachment);
				attachment.close();
				Element recipe = doc.getDocumentElement();
				return recipe.getAttribute("name");
        	} catch (Exception e) {
        		Toast.makeText(this, R.string.xml_create_error, Toast.LENGTH_LONG).show();
				return null;
        	}
        }
        
        //Return null if none of the above criteria are satisfied
        return null;
	}
	
	private void importRecipe() {
		//If the intent has no action then no recipe was imported - return null
		if (getIntent().getAction() == null) {
			return;
		}
		//Import recipe received via sms
        if (getIntent().getAction().contentEquals("com.captainfanatic.bites.RECEIVED_RECIPE"))
        {
        	importSMSRecipe();
        	return;
		}       
		//Import recipe received via XML file download
        if (getIntent().getScheme().equals("file")) {
        	importXMLDownload();
        	return;
        }
		//Recipe received via XML file as gmail attachment
        if (getIntent().getScheme().equals("content")) {
        	importXMLAttachment();
        	return;
        }
	}
	
    /**
     * importSMSRecipe
     * Add a recipe received via sms to the content provider
     */
	private void importSMSRecipe() {
		//Cancel the notification using the id extra in the intent
		NotificationManager nm = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
		nm.cancel(getIntent().getIntExtra(SmsReceiver.KEY_NOTIFY_ID,0));
		
		ContentValues values = new ContentValues();
		mRecipeName = getIntent().getStringExtra(SmsReceiver.KEY_RECIPE);
		values.put(Recipes.TITLE, mRecipeName);
		Uri recipeUri = getContentResolver().insert(Recipes.CONTENT_URI, values);
		mRecipeId = Long.parseLong(recipeUri.getLastPathSegment());

		//get ingredients from the intent extras and load into the content provider
		String ingredients[] = getIntent().getStringArrayExtra(SmsReceiver.KEY_ING_ARRAY);
			for (int i =0; i<ingredients.length; i++)
			{
				values = new ContentValues();
				values.put(Ingredients.RECIPE, mRecipeId);
				values.put(Ingredients.TEXT,ingredients[i]);
				getContentResolver().insert(Ingredients.CONTENT_URI, values);
			}
		
		//get methods from the intent extras and load into the content provider
		String methods[] = getIntent().getStringArrayExtra(SmsReceiver.KEY_METH_ARRAY);
		int methodSteps[] = getIntent().getIntArrayExtra(SmsReceiver.KEY_METH_STEP_ARRAY);
		
		for (int i = 0; i<methods.length; i++)
		{
			values = new ContentValues();
			values.put(Methods.RECIPE, mRecipeId);
			values.put(Methods.TEXT, methods[i]);
			values.put(Methods.STEP, (i<methodSteps.length) ? methodSteps[i] : i);
			getContentResolver().insert(Methods.CONTENT_URI, values);
		}
		
		//Change to ingredients tab and back to recipe tab to force onResume and requery etc.
		getTabHost().setCurrentTab(1);
		getTabHost().setCurrentTab(0);
	}
	
	/**
	 * importXMLDownload
	 * Parse a downloaded recipe xml file for recipe name, ingredients and methods and 
	 * add to recipe content provider.
	 */
	private void importXMLDownload() {
		
		//The scheme is "file" for a browser download
		if (!getIntent().getScheme().equals("file")) {
			return;
		}

		try {
			DocumentBuilder builder = DocumentBuilderFactory.newInstance()
			.newDocumentBuilder();
			String path = getIntent().getData().getPath();
			File file = new File(path);
			Document doc = builder.parse(file);
			Element recipe = doc.getDocumentElement();
			NodeList description = recipe.getElementsByTagName("description");
			NodeList ingredients = recipe.getElementsByTagName("ingredient");
			NodeList methods = recipe.getElementsByTagName("method");

			//Check for no ingredients or method steps - poorly formed?
			if (ingredients.getLength() <= 0 || methods.getLength() <= 0) {
				Toast.makeText(this, R.string.xml_create_error, Toast.LENGTH_LONG).show();
				mRecipeId = 0;
				mRecipeName = "";
				//Change to ingredients tab and back to recipe tab to force onResume and requery etc.
				getTabHost().setCurrentTab(1);
				getTabHost().setCurrentTab(0);
				return;
			}

			//Insert new recipe title
			ContentValues values = new ContentValues();
			mRecipeName = recipe.getAttribute("name");
			values.put(Recipes.TITLE, mRecipeName);
			values.put(Recipes.AUTHOR, recipe.getAttribute("author"));
			if (description.getLength()>0) {
				values.put(Recipes.DESCRIPTION, description.item(0).getFirstChild().getNodeValue());
			}
			Uri recipeUri = getContentResolver().insert(Recipes.CONTENT_URI, values);
			mRecipeId = Long.parseLong(recipeUri.getLastPathSegment());
			
			//insert ingredients for the new recipe
			values = new ContentValues();
			for (int i =0; i<ingredients.getLength(); i++)
			{
				values.put(Ingredients.RECIPE, mRecipeId);
				values.put(Ingredients.TEXT,ingredients.item(i).getFirstChild().getNodeValue());
				values.put(Ingredients.ORDINAL,i+1);
				getContentResolver().insert(Ingredients.CONTENT_URI, values);
			}
			
			//insert methods for the new recipe
			values = new ContentValues();
			for (int i =0; i<methods.getLength(); i++)
			{
				values.put(Methods.RECIPE, mRecipeId);
				values.put(Methods.STEP,((Element)methods.item(i)).getAttribute("step"));
				values.put(Methods.TEXT,methods.item(i).getFirstChild().getNodeValue());
				getContentResolver().insert(Methods.CONTENT_URI, values);
			}
		} catch (Exception e) {
			mRecipeId = 0;
			mRecipeName = "";
			//Pop a toast showing xml file error and return
			Toast.makeText(this, R.string.xml_create_error, Toast.LENGTH_LONG).show();
		} finally {
			//Change to ingredients tab and back to recipe tab to force onResume and requery etc.
			getTabHost().setCurrentTab(1);
			getTabHost().setCurrentTab(0);
		}
	}

	/**
	 * importXMLAttachment
	 * Parse a recipe xml file attached to an email for recipe name, ingredients and methods and 
	 * add to recipe content provider.
	 */
	private void importXMLAttachment() {

		if (!getIntent().getScheme().equals("content")) {
			return;
		}
		
		try {
			InputStream attachment = getContentResolver().openInputStream(getIntent().getData());
			DocumentBuilder builder = DocumentBuilderFactory.newInstance()
			.newDocumentBuilder();
			Document doc = builder.parse(attachment);
			attachment.close();
			Element recipe = doc.getDocumentElement();
			NodeList description = recipe.getElementsByTagName("description");
			NodeList ingredients = recipe.getElementsByTagName("ingredient");
			NodeList methods = recipe.getElementsByTagName("method");

			//Check for no ingredients or method steps - poorly formed?
			if (ingredients.getLength() <= 0 || methods.getLength() <= 0) {
				Toast.makeText(this, R.string.xml_create_error, Toast.LENGTH_LONG).show();
				mRecipeId = 0;
				mRecipeName = "";
				//Change to ingredients tab and back to recipe tab to force onResume and requery etc.
				getTabHost().setCurrentTab(1);
				getTabHost().setCurrentTab(0);
				return;
			}

			//Insert new recipe title
			ContentValues values = new ContentValues();
			mRecipeName = recipe.getAttribute("name");
			values.put(Recipes.TITLE, mRecipeName);
			values.put(Recipes.AUTHOR, recipe.getAttribute("author"));
			if (description.getLength()>0) {
				values.put(Recipes.DESCRIPTION,description.item(0).getFirstChild().getNodeValue());
			}
			Uri recipeUri = getContentResolver().insert(Recipes.CONTENT_URI, values);
			mRecipeId = Long.parseLong(recipeUri.getLastPathSegment());
						
			//insert ingredients for the new recipe
			values = new ContentValues();
			for (int i =0; i<ingredients.getLength(); i++)
			{
				values.put(Ingredients.RECIPE, mRecipeId);
				values.put(Ingredients.TEXT,ingredients.item(i).getFirstChild().getNodeValue());
				values.put(Ingredients.ORDINAL,i+1);
				getContentResolver().insert(Ingredients.CONTENT_URI, values);
			}
			
			//insert methods for the new recipe
			values = new ContentValues();
			for (int i =0; i<methods.getLength(); i++)
			{
				values.put(Methods.RECIPE, mRecipeId);
				values.put(Methods.STEP,((Element)methods.item(i)).getAttribute("step"));
				values.put(Methods.TEXT,methods.item(i).getFirstChild().getNodeValue());
				getContentResolver().insert(Methods.CONTENT_URI, values);
			}
		} catch (Exception e) {
			//Pop a toast showing xml file error and return
			Toast.makeText(this, R.string.xml_create_error, Toast.LENGTH_LONG);
			mRecipeId = 0;
			mRecipeName = "";
		} finally {
			//Change to ingredients tab and back to recipe tab to force onResume and requery etc.
			getTabHost().setCurrentTab(1);
			getTabHost().setCurrentTab(0);
		}
	}
}
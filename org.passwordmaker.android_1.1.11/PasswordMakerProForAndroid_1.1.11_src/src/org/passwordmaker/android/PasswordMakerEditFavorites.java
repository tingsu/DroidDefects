package org.passwordmaker.android;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;

public class PasswordMakerEditFavorites extends Activity {
	private PwmProfile profile;
	public static final String EXTRA_PROFILE = "pwmProfile";
	
	public class FavoriteRow extends TableRow {
		CheckBox chkItem ;
		public FavoriteRow(Context context, String title) {
			super(context);
			init(title, false);
		}
		
		public FavoriteRow(Context context, AttributeSet attrs, String title) {
			super(context, attrs);
			init(title, false);
		}

		private void init(String title, boolean isChecked) {
			chkItem = new CheckBox(p());
			chkItem.setChecked(isChecked);
			chkItem.setText(title);
			addView(chkItem);
		}
		
		private PasswordMakerEditFavorites p() {
			return PasswordMakerEditFavorites.this;
		}
		
		public boolean isChecked() {
			return chkItem.isChecked();
		}
		
		public String getTitle() {
			return chkItem.getText().toString();
		}
		
		public void setChecked(boolean checked) {
			chkItem.setChecked(checked);
		}
		
		public void setTitle(String title) {
			chkItem.setText(title);
		}
	}
	
	TableLayout tblFavoritesEdit;
	Button btnAdd;
	Button btnRem;
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)  {
	    if (  Integer.valueOf(android.os.Build.VERSION.SDK) < 7 //Instead use android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.ECLAIR
	            && keyCode == KeyEvent.KEYCODE_BACK
	            && event.getRepeatCount() == 0) {
	        // Take care of calling this method on earlier versions of
	        // the platform where it doesn't exist.
	        onBackPressed();
	    }

	    return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onBackPressed() {
	    // This will be called either automatically for you on 2.0
	    // or later, or by the code above on earlier versions of the
	    // platform.
		saveResult();
		return;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.edit_favorites);
		profile = (PwmProfile) getIntent().getExtras().getSerializable(EXTRA_PROFILE);
	    assert profile != null;
		
		tblFavoritesEdit = (TableLayout)findViewById(R.id.tblFavoritesEdit);
		btnAdd = (Button)findViewById(R.id.btnEditFavAdd);
		btnRem = (Button)findViewById(R.id.btnEditFavRemove);
		
		for ( String fav : profile.getFavorites() ) {
			addItem(fav);
		}
		btnAdd.setOnClickListener(btnAddClick);
		btnRem.setOnClickListener(btnRemClick);
	}
	
	public void addItem(String title) {
		addItem(title, false);
	}
	
	public void addItem(String title, boolean checked) {
		FavoriteRow favorite = new FavoriteRow(this, title);
		favorite.setChecked(checked);
		tblFavoritesEdit.addView(favorite, new TableLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));		
	}
	
	private void saveResult() {
		Intent resultIntent = new Intent();
		Set<String> favorites = new HashSet<String>();
		
		for (int i = 0; i < tblFavoritesEdit.getChildCount(); ++i) {
			FavoriteRow row = (FavoriteRow)tblFavoritesEdit.getChildAt(i);
			String str = row.chkItem.getText().toString();
			favorites.add(str);
		}
		profile.setProfiles(favorites);
		resultIntent.putExtra(EXTRA_PROFILE, profile);
		setResult(Activity.RESULT_OK, resultIntent);
		finish();
	}

	private void newFavorite() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		final EditText editView = new EditText(this);
		editView.setLines(1);
		editView.setMinimumWidth(200);
		builder.setView(editView);
		builder.setPositiveButton(R.string.AddFavorite,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						String newFav = editView.getText().toString();
						addItem(newFav);
					}
				});
		builder.setNegativeButton(R.string.Cancel, null);
		final AlertDialog alert = builder.create();
		editView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					alert.getWindow()
							.setSoftInputMode(
									WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
				}

			}
		});
		builder.setCancelable(true);
		alert.show();
	}
	
	private void removeSelectedItems() {
		ArrayList<View> toRemove = new ArrayList<View>();
		for (int i = 0; i < tblFavoritesEdit.getChildCount(); ++i) {
			FavoriteRow row = (FavoriteRow)tblFavoritesEdit.getChildAt(i);
			if ( row.chkItem.isChecked() ) {
				toRemove.add(row);
			}
		}
		for ( View row : toRemove ) {
			tblFavoritesEdit.removeView(row);
		}
	}
	
	OnClickListener btnAddClick = new OnClickListener() {
		@Override
		public void onClick(View v) {
			newFavorite();
		}
	}; 
	
	OnClickListener btnRemClick = new OnClickListener() {
		@Override
		public void onClick(View v) {
			removeSelectedItems();
		}
	}; 
}


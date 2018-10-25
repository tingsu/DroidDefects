/*
 *  Copyright 2011 James Stapleton
 * 
 *  This file is part of PasswordMaker Pro For Android.
 *
 *  PasswordMaker Pro For Android is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *  
 *  PasswordMaker Pro For Android is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with PasswordMaker Pro For Android.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.passwordmaker.android;

import java.util.UUID;

import org.passwordmaker.android.LeetConverter.LeetLevel;
import org.passwordmaker.android.LeetConverter.UseLeet;
import org.passwordmaker.android.PwmProfile.UrlComponents;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.Spinner;

public class PasswordMakerEditProfile extends Activity {
	private static final String LOG_TAG = "PasswordMakerEditProfile";
	private PasswordMaker pwm = new PasswordMaker();
	private PwmProfile profile;
	private EditText txtName;
	private CheckBox chkDomain;
	private CheckBox chkProtocol;
	private CheckBox chkSubdomain;
	private CheckBox chkOthers;
	private Spinner useLeet;
	private Spinner leetLevel;
	private Spinner hashAlgo;
	private EditText passLength;
	private EditText username;
	private EditText modifier;
	private Spinner charSet;
	private EditText prefix;
	private EditText suffix;
	private CheckBox chkPasswordHash;
	private TextView lblPasswordHash;
	private EditText txtPasswordHash;
	
	
	private EditText focusedText = null;
	
	public static final String EXTRA_PROFILE = "pwmProfile";
	
	public PasswordMakerEditProfile() {
	}
	
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
	
	private void getCustomCharSet() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		final EditText editView = new EditText(this);
		editView.setLines(5);
		editView.setMinimumWidth(100);
		editView.setText(profile.getCharacters());
		builder.setView(editView);
		builder.setPositiveButton(R.string.AddProfile, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				profile.setCharacters(editView.getText().toString());
				setupCharacterSet(); // reselect char set
			}
		});
		builder.setNegativeButton(R.string.Cancel, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				setupCharacterSet(); // reselect char set
			}
		});
		final AlertDialog alert = builder.create();
		editView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    alert.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                }
				
			}
        });
		builder.setCancelable(true);
		alert.show();
	}
	
	private void setupCharacterSet() {
		CharacterSetSelection sel = CharacterSetSelection.findSetName( profile.getCharacters() );
		charSet.setSelection(sel.ordinal());
		charSet.setOnItemSelectedListener(new OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				CharacterSetSelection sel = CharacterSetSelection.values()[parent.getSelectedItemPosition()];
				if ( sel == CharacterSetSelection.custom ) {
					getCustomCharSet();
				} else {
					profile.setCharacters(sel);
				}
			}
			public void onNothingSelected(AdapterView<?> parent) {}
		});
	}
	
	private void setupMemberVars() {
		
		txtName = (EditText)findViewById(R.id.txtName);
		txtName.setOnFocusChangeListener(new OnFocusChangeListener() {
			public void onFocusChange(View v, boolean hasFocus) {
				if ( ! hasFocus )
					profile.setName(txtName.getText().toString());
				else
					focusedText = txtName;
			}
		});
		chkProtocol = (CheckBox)findViewById(R.id.chkProtocol);
		chkProtocol.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if ( isChecked )
					profile.getUrlComponents().add(UrlComponents.Protocol);
				else
					profile.getUrlComponents().remove(UrlComponents.Protocol);
				
			}
		});
		chkSubdomain = (CheckBox)findViewById(R.id.chksubdomain);
		chkSubdomain.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if ( isChecked )
					profile.getUrlComponents().add(UrlComponents.Subdomain);
				else
					profile.getUrlComponents().remove(UrlComponents.Subdomain);
				
			}
		});
		chkOthers = (CheckBox)findViewById(R.id.chkOthers);
		chkOthers.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if ( isChecked )
					profile.getUrlComponents().add(UrlComponents.PortPathAnchorQuery);
				else
					profile.getUrlComponents().remove(UrlComponents.PortPathAnchorQuery);
				
			}
		});
		chkDomain = (CheckBox)findViewById(R.id.chkDomain);
		chkDomain.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if ( isChecked )
					profile.getUrlComponents().add(UrlComponents.Domain);
				else
					profile.getUrlComponents().remove(UrlComponents.Domain);
				
			}
		});
		useLeet = (Spinner)findViewById(R.id.selectLeet);
		useLeet.setOnItemSelectedListener(new OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				UseLeet sel = UseLeet.values()[parent.getSelectedItemPosition()];
				profile.setUseLeet(sel);
			}

			public void onNothingSelected(AdapterView<?> arg0) {}
		});
		leetLevel = (Spinner)findViewById(R.id.spinLeetLevel);
		leetLevel.setOnItemSelectedListener(new OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				LeetLevel lvl = LeetLevel.values()[parent.getSelectedItemPosition()];
				profile.setLeetLevel(lvl);
			}
			public void onNothingSelected(AdapterView<?> arg0) {}
		});
		hashAlgo = (Spinner)findViewById(R.id.selectHashAlgos);
		hashAlgo.setOnItemSelectedListener(new OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				HashAlgo sel = HashAlgo.values()[parent.getSelectedItemPosition()];
				profile.setHashAlgo(sel);
			}

			public void onNothingSelected(AdapterView<?> arg0) {}
		});
		passLength = (EditText)findViewById(R.id.txtPasswordLen);
		passLength.setOnFocusChangeListener(new OnFocusChangeListener() {
			public void onFocusChange(View v, boolean hasFocus) {
				System.out.println("Password length: " + Boolean.toString(hasFocus) + ": " + passLength.getText().toString());
				if ( ! hasFocus ) {
					if ( passLength.getText().length() == 0 ) {
						passLength.setText(Short.toString(profile.getLengthOfPassword()));
					} else {
						try {
							profile.setLengthOfPassword(Short.parseShort(passLength.getText().toString()));
						} catch (NumberFormatException e) {
							Log.e(LOG_TAG, "Can not set length of password, \"" + passLength.getText().toString() + "\" " +
									"using existing length of " + profile.getLengthOfPassword() + " Error: " + e.getMessage());
							passLength.setText(Short.toString(profile.getLengthOfPassword()));
						}
					}
				} else
					focusedText = passLength;
			}
		});
		username = (EditText)findViewById(R.id.txtUsername);
		username.setOnFocusChangeListener(new OnFocusChangeListener() {
			public void onFocusChange(View v, boolean hasFocus) {
				if ( ! hasFocus )
					profile.setUsername(username.getText().toString());
				else
					focusedText = username;
			}
		});
		modifier = (EditText)findViewById(R.id.txtModifier);
		modifier.setOnFocusChangeListener(new OnFocusChangeListener() {
			public void onFocusChange(View v, boolean hasFocus) {
				if ( ! hasFocus )
					profile.setModifier(modifier.getText().toString());
				else
					focusedText = modifier;
			}
		});
		charSet = (Spinner)findViewById(R.id.selectCharacterSet);
		// this listener is setup elsewhere
		prefix = (EditText)findViewById(R.id.txtPrefix);
		prefix.setOnFocusChangeListener(new OnFocusChangeListener() {
			public void onFocusChange(View v, boolean hasFocus) {
				if ( ! hasFocus )
					profile.setPrefix(prefix.getText().toString());
				else
					focusedText = prefix;
			}
		});
		suffix = (EditText)findViewById(R.id.txtSuffix);
		suffix.setOnFocusChangeListener(new OnFocusChangeListener() {
			public void onFocusChange(View v, boolean hasFocus) {
				if ( ! hasFocus )
					profile.setSuffix(suffix.getText().toString());
				else
					focusedText = suffix;
			}
		});
		lblPasswordHash = (TextView)findViewById(R.id.lblPasswordHash);
		chkPasswordHash = (CheckBox)findViewById(R.id.chkStorePasswordHash);
		chkPasswordHash.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if ( ! isChecked ) {
					profile.disablePasswordHash();
					lblPasswordHash.setVisibility(View.GONE);
					txtPasswordHash.setVisibility(View.GONE);
				} else {
					lblPasswordHash.setVisibility(View.VISIBLE);
					txtPasswordHash.setVisibility(View.VISIBLE);					
				}
			}
		});
		txtPasswordHash = (EditText)findViewById(R.id.txtMasterPasswordForHash);
		txtPasswordHash.setOnFocusChangeListener(new OnFocusChangeListener() {
			public void onFocusChange(View v, boolean hasFocus) {
				if ( ! hasFocus ) {
					// only set it if its different than before to keep the same salt
					final String masterPassword = txtPasswordHash.getText().toString();
					if (  ! profile.hasPasswordHash() || ! pwm.matchesPasswordHash(masterPassword)) {
						final String salt = UUID.randomUUID().toString();
						profile.setCurrentPasswordHash( pwm.generatePassword(salt, masterPassword), salt);
					}
				} else
					focusedText = txtPasswordHash;
			}
		});
	}

	private void setDefaultValues() {
	    txtName.setText(profile.getName());
	    chkProtocol.setChecked(profile.getUrlComponents().contains(UrlComponents.Protocol));
	    chkDomain.setChecked(profile.getUrlComponents().contains(UrlComponents.Domain));
	    chkSubdomain.setChecked(profile.getUrlComponents().contains(UrlComponents.Subdomain));
	    chkOthers.setChecked(profile.getUrlComponents().contains(UrlComponents.PortPathAnchorQuery));
	    useLeet.setSelection(profile.getUseLeet().ordinal());
	    leetLevel.setSelection(profile.getLeetLevel().ordinal());
	    hashAlgo.setSelection(profile.getCurrentAlgo().ordinal());
	    passLength.setText(Short.toString(profile.getLengthOfPassword()));
	    username.setText(profile.getUsername());
	    modifier.setText(profile.getModifier());
	    charSet.setSelection(CharacterSetSelection.findSetName(profile.getCharacters()).ordinal());
	    prefix.setText(profile.getPrefix());
	    suffix.setText(profile.getSuffix());
	    chkPasswordHash.setChecked(profile.hasPasswordHash());
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.edit_profile);
	    // get the profile
	    profile = (PwmProfile) getIntent().getExtras().getSerializable(EXTRA_PROFILE);
	    assert profile != null;
	    pwm.setProfile(profile);
	    setupMemberVars();
	    setupCharacterSet();
	    setDefaultValues();
	 }
	 
	@Override
	protected void onPause() {
		super.onPause();
	}
	
	private void saveResult() {
		if ( focusedText != null )
			focusedText.getOnFocusChangeListener().onFocusChange(focusedText, false);
		Intent resultIntent = new Intent();
		resultIntent.putExtra(EXTRA_PROFILE, profile);
		setResult(Activity.RESULT_OK, resultIntent);
		finish();
	}
}

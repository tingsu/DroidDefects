package caldwell.ben.bites;

import android.os.Bundle;
import android.preference.PreferenceActivity;

public class BitesPreferences extends PreferenceActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);
	}
}

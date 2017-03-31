package com.normsstuff.simpleserver;


import android.app.Activity;
import android.os.Bundle;

/**
 * Settings activity that contains a fragment displaying the preferences.
 */
public class SettingsActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Display the preferences fragment as the content of the activity
		getFragmentManager().beginTransaction()
				.replace(android.R.id.content, new SettingsFragment()).commit();
	}
}
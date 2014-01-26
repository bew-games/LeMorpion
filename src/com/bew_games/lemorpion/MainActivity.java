package com.bew_games.lemorpion;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends Activity {

	private TextView m_version;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		m_version = (TextView) findViewById(R.id.version);
		
		try {
			m_version.setText("v " + this.getPackageManager().getPackageInfo(this.getPackageName(), 0).versionName);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public void onClick(View v) {
		startActivity(new Intent (getApplicationContext(), ChoiceActivity.class));
	}
}

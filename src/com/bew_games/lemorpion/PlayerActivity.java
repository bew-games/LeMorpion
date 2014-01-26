package com.bew_games.lemorpion;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

//Classe permettant de choisir les nom des joueurs pour le partie qui va se dérouler
public class PlayerActivity extends Activity {

	private boolean m_soloPlayer = false;
	private EditText m_joueur1Edit;
	private EditText m_joueur2Edit;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_player);
		
		m_joueur1Edit = (EditText)findViewById(R.id.joueur1NameEdit);
		
		TextView joueur2Name = (TextView)findViewById(R.id.joueur2Name);
		m_joueur2Edit = (EditText)findViewById(R.id.joueur2NameEdit);
		
		Button start = (Button)findViewById(R.id.startButton);
		
		//Si l'on est en mode 1 joueur on rend invisible les champs qui concernent le joeur 2
		if(getIntent().getExtras().getInt("nbJoueur") == 1) {
			
			m_soloPlayer = true;
			
			joueur2Name.setVisibility(View.INVISIBLE);
			m_joueur2Edit.setVisibility(View.INVISIBLE);
		}
		
		start.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent i = new Intent (getApplicationContext(), Partie_activity.class);
      	        
				i.putExtra("nbJoueur", getIntent().getExtras().getInt("nbJoueur"));
				i.putExtra("nameJoueur1", m_joueur1Edit.getText().toString());
				
				if(! m_soloPlayer) {
					i.putExtra("nameJoueur2", m_joueur2Edit.getText().toString());
				} else {
					i.putExtra("nameJoueur2", getString(R.string.OrdinateurName));
				}
				
				startActivity(i);
			}
		});		
	}
}

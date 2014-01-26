package com.bew_games.lemorpion;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

//Cette classe permet de choisir si l'on veut jouer en mode 1 ou 2 joueurs
public class ChoiceActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_choice);
		
		//Bouton pour deux joueurs
		Button joueurs = (Button)findViewById(R.id.joueurs);
		
		//Bouton pour un joueur
		Button joueur = (Button)findViewById(R.id.joueur);
		
		joueurs.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i = new Intent (getApplicationContext(), PlayerActivity.class);
      	        i.putExtra("nbJoueur", 2);
				startActivity(i);
			}
		});
		
		joueur.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i = new Intent (getApplicationContext(), PlayerActivity.class);
				i.putExtra("nbJoueur", 1);
      	        startActivity(i);
			}
		});		
	}
}

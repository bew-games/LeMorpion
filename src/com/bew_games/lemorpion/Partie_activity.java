package com.bew_games.lemorpion;

import java.util.ArrayList;
import java.util.Random;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class Partie_activity extends Activity {
	
	//Image de la pièce en cours
	private int m_pieceCourante;
	
	//Image par défaut des boutons de la grille
	private Drawable m_background_default;
	
	//Layout contenant la grille de jeu
	private LinearLayout m_grille;
	
	//Tableau contenant les boutons de la grille de jeu
	private ImageButton [][] m_buttons;
	
	//Booléen permettant de bloquer la grille de jeu pendant la réflexion de l'IA
	private boolean m_processing = false;
	
	//Etat courant
	private int m_etat_courant;
	
	//Constante contenant la taille de la grille de jeu
	private final int m_taille_grille = 3;
	
	//Booléen permettant de savoir si l'on est en mode 1 ou 2 joueurs
	private boolean m_soloPlayer;
	
	//Textviews contenant le score de chaque joueur
	private TextView m_scoreTexteCroix;
	private TextView m_scoreTexteRond;
	
	//Chaines contenant les noms des joueurs
	private String m_nameJoueurRond;
	private String m_nameJoueurCroix;
	
	//Textviews contenant les noms des joueurs
	private TextView m_textJoueurCroix;
	private TextView m_textJoueurRond;
	
	//Image qui correspond à la pièce de départ de l'activité
	private int m_pieceDepart;
	
	//Objet random permettant de générer des chiffres aléatoires
	private Random m_random = new Random();
	
	//Listes contenant des positions sur la grille de jeu
	private ArrayList<Pair<Integer, Integer>> m_liste_coins = new ArrayList<Pair<Integer, Integer>>();
	private ArrayList<Pair<Integer, Integer>> m_liste_croix_centre = new ArrayList<Pair<Integer, Integer>>();
	
	//Constante contenant la position du centre de la grille 
	private final Pair<Integer, Integer> m_coup_centre = new Pair<Integer, Integer>(1, 1);
	
	//Compteur permettant de compter le nombre de coups déjà passés
	private int m_compteurCoup = 0;
	
	//Booléens permettant de savoir quelle technique est utilisée en ce moment par l'IA pour placer ses coups
	private boolean m_techniqueCoins = false;
	private boolean m_techniqueCentre = false;
	private boolean m_techniqueCentreCroix = false;
	
	//Booléen permettant de savoir si l'on joue en mode expert
	private boolean m_levelExpert = false;
	
	//Checkboxes permettant de changer le niveau de difficulté (masquée en mode deux joueurs)
	private CheckBox m_normal;
	private CheckBox m_expert;
	
	//Paire qui va être jouée par l'IA
	private Pair<Integer, Integer> m_paireIA;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//Affichage du layout dans l'activité
		setContentView(R.layout.activity_partie);
		
		//Récupération des composants nécessaires dans le layout
		m_grille = (LinearLayout) findViewById(R.id.grille);
		m_textJoueurCroix = (TextView) findViewById(R.id.nameJoueurCroix);
		m_textJoueurRond = (TextView) findViewById(R.id.nameJoueurRond);
		m_scoreTexteCroix = (TextView) findViewById(R.id.score_texte_croix);
		m_scoreTexteRond = (TextView) findViewById(R.id.score_texte_rond);
		m_normal = (CheckBox) findViewById(R.id.normal);
		m_expert = (CheckBox) findViewById(R.id.Expert);
		Button b_restart = (Button) findViewById(R.id.restart);
		
		//Remplissage des listes de coups
		m_liste_coins.add(new Pair<Integer, Integer>(0,0));
		m_liste_coins.add(new Pair<Integer, Integer>(0,2));
		m_liste_coins.add(new Pair<Integer, Integer>(2,0));
		m_liste_coins.add(new Pair<Integer, Integer>(2,2));
		
		m_liste_croix_centre.add(new Pair<Integer, Integer>(0,1));
		m_liste_croix_centre.add(new Pair<Integer, Integer>(1,2));
		m_liste_croix_centre.add(new Pair<Integer, Integer>(1,0));
		m_liste_croix_centre.add(new Pair<Integer, Integer>(2,1));

		//Récupération des paramètres donnés par l'activité précédente (mode de jeu, noms des joueurs ...)
		if(getIntent().getExtras().getInt("nbJoueur") == 1) {
			m_soloPlayer = true;			
		} else {
			m_soloPlayer = false;
		}
		
		m_nameJoueurRond = getIntent().getExtras().getString("nameJoueur1");
		
		m_nameJoueurCroix = getIntent().getExtras().getString("nameJoueur2");
		
		if(m_nameJoueurRond.isEmpty()) {
			m_nameJoueurRond = getResources().getString(R.string.Joueur1Name);
		}
		
		if(m_nameJoueurCroix.isEmpty()) {
			m_nameJoueurCroix = getResources().getString(R.string.Joueur2Name);
		}
		
		//Initialisation du tableau de boutons
		m_buttons = new ImageButton[m_taille_grille][m_taille_grille];
		
		//Mise en place de l'écouteur pour le bouton restart
		b_restart.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				restart();
			}
		});
		
		//Ecriture du nom des joueurs en bas de l'écran
		m_textJoueurCroix.setText(m_nameJoueurCroix);
		m_textJoueurRond.setText(m_nameJoueurRond);
		
		//Remplissage du layout et du tableau contenant la grille
		for(int i = 0; i < m_taille_grille; i++) {
			
			LinearLayout l_layout = new LinearLayout(this);
			
			l_layout.setOrientation(LinearLayout.HORIZONTAL);
			
			for(int j = 0; j < m_taille_grille; j++) {
				ImageButton imgb_button = new ImageButton(this);
				
				imgb_button.setOnClickListener(new PieceClickListener(imgb_button));
				
				m_buttons[i][j] = imgb_button;
				
				int i_dimension = (int) getResources().getDimension(R.dimen.taille_bouton);
				
				l_layout.addView(imgb_button, i_dimension, i_dimension);
			}
			
			m_grille.addView(l_layout, i, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		}
		
		//Sauvegarde de l'image par défaut des boutons dans son état actuel
		m_background_default = m_buttons[0][0].getBackground().getCurrent();
		
		//Génération aléatoire pour déterminer quel joueur va commencer
		int i_joueur = m_random.nextInt(1);
		
		if(i_joueur == 0 || m_soloPlayer) {
			m_pieceDepart = R.drawable.rond;
			m_etat_courant = Etats.TOUR_ROND.ordinal();
			m_pieceCourante = R.drawable.rond;
			
			Toast.makeText(getApplicationContext(), m_nameJoueurRond + " " + getResources().getString(R.string.startGame),
			Toast.LENGTH_SHORT).show();
		} else {
			m_pieceDepart = R.drawable.croix;
			m_etat_courant = Etats.TOUR_CROIX.ordinal();
			m_pieceCourante = R.drawable.croix;
			
			Toast.makeText(getApplicationContext(), m_nameJoueurCroix + " " + getResources().getString(R.string.startGame),
			Toast.LENGTH_SHORT).show();
		}
		
		//Mise en place des écouteurs pour le changement de niveau de difficulté
		if(m_soloPlayer) {
			m_normal.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					if(m_normal.isChecked()) {
						m_expert.setChecked(false);
						m_levelExpert = false;
						
						Toast.makeText(getApplicationContext(), getResources().getString(R.string.niveau) + m_normal.getText(),
						Toast.LENGTH_SHORT).show();
						
						restart();
					}
				}
			});
			
			m_expert.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					if(m_expert.isChecked()) {
						m_normal.setChecked(false);
						m_levelExpert = true;
						
						Toast.makeText(getApplicationContext(), getResources().getString(R.string.niveau) + m_expert.getText(),
						Toast.LENGTH_SHORT).show();
						
						restart();
					}
				}
			});
		} else {
			m_normal.setVisibility(View.INVISIBLE);
			m_expert.setVisibility(View.INVISIBLE);
		}
	}
	
	//Fonction permettant de recommencer une partie
	public void restart() {
		
		m_compteurCoup = 0;
		m_techniqueCentre = false;
		m_techniqueCoins = false;
		
		//RAZ des boutons de la grille de jeu
		for(int i = 0; i < m_taille_grille; i++) {
			for(int j = 0; j < m_taille_grille; j++) {
				m_buttons[i][j].setBackground(m_background_default);
				m_buttons[i][j].setClickable(true);
			}
		}
		
		//Le joueur qui n'a pas commencé à cette partie sera premier à la prochaine
		if(m_pieceDepart == R.drawable.croix){
			m_pieceDepart = R.drawable.rond;
			m_etat_courant = Etats.TOUR_ROND.ordinal();
			m_pieceCourante = R.drawable.rond;
			
			Toast.makeText(getApplicationContext(), m_nameJoueurRond + " " + getResources().getString(R.string.startGame),
			Toast.LENGTH_SHORT).show();
		} else {
			m_pieceDepart = R.drawable.croix;
			m_etat_courant = Etats.TOUR_CROIX.ordinal();
			m_pieceCourante = R.drawable.croix;
			
			Toast.makeText(getApplicationContext(), m_nameJoueurCroix + " " + getResources().getString(R.string.startGame),
			Toast.LENGTH_SHORT).show();
			
			if(m_soloPlayer) {
				appuiBouton(m_buttons[m_random.nextInt(2)][m_random.nextInt(2)]);
				
				m_etat_courant = Etats.TOUR_ROND.ordinal();
				m_pieceCourante = R.drawable.rond;
			}
		}
	}
	
	//Fonction permettant de mettre en oeuvre le mécanisme de tour de jeu
	public void machineAEtat() {
		
		switch(m_etat_courant)
		{ 
			case 0: 
				
				if(finie()) {					
					m_scoreTexteCroix.setText(String.valueOf(Integer.parseInt(m_scoreTexteCroix.getText().toString()) + 1));
					
					dialogFin(m_nameJoueurCroix + " " + getResources().getString(R.string.winText));
				}
				else if(nulle()){
					dialogFin(getResources().getString(R.string.nullText));
				}
				else {					
					m_pieceCourante = R.drawable.rond;
					m_etat_courant = Etats.TOUR_ROND.ordinal();
				}
				
				break;
			case 1: 
				
				if(finie()) {					
					m_scoreTexteRond.setText(String.valueOf(Integer.parseInt(m_scoreTexteRond.getText().toString()) + 1));
					
					dialogFin(m_nameJoueurRond + " " + getResources().getString(R.string.winText));
				}
				else if(nulle()){
					dialogFin(getResources().getString(R.string.nullText));
				}
				else {					
					if(m_soloPlayer) {
						tourIA();
					} else {
						m_pieceCourante = R.drawable.croix;
						m_etat_courant = Etats.TOUR_CROIX.ordinal();
					}
					
				}
				
				break;
		}
	}
	
	//Fonction permettant d'afficher le message de fin de partie
	public void dialogFin(String message) {
		
		AlertDialog.Builder adb_alertDialogBuilder = new AlertDialog.Builder(this);
 
		adb_alertDialogBuilder.setTitle(message);

		adb_alertDialogBuilder.setCancelable(false);
		adb_alertDialogBuilder.setPositiveButton(getResources().getString(R.string.Recommencer), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog,int id) {
				restart();
			}
		});


		AlertDialog ad_alertDialog = adb_alertDialogBuilder.create();

		ad_alertDialog.show();
	}
	
	//Fonction permettant de déterminer selon l'état de la grille de jeu si la partie a un gagnant
	public boolean finie() {
		
		int i_diagonaleGauche = 0;
		int i_diagonaleDroite = 0;
		
		for(int i = 0; i < m_taille_grille; i++) {
			
			int i_horizontal = 0;
			int i_vertical = 0;
			
			for(int j = 0; j < m_taille_grille; j++) {
				if(m_buttons[i][j].getBackground().getConstantState().equals(getResources().getDrawable(m_pieceCourante).getConstantState())) {
					i_horizontal++;
				}
				
				if(m_buttons[j][i].getBackground().getConstantState().equals(getResources().getDrawable(m_pieceCourante).getConstantState())) {
					i_vertical++;
				}
			}
			
			if(i_horizontal == m_taille_grille || i_vertical == m_taille_grille) {
				return true;
			}
			
			if(m_buttons[i][i].getBackground().getConstantState().equals(getResources().getDrawable(m_pieceCourante).getConstantState())) {
				i_diagonaleDroite++;
			}
			
			if(m_buttons[(m_taille_grille - 1) - i][i].getBackground().getConstantState().equals(getResources().getDrawable(m_pieceCourante).getConstantState())) {
				i_diagonaleGauche++;
			}
		}
		
		if(i_diagonaleDroite == m_taille_grille || i_diagonaleGauche == m_taille_grille) {			
			return true;
		}
		
		return false;
	}
	
	//Fonction permettant de savoir si la partie est nulle
	public boolean nulle() {
		
		int i_nombreTouche = 0;
		
		for(int i = 0; i < m_taille_grille; i++) {
			for(int j = 0; j < m_taille_grille; j++) {
				if(! m_buttons[i][j].isClickable()) {
					i_nombreTouche ++;
				}
			}
		}
		
		if(i_nombreTouche == m_taille_grille * m_taille_grille) {			
			return true;
		}
		
		return false;
	}
	
	//Création d'un écouteur propre au cases de la grille de jeu
	public class PieceClickListener implements View.OnClickListener {
		
		private ImageButton m_button;
		
		public PieceClickListener(ImageButton button) {
			m_button = button;
		}
		
		@Override
		public void onClick(View v) {
			if(! m_processing) {
				appuiBouton(m_button);
				machineAEtat();
			}
		}
	}
	
	//Fonction permettant de simuler d'appuyer sur un bouton de la grille de jeu
	public void appuiBouton(ImageButton b_button) {
		
		m_compteurCoup++;
		
		b_button.setBackgroundResource(m_pieceCourante);
		b_button.setClickable(false);
	}
	
	//Fonction permettant juste après le premier coup de la partie de savoir où a été joué ce coup
	public Pair<Integer, Integer> premierePieceAppuyee(int [][] tab_tabIntJeu) {
		for(int i = 0; i < 3; i++) {
			for(int j = 0; j < 3; j++) {
				if(tab_tabIntJeu[i][j] == 1) {
					return new Pair<Integer, Integer>(i, j);
				}
			}
		}
		
		return new Pair<Integer, Integer>(0, 0);
	}
	
	//Cette fonction permet prendre un coup dans une liste parmis les coups encore non joués
	public Pair<Integer, Integer> bouclePaire(ArrayList<Pair<Integer, Integer>> list_listeCoups) {
		Pair<Integer, Integer> paireBoucle;
		
		do {
			int i_position = m_random.nextInt(3);
			
			paireBoucle = new Pair<Integer, Integer>(list_listeCoups.get(i_position).first, list_listeCoups.get(i_position).second);
		} while (! m_buttons[paireBoucle.first][paireBoucle.second].isClickable());
		
		return paireBoucle;
	}
	
	//Cette fonction joue le coup de l'IA
	public void tourIA() {
		
		//La pièce courante devient celle de l'IA
		m_pieceCourante = R.drawable.croix;
		
		//On est entrain de calculer le coup de l'IA le plateau de jeu est donc bloqué pour le joueur 1
		m_processing = true;
		
		//Transformation du plateau de jeu en tableau de chiffres
		int [][] tab_tabIntJeu = new int[3][3];
		IA.calculeTabIntJeu(m_buttons, getResources().getDrawable(m_pieceCourante), tab_tabIntJeu);

		//Initialisation de la paire qui va être écrite
		m_paireIA = new Pair<Integer, Integer>(0, 0);
		
		if(m_pieceDepart == R.drawable.rond && m_levelExpert) {
			if(m_compteurCoup == 1 && premierePieceAppuyee(tab_tabIntJeu).equals(m_coup_centre)) {
				m_techniqueCentre = true;
				m_paireIA = bouclePaire(m_liste_coins);
			}
			
			if(m_compteurCoup == 1 && m_liste_coins.contains(premierePieceAppuyee(tab_tabIntJeu))) {
				m_techniqueCoins = true;
				m_paireIA = new Pair<Integer, Integer>(m_coup_centre.first, m_coup_centre.second);
			}
			
			if(m_compteurCoup == 1 && m_liste_croix_centre.contains(premierePieceAppuyee(tab_tabIntJeu))) {
				m_techniqueCentreCroix = true;
				m_paireIA = new Pair<Integer, Integer>(m_coup_centre.first, m_coup_centre.second);
			}
			
			if(m_compteurCoup == 3 && IA.coupContre(tab_tabIntJeu).first == -1 && m_techniqueCentre) {
				m_paireIA = bouclePaire(m_liste_coins);
				m_techniqueCentre = false;
			}
			
			if(m_compteurCoup == 3 && IA.coupContre(tab_tabIntJeu).first == -1 && m_techniqueCoins) {					
				m_paireIA = bouclePaire(m_liste_croix_centre);
				m_techniqueCoins = false;
			}
			
			if(m_compteurCoup == 3 && IA.coupContre(tab_tabIntJeu).first == -1 && m_techniqueCentreCroix) {					
				m_paireIA = bouclePaire(m_liste_coins);
				m_techniqueCentreCroix = false;
			}
			
			if(m_compteurCoup <= 3 && IA.coupContre(tab_tabIntJeu).first != -1) {
				m_paireIA = IA.calculeIA(this.m_buttons, getResources().getDrawable(m_pieceCourante), 9);
			}
			
			if(m_compteurCoup > 3) {
				m_paireIA = IA.calculeIA(this.m_buttons, getResources().getDrawable(m_pieceCourante), 9);
			}

		} else {
			m_paireIA = IA.calculeIA(this.m_buttons, getResources().getDrawable(m_pieceCourante), 9);
		}
		
		//Appui sur le plateau de jeu sur le bouton correspondant à la paire choisie
	    appuiBouton(m_buttons[m_paireIA.first][m_paireIA.second]);

	    //Le plateau de jeu est de nouveau accessible
		m_processing = false;
		
		if(finie()) {
			if(getResources().getDrawable(R.drawable.rond).getConstantState().equals(getResources().getDrawable(m_pieceCourante).getConstantState())) {
				m_scoreTexteRond.setText(String.valueOf(Integer.parseInt(m_scoreTexteRond.getText().toString()) + 1));
			} else {				
				m_scoreTexteCroix.setText(String.valueOf(Integer.parseInt(m_scoreTexteCroix.getText().toString()) + 1));
			}
			
			dialogFin(m_nameJoueurCroix + " " + getResources().getString(R.string.winText));
		}
		else if(nulle()){
			dialogFin(getResources().getString(R.string.nullText));
		}
		else {
			m_pieceCourante = R.drawable.rond;
		}
	}
}

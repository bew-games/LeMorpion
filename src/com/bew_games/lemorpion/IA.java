package com.bew_games.lemorpion;

import java.util.Random;

import android.graphics.drawable.Drawable;
import android.util.Pair;
import android.widget.ImageButton;

//Cette classe permet gr�ce � un algorythme minmax simplifi� de calculer quel sera le meilleur coup � jouer,
//avec la configuration actuelle des pions
public class IA {
	
	//Tableau repr�sentant le plateau de jeu en mettant le chiffre 1 pour le joueur 1,
	//le chiffre 2 pour l'IA et le chiffre 0 pour une case non jou�e
	private static int[][] m_tabIntJeu = new int[3][3];
	
	//Permet de g�n�rer des entiers de fa�on al�atoire
	private static Random m_random = new Random();
	
	//Cette m�thode permet de r�cup�rer les r�sultats pond�r�s au fur et � mesure du calcul des coups
	public static Pair<Integer, Integer> calculeIA(ImageButton [][] imgb_buttons, Drawable drw_joueur, int i_profondeur) {
		
		//Transformation du tableau de boutons en tableau de chiffres
		calculeTabIntJeu(imgb_buttons, drw_joueur, m_tabIntJeu);
		
		int i_min = 10000;
		int i_tmp;
		int i_maxi = 0;
		int i_maxj = 0;
		
		//Parcours des diff�rents coups possible de d�part
		for(int i = 0; i < 3; i++) {
			for(int j = 0; j < 3; j++) {
				//Si la case est vide
				if(m_tabIntJeu[i][j] == 0) {
					//On met un pion de l'IA
					m_tabIntJeu[i][j] = 2;
					
					//Initialisation d'un tableau secondaire pour ne pas se servir du tableau membre
					int [][] tab_tabIntJeu = new int[3][3];
					
					//Remplissage du nouveau tableau avec les valeurs du tableau membre
					for(int x = 0; x < 3; x++) {
						for(int y = 0; y < 3; y++) {
							tab_tabIntJeu[x][y] = m_tabIntJeu[x][y];
						}
					}
					
					//Calcul du prochain coup du joueur physique, le tmp correspond � la pond�ration
					i_tmp = Min(i_profondeur - 1, tab_tabIntJeu);
				    
					//Si le poids est plus bas que le pr�c�dent on le stocke � la place de l'ancien
					//Si il est �gal on choisit au hasard de le stocker ou non
					if(i_tmp < i_min || ( (i_tmp == i_min) && (m_random.nextInt() % 2 == 0))) {
						i_min = i_tmp;
						i_maxi = i;
						i_maxj = j;
					}
				    
					//On revient en arri�re sur le coup simul�
					m_tabIntJeu[i][j] = 0;
				}
			}
		}
		
		//On joue le coup dans le tableau membre afin de voir si l'on va �tre gagnant apr�s ce coup
		m_tabIntJeu[i_maxi][i_maxj] = 2;
		
		//Si l'on est pas gagnant
		if(gagnant(m_tabIntJeu) != 2) {
			
			//On enl�ve le coup simul�
			m_tabIntJeu[i_maxi][i_maxj] = 0;
			
			//Et on regarde si l'ennemi a deux pions � la suite
			Pair<Integer, Integer> pair_result = coupContre(m_tabIntJeu);
		    
			//Si c'est le cas on retourne la position pour le bloquer
			if(pair_result.first != -1) {
				return pair_result;
			}
		}
		
		//Sinon on retourne la position calcul�e initialement
		return new Pair<Integer, Integer>(i_maxi, i_maxj);
	}
	
	//Calcul du prochain coup de l'IA
	public static int Max(int i_profondeur, int[][] tab_tabIntJeu) {
		//Si l'on a parcouru les coups jusqu'� la fin de la partie ou qu'il y a un gagnant
		if(i_profondeur == 0 || gagnant(tab_tabIntJeu) != 0) {
			//On calcule le poids et on le retourne
			return eval(tab_tabIntJeu);
		}
		
		//Valeur d'initialisation du poids
		int i_max = -10000;
		
		//La partie �volue � partir de la prochaine case vide
		for(int i = 0; i < 3; i++) {
			for(int j = 0; j < 3; j++) {
				//A la premi�re case vide on simule un coup de l'IA
				if(tab_tabIntJeu[i][j] == 0) {
					tab_tabIntJeu[i][j] = 2;
					return(Min(i_profondeur-1, tab_tabIntJeu));
				}
			}
		}
		
		//Si aucun poids n'est retourn� on retourne le poids par d�faut
		return i_max;
	}
	
	//Calcul du prochain coup du joueur physique
	public static int Min(int i_profondeur, int[][] tab_tabIntJeu) {
		//Si l'on a parcouru les coups jusqu'� la fin de la partie ou qu'il y a un gagnant
		if(i_profondeur == 0 || gagnant(tab_tabIntJeu) != 0) {
			//On calcule le poids et on le retourne
			return eval(tab_tabIntJeu);
		}
		
		//Valeur d'initialisation du poids
		int i_min = 10000;
		
		//La partie �volue � partir de la prochaine case vide
		for(int i = 0; i < 3; i++) {
			for(int j = 0; j < 3; j++) {
				//A la premi�re case vide on simule un coup du joueur physique
				if(tab_tabIntJeu[i][j] == 0) {
					tab_tabIntJeu[i][j] = 1;     	  
					return (Max(i_profondeur-1, tab_tabIntJeu));
				}
			}
		}
		
		return i_min;
	}
	
	//Calcul du nombre de s�ries de pions, pour le joueur 1 et le joueur, on entre en param�tre la longueur de la s�rie
	public static Pair<Integer, Integer> nb_series(int n , int[][] tab_tabIntJeu) { 
		int i_compteur1, i_compteur2, i, j;
		
		int i_seriesJ1 = 0;
		int i_seriesJ2 = 0;
		i_compteur1 = 0;
		i_compteur2 = 0;
		   
		//Diagonale descendante
		for(i = 0; i < 3; i++) {
			if(tab_tabIntJeu[i][i] == 1) {
				i_compteur1++;
				i_compteur2 = 0;
				           
				if(i_compteur1 == n)  {
					i_seriesJ1++;
				}
			} else if(tab_tabIntJeu[i][i] == 2) {
				i_compteur2++;
				i_compteur1 = 0;
			  
				if(i_compteur2 == n) {
					i_seriesJ2++;
				}
			}
		}
		
		i_compteur1 = 0;
		i_compteur2 = 0;
		 
		//Diagonale montante
		for(i = 0; i < 3; i++) {
			if(tab_tabIntJeu[i][2 - i] == 1) {
				i_compteur1++;
				i_compteur2 = 0;
				
				if(i_compteur1 == n) {
					i_seriesJ1++;
				}
			} else if(tab_tabIntJeu[i][2-i] == 2) {
				i_compteur2++;
				i_compteur1 = 0;
				  
				if(i_compteur2 == n) {
					i_seriesJ2++;
				}
			}
		}
		 
		//En ligne
		for(i = 0; i < 3; i++) {
			i_compteur1 = 0;
			i_compteur2 = 0;
			
			//Horizontalement
			for(j = 0; j < 3; j++) {
				if(tab_tabIntJeu[i][j] == 1) {
					i_compteur1++;
					i_compteur2 = 0;
					
					if(i_compteur1 == n) {
						i_seriesJ1++;
					}
				} else if(tab_tabIntJeu[i][j] == 2) {
					i_compteur2++;
					i_compteur1 = 0;
					
					if(i_compteur2 == n) {
						i_seriesJ2++;
					}
				}
			}
			
			i_compteur1 = 0;
			i_compteur2 = 0;
			  
			//Verticalement
			for(j = 0; j < 3; j++) {
				if(tab_tabIntJeu[j][i] == 1) {
					i_compteur1++;
					i_compteur2 = 0;
					
					if(i_compteur1 == n) {
						i_seriesJ1++;
					}
				} else if(tab_tabIntJeu[j][i] == 2) {
					i_compteur2++;
					i_compteur1 = 0;
					
					if(i_compteur2 == n) {
						i_seriesJ2++;
					}
				}
			}
		}
		
		return new Pair<Integer, Integer>(i_seriesJ1, i_seriesJ2);
	}
	
	//Retourne le premier coup trouv� que l'IA doit jouer pour emp�cher une ligne avec deux pions ennemis de se remplir
	public static Pair<Integer, Integer> coupContre(int[][] tab_tabIntJeu) { 
		int i_compteur = 0;
		int i, j;
		
		//Diagonale descendante
		for(i = 0; i < 3; i++) {
			if(tab_tabIntJeu[i][i] == 1) {
				i_compteur++;
			}
		}
		
		if(i_compteur == 2) {
			for(i = 0; i < 3; i++) {
				if(tab_tabIntJeu[i][i] == 0) {
					return new Pair<Integer, Integer>(i, i);
				}
			}
		}
		
		i_compteur = 0;
		 
		//Diagonale montante
		for(i = 0; i < 3; i++) {
			if(tab_tabIntJeu[i][2 - i] == 1) {
				i_compteur++;
			}
		}
		
		if(i_compteur == 2) {
			for(i = 0; i < 3; i++) {
				if(tab_tabIntJeu[i][2 - i] == 0) {
					return new Pair<Integer, Integer>(i, 2 - i);
				}
			}
		}
		 
		//En ligne
		for(i = 0; i < 3; i++) {
			i_compteur = 0;
			
			//Horizontalement
			for(j = 0; j < 3; j++) {
				if(tab_tabIntJeu[i][j] == 1) {
					i_compteur++;
				}
			}
			
			if(i_compteur == 2) {
				for(j = 0; j < 3; j++) {
					if(tab_tabIntJeu[i][j] == 0) {
						return new Pair<Integer, Integer>(i, j);
					}
				}
			}
			
			i_compteur = 0;
			
			//Verticalement
			for(j = 0; j < 3; j++) {
				if(tab_tabIntJeu[j][i] == 1) {
					i_compteur++;
				}
			}
			
			if(i_compteur == 2) {
				for(j = 0; j < 3; j++) {
					if(tab_tabIntJeu[j][i] == 0) {
						return new Pair<Integer, Integer>(j, i);
					}
				}
			}
		}
		
		return new Pair<Integer, Integer>(-1, -1);
	}
	
	//Calcule le poids des coups jou�s en fonction du nombre de coups pour arriver � ce point 
	//et est ce que la partie est nulle, gagn�e ou perdue
	public static int eval(int[][] tab_tabIntJeu)
	{
		int i_vainqueur,i_nb_de_pions = 0;
		
		//On compte le nombre de pions pr�sents sur le plateau
		for(int i = 0; i < 3; i++) {
			for(int j = 0; j < 3; j++) {
				if(tab_tabIntJeu[i][j] != 0) {
					i_nb_de_pions++;
				}
			}
		}
		
		i_vainqueur = gagnant(tab_tabIntJeu);
		 
		if(i_vainqueur != 0) {
			if( i_vainqueur == 1 ) {
				return 1000 - i_nb_de_pions;
			} else if( i_vainqueur == 2 ) {
				return -1000 + i_nb_de_pions;
			} else {
				return 0;
			}
		}
		
		//On compte le nombre de s�ries de 2 pions align�s de chacun des joueurs
		Pair<Integer, Integer> paireSeries = nb_series(2, tab_tabIntJeu);
		
		return paireSeries.first - paireSeries.second;
	}
	
	//D�termine si un joueur est gagnant et retourne lequel
	public static int gagnant(int [][] tab_tabIntJeu)
	{
		int i,j;
		
		Pair<Integer, Integer> paireSeries = nb_series(3, tab_tabIntJeu);
		
		int i_seriesJ1 = paireSeries.first;
		int i_seriesJ2 = paireSeries.second;
		
		if(i_seriesJ1 != 0) {
			return 1;
		} else if(i_seriesJ2 != 0) {
			return 2;
		} else {
			//Si le jeu n'est pas fini et que personne n'a gagn�, on renvoie 0
			for(i = 0; i < 3; i++) {
				for(j = 0; j < 3; j++) {
					if(tab_tabIntJeu[i][j] == 0) {
						return 0;
					}
				}
			}
		}
		
		//Si le jeu est fini et que personne n'a gagn�, on renvoie 3
		return 3;
	}
	
	//Transformation du tableau de boutons en tableau de chiffres
	public static void calculeTabIntJeu(ImageButton [][] imgb_buttons, Drawable drw_joueur, int[][] tab_tabIntJeu) {
		for(int i = 0; i < 3; i++) {
			for(int j = 0; j < 3; j++) {
				if(imgb_buttons[i][j].getBackground().getConstantState().equals(drw_joueur.getConstantState())) {
					tab_tabIntJeu[i][j] = 2;
				} else {
					if(imgb_buttons[i][j].isClickable()) {
						tab_tabIntJeu[i][j] = 0;
					} else {
						tab_tabIntJeu[i][j] = 1;
					}
				}
			}
		}
	}
}

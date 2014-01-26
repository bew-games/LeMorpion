package com.bew_games.lemorpion;

public enum Etats {
	
	TOUR_CROIX(0),
	TOUR_ROND(1);

	public int m_number;
	    
	//Constructeur
	Etats(int number){
		m_number = number;
	}
}

package Analaser;

import java.io.IOException;

import Database.addToDatabase;
import Logger.logMaker;
import Structure.footballMatch;

public class Analyser {
	
	addToDatabase db = new addToDatabase();
	
	public void init() throws IOException {
		db.initConnection();
		
		CalculateMatchesResults();
		
		
		db.closeConnection();
	}

	private void CalculateMatchesResults() {
	//1. Pobranie zbliżającego się meczu (drużyna A przeciw drużynie B)
		footballMatch currentMatch = null;
	//2. Wyzerowanie współczynnika skuteczności drużyny A i B.
		Double efficiencyA = 0.0;
		Double efficiencyB = 0.0;
	//3. Korekcja współczynników skuteczności względem ostatnio rozegranych meczów (czy były wygrane, czy przegrane i z jaką przewagą)
		
	//4. Korekcja współczynników skuteczności na podstawie wiadomości czy dane drużyny lepiej grają na wyjazdach czy u siebie.
	
	//5. Korekcja współczynników na podstawie kursów bukmacherów.	
		
	//6. Dowolne możliwe korekcje na podstawie posiadanych danych.
		
	//7. Oznaczenie, że analiza dla podanego meczu została wykonana.

	}

}

package Analaser;

import java.io.IOException;
import java.util.List;

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
		// Pobranie wszystkich nie policzonych meczy
		List<footballMatch> matches = db.getAllNewIncommingMatches();
		for (int i=0;i<matches.size();i++){
			CalculateMatchResults(matches.get(i));	
		}
	}

	//1. Pobranie zbliżającego się meczu (drużyna A przeciw drużynie B)
	private void CalculateMatchResults(footballMatch currentMatch) {
		
		String TeamA = currentMatch.returnTeamA();
		String TeamB = currentMatch.returnTeamB();
		
	//2. Wyzerowanie współczynnika skuteczności drużyny A i B.
		Double efficiencyA = 0.0;
		Double efficiencyB = 0.0;
		
		//TODO 1 
		/*Należy Dla drużyny A i B pobrać wszystkie mecze z Matches_Results*/
		// Rozegrane mecze Teamu A bez meczów z Teamem B
		List<footballMatch> TeamAWitoutTeamBMatchesResults = db.getMatchesResultsFromDatabase(TeamA, TeamB, false);
		// Rozegrane mecze Teamu B bez meczów z Teamem A
		List<footballMatch> TeamBWithoutTeamAMatchesResults = db.getMatchesResultsFromDatabase(TeamB, TeamA, false);
		// Rozegrane mecze Teamu A przeciwko Teamu B (Drużyna A grała u siebie)
		List<footballMatch> TeamAAndTeamBMatchesResults = db.getMatchesResultsFromDatabase(TeamA, TeamB, true);
		// Rozegrane mecze Teamu B przeciwko Teamu A (Drużyna B grała u siebie)
		List<footballMatch> TeamBAndTeamAMatchesResults = db.getMatchesResultsFromDatabase(TeamB, TeamA, true);
	//3. Korekcja współczynników skuteczności względem ostatnio rozegranych meczów (czy były wygrane, czy przegrane i z jaką przewagą)
	//4. Korekcja współczynników skuteczności na podstawie wiadomości czy dane drużyny lepiej grają na wyjazdach czy u siebie.
	
	//5. Korekcja współczynników na podstawie kursów bukmacherów.	
		
	//6. Dowolne możliwe korekcje na podstawie posiadanych danych.
		
	//7. Oznaczenie, że analiza dla podanego meczu została wykonana.

	}

}

package Analaser;

import java.io.IOException;
import java.util.List;

import Database.DatabaseManager;
import Logger.LogMaker;
import Structure.FootballMatch;

public class Analyser {
	
	DatabaseManager db = new DatabaseManager();
	
	public void init() throws IOException {
		db.initConnection();
		
		CalculateMatchesResults();
		
		
		db.closeConnection();
	}
	
	private void CalculateMatchesResults() {
		// Pobranie wszystkich nie policzonych meczy
		List<FootballMatch> matches = db.getAllNewIncommingMatches();
		for (int i=0;i<matches.size();i++){
			FootballMatch calculatedMatch = CalculateMatchResults(matches.get(i));	
		}
	}

	//1. Pobranie zbliżającego się meczu (drużyna A przeciw drużynie B)
	private FootballMatch CalculateMatchResults(FootballMatch currentMatch){
		
		int TeamAID = currentMatch.returnTeamA();
		int TeamBID = currentMatch.returnTeamB();
		
	//2. Wyzerowanie współczynnika skuteczności drużyny A i B.
		double efficiencyA = 0.0;
		double efficiencyB = 0.0;
		
		/*Należy Dla drużyny A i B pobrać wszystkie mecze z Matches_Results*/
		// Rozegrane mecze Teamu A bez meczów z Teamem B
		List<FootballMatch> TeamAWitoutTeamBMatchesResults = db.getMatchesResultsFromDatabase(TeamAID, TeamBID, false);
		// Rozegrane mecze Teamu B bez meczów z Teamem A
		List<FootballMatch> TeamBWithoutTeamAMatchesResults = db.getMatchesResultsFromDatabase(TeamBID, TeamAID, false);
		// Rozegrane mecze Teamu A przeciwko Teamu B (Drużyna A grała u siebie)
		List<FootballMatch> TeamAAndTeamBMatchesResults = db.getMatchesResultsFromDatabase(TeamAID, TeamBID, true);
		// Rozegrane mecze Teamu B przeciwko Teamu A (Drużyna B grała u siebie)
		List<FootballMatch> TeamBAndTeamAMatchesResults = db.getMatchesResultsFromDatabase(TeamBID, TeamAID, true);
		
	//Korekcje 
	/*
		Założenia : 
			Kolejność korekcje zostały obrane według priorytetów. (Najważniejsza ma niższy numer)
			Wygrana w meczu jest ważniejsza niż liczba zdobytych goli.
	 */
		
	//3. Korekcja współczynników skuteczności względem ostatnio rozegranych meczów między sobą (czy były wygrane, czy przegrane i z jaką przewagą)	
		//Umożliwienie konfiguralności
		double pointsForWin = 4;
		double pointsForDraw = 0;
		double pointsForLose = -4;
		double pointsForGoal = 1;
		
		// Draw default
		int scoreA = 0;
		int scoreB = 0;
		
		int howManyWinsTeamA = 0;
		int howManyDrawsTeamA = 0;
		int howManyLoosesTeamA = 0;
		int howManyWinsTeamB = 0;
		int howManyDrawsTeamB = 0;
		int howManyLoosesTeamB = 0;
		
		for (int i=0;i<TeamAAndTeamBMatchesResults.size();i++){
			scoreA = TeamAAndTeamBMatchesResults.get(i).returnScoreA();
			scoreB = TeamAAndTeamBMatchesResults.get(i).returnScoreB();
			if (scoreA > scoreB){
				efficiencyA += pointsForWin;
				efficiencyB += pointsForLose;
			}
			else if (scoreA < scoreB){
				efficiencyB += pointsForWin;
				efficiencyA += pointsForLose;
			}
			else{
				efficiencyA += pointsForDraw;
				efficiencyB += pointsForDraw;
			}
			
			efficiencyA += pointsForGoal*scoreA;
			efficiencyB += pointsForGoal*scoreB;
		}
		
		for (int i=0;i<TeamBAndTeamAMatchesResults.size();i++){
			scoreA = TeamBAndTeamAMatchesResults.get(i).returnScoreA();
			scoreB = TeamBAndTeamAMatchesResults.get(i).returnScoreB();
			if (scoreA > scoreB){
				efficiencyA += pointsForWin;
				efficiencyB += pointsForLose;
			}
			else if (scoreA < scoreB){
				efficiencyB += pointsForWin;
				efficiencyA += pointsForLose;
			}
			else{
				efficiencyA += pointsForDraw;
				efficiencyB += pointsForDraw;
			}
			
			efficiencyA += pointsForGoal*scoreA;
			efficiencyB += pointsForGoal*scoreB;
		}
		
	//4. Korekcja współczynników skuteczności względem ostatnio rozegranych meczów z innymi drużynami(czy były wygrane, czy przegrane i z jaką przewagą)
		for (int i=0;i<TeamAWitoutTeamBMatchesResults.size();i++){
			scoreA = TeamAWitoutTeamBMatchesResults.get(i).returnScoreA();
			scoreB = TeamAWitoutTeamBMatchesResults.get(i).returnScoreB();
			if (scoreA > scoreB){
				efficiencyA += pointsForWin;
				efficiencyB += pointsForLose;
			}
			else if (scoreA < scoreB){
				efficiencyB += pointsForWin;
				efficiencyA += pointsForLose;
			}
			else{
				efficiencyA += pointsForDraw;
				efficiencyB += pointsForDraw;
			}
			
			efficiencyA += pointsForGoal*scoreA;
			efficiencyB += pointsForGoal*scoreB;
		}
		
		for (int i=0;i<TeamBWithoutTeamAMatchesResults.size();i++){
			scoreA = TeamBWithoutTeamAMatchesResults.get(i).returnScoreA();
			scoreB = TeamBWithoutTeamAMatchesResults.get(i).returnScoreB();
			if (scoreA > scoreB){
				efficiencyA += pointsForWin;
				efficiencyB += pointsForLose;
			}
			else if (scoreA < scoreB){
				efficiencyB += pointsForWin;
				efficiencyA += pointsForLose;
			}
			else{
				efficiencyA += pointsForDraw;
				efficiencyB += pointsForDraw;
			}
			
			efficiencyA += pointsForGoal*scoreA;
			efficiencyB += pointsForGoal*scoreB;
		}
		
	//5. Korekcja współczynników skuteczności na podstawie wiadomości czy dane drużyny lepiej grają na wyjazdach czy u siebie.
		
	//6. Korekcja współczynników na podstawie kursów bukmacherów.	
		
	//7. Dowolne możliwe korekcje na podstawie posiadanych danych.
		double sum = 0;
		double winA = 0;
		double draw = 100;
		double winB = 0;
		
		double efficiencyDifference = Math.abs(efficiencyA - efficiencyB);
		if (efficiencyDifference > 0){
			double howManyTimesIsefficiencyisGreater = efficiencyA/efficiencyB;
			winA = 1/3 * howManyTimesIsefficiencyisGreater;
			if (howManyTimesIsefficiencyisGreater > 0){
				draw = draw / howManyTimesIsefficiencyisGreater;
			}
			else{
				draw = draw * howManyTimesIsefficiencyisGreater;
			}
			winB = 1/3 * howManyTimesIsefficiencyisGreater;
		}
		
		/*double sum = efficiencyA + efficiencyDifference + efficiencyB;
		double winA = 0 + (efficiencyA - efficiencyB);
		double draw = 100 - efficiencyDifference;
		double winB = 0 + efficiencyB - efficiencyA;
		
		FootballMatch calculatedMatch = new FootballMatch(currentMatch.returnDate(), TeamAID , TeamBID, efficiencyA, efficiencyB, currentMatch.returnLeague());
		return calculatedMatch;*/
		return null;
	}
	
	double checkIfWinDrawOrLose(int scoreA, int scoreB){
		if (scoreA > scoreB){
			return 1;
		}
		else if (scoreA < scoreB){
			return -1;
		}
		return 0;
	}
}

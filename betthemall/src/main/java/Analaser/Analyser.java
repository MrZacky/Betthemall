package Analaser;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

import Database.DatabaseManager;
import Logger.LogMaker;
import Structure.FootballMatch;

public class Analyser {

	DatabaseManager db = new DatabaseManager();

	public void init(boolean addToDatabase) throws IOException {
		db.initConnection();

		CalculateMatchesResults(addToDatabase);

		db.closeConnection();
	}

	public void init(int k, boolean addToDatabase) throws IOException {
		db.initConnection();

		CalculateMatchesResultsForKMatches(k, addToDatabase);

		db.closeConnection();
	}
	
	public void init(String dateFrom, String dateTo, boolean addToDatabase) throws IOException {
		db.initConnection();

		CalculateMatchesResultsByPeriod(dateFrom, dateTo, addToDatabase);

		db.closeConnection();
	}

	public void CalculateMatchesResultsForKMatches(int k, boolean addToDatabase) {
		// Pobranie wszystkich nie policzonych meczy
		int iterations = k;

		List<FootballMatch> matches = db.getAllNewIncommingMatches();

		if (matches.size() < iterations) {
			iterations = matches.size();
		}

		for (int i = 0; i < iterations; i++) {
			FootballMatch calculatedMatch = CalculateMatchResults(matches.get(i));
			if (addToDatabase){
				db.addFinalMatchResultToDatabase(calculatedMatch);
			}
		}
	}
	
	private void CalculateMatchesResultsByPeriod(String dateFrom, String dateTo, boolean addToDatabase) {
		// Pobranie wszystkich nie policzonych meczy
		List<FootballMatch> matches = db.getAllNewIncommingMatchesByPeriod(dateFrom, dateTo);
		for (int i = 0; i < matches.size(); i++) {
			FootballMatch calculatedMatch = CalculateMatchResults(matches.get(i));
			if (addToDatabase){
				db.addFinalMatchResultToDatabase(calculatedMatch);
			}
		}
	}

	private void CalculateMatchesResults(boolean addToDatabase) {
		// Pobranie wszystkich nie policzonych meczy
		List<FootballMatch> matches = db.getAllNewIncommingMatches();
		for (int i = 0; i < matches.size(); i++) {
			FootballMatch calculatedMatch = CalculateMatchResults(matches.get(i));
			if (addToDatabase){
				db.addFinalMatchResultToDatabase(calculatedMatch);
			}
		}
	}

	// 1. Pobranie zbliżającego się meczu (drużyna A przeciw drużynie B)
	public FootballMatch CalculateMatchResults(FootballMatch currentMatch) {

		int TeamAID = db.getOriginalTeamIDByID(currentMatch.returnTeamA());
		int TeamBID = db.getOriginalTeamIDByID(currentMatch.returnTeamB());

		System.out.println("Match date : "+currentMatch.returnDate()+" "+db.getTeamNameByID(TeamAID) + " vs " + db.getTeamNameByID(TeamBID));

		// 2. Wyzerowanie współczynnika skuteczności drużyny A i B.
		double efficiencyA = 0.0;
		double efficiencyB = 0.0;

		/* Należy Dla drużyny A i B pobrać wszystkie mecze z Matches_Results */
		// Rozegrane mecze Teamu A w domu przeciwko Teamu B (Drużyna A grała u siebie)
		List<FootballMatch> TeamAAndTeamBMatchesResults = db.getMatchesResultsFromDatabase(TeamAID, TeamBID, 1);
		// Rozegrane mecze Teamu B w domu przeciwko Teamu A (Drużyna B grała u siebie)
		List<FootballMatch> TeamBAndTeamAMatchesResults = db.getMatchesResultsFromDatabase(TeamBID, TeamAID, 1);
		// Rozegrane mecze Teamu A w domu bez meczów z Teamem B
		List<FootballMatch> TeamAInHomeWitoutTeamBMatchesResults = db.getMatchesResultsFromDatabase(TeamAID, TeamBID, 2);
		// Rozegrane mecze Teamu B w domu bez meczów z Teamem A
		List<FootballMatch> TeamBInHomeWithoutTeamAMatchesResults = db.getMatchesResultsFromDatabase(TeamBID, TeamAID, 2);
		// Rozegrane mecze Teamu A na wyjeździe bez meczów z Teamem B
		List<FootballMatch> TeamAAsGuestWitoutTeamBMatchesResults = db.getMatchesResultsFromDatabase(TeamAID, TeamBID, 3);
		// Rozegrane mecze Teamu B na wyjeździe bez meczów z Teamem A
		List<FootballMatch> TeamBAsGuestWithoutTeamAMatchesResults = db.getMatchesResultsFromDatabase(TeamBID, TeamAID, 3);

		// Korekcje
		/*
		 * Założenia : Kolejność korekcje zostały obrane według priorytetów.
		 * (Najważniejsza ma niższy numer) Wygrana w meczu jest ważniejsza niż
		 * liczba zdobytych goli.
		 */

		// 3. Korekcja współczynników skuteczności względem ostatnio rozegranych
		// meczów między sobą (czy były wygrane, czy przegrane i z jaką
		// przewagą)
		// Umożliwienie konfiguralności
		double pointsForWin = 3;
		double pointsForDraw = 1.5;
		double pointsForLose = -3;
		double pointsForGoal = 0.5;

		double winAsHomeA = 0;
		double loseAsHomeA = 0;
		double winAsGuestA = 0;
		double loseAsGuestA = 0;

		double winAsHomeB = 0;
		double loseAsHomeB = 0;
		double winAsGuestB = 0;
		double loseAsGuestB = 0;
		

		// Draw default
		int scoreA = 0;
		int scoreB = 0;

		
		int dicreaser = 0;
		
		for (int i = 0; i < TeamAAndTeamBMatchesResults.size(); i++) {
			scoreA = TeamAAndTeamBMatchesResults.get(i).returnScoreA();
			scoreB = TeamAAndTeamBMatchesResults.get(i).returnScoreB();
			if (i % 3 == 0){
				dicreaser++;
			}
			if (scoreA > scoreB) {
				efficiencyA += pointsForWin * (1/dicreaser);
				efficiencyB += pointsForLose * (1/dicreaser);
				winAsHomeA++;
				loseAsGuestB++;
			} else if (scoreA < scoreB) {
				efficiencyB += pointsForWin * (1/dicreaser);
				efficiencyA += pointsForLose * (1/dicreaser);
				loseAsHomeA++;
				winAsGuestB++;
			} else {
				efficiencyA += pointsForDraw * (1/dicreaser);
				efficiencyB += pointsForDraw * (1/dicreaser);
			}

			efficiencyA += pointsForGoal * scoreA;
			efficiencyB += pointsForGoal * scoreB;
		}
		
		dicreaser = 0;

		for (int i = 0; i < TeamBAndTeamAMatchesResults.size(); i++) {
			scoreA = TeamBAndTeamAMatchesResults.get(i).returnScoreB();
			scoreB = TeamBAndTeamAMatchesResults.get(i).returnScoreA();
			if (i % 3 == 0){
				dicreaser++;
			}
			if (scoreA > scoreB) {
				efficiencyA += pointsForWin * (1/dicreaser);
				efficiencyB += pointsForLose * (1/dicreaser);
				winAsGuestA++;
				loseAsHomeB++;
			} else if (scoreA < scoreB) {
				efficiencyB += pointsForWin * (1/dicreaser);
				efficiencyA += pointsForLose * (1/dicreaser);
				loseAsGuestA++;
				winAsHomeB++;
			} else {
				efficiencyA += pointsForDraw * (1/dicreaser);
				efficiencyB += pointsForDraw * (1/dicreaser);
			}

			efficiencyA += pointsForGoal * scoreA * (1/dicreaser);
			efficiencyB += pointsForGoal * scoreB * (1/dicreaser);
		}

		// 4. Korekcja współczynników skuteczności względem ostatnio rozegranych
		// meczów z innymi drużynami(czy były wygrane, czy przegrane i z jaką
		// przewagą)
		
		// TeamA w domu
		
		dicreaser = 0;
		
		for (int i = 0; i < TeamAInHomeWitoutTeamBMatchesResults.size(); i++) {
			scoreA = TeamAInHomeWitoutTeamBMatchesResults.get(i).returnScoreA();
			scoreB = TeamAInHomeWitoutTeamBMatchesResults.get(i).returnScoreB();
			if (i % 3 == 0){
				dicreaser++;
			}
			if (scoreA > scoreB) {
				efficiencyA += pointsForWin * (1/dicreaser);
				winAsHomeA++;
			} else if (scoreA < scoreB) {
				efficiencyA += pointsForLose * (1/dicreaser);
				loseAsHomeA++;
			} else {
				efficiencyA += pointsForDraw * (1/dicreaser);
			}

			efficiencyA += pointsForGoal * scoreA * (1/dicreaser);
		}
		
		// TeamB w domu
		
		dicreaser = 0;

		for (int i = 0; i < TeamBInHomeWithoutTeamAMatchesResults.size(); i++) {
			scoreA = TeamBInHomeWithoutTeamAMatchesResults.get(i).returnScoreB();
			scoreB = TeamBInHomeWithoutTeamAMatchesResults.get(i).returnScoreA();
			if (i % 3 == 0){
				dicreaser++;
			}
			if (scoreA > scoreB) {
				efficiencyB += pointsForLose * (1/dicreaser);
				loseAsHomeB++;
			} else if (scoreA < scoreB) {
				efficiencyB += pointsForWin * (1/dicreaser);
				winAsHomeB++;
			} else {
				efficiencyB += pointsForDraw * (1/dicreaser);
			}
			
			efficiencyB += pointsForGoal * scoreB * (1/dicreaser);
		}	
		
		// TeamA na wyjeździe
		
		dicreaser = 0;
		
		for (int i = 0; i < TeamAAsGuestWitoutTeamBMatchesResults.size(); i++) {
			scoreA = TeamAAsGuestWitoutTeamBMatchesResults.get(i).returnScoreB();
			scoreB = TeamAAsGuestWitoutTeamBMatchesResults.get(i).returnScoreA();
			if (i % 3 == 0){
				dicreaser++;
			}
			if (scoreA > scoreB) {
				efficiencyA += pointsForWin * (1/dicreaser);
				winAsGuestA++;
			} else if (scoreA < scoreB) {
				efficiencyA += pointsForLose * (1/dicreaser);
				loseAsGuestA++;
			} else {
				efficiencyA += pointsForDraw * (1/dicreaser);
			}

			efficiencyA += pointsForGoal * scoreA * (1/dicreaser);
		}

		// TeamB na wyjeździe
		
		dicreaser = 0;
		
		for (int i = 0; i < TeamBAsGuestWithoutTeamAMatchesResults.size(); i++) {
			scoreA = TeamBAsGuestWithoutTeamAMatchesResults.get(i).returnScoreA();
			scoreB = TeamBAsGuestWithoutTeamAMatchesResults.get(i).returnScoreB();
			if (i % 3 == 0){
				dicreaser++;
			}
			if (scoreA > scoreB) {
				loseAsGuestB++;
			} else if (scoreA < scoreB) {
				efficiencyB += pointsForWin * (1/dicreaser);
				winAsGuestB++;
			} else {
				efficiencyB += pointsForDraw * (1/dicreaser);
			}

			efficiencyB += pointsForGoal * scoreB * (1/dicreaser);
		}
		// 5. Korekcja współczynników skuteczności na podstawie wiadomości czy
		// dane drużyny lepiej grają na wyjazdach czy u siebie.
		// Można wykorzystystać pętle z Korekcji 4.
		// Sprawdzamy jak dobrze drużyna A gra u siebie i jak drużyna B na
		// wyjeździe.
		// Drużyna A
		double homePlayQualityA = winAsHomeA / loseAsHomeA;
		double guestPlayQualityA = winAsGuestA / loseAsGuestA;

		// Sprawdzamy czy TeamA lepiej gra u siebie niż na wyjeździe
		double homeToGuestPlayQualityA = homePlayQualityA / guestPlayQualityA;

		// Drużyna B
		double homePlayQualityB = winAsHomeB / loseAsHomeB;
		double guestPlayQualityB = winAsGuestB / loseAsGuestB;

		// Sprawdzamy czy TeamB lepiej gra na wyjeździe niż u siebie
		double guestToHomePlayQualityB = guestPlayQualityB / homePlayQualityB;

		// 6. Korekcja współczynników na podstawie kursów bukmacherów.

		// 7. Wyliczanie potencjalnej wygranej, remisu lub przegranej Teamu A
		// przeciwko Teamu B

		// efficiency should be > 0, but in my case some proparties can
		// decrease efficiency,
		// so we have to add max(abs(efficiencyA),abs(efficiencyB)) to
		// efficiencyA and efficiencyB
		if (efficiencyA < 0 || efficiencyB < 0) {
			double maxEfficinecyAB = Math.max(Math.abs(efficiencyA), Math.abs(efficiencyB));
			efficiencyA += maxEfficinecyAB+1;
			efficiencyB += maxEfficinecyAB+1;
		}

		double winA = 0;
		double draw = 0;
		double winB = 0;

		//double howManyTimesIsEfficiencyGreater = 0;

		// Tym mniejszy efficiencyDifference tym remis jest bardziej
		// prawdopodobny.
		double efficiencyDifference = Math.abs(efficiencyA - efficiencyB) + 1;

		// Plus 1, because we wan't to avoid x/0, if efficiencyDifference = 0 =>
		// drawEfficiency = 1 (100%);
		double drawEfficiency = (efficiencyA + efficiencyDifference + efficiencyB + 1)
				/ (efficiencyA + efficiencyB + 1);

		//double sum1 = efficiencyA  + efficiencyB;

		//efficiencyA/=sum1;
		//efficiencyB/=sum1;
		
		double sum = efficiencyA + efficiencyDifference + efficiencyB;
		efficiencyDifference = sum / Math.pow(drawEfficiency, 2);
		double sum2 = efficiencyA + efficiencyDifference + efficiencyB;
		
		winA = efficiencyA / sum2 * 100;
		draw = efficiencyDifference / sum2 * 100;
		winB = efficiencyB / sum2 * 100;
		
		
		//winA = new BigDecimal(winA).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
		//draw = new BigDecimal(draw).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
		//winB = new BigDecimal(winB).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
		//winA = Double.

		System.out.println("efficiencyA = " + efficiencyA);
		System.out.println("efficiencyB = " + efficiencyB);
		System.out.println("efficiencyDifference = " + efficiencyDifference);
		System.out.println("winA = " + winA + "%");
		System.out.println("draw = " + draw + "%");
		System.out.println("winB = " + winB + "%");

		FootballMatch calculatedMatch = new FootballMatch(currentMatch.returnDate(), TeamAID , TeamBID,
											efficiencyA, efficiencyDifference, efficiencyB, currentMatch.returnLeague()); 
		return calculatedMatch;
	}

	double checkIfWinDrawOrLose(int scoreA, int scoreB) {
		if (scoreA > scoreB) {
			return 1;
		} else if (scoreA < scoreB) {
			return -1;
		}
		return 0;
	}
}

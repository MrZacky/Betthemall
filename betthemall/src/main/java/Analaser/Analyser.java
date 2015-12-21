package Analaser;

import java.io.IOException;
import java.math.BigDecimal;
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

	public void init(int k) throws IOException {
		db.initConnection();

		CalculateMatchesResultsForKMatches(k);

		db.closeConnection();
	}

	public void CalculateMatchesResultsForKMatches(int k) {
		// Pobranie wszystkich nie policzonych meczy
		int iterations = k;

		List<FootballMatch> matches = db.getAllNewIncommingMatches();

		if (matches.size() < iterations) {
			iterations = matches.size();
		}

		for (int i = 0; i < iterations; i++) {
			FootballMatch calculatedMatch = CalculateMatchResults(matches.get(i));
		}
	}

	private void CalculateMatchesResults() {
		// Pobranie wszystkich nie policzonych meczy
		List<FootballMatch> matches = db.getAllNewIncommingMatches();
		for (int i = 0; i < matches.size(); i++) {
			FootballMatch calculatedMatch = CalculateMatchResults(matches.get(i));
		}
	}

	// 1. Pobranie zbliżającego się meczu (drużyna A przeciw drużynie B)
	public FootballMatch CalculateMatchResults(FootballMatch currentMatch) {

		int TeamAID = currentMatch.returnTeamA();
		int TeamBID = currentMatch.returnTeamB();

		System.out.println("Match date : "+currentMatch.returnDate()+" "+db.getTeamNameByID(TeamAID) + " againts " + db.getTeamNameByID(TeamBID));

		// 2. Wyzerowanie współczynnika skuteczności drużyny A i B.
		double efficiencyA = 0.0;
		double efficiencyB = 0.0;

		/* Należy Dla drużyny A i B pobrać wszystkie mecze z Matches_Results */
		// Rozegrane mecze Teamu A bez meczów z Teamem B
		List<FootballMatch> TeamAWitoutTeamBMatchesResults = db.getMatchesResultsFromDatabase(TeamAID, TeamBID, false);
		// Rozegrane mecze Teamu B bez meczów z Teamem A
		List<FootballMatch> TeamBWithoutTeamAMatchesResults = db.getMatchesResultsFromDatabase(TeamBID, TeamAID, false);
		// Rozegrane mecze Teamu A przeciwko Teamu B (Drużyna A grała u siebie)
		List<FootballMatch> TeamAAndTeamBMatchesResults = db.getMatchesResultsFromDatabase(TeamAID, TeamBID, true);
		// Rozegrane mecze Teamu B przeciwko Teamu A (Drużyna B grała u siebie)
		List<FootballMatch> TeamBAndTeamAMatchesResults = db.getMatchesResultsFromDatabase(TeamBID, TeamAID, true);

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
		double pointsForWin = 4;
		double pointsForDraw = 0;
		double pointsForLose = -4;
		double pointsForGoal = 1;

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

		for (int i = 0; i < TeamAAndTeamBMatchesResults.size(); i++) {
			scoreA = TeamAAndTeamBMatchesResults.get(i).returnScoreA();
			scoreB = TeamAAndTeamBMatchesResults.get(i).returnScoreB();
			if (scoreA > scoreB) {
				efficiencyA += pointsForWin;
				efficiencyB += pointsForLose;
				winAsHomeA++;
				loseAsGuestB++;
			} else if (scoreA < scoreB) {
				efficiencyB += pointsForWin;
				efficiencyA += pointsForLose;
				loseAsHomeA++;
				winAsGuestB++;
			} else {
				efficiencyA += pointsForDraw;
				efficiencyB += pointsForDraw;
			}

			efficiencyA += pointsForGoal * scoreA;
			efficiencyB += pointsForGoal * scoreB;
		}

		for (int i = 0; i < TeamBAndTeamAMatchesResults.size(); i++) {
			scoreA = TeamBAndTeamAMatchesResults.get(i).returnScoreB();
			scoreB = TeamBAndTeamAMatchesResults.get(i).returnScoreA();
			if (scoreA > scoreB) {
				efficiencyA += pointsForWin;
				efficiencyB += pointsForLose;
				winAsGuestA++;
				loseAsHomeB++;
			} else if (scoreA < scoreB) {
				efficiencyB += pointsForWin;
				efficiencyA += pointsForLose;
				loseAsGuestA++;
				winAsHomeB++;
			} else {
				efficiencyA += pointsForDraw;
				efficiencyB += pointsForDraw;
			}

			efficiencyA += pointsForGoal * scoreA;
			efficiencyB += pointsForGoal * scoreB;
		}

		// 4. Korekcja współczynników skuteczności względem ostatnio rozegranych
		// meczów z innymi drużynami(czy były wygrane, czy przegrane i z jaką
		// przewagą)
		for (int i = 0; i < TeamAWitoutTeamBMatchesResults.size(); i++) {
			scoreA = TeamAWitoutTeamBMatchesResults.get(i).returnScoreA();
			scoreB = TeamAWitoutTeamBMatchesResults.get(i).returnScoreB();
			if (scoreA > scoreB) {
				efficiencyA += pointsForWin;
				efficiencyB += pointsForLose;
				winAsHomeA++;
				loseAsGuestB++;
			} else if (scoreA < scoreB) {
				efficiencyB += pointsForWin;
				efficiencyA += pointsForLose;
				loseAsHomeA++;
				winAsGuestB++;
			} else {
				efficiencyA += pointsForDraw;
				efficiencyB += pointsForDraw;
			}

			efficiencyA += pointsForGoal * scoreA;
			efficiencyB += pointsForGoal * scoreB;
		}

		for (int i = 0; i < TeamBWithoutTeamAMatchesResults.size(); i++) {
			scoreA = TeamBWithoutTeamAMatchesResults.get(i).returnScoreB();
			scoreB = TeamBWithoutTeamAMatchesResults.get(i).returnScoreA();
			if (scoreA > scoreB) {
				efficiencyA += pointsForWin;
				efficiencyB += pointsForLose;
				winAsGuestA++;
				loseAsHomeB++;
			} else if (scoreA < scoreB) {
				efficiencyB += pointsForWin;
				efficiencyA += pointsForLose;
				loseAsGuestA++;
				winAsHomeB++;
			} else {
				efficiencyA += pointsForDraw;
				efficiencyB += pointsForDraw;
			}

			efficiencyA += pointsForGoal * scoreA;
			efficiencyB += pointsForGoal * scoreB;
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

		// Tym większy efficiencyDifference tym remis jest bardziej
		// prawdopodobny.
		double efficiencyDifference = Math.abs(efficiencyA - efficiencyB);

		// Plus 1, because we wan't to avoid x/0, if efficiencyDifference = 0 =>
		// drawEfficiency = 1 (100%);
		double drawEfficiency = (efficiencyA + efficiencyDifference + efficiencyB + 1)
				/ (efficiencyA + efficiencyB + 1);

		double sum1 = efficiencyA  + efficiencyB;

		efficiencyA/=sum1;
		efficiencyB/=sum1;
		
		double sum = efficiencyA + drawEfficiency + efficiencyB;
		
		winA = efficiencyA / sum * 100;
		draw = drawEfficiency / sum * 100;
		winB = efficiencyB / sum * 100;
		
		
		//winA = new BigDecimal(winA).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
		//draw = new BigDecimal(draw).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
		//winB = new BigDecimal(winB).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
		//winA = Double.

		System.out.println("efficiencyA = " + efficiencyA);
		System.out.println("efficiencyB = " + efficiencyB);
		System.out.println("drawEfficiency = " + drawEfficiency);
		System.out.println("winA = " + winA);
		System.out.println("draw = " + draw);
		System.out.println("winB = " + winB);

		/*
		 * double sum = efficiencyA + efficiencyDifference + efficiencyB; double
		 * winA = 0 + (efficiencyA - efficiencyB); double draw = 100 -
		 * efficiencyDifference; double winB = 0 + efficiencyB - efficiencyA;
		 * 
		 * FootballMatch calculatedMatch = new
		 * FootballMatch(currentMatch.returnDate(), TeamAID , TeamBID,
		 * efficiencyA, efficiencyB, currentMatch.returnLeague()); return
		 * calculatedMatch;
		 */
		return null;
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

package Crawler.MatchesResultsParsers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import Database.DatabaseManager;
import Logger.LogMaker;
import Structure.FootballMatch;

public class ParseLiveScore {

	private static String[] urls = { "http://www.livescore.com/soccer/england/premier-league/results/all/",
			"http://www.livescore.com/soccer/spain/primera-division/results/all/",
			"http://www.livescore.com/soccer/germany/bundesliga/results/all/",
			"http://www.livescore.com/soccer/italy/serie-a/results/all/",
			"http://www.livescore.com/soccer/france/ligue-1/results/all/",
			"http://www.livescore.com/soccer/poland/ekstraklasa/results/all/" };

	private static String[] leagueShort = { "UK1", "ES1", "DE1", "IT1", "FR1", "PL1" };
	
	static LogMaker logMaker;
	
	public String webName;

	DatabaseManager db = new DatabaseManager();

	public ParseLiveScore() {
		this.webName = "LiveScore.com";
	}

	public void init() throws IOException {
		logMaker = LogMaker.getInstance();
		db.initConnection();
		System.out.println("Starting parsing urls...");
		for (int i = 0; i < urls.length; i++) {
			System.out.println("Parsing..." + urls[i]);
			logMaker.logInfo("Parsing..." + urls[i]);
			findMatches(urls[i], leagueShort[i]);
		}
		System.out.println("all urls parsed.");
		db.closeConnection();
	}
	
	public void findMatchesAndAddThemToDatabase(String url, String leagueShort){
		
		ArrayList<FootballMatch> matchesResults = null;
		try {
			matchesResults = findMatches(url, leagueShort);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			logMaker.logError(e.getMessage());
		}
		System.out.println("url + "+url+" found matches : "+matchesResults.size());
		addMatchesToDatabase(matchesResults);
	}

	public ArrayList<FootballMatch> findMatches(String url, String leagueShort) throws IOException {
		/* Don't add matches to database with any error */
		Boolean error = false;
		
		Document doc = Jsoup.connect(url).get();
		
		ArrayList<FootballMatch> matchesResults = new ArrayList<FootballMatch>();
		// System.out.println(doc.toString());

		/* Example framework options */
		// <table class="league-table mtn">
		// Elements table = doc.getElementsByClass("league-table").select("tr");

		// "row-gray" - {"FT",TeamA,"ScoreA - ScoreB",TeamB}
		// "row-tall bt0" - {Month DayOfMonth}
		Elements table = doc.getElementsByAttributeValueMatching("class", "row-gray|row-tall bt0");

		/* Example found data "FT Aston Villa 0 - 0 Manchester City" */
		String temp[], temp2[];
		String data = "";
		String teamA = "", teamB = "";
		int scoreA = 0, scoreB = 0;
		
		Pattern date = Pattern.compile("(January|February|March|April|May|June|July|"
				+ "August|September|October|November|December)\\s([1-9]|[1-2][0-9]|3[0-1])");
		
		for (int k = 0; k < table.size(); k++) {
			
			Matcher m = date.matcher(table.get(k).text());

			if (m.matches()) {
				data = table.get(k).text();
				data = changeDate(data);
			} else {
				error = false;
				// 1. Split by " - ", Example Result : temp = {"FT Aston Villa
				// 0","0 Manchester City"}
				temp = (table.get(k).text().split(" - "));
				
				// 2. Split by " " first part of temp1, Example Result : temp2 =
				// {"FT","Aston","Villa","0"}
				temp2 = temp[0].split(" ");
				// Score of Team A
				try {
					scoreA = Integer.parseInt(temp2[temp2.length - 1]);
				} catch (NumberFormatException ex) {
					/* Przypadek np. "?" zamiast liczby*/		
					continue;
				}

				teamA = "";
				// 3. TeamA Name - Sum of Strings in temp2 + " " from index = 1
				// to length-1
				for (int j = 1; j < temp2.length - 1; j++) {
					teamA = teamA + " " + temp2[j];
				}
				// 4. Split by " " second part of temp1, Example Result : temp2
				// = {"0","Manchester","City"}
				temp2 = temp[1].split(" ");
				try {
					scoreB = Integer.parseInt(temp2[0]);
				} catch (NumberFormatException ex) {
					/* Przypadek np. "?" zamiast liczby */
					continue;
				}
				teamB = "";
				// TeamA Name - Sum of Strings in temp2 + " " from index = 1 to
				// length-1
				for (int j = 1; j < temp2.length; j++) {
					teamB = teamB + " " + temp2[j];
				}
				// 5. Remove added " " add begin of Team name {teamA,teamB}
				teamA = teamA.replaceFirst(" ", "");
				teamB = teamB.replaceFirst(" ", "");
				// Testing gained Data
				// System.out.println("("+data+";"+teamA+";"+teamB+";"+scoreA+";"+scoreB+";"+leagueShort+")");
				
				int teamAID = db.getTeamNamesIDByTeamName(teamA);
				int teamBID = db.getTeamNamesIDByTeamName(teamB);
				
				if (teamAID == -1){
					teamAID = db.addUnknownTeamNameToDatabaseAndGetNewTeamID(teamA, leagueShort);
				}
				
				if (teamBID == -1){
					teamBID = db.addUnknownTeamNameToDatabaseAndGetNewTeamID(teamB, leagueShort);	
				}
				
				if (teamAID == -1 || teamBID == -1){
					continue;
				}
				
				/* Don't add matches to database with any error */
				if (error == false){
					matchesResults.add(new FootballMatch(data, teamAID, teamBID, scoreA, scoreB, leagueShort));
				}	
			}

		}
		return matchesResults;
	}

	/**
	 * Changing date in text in date in number (February -> 01) ExampleResult :
	 * 2015-02-03
	 **/
	public String changeDate(String data) {
		String[] temp = data.split(" ");
		Date da = new Date();
		String year = Integer.toString(Calendar.getInstance().get(Calendar.YEAR));
		if (temp[0].equals("January"))
			temp[0] = "01";
		else if (temp[0].equals("February"))
			temp[0] = "02";
		else if (temp[0].equals("March"))
			temp[0] = "03";
		else if (temp[0].equals("April"))
			temp[0] = "04";
		else if (temp[0].equals("May"))
			temp[0] = "05";
		else if (temp[0].equals("June"))
			temp[0] = "06";
		else if (temp[0].equals("July")) {
			temp[0] = "07";
		} else if (temp[0].equals("August")) {
			temp[0] = "08";
		} else if (temp[0].equals("September")) {
			temp[0] = "09";
		} else if (temp[0].equals("October")) {
			temp[0] = "10";
		} else if (temp[0].equals("November")) {
			temp[0] = "11";
		} else if (temp[0].equals("December")) {
			temp[0] = "12";
		} else
			temp[1] = "Wrong date";
		data = (year + "-" + temp[0] + "-" + temp[1]);

		return data;
	}

	/** Adding many matches to the Database **/
	public void addMatchesToDatabase(ArrayList<FootballMatch> matches) {
		for (int k = 0; k < matches.size(); k++) {
			// System.out.println(matches.get(k).returnMatchResult());
			addMatchToDatabase(matches.get(k));
		}
	}

	/** Adding single match to the Database **/
	public void addMatchToDatabase(FootballMatch match) {
		db.addMatchResultToDatabase(match);
	}

}

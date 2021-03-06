package Crawler.IncommingMatchesParsers;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import Database.DatabaseManager;
import Logger.LogMaker;
import Structure.FootballMatch;

/**
 * Klasa parsująca stronę http://www.soccer-rating.com W chwili obecnej działa
 * tylko dla ligi hiszpańskiej i angielskiej ze względu na ilość danych. Posiada
 * metody: szukania drużyn w lidze dodania drużyn do tabeli TEAM_NAMES
 * wyszukania meczów przyszłych i histori meczów dla danej drużyny dodania tych
 * meczów do tabeli FOOTBALL_MATCHES
 */
public class ParseSoccerRating {

	private static String[] urls = { "http://www.soccer-rating.com/England/", "http://www.soccer-rating.com/Spain/",
			"http://www.soccer-rating.com/Germany/", "http://www.soccer-rating.com/Italy/",
			"http://www.soccer-rating.com/France/", "http://www.soccer-rating.com/Poland/" };
	private static String[] leagueShort = { "UK1", "ES1", "DE1", "IT1", "FR1", "PL1" };

	static LogMaker logMaker;

	public String webName;
	ArrayList<FootballMatch> matches;
	DatabaseManager db = new DatabaseManager();

	public ParseSoccerRating() {
		this.webName = "Soccer-Rating.com";
	}

	public void init() throws IOException {
		logMaker = LogMaker.getInstance();
		db.initConnection();
		for (int i = 0; i < urls.length; i++) {
			System.out.println("Parsing..." + urls[i]);
			logMaker.logInfo("Parsing..." + urls[i]);
			findMatchesAndAddThemToDatabase(urls[i], leagueShort[i]);
		}
		System.out.println("all urls parsed.");
		db.closeConnection();
	}

	public void findMatchesAndAddThemToDatabase(String url, String leagueShort) {

		ArrayList<FootballMatch> matches = null;
		try {
			matches = findMatches(url, leagueShort);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			logMaker.logError(e.getMessage());
		}
		System.out.println("Number of matches found : " + matches.size());
		addMatchesToDatabase(matches);

		/*
		 * Document doc = null; try { doc = Jsoup.connect(url).get(); } catch
		 * (IOException e) { // TODO Auto-generated catch block
		 * logMaker.logError(e.getMessage()); //e.printStackTrace(); } Elements
		 * table = doc.getElementsByClass("bigtable"); Elements links =
		 * table.first().select("a[href]"); /*for (int k = 0; k < links.size();
		 * k++) { //Jednorazowo dodaje druzyny do bazy danych sprawdzajac czy
		 * juz istnieja db.addTeamNameToDatabase(links.get(k).text()); }
		 */
		/*
		 * for (int k = 0; k < links.size(); k++) {
		 * findMatches(links.get(k).attr("abs:href"), leagueShort); }
		 */
	}

	public ArrayList<FootballMatch> findMatches(String url, String leagueName) throws IOException {
		Document doc = null;
		try {
			doc = Jsoup.connect(url).get();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logMaker.logError(e.getMessage());
		}
		/* There are 6 or 5 bigtables classes, the third has got new matches */
		Elements tables = doc.getElementsByClass("bigtable");
		// If exist 6 tables we get 4 index of table, if 5 we get 3
		Element table;
		try{
			table = tables.get((tables.size() + 1) % 2 + 3);
		}
		catch(Exception e){
			return new ArrayList();
		}

		Elements tr = table.select("tr");
		Elements temp;

		matches = new ArrayList<FootballMatch>();
		for (int k = 1; k < tr.size(); k++) {
			temp = tr.get(k).select("td");
			
			if (temp.size() != 11){
				continue;
			}

			if (leagueName.equals(temp.get(3).text())) {

				String League = temp.get(3).text();

				String teamA = teamFormat(temp.get(5).text());
				String teamB = teamFormat(temp.get(7).text());

				int teamAID = db.getTeamNamesIDByTeamName(teamA);
				int teamBID = db.getTeamNamesIDByTeamName(teamB);

				if (teamAID == -1) {
					teamAID = db.addUnknownTeamNameToDatabaseAndGetNewTeamID(teamA, League);
				}

				if (teamBID == -1) {
					teamBID = db.addUnknownTeamNameToDatabaseAndGetNewTeamID(teamB, League);
				}

				matches.add(new FootballMatch(changeDate(temp.get(0).text()), teamAID, teamBID, changeOdd(temp.get(8).text()),
						changeOdd(temp.get(9).text()), changeOdd(temp.get(10).text()), League));
			}
		}
		return matches;
	}

	public String teamFormat(String mecz) {
		if (mecz.endsWith("↑") || mecz.endsWith("↓"))
			return mecz.substring(0, mecz.length() - 2);
		return mecz;
	}
	
	public double changeOdd(String odd) {
		return Double.parseDouble(odd);
	}

	// 19.08.12 ->2012-08-19
	public String changeDate(String date) {
		return date.substring(0, 10);
	}

	// 2014-12-28
	public boolean compareDateWithToday(String date) {
		Date today = new Date();
		SimpleDateFormat fd = new SimpleDateFormat("yyyy-MM-dd");
		try {
			Date d = fd.parse(date);
			if (d.after(today))
				return true;
			else if (d.before(today))
				return false;
			else
				return true;
		} catch (java.text.ParseException e) {
			System.out.println("[WARNING] Błąd porównania daty");
			logMaker.logError(e.getMessage());
			// e.printStackTrace();
		}
		return false;
	}

	public void addMatchesToDatabase(ArrayList<FootballMatch> matches) {
		for (int k = 0; k < matches.size(); k++){
			addMatchToDatabase(matches.get(k));
		}
	}

	public void addMatchToDatabase(FootballMatch match) {
		db.addMatchToDatabase(match, webName);
	}

}

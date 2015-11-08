package Crawler.MatchesResultsParsers;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import Database.addToDatabase;
import Logger.logMaker;
import Structure.footballMatch;


public class parseLiveScore {


	private static String[] urls= {	"http://www.livescore.com/soccer/england/premier-league/results/all/",
									"http://www.livescore.com/soccer/spain/primera-division/results/all/",
									"http://www.livescore.com/soccer/germany/bundesliga/results/all/",
									"http://www.livescore.com/soccer/italy/serie-a/results/all/",
									"http://www.livescore.com/soccer/france/ligue-1/results/all/",
									"http://www.livescore.com/soccer/poland/ekstraklasa/results/all/" 
									};
	
	private static String[] leagueShort = {	"UK1", 
											"ES1",
											"DE1",
											"IT1",
											"FR1",
											"PL1"};

	public String webName;
	ArrayList<footballMatch> matchesResults = new ArrayList<footballMatch>();
	addToDatabase db = new addToDatabase();
	
	public parseLiveScore() {
		this.webName = "LiveScore.com";
	}
	
	public void init() throws IOException {
		db.initConnection();
		for (int i = 0; i < urls.length; i++) {
			logMaker.logInfo("Parsing..." + urls[i]);
			findMatches(urls[i], leagueShort[i]);
		}
		db.closeConnection();
	}
	
	public void findMatches(String url, String leagueShort)throws IOException {
		Document doc = Jsoup.connect(url).get();
		//System.out.println(doc.toString());
		//<table class="league-table mtn"> 
	    //Elements table = doc.getElementsByClass("league-table").select("tr");
		Elements table = doc.getElementsByAttributeValueMatching("class", "row-gray|row-tall");
	
		String temp[], temp2[];
		String data ="";
		String teamA="", teamB="";
		int scoreA = 0, scoreB = 0;
		for (int k = 0; k < table.size(); k++) {
			if ((table.get(k).text()).matches("(January|February|March|April|May|June|July|"
					+ "August|September|October|November|December)\\s([1-9]|[1-2][0-9]|3[0-1])")) {
				data = table.get(k).text();
				data = changeDate(data);
			}
			else {
				temp = (table.get(k).text().split(" - "));
				temp2 = temp[0].split(" ");
				scoreA = Integer.parseInt(temp2[temp2.length-1]);
				teamA = "";
				for(int j = 1; j < temp2.length - 1; j++) {
					teamA = teamA + " " + temp2[j];
				}
				temp2 = temp[1].split(" ");
				scoreB = Integer.parseInt(temp2[0]);
				teamB = "";
				for(int j = 1; j < temp2.length; j++) {
					teamB = teamB + " " +temp2[j];
				}
				teamA = teamA.replaceFirst(" ", "");
				teamB = teamB.replaceFirst(" ", "");
				matchesResults.add(new footballMatch(data, teamA, teamB, scoreA, scoreB, leagueShort));
			}
			
			
		}
		 addMatchesToDatabase(matchesResults);
	}

	/**Changing date in text in date in number (February -> 01) ExampleResult : 2015.02.03**/
	public String changeDate(String data) {
			String[] temp = data.split(" ");
			Date da = new Date();
			String year = Integer.toString(Calendar.getInstance().get(Calendar.YEAR));
				 if (temp[0].equals("January"))			temp[0] = "01";
			else if (temp[0].equals("February"))		temp[0] = "02";
			else if (temp[0].equals("March"))			temp[0] = "03";
			else if (temp[0].equals("April"))			temp[0] = "04";
			else if (temp[0].equals("May"))				temp[0] = "05";
			else if (temp[0].equals("June"))			temp[0] = "06";
			else if (temp[0].equals("July"))			{
														temp[0] = "07";	
														}
			else if (temp[0].equals("August"))			{
														temp[0] = "08";	
														}
			else if (temp[0].equals("September"))		{
														temp[0] = "09";	
														}
			else if (temp[0].equals("October"))			{
														temp[0] = "10";	
														}
			else if (temp[0].equals("November"))		{
														temp[0] = "11";	
														}	
			else if (temp[0].equals("December"))		{
														temp[0] = "12";	
														}
			else temp[1] = "Wrong date";
			data = (year + "-" + temp[0] + "-" + temp[1]);
			
			return data;
	 }
	
	/**Adding many matches to the Database**/
	public void addMatchesToDatabase(ArrayList<footballMatch> matches) {
		for (int k = 0; k < matches.size(); k++) {
			//System.out.println(matches.get(k).returnMatchResult());
			addMatchToDatabase(matches.get(k));
		}
	}
	
	/**Adding single match to the Database**/
	public void addMatchToDatabase(footballMatch match) {
			db.addMatchResultToDatabase(match);
	}

}

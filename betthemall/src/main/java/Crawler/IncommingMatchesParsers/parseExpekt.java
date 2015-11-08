package Crawler.IncommingMatchesParsers;

import java.io.IOException;
import java.util.ArrayList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import Database.addToDatabase;
import Structure.footballMatch;


public class parseExpekt {

	
	private static String[] urls= {	"https://en.expekt.com/football/eng-premier-league-3",
									//"https://www.bet-at-home.com//en/sport/football/spain/primera-division/222",
									//"https://www.bet-at-home.com/en/sport/football/germany/bundesliga/9086",
									//"https://www.bet-at-home.com/en/sport/football/italy/serie-a/219",
									//"https://www.bet-at-home.com/en/sport/football/france/ligue-1/4462",
									//"https://www.bet-at-home.com/"
									};
	
	private static String[] leagueShort = {	"UK1", 
											"ES1",
											"DE1",
											"IT1",
											"FR1",
											"PL1"};

	public String webName;
	ArrayList<footballMatch> matches = new ArrayList<footballMatch>();
	addToDatabase db = new addToDatabase();
	
	public parseExpekt() {
		this.webName = "expekt.com";
	}
	
	public void init() throws IOException {
		db.initConnection();
		for (int i = 0; i < urls.length; i++) {
			findMatches(urls[i], leagueShort[i]);
		}
		db.closeConnection();
	}
	
	public void findMatches(String url, String leagueShort)throws IOException {
		Document doc = Jsoup.connect(url).get();
		//System.out.println(doc.toString());
		Elements table = doc.getElementsByAttribute("data-date");
		//System.out.println(doc.html());
		Elements matchesOfDay = table.attr("class", "cal-day-entry");
		Elements temp;
		for (int j = 0; j <  matchesOfDay.size(); j++) {
			Elements eMatches = matchesOfDay.get(j).getElementsByClass("match-entry");
			Elements date = matchesOfDay.get(j).getElementsByClass("cal-day").select("span");
			if (!date.text().equals("")){
				//System.out.println("Data #" + date.text() + "#");
				for (int i = 0; i < eMatches.size(); i++) {
					//System.out.println(eMatches.get(i).text());
					temp = eMatches.get(i).select("div");
					translateResultsToClassMatch(temp, changeDate(date.text()), leagueShort);
				}
			}
		}
		addMatchesToDatabase(matches);
	}

	public void translateResultsToClassMatch(Elements eMatch, String date, String leagueName) throws IOException {
				matches.add(new footballMatch(	date, 
												homeTeam(eMatch.get(1).text()),
												awayTeam(eMatch.get(1).text()), 
												changeOdd(eMatch.get(2).text()), 
												changeOdd(eMatch.get(3).text()),
												changeOdd(eMatch.get(4).text()),  
												leagueName));
	}

	 public String homeTeam(String mecz) {
		 	
			String []teams = mecz.split(" - ");
			return teams[0];
	 }
	 public String awayTeam(String mecz) {
		
			String []teams = mecz.split(" - ");
			return teams[1];
		 
	 }    

	 //Sunday 28 December 2014 - > 2014-12-28
	public String changeDate(String data) {
		String[] temp = data.split(" ");
		String month = "00";
		if (temp[2].equals("January"))
			month = "01";
		else if (temp[2].equals("February"))
			month = "02";
		else if (temp[2].equals("March"))
			month = "03";
		else if (temp[2].equals("April"))
			month = "04";
		else if (temp[2].equals("May"))
			month = "05";
		else if (temp[2].equals("June"))
			month = "06";
		else if (temp[2].equals("July"))
			month = "07";
		else if (temp[2].equals("August"))
			month = "08";
		else if (temp[2].equals("September"))
			month = "09";
		else if (temp[2].equals("November"))
			month = "10";
		else if (temp[2].equals("October"))
			month = "11";
		else if (temp[2].equals("December"))
			month = "12";
		return (temp[3] + "-" + month + "-" + temp[1]);
	}
	
	public double changeOdd(String odd) {
		String[] temp = odd.split(" ");
		return Double.parseDouble(temp[1]);
	}
	
	
	public void addMatchesToDatabase(ArrayList<footballMatch> matches) {
		for (int k = 0; k < matches.size(); k++) {
			addMatchToDatabase(matches.get(k));
		}
	}
	
	public void addMatchToDatabase(footballMatch match) {
		db.addMatchToDatabase(match, webName);
	}

}

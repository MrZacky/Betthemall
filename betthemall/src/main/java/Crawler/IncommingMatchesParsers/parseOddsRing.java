package Crawler.IncommingMatchesParsers;
import java.io.IOException;
import java.util.ArrayList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import Database.addToDatabase;
import Structure.footballMatch;


public class parseOddsRing {


	private static String[] urls= {	"http://pl.oddsring.com/betoffer/1/31"};
									//"http://sports.williamhill.com/bet/pl/betting/t/338/",
									//"http://sports.williamhill.com/bet/pl/betting/t/315/"};
									//"http://sports.williamhill.com/bet/pl/betting/t/321/",
									//"http://sports.williamhill.com/bet/pl/betting/t/312/",
									//"http://sports.williamhill.com/bet/pl/betting/t/330/"
									//};
	
	private static String[] leagueShort = {	"UK1", 
											"ES1",
											"DE1",
											"IT1",
											"FR1",
											"PL1"};

	public String webName;
	ArrayList<footballMatch> matches = new ArrayList<footballMatch>();
	addToDatabase db = new addToDatabase();
	
	public parseOddsRing() {
		this.webName = "OddsRing.com";
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
		Elements table = doc.getElementsByAttributeValueMatching("class", "row1|row2").select("tr");
		Elements temp; 
		for (int k = 0; k < table.size(); k++) {
			temp = table.get(k).select("td");
			translateResultsToClassMatch(k, temp, leagueShort);
			//System.out.println(matches[k].returnFullMatchAsString());		
		}
		 addMatchesToDatabase(matches);
	}

	public void translateResultsToClassMatch(int k, Elements temp, String leagueName) throws IOException {
		matches.add(new footballMatch(	changeDate(temp.get(1).text()), 
										homeTeam(temp.get(2).text()),
										awayTeam(temp.get(2).text()), 
										changeOdd(temp.get(3).text()),
										changeOdd(temp.get(4).text()),   
										changeOdd(temp.get(5).text()),  
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

	public String changeDate(String data) {
		String[] temp = data.split("/");
		String year = "2015";
			if (temp[1].substring(0,2).equals("12")) year = "2014";
		
		return (year + "-" + temp[1].substring(0,2) + "-" + temp[0]);
	}
	
	public double changeOdd(String odd) {
		String[] temp = odd.split(" ");
		return Double.parseDouble(temp[0]);
	}
	
	
	public void addMatchesToDatabase(ArrayList<footballMatch> matches) {
		for (int k = 0; k < matches.size(); k++)
			addMatchToDatabase(matches.get(k));
	}
	
	public void addMatchToDatabase(footballMatch match) {
		db.addMatchToDatabase(match, webName);
	}

}

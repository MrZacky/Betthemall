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


	/**Klasa parsująca stronę http://www.soccer-rating.com
	 * W chwili obecnej działa tylko dla ligi hiszpańskiej i angielskiej ze względu na ilość danych.
	 * Posiada metody:
	 * 		szukania drużyn w lidze
	 * 		dodania drużyn do tabeli TEAM_NAMES
	 * 		wyszukania meczów przyszłych i histori meczów dla danej drużyny
	 * 		dodania tych meczów do tabeli FOOTBALL_MATCHES*/
public class ParseSoccerRating{

	private static String[] urls= {	"http://www.soccer-rating.com/England/",
								   	"http://www.soccer-rating.com/Spain/",
									"http://www.soccer-rating.com/Germany/",
								   	"http://www.soccer-rating.com/Italy/",
								   	"http://www.soccer-rating.com/France/",
								   	"http://www.soccer-rating.com/Poland/"};
	private static String[] leagueShort = {	"UK1", 
											"ES1",
											"DE1",
											"IT1",
											"FR1",
											"PL1"};
	
	static LogMaker logMaker;
	
	public String webName;
	ArrayList<FootballMatch> matches;
	DatabaseManager db = new DatabaseManager();
	
	public ParseSoccerRating()  {
		this.webName = "Soccer-Rating.com";
	}
	
	public void init(){
		logMaker = LogMaker.getInstance();
		db.initConnection();
		for (int i = 0; i < urls.length; i++) {
			System.out.println("Parsing..." + urls[i]);
			logMaker.logInfo("Parsing..." + urls[i]);
			findLinksToTeams(urls[i], leagueShort[i]);
		}
		System.out.println("all urls parsed.");
		db.closeConnection();
	}
	
	
	public void findLinksToTeams(String url, String leagueShort){
		Document doc = null;
		try {
			doc = Jsoup.connect(url).get();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			logMaker.logError(e.getMessage());
			//e.printStackTrace();
		}
		Elements table = doc.getElementsByClass("bigtable");
		Elements links = table.first().select("a[href]");
		/*for (int k = 0; k < links.size(); k++) {
			//Jednorazowo dodaje druzyny do bazy danych sprawdzajac czy juz istnieja 
			db.addTeamNameToDatabase(links.get(k).text());
		}*/
		for (int k = 0; k < links.size(); k++) {
			findResultForTeam(links.get(k).attr("abs:href"), links.get(k).text(), leagueShort);
		}
	}
	
	public void findResultForTeam(String url, String teamName, String leagueName){
		Document doc = null;
		try {
			doc = Jsoup.connect(url).get();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logMaker.logError(e.getMessage());
		}
		/*There are 3 bigtables classes, the third has got new matches*/
		Element table = doc.getElementsByClass("bigtable").get(3);
		//Element table1 = table.get(3);
		//System.out.println(table1);
	    Elements tr = table.select("tr");
	    Elements temp;
	    
	  //  int k = 0;
	    /*while (!tr.get(k).text().startsWith("30")) { //pierwszt mecz na strone ma jakby indeks 30
	       k = k + 1; 
	    }*/
	    matches = new ArrayList<FootballMatch>();
	    for (int k = 1 ; k < tr.size(); k++) { //tr.size()
	    	temp = tr.get(k).select("td");
	    	//System.out.println(temp);
	    	//System.out.println("-------------");
	    	if (leagueName.equals(temp.get(6).text())){
	    		
	    		/*System.out.println("------------------------------");
	    		System.out.println(homeTeam(temp.get(2).text()));
	    		System.out.println(awayTeam(temp.get(2).text()));
	    		System.out.println(changeDate(temp.get(1).text()));
	    		System.out.println("------------------------------");*/
	    		
	    		String League = temp.get(6).text();
	    		int TeamAID = db.addUnknownTeamNameToDatabaseAndGetNewTeamID(homeTeam(temp.get(2).text()), League);
	    		int TeamBID = db.addUnknownTeamNameToDatabaseAndGetNewTeamID(awayTeam(temp.get(2).text()), League);
	    		
	    		
 			matches.add(new FootballMatch(changeDate(temp.get(1).text()), 
 											TeamAID,
 											TeamBID, 
     										changeOdd(temp.get(3).text()), 
     										changeOdd(temp.get(4).text()),
     										changeOdd(temp.get(5).text()),  
     										League));	  
 			}
	    }
	    addMatchesToDatabase(matches);
	   }
	

	 public String homeTeam(String mecz) {
			String []teams = mecz.split(" - ");
			if (teams[0].endsWith("↑") || teams[0].endsWith("↓")) 
				return teams[0].substring(0, teams[0].length() - 2);
			return teams[0];
     }
	 
	 public String awayTeam(String mecz) {
		 	String []teams = mecz.split(" - ");
			if (teams[1].endsWith("↑") || teams[1].endsWith("↓"))
				return teams[1].substring(0, teams[1].length() - 2);
			return teams[1];
	 }    
	 
	 public double changeOdd(String odd){
		 return Double.parseDouble(odd);
	 }
	 
	 //19.08.12 ->2012-08-19
	 public String changeDate(String date) {
		 	date = date.replace(".", "-");
		 	String[] temp = date.split("-");
		 			return ("20" + temp[2] + "-" + temp[1] + "-" + temp[0]);
	 }
	 
	 //2014-12-28
	 public boolean compareDateWithToday(String date) {
		 Date today = new Date();
		 SimpleDateFormat fd = new SimpleDateFormat ("yyyy-MM-dd");
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
				//e.printStackTrace();
			}
		 return false; 	 
	 }
	
	 public void addMatchesToDatabase(ArrayList<FootballMatch> matches) {
			for (int k = 0; k < matches.size(); k++)
				if (compareDateWithToday(matches.get(k).returnDate())){
		    		addMatchToDatabase(matches.get(k));
		    	}	
				else return;
		}
		
	public void addMatchToDatabase(FootballMatch match) {
			db.addMatchToDatabase(match, webName);
		}


}

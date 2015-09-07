import java.io.IOException;
import java.util.ArrayList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;


public class parseBetClic {
	private static String[] urls= {	"https://pl.betclic.com/pi%C5%82ka-nozna/anglia-premiership-e3",
									"https://pl.betclic.com/pi%C5%82ka-nozna/hiszpania-la-liga-e7",
									"https://pl.betclic.com/pi%C5%82ka-nozna/niemcy-bundesliga-e5",
									"https://pl.betclic.com/pi%C5%82ka-nozna/w%C5%82ochy-serie-a-e6",
									"https://pl.betclic.com/pi%C5%82ka-nozna/francja-ligue-1-e4",
									//"https://pl.betano.com/league/Soccer-FOOT/Ekstraklasa-Poland-17080"
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

	public parseBetClic() {
	this.webName = "BetClick.com";
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
		Elements table = doc.getElementsByAttribute("data-date");
		//System.out.println(table.html());
		Elements matchesOfDay = table.attr("class", "match-entry");
		Elements temp, temp2;
		String data;
		for (int i = 0; i <table.size(); i++) {
			if(table.get(i).attr("data-date").matches("(20[1-9][1-9])-([1-9]|0[0-2])-([1-9]|[1-2][0-9]|3[0-2])")) {
				//System.out.println(table.get(i).attr("data-date"));
				data = changeDate(table.get(i).attr("data-date"));
				//System.out.println("Data " + data);
				//System.out.println( i + " "  + matchesOfDay.get(i).select("div").text());
				temp = matchesOfDay.get(i).getElementsByClass("schedule");
				for (int j = 0; j < temp.size(); j++) {
					temp2 = temp.get(j).getElementsByClass("match-entry");
					//System.out.println("Godzina " + temp.get(j).getElementsByClass("hour").text());
					for (int k = 0; k < temp2.size(); k++) {
						translateResultsToClassMatch(temp2.get(k).select("div"), data, leagueShort);
					}
				}
			}
		}
		addMatchesToDatabase(matches);
	}
	
	public void translateResultsToClassMatch(Elements eMatch, String date, String leagueName) throws IOException {
		//for (int i = 0; i< eMatch.size(); i++)
			//System.out.println(i + "#"+eMatch.get(i).text());
			/*System.out.println(date+ " " +
											homeTeam(eMatch.get(1).text())+ " " +
											awayTeam(eMatch.get(1).text())+ " " + 
											changeOdd(eMatch.get(3).text())+ " " + 
											changeOdd(eMatch.get(4).text())+ " " +
											changeOdd(eMatch.get(5).text())+ " " +  
											leagueName);*/
		
			matches.add(new footballMatch(	date, 
											homeTeam(eMatch.get(1).text()),
											awayTeam(eMatch.get(1).text()), 
											changeOdd(eMatch.get(3).text()), 
											changeOdd(eMatch.get(4).text()),
											changeOdd(eMatch.get(5).text()),  
											leagueName));
	}
	
	public String homeTeam(String mecz) {
		mecz = mecz.replace("'", "");
		String []teams = mecz.split(" - ");
		return teams[0];
	}
	
	public String awayTeam(String mecz) {
		mecz = mecz.replace("'", "");
		String []teams = mecz.split(" - ");
		return teams[1];
	}    
	
	//2015-1-1 -> 2015-01-01
	//2014-12-28 -> 2014-12-28
		public String changeDate(String data) {
			String[] temp = data.split("-");
			if (temp[1].length() == 1)
				temp[1] = "0" + temp[1];
			if (temp[2].length() == 1)
				temp[2] = "0" + temp[2];
			return (temp[0] + "-" + temp[1] + "-" + temp[2]);
		}
	
	public double changeOdd(String odd) {
		odd = odd.replace(",", ".");
		return Double.parseDouble(odd);
	}
	
	
	public void addMatchesToDatabase(ArrayList<footballMatch> matches) {
		for (int k = 0; k < matches.size(); k++) {
			addMatchToDatabase(matches.get(k));
			//System.out.println(matches.get(k).returnMatchAsString());
		}
	}
	
	public void addMatchToDatabase(footballMatch match) {
		db.addMatchToDatabase(match, webName);
	}
}

import java.io.IOException;
import java.util.ArrayList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;


public class parseBetAtHome {

	
	private static String[] urls= {	"https://www.bet-at-home.com/en/sport/football/england/premier-league/197",
									"https://www.bet-at-home.com//en/sport/football/spain/primera-division/222",
									"https://www.bet-at-home.com/en/sport/football/germany/bundesliga/9086",
									"https://www.bet-at-home.com/en/sport/football/italy/serie-a/219",
									"https://www.bet-at-home.com/en/sport/football/france/ligue-1/4462",
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
	
	public parseBetAtHome() {
		this.webName = "bet-at-home.com";
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
		Elements table = doc.getElementsByClass("h-bgTo-04517f");
		//Elements table = doc.getElementsByAttributeValueMatching("class", "row1|row2").select("tr");
		Elements temp; 
		for (int k = 0; k < table.size(); k++) {
			temp = table.get(k).select("td");
			translateResultsToClassMatch(k, temp, leagueShort);
		}
		 addMatchesToDatabase(matches);
	}

	public void translateResultsToClassMatch(int k, Elements eMatch, String leagueName) throws IOException {
		/*System.out.println((k+1) + " " + eMatch.text());
		for (int i = 0; i < eMatch.size(); i++) {
			System.out.println("#(" + i + ")" + eMatch.get(i).text()+ "#");
		}
		System.out.println(homeTeam(eMatch.get(0).text()));
		System.out.println(awayTeam(eMatch.get(0).text()));
		System.out.println(changeDate(eMatch.get(1).text()));*/
		matches.add(new footballMatch(	changeDate(eMatch.get(1).text()), 
										homeTeam(eMatch.get(0).text()),
										awayTeam(eMatch.get(0).text()), 
										changeOdd(eMatch.get(2).text()), 
										changeOdd(eMatch.get(3).text()),
										changeOdd(eMatch.get(4).text()),  
										leagueName));
	}

	 public String homeTeam(String mecz) {
		 	if (mecz.endsWith("live"))
		 		mecz = mecz.substring(0, mecz.length()-4);
			String []teams = mecz.split(" - ");
			return teams[0];
	 }
	 public String awayTeam(String mecz) {
		 	if (mecz.endsWith("live"))
		 		mecz = mecz.substring(0, mecz.length()-4);
			String []teams = mecz.split(" - ");
			return teams[1];
		 
	 }    

	 //01.01.15 16:00 -> 2015-01-01
	public String changeDate(String data) {
		String[] temp = data.split(" ");
		return ("20" + temp[0].substring(6, 8) + "-" + temp[0].substring(3, 5) + "-" + temp[0].substring(0, 2));
	}
	
	public double changeOdd(String odd) {
		String[] temp = odd.split(" ");
		return Double.parseDouble(temp[0]);
	}
	
	
	public void addMatchesToDatabase(ArrayList<footballMatch> matches) {
		for (int k = 0; k < matches.size(); k++)
			db.addMatchToDatabase(matches.get(k), webName);
	}
	
	public void addMatchToDatabase(footballMatch match) {
		db.addMatchToDatabase(match, webName);
	}

}

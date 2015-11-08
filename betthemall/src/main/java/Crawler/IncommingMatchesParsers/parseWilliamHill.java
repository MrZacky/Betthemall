package Crawler.IncommingMatchesParsers;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import Database.addToDatabase;
import Structure.footballMatch;


public class parseWilliamHill {

	private static String[] urls= {	"http://sports.williamhill.com/bet/pl/betting/t/295/",
									"http://sports.williamhill.com/bet/pl/betting/t/338/",
									"http://sports.williamhill.com/bet/pl/betting/t/315/",
									"http://sports.williamhill.com/bet/pl/betting/t/321/",
									"http://sports.williamhill.com/bet/pl/betting/t/312/",
									"http://sports.williamhill.com/bet/pl/betting/t/330/",
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
	
	public parseWilliamHill() {
		this.webName =  "WilliamHill.com";
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
		Elements table = doc.getElementsByClass("rowOdd");
		Elements temp;
		for (int k = 0; k < table.size(); k++) {
			temp = table.get(k).select("td");
			translateResultsToClassMatch(temp, k, leagueShort);
			//System.out.println(matches[k].returnFullMatchAsString());
		}
		addMatchesToDatabase(matches);
	}

	public void translateResultsToClassMatch(Elements temp, int k, String leagueName) throws IOException {
		matches.add(new footballMatch(	changeDate(temp.get(0).text()), 
										homeTeam(temp.get(2).text().replaceAll("\u00a0", " ")),
										awayTeam(temp.get(2).text().replaceAll("\u00a0", " ")), 
										changeOdd(temp.get(4).text()),
										changeOdd(temp.get(5).text()),   
										changeOdd(temp.get(6).text()),  
										leagueName));
	}

	 public String homeTeam(String mecz) {
		 	mecz = mecz.replace("'", "");
			String []teams = mecz.split("   ");
			return teams[0];
	 }
	 public String awayTeam(String mecz) {
		 	mecz = mecz.replace("'", "");
			String []teams = mecz.split("   ");
			return teams[2];
	 }    

	 public double changeOdd(String odd){
		 return Double.parseDouble(odd);
	 }

	public String changeDate(String data) {
		if (data.equals("")) {
			 Date today = new Date();
			 SimpleDateFormat fd = new SimpleDateFormat ("yyyy-MM-dd");
			 data = fd.format(today);
		}
		else {
			String[] temp = data.split(" ");
			String year = "2015";
				 if (temp[1].equals("Sty"))			temp[1] = "01";
			else if (temp[1].equals("Lut"))			temp[1] = "02";
			else if (temp[1].equals("Mar"))			temp[1] = "03";
			else if (temp[1].equals("Kwi"))			temp[1] = "04";
			else if (temp[1].equals("Maj"))			temp[1] = "05";
			else if (temp[1].equals("Cze"))			temp[1] = "06";
			else if (temp[1].equals("Lip"))			temp[1] = "07";
			else if (temp[1].equals("Sie"))			temp[1] = "08";
			else if (temp[1].equals("Wrz"))			temp[1] = "09";
			else if (temp[1].equals("Pa�"))			temp[1] = "10";
			else if (temp[1].equals("Lis"))			temp[1] = "11";	
			else if (temp[1].equals("Gru"))		   	{
													temp[1] = "12";	
					  								year = "2014";
													}
			else temp[1] = "Wrong date";
			data = (year + "-" + temp[1] + "-" + temp[0]);
		}
		return data;
	}

	public void addMatchesToDatabase(ArrayList<footballMatch> matches) {
		for (int k = 0; k < matches.size(); k++)
			addMatchToDatabase(matches.get(k));
	}
	
	public void addMatchToDatabase(footballMatch match) {
		db.addMatchToDatabase(match, webName);
	}
}

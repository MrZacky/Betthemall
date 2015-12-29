import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import Database.DatabaseManager;
import Logger.LogMaker;
import Structure.FootballMatch;

public class DatabaseTest {
	
	//@Test
	public void testNames() throws IOException {
		
		DatabaseManager db = new DatabaseManager();
		db.initConnection();
		
		
		String TeamAName = db.getTeamNameByID(1577);
		String TeamBName = db.getTeamNameByID(1578);
		
		System.out.println("TeamA name : "+TeamAName);
		System.out.println("TeamB name : "+TeamBName);
		
		db.closeConnection();
	}
	
	//@Test
	public void testGetIDByNames() throws IOException {
		
		DatabaseManager db = new DatabaseManager();
		db.initConnection();
		
		int TeamAID = db.getTeamNamesIDByTeamName("Liverpool");
		int TeamBID = db.getTeamNamesIDByTeamName("Leicester City");
		
		System.out.println("TeamA id : "+TeamAID);
		System.out.println("TeamB id : "+TeamBID);
		
		db.closeConnection();
	}
	
	
	@Test
	public void test() throws IOException {
		DatabaseManager db = new DatabaseManager();
		db.initConnection();
		
		int TeamAName = 1693;
		int TeamBName = 1591;
		
		//int TeamAName = 1591;
		//int TeamBName = 1583;
		
		String TeamAName2 = db.getTeamNameByID(1693);
		String TeamBName2 = db.getTeamNameByID(1591);
		
		System.out.println("TeamA name : "+TeamAName2);
		System.out.println("TeamB name : "+TeamBName2);
		
		List<FootballMatch> TeamAAndTeamBMatchesResults = db.getMatchesResultsFromDatabase(TeamAName,TeamBName,1);
		List<FootballMatch> TeamBAndTeamAMatchesResults = db.getMatchesResultsFromDatabase(TeamBName,TeamAName,1);
		List<FootballMatch> TeamAWitoutTeamBMatchesResults = db.getMatchesResultsFromDatabase(TeamAName,TeamBName,2);
		List<FootballMatch> TeamBWithoutTeamAMatchesResults = db.getMatchesResultsFromDatabase(TeamBName,TeamAName,2);
		List<FootballMatch> TeamAAsGuestWitoutTeamBMatchesResults = db.getMatchesResultsFromDatabase(TeamAName, TeamBName, 3);
		List<FootballMatch> TeamBAsGuestWithoutTeamAMatchesResults = db.getMatchesResultsFromDatabase(TeamBName, TeamAName, 3);
		
		System.out.println("TeamAAndTeamBMatchesResults : ");
		
		for (int i=0;i<TeamAAndTeamBMatchesResults.size();i++){
			System.out.println(TeamAAndTeamBMatchesResults.get(i).returnMatchResult());
		}
		
		System.out.println("TeamBAndTeamAMatchesResults : ");
		
		for (int i=0;i<TeamBAndTeamAMatchesResults.size();i++){
			System.out.println(TeamBAndTeamAMatchesResults.get(i).returnMatchResult());
		}
		
		System.out.println("TeamAWitoutTeamBMatchesResults : ");
		
		for (int i=0;i<TeamAWitoutTeamBMatchesResults.size();i++){
			System.out.println(TeamAWitoutTeamBMatchesResults.get(i).returnMatchResult());
		}
		
		System.out.println("TeamBWithoutTeamAMatchesResults : ");
		
		for (int i=0;i<TeamBWithoutTeamAMatchesResults.size();i++){
			System.out.println(TeamBWithoutTeamAMatchesResults.get(i).returnMatchResult());
		}
		
		System.out.println("TeamAAsGuestWitoutTeamBMatchesResults : ");
		
		for (int i=0;i<TeamAAsGuestWitoutTeamBMatchesResults.size();i++){
			System.out.println(TeamAAsGuestWitoutTeamBMatchesResults.get(i).returnMatchResult());
		}
		
		System.out.println("TeamBAsGuestWithoutTeamAMatchesResults : ");
		
		for (int i=0;i<TeamBAsGuestWithoutTeamAMatchesResults.size();i++){
			System.out.println(TeamBAsGuestWithoutTeamAMatchesResults.get(i).returnMatchResult());
		}
		
		
		
		
		db.closeConnection();
	}
	
	//@Test
	public void test2() throws IOException {
		DatabaseManager db = new DatabaseManager();
		db.initConnection();
		
		List<FootballMatch> incommingMatches = db.getAllNewIncommingMatches();
		
		System.out.println("incommingMatches List Size : "+incommingMatches.size());
		
		// How Many Iteration If Possible, If not k = incommingMatches.size()
		int k = 5;
		
		if (incommingMatches.size()<k){
			k = incommingMatches.size();
		}
		
		for (int i=0;i<k;i++){
			System.out.println(incommingMatches.get(i).returnMatchAsString());
		}
		
		db.closeConnection();
	}
	
	
	//@Test
	public void test3() throws IOException {
		DatabaseManager db = new DatabaseManager();
		db.initConnection();
		
		List<FootballMatch> incommingMatches = db.getAllNewIncommingMatches();
		
		if (incommingMatches.size()>0){
			db.addFinalMatchResultToDatabase(incommingMatches.get(0));
		}
		
		db.closeConnection();
	}
	
	//@Test
	public void test4() throws IOException {
		DatabaseManager db = new DatabaseManager();
		db.initConnection();
		
		List<FootballMatch> incommingMatches = db.getAllNewIncommingMatchesByPeriod("2015-12-29", "2015-12-29");
		
		System.out.println("incommingMatches List Size : "+incommingMatches.size());
		
		// How Many Iteration If Possible, If not k = incommingMatches.size()
		int k = incommingMatches.size();
		
		if (incommingMatches.size()<k){
			k = incommingMatches.size();
		}
		
		for (int i=0;i<k;i++){
			System.out.println(incommingMatches.get(i).returnMatchAsString());
		}
		
		db.closeConnection();
	}
}
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
		
		//TODO Naprawić Id dla incomming matches bo coś nie mają nazw drużyny
		
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
		
		//TODO Naprawić Id dla incomming matches bo coś nie mają nazw drużyny
		
		int TeamAID = db.getTeamNamesIDByTeamName(db.getTeamNameByID(1577));
		int TeamBID = db.getTeamNamesIDByTeamName(db.getTeamNameByID(1578));
		
		System.out.println("TeamA id : "+TeamAID);
		System.out.println("TeamB id : "+TeamBID);
		
		db.closeConnection();
	}
	
	
	@Test
	public void test() throws IOException {
		DatabaseManager db = new DatabaseManager();
		db.initConnection();
		
		int TeamAName = 1577;
		int TeamBName = 1578;
		
		List<FootballMatch> TeamAWitoutTeamBMatchesResults = db.getMatchesResultsFromDatabase(TeamAName,TeamBName,false);
		List<FootballMatch> TeamBWithoutTeamAMatchesResults = db.getMatchesResultsFromDatabase(TeamBName,TeamAName,false);
		List<FootballMatch> TeamAAndTeamBMatchesResults = db.getMatchesResultsFromDatabase(TeamAName,TeamBName,true);
		List<FootballMatch> TeamBAndTeamAMatchesResults = db.getMatchesResultsFromDatabase(TeamBName,TeamAName,true);
		
		
		System.out.println("TeamAWitoutTeamBMatchesResults : ");
		
		for (int i=0;i<TeamAWitoutTeamBMatchesResults.size();i++){
			System.out.println(TeamAWitoutTeamBMatchesResults.get(i).returnMatchResult());
		}
		
		System.out.println("TeamBWithoutTeamAMatchesResults : ");
		
		for (int i=0;i<TeamBWithoutTeamAMatchesResults.size();i++){
			System.out.println(TeamBWithoutTeamAMatchesResults.get(i).returnMatchResult());
		}
		
		System.out.println("TeamAAndTeamBMatchesResults : ");
		
		for (int i=0;i<TeamAAndTeamBMatchesResults.size();i++){
			System.out.println(TeamAAndTeamBMatchesResults.get(i).returnMatchResult());
		}
		
		System.out.println("TeamBAndTeamAMatchesResults : ");
		
		for (int i=0;i<TeamBAndTeamAMatchesResults.size();i++){
			System.out.println(TeamBAndTeamAMatchesResults.get(i).returnMatchResult());
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
}
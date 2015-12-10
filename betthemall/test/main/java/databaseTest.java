import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import Database.addToDatabase;
import Logger.logMaker;
import Structure.footballMatch;

public class databaseTest {
	
	//@Test
	public void testNames() throws IOException {
		
		addToDatabase db = new addToDatabase();
		db.initConnection();
		
		//TODO Naprawić Id dla incomming matches bo coś nie mają nazw drużyny
		
		String TeamAName = db.getTeamNameByID(642);
		String TeamBName = db.getTeamNameByID(655);
		
		System.out.println("TeamA name : "+TeamAName);
		System.out.println("TeamB name : "+TeamBName);
		
		db.closeConnection();
	}
	
	
	@Test
	public void test() throws IOException {
		addToDatabase db = new addToDatabase();
		db.initConnection();
		
		int TeamAName = 846;
		int TeamBName = 847;
		
		List<footballMatch> TeamAWitoutTeamBMatchesResults = db.getMatchesResultsFromDatabase(TeamAName,TeamBName,false);
		List<footballMatch> TeamBWithoutTeamAMatchesResults = db.getMatchesResultsFromDatabase(TeamBName,TeamAName,false);
		List<footballMatch> TeamAAndTeamBMatchesResults = db.getMatchesResultsFromDatabase(TeamAName,TeamBName,true);
		List<footballMatch> TeamBAndTeamAMatchesResults = db.getMatchesResultsFromDatabase(TeamBName,TeamAName,true);
		
		
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
		addToDatabase db = new addToDatabase();
		db.initConnection();
		
		List<footballMatch> incommingMatches = db.getAllNewIncommingMatches();
		
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
		addToDatabase db = new addToDatabase();
		db.initConnection();
		
		List<footballMatch> incommingMatches = db.getAllNewIncommingMatches();
		
		db.addFinalMatchResultToDatabase(incommingMatches.get(0));
		
		db.closeConnection();
	}
}
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
	public void test() throws IOException {
		addToDatabase db = new addToDatabase();
		db.initConnection();
		
		String TeamAName = db.getTeamNameByID("846");
		String TeamBName = db.getTeamNameByID("847");
		
		System.out.println("TeamA name : "+TeamAName);
		System.out.println("TeamB name : "+TeamBName);
		
		List<footballMatch> TeamAWitoutTeamBMatchesResults = db.getMatchesResultsFromDatabase("846","847",false);
		List<footballMatch> TeamBWithoutTeamAMatchesResults = db.getMatchesResultsFromDatabase("847","846",false);
		List<footballMatch> TeamAAndTeamBMatchesResults = db.getMatchesResultsFromDatabase("846","847",true);
		List<footballMatch> TeamBAndTeamAMatchesResults = db.getMatchesResultsFromDatabase("847","846",true);
		
		
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
	
	@Test
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
}
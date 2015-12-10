package Database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import Batches.WorkerProcess;
import Logger.LogMaker;
import Structure.FootballMatch;

/**
 * Class addToDatabase connecting with database. Methods: adding match to
 * database, adding team to database
 */
public class DatabaseManager {

	private static final String HOST = "91.189.37.233";
	private static final String PORT = "5432";
	private static final String DATABASE_NAME = "betthemalldb";
	private static final String USERNAME = "root";
	private static final String PASSWORD = "wakacje";

	private static final String DRIVER = "org.postgresql.Driver";
	private static final String URL = "jdbc:postgresql://" + HOST + ":" + PORT + "/" + DATABASE_NAME;
	public Connection connection = null;
	
	static LogMaker logMaker = LogMaker.getInstance();

	public int addMatches = 0;
	public int addFinalMatches = 0;

	public Connection getConnection() {
		try {
			Class.forName(DRIVER);
		} catch (ClassNotFoundException ex) {
			logMaker.logError("You give me wrong path to postgresql!");
			return null;
		}
		Connection conn = null;
		try {
			conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			logMaker.logError("Cannot connect with Database");
			logMaker.logError(e.getMessage());
			// e.printStackTrace();
		}
		return conn;
	}

	public DatabaseManager() {

	}

	public void initConnection() {
		connection = getConnection();
		System.out.println("Connection accepted");
		logMaker.logInfo("Connection accepted");
	}

	public void closeConnection() {
		try {
			connection.close();
			System.out.println("Crawler :");
			logMaker.logInfo("Crawler :");
			System.out.println("Count of added matches: " + addMatches);
			logMaker.logInfo("Count of added matches: " + addMatches);
			System.out.println("Analyser :");
			logMaker.logInfo("Analyser :");
			System.out.println("Count of added final matches results: " + addFinalMatches);
			logMaker.logInfo("Count of added final matches results: " + addFinalMatches);
			System.out.println("Connection Closed.");
			logMaker.logInfo("Connection Closed.");

			// If Any Matches added or updated, change newData in Worker Process
			// to true
			if (addMatches > 0) {
				WorkerProcess.sendSignalNewDataSent();
			}

		} catch (SQLException e) {
			logMaker.logError("Problem with close connection");
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @param TeamID
	 * @return TeamName - if TeamID exist return name of team for that ID, else
	 *         return "Name not found";
	 */
	public String getTeamNameByID(int TeamID) {
		ResultSet rs = null;
		Statement stmt = null;
		String sql = null;
		String TeamName = "";
		boolean t;
		try {
			stmt = connection.createStatement();
			/** Select all matches results of teamA againts teamB **/
			sql = "SELECT \"Name\" FROM public.\"TEAM_NAMES\"" + "WHERE id = '" + TeamID + "'";

			rs = stmt.executeQuery(sql);

			t = rs.next();
			if (t) {
				TeamName = rs.getString("Name");
			} else {
				TeamName = "Name not found";
			}
		} catch (SQLException e) {
			logMaker.logError("SQL expression is wrong. <<class.addUnknownTeamToDatabse>>");
			logMaker.logError(e.getMessage());
			// e.printStackTrace();
		}
		return TeamName;
	}
	/**
	 * 
	 * 
	 * @return matchesList
	 */
	public List<FootballMatch> getAllNewIncommingMatches() {
		ResultSet rs = null;
		Statement stmt = null;
		String sql = null;
		List<FootballMatch> matchesList = new ArrayList();

		try {
			stmt = connection.createStatement();

			sql = "SELECT  id ,\"TeamA_ID\", \"TeamB_ID\", \"MatchDate\", \"WinA\",\"Draw\", \"WinB\", "
					+ "\"League\", \"Page\" FROM public.\"FOOTBALL_MATCHES\""
					+ "WHERE \"FINAL_FOOTBALL_MATCH_ID\" = -1";
			rs = stmt.executeQuery(sql);

			while (rs.next()) {
				// public footballMatch(String ID, String data, String teamA,
				// String teamB, double winA, double draw, double winB, String
				// league)
				String MatchID = rs.getString("ID");
				int TeamA = rs.getInt("TeamA_ID");
				int TeamB = rs.getInt("TeamB_ID");
				String MatchDate = rs.getString("MatchDate");
				double WinA = rs.getDouble("WinA");
				double Draw = rs.getDouble("Draw");
				double WinB = rs.getDouble("WinB");
				String League = rs.getString("League");
				FootballMatch match = new FootballMatch(MatchID, MatchDate, TeamA, TeamB, WinA, Draw, WinB, League);
				matchesList.add(match);
			}
		} catch (SQLException e) {
			logMaker.logError("SQL expression is wrong. <<class.addUnknownTeamToDatabse>>");
			logMaker.logError(e.getMessage());
			// e.printStackTrace();
		}
		return matchesList;
	}

	/** Get all matches results from database of teamA **/
	public List<FootballMatch> getMatchesResultsFromDatabase(int teamAID,int teamBID,
			boolean MatchesOnlyAgaintsTeamB) {
		ResultSet rs = null;
		Statement stmt = null;
		String sql = null;
		List<FootballMatch> matchesList = new ArrayList();
		// public footballMatch(String data, String teamA, String teamB, double
		// winA, double draw, double winB, String league);
		boolean t;
		try {
			stmt = connection.createStatement();

			if (MatchesOnlyAgaintsTeamB) {
				/** Select all matches results of teamA againts teamB **/
				sql = "SELECT \"TeamA_ID\", \"TeamB_ID\", \"MatchDate\", \"TeamA_Score\",\"TeamB_Score\", "
						+ "\"League\" FROM public.\"MATCHES_RESULTS\"" + "WHERE \"TeamA_ID\" = '" + teamAID
						+ "' and \"TeamB_ID\" = '" + teamBID + "';";
			} else {
				/**
				 * Select all matches results of teamA also without matches with
				 * teamB
				 **/
				sql = "SELECT \"TeamA_ID\", \"TeamB_ID\", \"MatchDate\", \"TeamA_Score\",\"TeamB_Score\", "
						+ "\"League\" FROM public.\"MATCHES_RESULTS\"" + "WHERE \"TeamA_ID\" = '" + teamAID
						+ "' and \"TeamB_ID\" != '" + teamBID + "';";
			}

			rs = stmt.executeQuery(sql);
			while (rs.next()) {
				// public footballMatch(String data, String teamA, String teamB,
				// int scoreA, int scoreB, String league) {
				int TeamA = rs.getInt("TeamA_ID");
				int TeamB = rs.getInt("TeamB_ID");
				String MatchDate = rs.getString("MatchDate");
				int TeamAScore = rs.getInt("TeamA_Score");
				int TeamBScore = rs.getInt("TeamB_Score");
				String League = rs.getString("League");
				FootballMatch match = new FootballMatch(MatchDate, TeamA, TeamB, TeamAScore, TeamBScore, League);
				matchesList.add(match);
			}
		} catch (SQLException e) {
			logMaker.logError("SQL expression is wrong. <<class.addUnknownTeamToDatabse>>");
			logMaker.logError(e.getMessage());
			// e.printStackTrace();
		}

		return matchesList;
	}

	/**
	 * Method which add match as line to database id TeamA_ID, TeamB_ID
	 * MatchDate, Draw, WinA, WinB
	 */
	public void addMatchToDatabase(FootballMatch match, String web) {
		if (connection != null) {
			ResultSet rs = null;
			Statement stmt = null;
			String sql = null;
			boolean t;
			try {
				stmt = connection.createStatement();

				int idA = match.returnTeamA();

				int idB = match.returnTeamB();

				/**
				 * If given match isn't exist in the database, insert match to
				 * the database
				 */
				sql = "SELECT * FROM public.\"FOOTBALL_MATCHES\" WHERE \"TeamA_ID\" = '" + idA + "' AND "
						+ "\"TeamB_ID\" = '" + idB + "' AND \"MatchDate\" = '" + match.returnDate()
						+ "' AND \"Page\" = '" + web + "'";
				rs = stmt.executeQuery(sql);
				t = rs.next();
				if (!t) {
					sql = "SELECT nextval('public.\"FOOTBALL_MATCHES_SEQ\"')";
					rs = stmt.executeQuery(sql);
					rs.next();
					int id = Integer.parseInt(rs.getString(rs.getRow()));

					sql = "INSERT INTO  public.\"FOOTBALL_MATCHES\" ( id ,\"TeamA_ID\", \"TeamB_ID\", \"MatchDate\", \"WinA\",\"Draw\", \"WinB\", "
							+ "\"WinApercent\",\"Drawpercent\", \"WinBpercent\", \"League\", \"Page\")" + "VALUES ("
							+ id + ", '" + idA + "', '" + idB + "', '" + match.returnDate() + "', '"
							+ match.returnOddForWinA() + "', '" + match.returnOddForDraw() + "'," + " '"
							+ match.returnOddForWinB() + "', '" + match.returnPercentForWinA() + "', '"
							+ match.returnPercentForDraw() + "'," + " '" + match.returnPercentForWinB() + "', '"
							+ match.returnLeague() + "', '" + web + "');";
					stmt.executeUpdate(sql);
					addMatches = addMatches + 1;
					// logs.logAdd("Add match " + match.returnMatchAsString() +
					// " to database");

				}
				/** Else print message this match already exist. */
				/*
				 * else if (t){ //logs.logWarrning( web +
				 * " This competition is already in database: "+
				 * match.returnMatchAsString()); }
				 */

			} catch (SQLException e) {
				logMaker.logError("SQL expression is wrong. <<class.addMatchToDatabse>>");
				logMaker.logError(e.getMessage());
				// e.printStackTrace();
			}
		} else
			logMaker.logError("Failed connection with database.");
	}

	public void addFinalMatchResultToDatabase(FootballMatch finalMatch) {
		if (connection != null) {
			ResultSet rs = null;
			Statement stmt = null;
			String sql = null;
			boolean t;
			try {
				stmt = connection.createStatement();

				System.out.println("New ID Selected");

				sql = "SELECT nextval('public.\"FINAL_FOOTBALL_MATCHES_SEQ\"');";
				rs = stmt.executeQuery(sql);
				rs.next();
				int id = Integer.parseInt(rs.getString(rs.getRow()));

				String TeamAName = getTeamNameByID(finalMatch.returnTeamA());
				String TeamBName = getTeamNameByID(finalMatch.returnTeamB());

				System.out.println("Team Names Selected");

				sql = "INSERT INTO  public.\"FINAL_FOOTBALL_MATCHES\" ( id ,\"TeamA_Name\", \"TeamB_Name\", \"MatchDate\", \"WinA\", \"Draw\", \"WinB\",\"League\")"
						+ "VALUES (" + id + ", '" + TeamAName + "', '" + TeamBName + "', '" + finalMatch.returnDate()
						+ "', '" + finalMatch.returnOddForWinA() + "', '" + finalMatch.returnOddForDraw() + "', '"
						+ finalMatch.returnOddForWinB() + "', '" + finalMatch.returnLeague() + "');";
				stmt.executeUpdate(sql);
				addFinalMatches = addFinalMatches + 1;

				System.out.println("FINAL_FOOTBAL_MATCH WITH ID " + id + " ADDED");

				sql = "UPDATE public.\"FOOTBALL_MATCHES\"" + "SET \"FINAL_FOOTBALL_MATCH_ID\" = '" + id + "'"
						+ "WHERE \"id\" = '" + finalMatch.returnID() + "';";
				stmt.executeUpdate(sql);

				System.out.println("FOOTBALL_MATCH WITH ID " + finalMatch.returnID() + " UPDATED");

			} catch (SQLException e) {
				System.out.println(e.getMessage());
				// logMaker.logError("SQL expression is wrong.
				// <<class.addMatchToDatabse>>");
				// logMaker.logError(e.getMessage());
				// e.printStackTrace();
			}
		} else
			logMaker.logError("Failed connection with database.");
	}
	
	/**
	 * 
	 * If Team Name exist for TeamID returns Team Name else returns -1
	 **/
	public int getTeamNamesIDByTeamName(String TeamName){
		if (connection != null) {
			ResultSet rs = null;
			Statement stmt = null;
			String sql = null;
			boolean t;
			try {
				stmt = connection.createStatement();

				/**
				 * If team A isn't exist in the database, insert team A to the
				 * database
				 */
				sql = "SELECT id FROM  public.\"TEAM_NAMES\" WHERE \"Name\" LIKE '" + TeamName + "'";// System.out.println(sql);
				rs = stmt.executeQuery(sql);
				t = rs.next();
				if (t){
					int id = rs.getInt("id");
					return id;
				}
			}
			catch (SQLException e) {
				logMaker.logError("SQL expression is wrong. <<class.addMatchToDatabse>>");
				logMaker.logError(e.getMessage());
				// e.printStackTrace();
			} 
		}
		else{
			logMaker.logError("Failed connection with database.");
		}
		return -1;
	}

	/**
	 * Method which add team as line to database id, TeamID, name
	 */
	public void addMatchResultToDatabase(FootballMatch match) {
		if (connection != null) {
			ResultSet rs = null;
			Statement stmt = null;
			String sql = null;
			boolean t;
			try {
				stmt = connection.createStatement();

				int idA = match.returnTeamA();

				int idB = match.returnTeamB();

				/**
				 * If given match isn't exist in the database, insert match to
				 * the database
				 */
				sql = "SELECT * FROM public.\"MATCHES_RESULTS\" WHERE \"TeamA_ID\" = '" + idA + "' AND "
						+ "\"TeamB_ID\" = '" + idB + "' AND \"MatchDate\" = '" + match.returnDate() + "'";
				rs = stmt.executeQuery(sql);
				t = rs.next();
				if (!t) {
					sql = "SELECT nextval('public.\"MATCHES_RESULTS_SEQ\"')";
					rs = stmt.executeQuery(sql);
					rs.next();
					int id = Integer.parseInt(rs.getString(rs.getRow()));

					sql = "INSERT INTO public.\"MATCHES_RESULTS\" ( id ,\"TeamA_ID\", \"TeamB_ID\", \"TeamA_Score\", \"TeamB_Score\",\"MatchDate\", \"League\")"
							+ "VALUES (" + id + ", '" + idA + "', '" + idB + "', '" + match.returnScoreA() + "', '"
							+ match.returnScoreB() + "', '" + match.returnDate() + "', '" + match.returnLeague()
							+ "');";
					stmt.executeUpdate(sql);
					addMatches = addMatches + 1;
					// logs.logAdd("Add match " + match.returnMatchAsString() +
					// " to database");

				}
				/** Else print message this match already exist. */
				/*
				 * else if (t){ //logs.logWarrning( web +
				 * " This competition is already in database: "+
				 * match.returnMatchAsString()); }
				 */

			} catch (SQLException e) {
				logMaker.logError("SQL expression is wrong. <<class.addMatchToDatabse>>");
				logMaker.logError(e.getMessage());
				// e.printStackTrace();
			}
		} else
			logMaker.logError("Failed connection with database.");
	}

	public int addUnknownTeamNameToDatabaseAndGetNewTeamID(String name, String league) {
		ResultSet rs = null;
		Statement stmt = null;
		String sql = null;
		int id = -1;
		boolean t;
		try {
			stmt = connection.createStatement();

			/**
			 * Check if given Team Name exist in the database. If not, insert
			 * Team name to the database.
			 */
			sql = "SELECT id, \"Name\" FROM public.\"TEAM_NAMES\" WHERE \"Name\" like '" + name + "';";
			rs = stmt.executeQuery(sql);
			t = rs.next();
			if (!t) {
				sql = "SELECT nextval('public.\"TEAM_NAMES_SEQ\"')";
				rs = stmt.executeQuery(sql);
				rs.next();
				id = Integer.parseInt(rs.getString(rs.getRow()));
				sql = "INSERT INTO  public.\"TEAM_NAMES\"( id, \"League\", \"Name\", \"OriginalTeamID\")" + "VALUES (" + id + ", '" + league
						+ "'" + ", '" + name + "'" + ", '" + id + "');";
				stmt.executeUpdate(sql);
				logMaker.logAdd("Add unknown team " + name + " to database");

			}
			/** Else print message this team name already exist. */
			else if (t) {
				logMaker.logWarrning("This team is already in database: " + name);
			}
		} catch (SQLException e) {
			logMaker.logError("SQL expression is wrong. <<class.addUnknownTeamToDatabse>>");
			logMaker.logError(e.getMessage());
			// e.printStackTrace();
		}
		
		return id;
	}
}

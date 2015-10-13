import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

	/**Class addToDatabase connecting with database.
	 * Methods:
	 * 		adding match to database,
	 * 		adding team to database*/
public class addToDatabase {
	
	 private static final String DRIVER = "org.postgresql.Driver";   
	 private static final String URL = "jdbc:postgresql://ec2-23-23-210-37.compute-1.amazonaws.com:5432/d51p1baokcdgcm?ssl=true&sslfactory=org.postgresql.ssl.NonValidatingFactory";   
	 private static final String USERNAME = "bdwqkpvebkleol";   
	 private static final String PASSWORD = "QtZOLTMuD-13V1OOsw_dthxqRB"; 
	 public Connection connection = null;
	 
	 public int updateMatches = 0;
	 public int addMatches = 0;
	 public Connection getConnection(){   
		 try {   
			 Class.forName(DRIVER);   
		 } 
		 catch (ClassNotFoundException ex) {   
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
			//e.printStackTrace();
		}   
	    return conn;   
	 } 
	 
	 public addToDatabase(){
		
	 } 
	 	 
	public void initConnection() {
		connection = getConnection();  
		logMaker.logInfo("Connection accepted"); 	
	}
	
	 public void closeConnection() {
		 try {
			connection.close();
			logMaker.logInfo("Count of added matches: " + addMatches);
			logMaker.logInfo("Count of updated matches: " + updateMatches); 
			logMaker.logInfo("Connection Closed."); 
		} catch (SQLException e) {
			logMaker.logError("Problem with close connection");   
			e.printStackTrace();
		}
	 }
	 
	 /** Method which add match as line to database
	  * 	id
	  *  	TeamA_ID, TeamB_ID
	  *  	MatchDate,
	  *  	Draw, WinA, WinB
	  *  */
	 public void addMatchToDatabase(footballMatch match, String web) {
	 		 if (connection != null) {   
	 			    ResultSet rs = null;
	 			    Statement stmt = null;
	 			    String sql = null;
	 			    boolean t;
		         try {
					stmt = connection.createStatement();
					
					
					/**If team A isn't exist in the database, insert team A to the database*/
					sql = "SELECT id FROM  public.\"TEAM_NAMES\" WHERE \"Name\" LIKE '" + match.returnTeamA() + "'";//System.out.println(sql);
					rs = stmt.executeQuery(sql);
					t = rs.next();
					if(!t) {
						addUnknownTeamNameToDatabase(match.returnTeamA());
						rs = stmt.executeQuery(sql);
						rs.next();
					}
					int idA = rs.getInt("id");
					
					
					/**If team A isn't exist in the database, insert team B to the database.*/
					sql = "SELECT id FROM  public.\"TEAM_NAMES\" WHERE \"Name\" LIKE '" + match.returnTeamB() + "'";//System.out.println(sql);
		 			rs = stmt.executeQuery(sql);
					t = rs.next();
		 			if(!t) {
						addUnknownTeamNameToDatabase(match.returnTeamB());
						rs = stmt.executeQuery(sql);
						rs.next();
					}
					int idB = rs.getInt("id");
					
					
					/**If given match isn't exist in the database, insert match to the database*/
					sql = "SELECT * FROM public.\"FOOTBALL_MATCHES\" WHERE \"TeamA_ID\" = '" + idA + "' AND "
							+ "\"TeamB_ID\" = '" + idB +"' AND \"MatchDate\" = '" + match.returnDate()+"' AND \"Page\" = '" + web + "'";
					rs = stmt.executeQuery(sql);
					t = rs.next();
					if (!t) {
					    sql = "SELECT nextval('public.\"FOOTBALL_MATCHES_SEQ\"')";
					    rs = stmt.executeQuery(sql);
						rs.next();
						int id = Integer.parseInt(rs.getString(rs.getRow()));

						sql = "INSERT INTO  public.\"FOOTBALL_MATCHES\" ( id ,\"TeamA_ID\", \"TeamB_ID\", \"MatchDate\", \"WinA\",\"Draw\", \"WinB\", "
								+ "\"WinApercent\",\"Drawpercent\", \"WinBpercent\", \"League\", \"Page\")"
								+ "VALUES ("+ id +", '" + idA +"', '" + idB +"', '" + match.returnDate() +"', '" 
											+ match.returnOddForWinA() +"', '" + match.returnOddForDraw() + "',"+ " '" + match.returnOddForWinB() + "', '"
											+ match.returnPercentForWinA() +"', '" + match.returnPercentForDraw() + "',"+ " '" + match.returnPercentForWinB() + "', '"
											+ match.returnLeague() + "', '"+ web +"');";
						stmt.executeUpdate(sql);
						addMatches = addMatches + 1;
						//logs.logAdd("Add match " + match.returnMatchAsString() + " to database");

					}
					/**Else print message this match already exist.*/
					else if (t){
						updateMatches = updateMatches + 1;
						//logs.logWarrning(  web + " This competition is already in database: "+ match.returnMatchAsString());
					}
					
				
				} catch (SQLException e) {
					logMaker.logError("SQL expression is wrong. <<class.addMatchToDatabse>>");
					logMaker.logError(e.getMessage());
					//e.printStackTrace();
				}
	 		 }
	 		 else 
	 			logMaker.logError("Failed connection with database.");
	 	}

		 /** Method which add team as line to database
		  * 	id,
		  *  	TeamID,
		  *  	name
		  *  */
	 
	 public void addMatchResultToDatabase(footballMatch match) {
 		 if (connection != null) {   
 			    ResultSet rs = null;
 			    Statement stmt = null;
 			    String sql = null;
 			    boolean t;
	         try {
				stmt = connection.createStatement();
				
				
				/**If team A isn't exist in the database, insert team A to the database*/
				sql = "SELECT id FROM  public.\"TEAM_NAMES\" WHERE \"Name\" LIKE '" + match.returnTeamA() + "'";//System.out.println(sql);
				rs = stmt.executeQuery(sql);
				t = rs.next();
				if(!t) {
					addUnknownTeamNameToDatabase(match.returnTeamA());
					rs = stmt.executeQuery(sql);
					rs.next();
				}
				int idA = rs.getInt("id");
				
				
				/**If given match isn't exist in the database, insert match to the database*/
				sql = "SELECT id FROM  public.\"TEAM_NAMES\" WHERE \"Name\" LIKE '" + match.returnTeamB() + "'";//System.out.println(sql);
	 			rs = stmt.executeQuery(sql);
				t = rs.next();
	 			if(!t) {
					addUnknownTeamNameToDatabase(match.returnTeamB());
					rs = stmt.executeQuery(sql);
					rs.next();
				}
				int idB = rs.getInt("id");
				
				
				/**If given match isn't exist in the database, insert match to the database*/
				sql = "SELECT * FROM public.\"MATCHES_RESULTS\" WHERE \"TeamA_ID\" = '" + idA + "' AND "
						+ "\"TeamB_ID\" = '" + idB +"' AND \"MatchDate\" = '" + match.returnDate()+"'";
				rs = stmt.executeQuery(sql);
				t = rs.next();
				if (!t) {
				    sql = "SELECT nextval('public.\"MATCHES_RESULTS_SEQ\"')";
				    rs = stmt.executeQuery(sql);
					rs.next();
					int id = Integer.parseInt(rs.getString(rs.getRow()));

					sql = "INSERT INTO  public.\"MATCHES_RESULTS\" ( id ,\"TeamA_ID\", \"TeamB_ID\", \"TeamA_Score\", \"TeamB_Score\",\"MatchDate\", \"League\")"
							+ "VALUES ("+ id +", '" + idA +"', '" + idB +"', '" + match.returnScoreA() +"', '" 
							+	match.returnScoreB() +"', '" + match.returnDate() +"', '" + match.returnLeague() + "');";
					stmt.executeUpdate(sql);
					addMatches = addMatches + 1;
					//logs.logAdd("Add match " + match.returnMatchAsString() + " to database");

				}
				/**Else print message this match already exist.*/
				else if (t){
					updateMatches = updateMatches + 1;
					//logs.logWarrning(  web + " This competition is already in database: "+ match.returnMatchAsString());
				}
				
			
			} catch (SQLException e) {
				logMaker.logError("SQL expression is wrong. <<class.addMatchToDatabse>>");
				logMaker.logError(e.getMessage());
				//e.printStackTrace();
			}
 		 }
 		 else 
 			logMaker.logError("Failed connection with database.");
 	}
	 
		public void addTeamNameToDatabase(String name) {
				ResultSet rs = null;
			    Statement stmt = null;
			    String sql = null;
			    boolean t;
				try {
					stmt = connection.createStatement();
					
					/**Check if given Team Name exist in the database. If not, insert Team name to the database.*/
					sql = "SELECT id, \"TeamID\", \"Name\"FROM public.\"TEAM_NAMES\" WHERE \"Name\" like '" + name +"';";
					rs = stmt.executeQuery(sql);
					t = rs.next();
					if (!t) {
						sql = "SELECT nextval('public.\"TEAM_NAMES_SEQ\"')";
						rs = stmt.executeQuery(sql);
						rs.next();
						int id = Integer.parseInt(rs.getString(rs.getRow()));
						sql = "INSERT INTO  public.\"TEAM_NAMES\"( id, \"TeamID\", \"Name\")"
								+ "VALUES (" + id + ", '" + id + "', '" + name + "');";
						stmt.executeUpdate(sql);
						System.out.println("Add team " + name + " to database");

					}
					/**Else print message this team name already exist.*/
					else if (t){
						logMaker.logWarrning("This team is already in database: " + name);
					}
				} 
				catch (SQLException e) {
					logMaker.logError("SQL expression is wrong. <<class.addTeamToDatabse>>");
					logMaker.logError(e.getMessage());
					//e.printStackTrace();
				}
		}
		
		public void addUnknownTeamNameToDatabase(String name) {
					ResultSet rs = null;
				    Statement stmt = null;
				    String sql = null;
				    boolean t;
					try {
						stmt = connection.createStatement();
						
						/**Check if given Team Name exist in the database. If not, insert Team name to the database.*/
						sql = "SELECT id, \"TeamID\", \"Name\"FROM public.\"TEAM_NAMES\" WHERE \"Name\" like '" + name +"';";
						rs = stmt.executeQuery(sql);
						t = rs.next();
						if (!t) {
							sql = "SELECT nextval('public.\"TEAM_NAMES_SEQ\"')";
							rs = stmt.executeQuery(sql);
							rs.next();
							int id = Integer.parseInt(rs.getString(rs.getRow()));
							sql = "INSERT INTO  public.\"TEAM_NAMES\"( id, \"TeamID\", \"Name\")"
									+ "VALUES (" + id + ", null, '" + name + "');";
							stmt.executeUpdate(sql);
							logMaker.logAdd("Add unknown team " + name + " to database");

						}
						/**Else print message this team name already exist.*/
						else if (t){
							logMaker.logWarrning("This team is already in database: " + name);
						}
					} 
					catch (SQLException e) {
						logMaker.logError("SQL expression is wrong. <<class.addUnknownTeamToDatabse>>");
						logMaker.logError(e.getMessage());
						//e.printStackTrace();
					}
		}
}

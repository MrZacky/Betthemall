import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

	/**Klasa addToDatabase ³¹czy siê z baz¹ danych.
	 * Posiada metody:
	 * 		dodania meczu do bazy danych,
	 * 		dodania dru¿yny do bazy
	 * 		wkrótce wiêcej ;)*/
public class addToDatabase {
	
	 private static final String DRIVER = "org.postgresql.Driver";   
	 private static final String URL = "jdbc:postgresql://ec2-23-23-210-37.compute-1.amazonaws.com:5432/d51p1baokcdgcm?ssl=true&sslfactory=org.postgresql.ssl.NonValidatingFactory";   
	 private static final String USERNAME = "bdwqkpvebkleol";   
	 private static final String PASSWORD = "QtZOLTMuD-13V1OOsw_dthxqRB"; 
	 public Connection connection = null;
	 private logMaker logs = new logMaker();
	 
	 public int updateMatches = 0;
	 public int addMatches = 0;
	 public Connection getConnection() throws SQLException {   
		 try {   
			 Class.forName(DRIVER);   
		 } 
		 catch (ClassNotFoundException ex) {   
			 logs.logError("You give me wrong path to postgresql!");   
			 return null;   
	    }   
	    Connection conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);   
	    return conn;   
	 } 
	 
	 /**Konstruktor klasy addToDatabase
	  * ³¹czy siê z baz¹ danych
	 * @throws FileNotFoundException */
	 public addToDatabase(){
		
	 } 
	 	 
	public void initConnection() {
		try {   
			connection = getConnection();   
			logs.logInfo("Connection accepted");
		}
		catch (SQLException ex) {   
			logs.logError("Connection rejected");   
		}  	
	}
	
	 public void closeConnection() {
		 try {
			connection.close();
			logs.logInfo("Count of added matches: " + addMatches);
			logs.logInfo("Count of updated matches: " + updateMatches); 
			logs.logInfo("Connection Closed."); 
		} catch (SQLException e) {
			logs.logError("Problem with close connection");   
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
					
					
					/**Jeœli w bazie nie ma dru¿yny A to j¹ dodaje do bazy.*/
					sql = "SELECT id FROM  public.\"TEAM_NAMES\" WHERE \"Name\" LIKE '" + match.returnTeamA() + "'";//System.out.println(sql);
					rs = stmt.executeQuery(sql);
					t = rs.next();
					if(!t) {
						addUnknownTeamNameToDatabase(match.returnTeamA());
						rs = stmt.executeQuery(sql);
						rs.next();
					}
					int idA = rs.getInt("id");
					
					
					/**Jeœli w bazie nie ma dru¿yny B to j¹ dodaje do bazy.*/
					sql = "SELECT id FROM  public.\"TEAM_NAMES\" WHERE \"Name\" LIKE '" + match.returnTeamB() + "'";//System.out.println(sql);
		 			rs = stmt.executeQuery(sql);
					t = rs.next();
		 			if(!t) {
						addUnknownTeamNameToDatabase(match.returnTeamB());
						rs = stmt.executeQuery(sql);
						rs.next();
					}
					int idB = rs.getInt("id");
					
					
					/**Jeœli danego meczu nie ma jeszcze w bazie to go dodaje do bazy*/
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
					/**W przeciwnym wypadku wyœwietlam komunikat, ¿e taki mecz ju¿ istnieje.*/
					else if (t){
						updateMatches = updateMatches + 1;
						//logs.logWarrning(  web + " This competition is already in database: "+ match.returnMatchAsString());
					}
					
				
				} catch (SQLException e) {
					logs.logError("SQL expression is wrong. <<class.addMatchToDatabse>>");
					e.printStackTrace();
				}
	 		 }
	 		 else 
	 			 logs.logError("Failed connection with database.");
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
				
				
				/**Jeœli w bazie nie ma dru¿yny A to j¹ dodaje do bazy.*/
				sql = "SELECT id FROM  public.\"TEAM_NAMES\" WHERE \"Name\" LIKE '" + match.returnTeamA() + "'";//System.out.println(sql);
				rs = stmt.executeQuery(sql);
				t = rs.next();
				if(!t) {
					addUnknownTeamNameToDatabase(match.returnTeamA());
					rs = stmt.executeQuery(sql);
					rs.next();
				}
				int idA = rs.getInt("id");
				
				
				/**Jeœli w bazie nie ma dru¿yny B to j¹ dodaje do bazy.*/
				sql = "SELECT id FROM  public.\"TEAM_NAMES\" WHERE \"Name\" LIKE '" + match.returnTeamB() + "'";//System.out.println(sql);
	 			rs = stmt.executeQuery(sql);
				t = rs.next();
	 			if(!t) {
					addUnknownTeamNameToDatabase(match.returnTeamB());
					rs = stmt.executeQuery(sql);
					rs.next();
				}
				int idB = rs.getInt("id");
				
				
				/**Jeœli danego meczu nie ma jeszcze w bazie to go dodaje do bazy*/
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
				/**W przeciwnym wypadku wyœwietlam komunikat, ¿e taki mecz ju¿ istnieje.*/
				else if (t){
					updateMatches = updateMatches + 1;
					//logs.logWarrning(  web + " This competition is already in database: "+ match.returnMatchAsString());
				}
				
			
			} catch (SQLException e) {
				logs.logError("SQL expression is wrong. <<class.addMatchToDatabse>>");
				e.printStackTrace();
			}
 		 }
 		 else 
 			 logs.logError("Failed connection with database.");
 	}
	 
		public void addTeamNameToDatabase(String name) {
				ResultSet rs = null;
			    Statement stmt = null;
			    String sql = null;
			    boolean t;
				try {
					stmt = connection.createStatement();
					
					/**Sprawdzam, czy ta dru¿yna istnieje ju¿ w bazie danych. Jeœli nie to j¹ dodaje do bazy*/
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
					/**W przeciwnym wypadku wyœwietlam komunikat, ¿e taka dru¿yna ju¿ istnieje w bazie danych.*/
					else if (t){
						logs.logWarrning("This team is already in database: " + name);
					}
				} 
				catch (SQLException e) {
					logs.logError("SQL expression is wrong. <<class.addTeamToDatabse>>");
					e.printStackTrace();
				}
		}
		
		public void addUnknownTeamNameToDatabase(String name) {
					ResultSet rs = null;
				    Statement stmt = null;
				    String sql = null;
				    boolean t;
					try {
						stmt = connection.createStatement();
						
						/**Sprawdzam, czy ta dru¿yna istnieje ju¿ w bazie danych. Jeœli nie to j¹ dodaje do bazy*/
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
							logs.logAdd("Add unknown team " + name + " to database");

						}
						/**W przeciwnym wypadku wyœwietlam komunikat, ¿e taka dru¿yna ju¿ istnieje w bazie danych.*/
						else if (t){
							logs.logWarrning("This team is already in database: " + name);
						}
					} 
					catch (SQLException e) {
						logs.logError("SQL expression is wrong. <<class.addUnknownTeamToDatabse>>");
						e.printStackTrace();
					}
		}
}

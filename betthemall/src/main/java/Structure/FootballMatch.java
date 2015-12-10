package Structure;
public class FootballMatch {
	
		private String ID;
		private String data;
		private int teamAID;
		private int teamBID;
		private double winA;
		private double draw;
		private double winB;
		private int scoreA;
		private int scoreB;
		private double winApercent;
		private double drawpercent;
		private double winBpercent;
		private String league;
		
		public FootballMatch(String data, int teamAID, int teamBID,  double winA, double draw, double winB, String league) {
			this.data  = data;
			this.teamAID = teamAID;
			this.teamBID = teamBID;
			this.winA = winA;
			this.draw = draw;
			this.winB = winB;
			this.league = league;
			changeOddToPerCent();
		}
		
		public FootballMatch(String ID, String data, int teamAID, int teamBID,  double winA, double draw, double winB, String league) {
			this.ID = ID;
			this.data  = data;
			this.teamAID = teamAID;
			this.teamBID = teamBID;
			this.winA = winA;
			this.draw = draw;
			this.winB = winB;
			this.league = league;
			changeOddToPerCent();
		}
		
		public FootballMatch(String data, int teamAID, int teamBID,  int scoreA, int scoreB, String league) {
			this.data  = data;
			this.teamAID = teamAID;
			this.teamBID = teamBID;
			this.scoreA = scoreA;
			this.scoreB = scoreB;
			this.league = league;

		}
		
		public void changeOddToPerCent() {
			winApercent = 1 /winA;
			winBpercent = 1 /winB;
			drawpercent = 1 /draw;
			double sum = winApercent + winBpercent + drawpercent;
			winApercent = 100*winApercent/sum;
			winBpercent = 100*winBpercent/sum;
			drawpercent = 100*drawpercent/sum;
		}
		public String returnID(){
			return ID;
		}
		public int returnTeamA(){
			return teamAID;
		}
		public int returnTeamB(){
			return teamBID;
		}
		public String returnDate(){
			return data;
		}
		public String returnLeague(){
			return league;
		}
		public double returnOddForDraw(){
			return draw;
		}
		public double returnOddForWinA(){
			return winA;
		}
		public double returnOddForWinB(){
			return winB;
		}
	
		public double returnPercentForDraw(){
			return drawpercent;
		}
		public double returnPercentForWinA(){
			return winApercent;
		}
		public double returnPercentForWinB(){
			return winBpercent;
		}
		public int returnScoreA(){
			return scoreA;
		}
		public int returnScoreB(){
			return scoreB;
		}
		
		public String returnFullMatchAsString() {
			return (ID + " - " + teamAID + " - " + teamBID + " " + data + " " + winA + " " + winApercent + " "  + draw + " " + drawpercent + " "  + winB + " " + winBpercent + " " + league);
		}
		
		public String returnMatchAsString() {
			return (ID + " - " + teamAID + " - " + teamBID + " " + data + " " + winA + " " + draw + " " + winB + " " + league);
		}
		
		public String returnMatchResult() {
			return (data + " " + teamAID + " - " + teamBID + " " + scoreA + "-" + scoreB + " " + league);
		}
		
		
}

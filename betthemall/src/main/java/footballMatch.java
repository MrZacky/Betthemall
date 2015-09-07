public class footballMatch {
	
		private String data;
		private String teamA;
		private String teamB;
		private double winA;
		private double draw;
		private double winB;
		private int scoreA;
		private int scoreB;
		private double winApercent;
		private double drawpercent;
		private double winBpercent;
		private String league;
		
		public footballMatch(String data, String teamA, String teamB,  double winA, double draw,double winB, String league) {
			this.data  = data;
			this.teamA = teamA;
			this.teamB = teamB;
			this.winA = winA;
			this.draw = draw;
			this.winB = winB;
			this.league = league;
			changeOddToPerCent();
		}
		
		public footballMatch(String data, String teamA, String teamB,  int scoreA, int scoreB, String league) {
			this.data  = data;
			this.teamA = teamA;
			this.teamB = teamB;
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
		
		public String returnTeamA(){
			return teamA;
		}
		public String returnTeamB(){
			return teamB;
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
			return (teamA + " - " + teamB + " " + data + " " + winA + " " + winApercent + " "  + draw + " " + drawpercent + " "  + winB + " " + winBpercent + " " + league);
		}
		
		public String returnMatchAsString() {
			return (teamA + " - " + teamB + " " + data + " " + winA + " " + draw + " " + winB + " " + league);
		}
		
		public String returnMatchResult() {
			return (data + " " + teamA + " - " + teamB + " " + scoreA + "-" + scoreB + " " + league);
		}
		
		
}

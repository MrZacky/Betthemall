import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Test;


public class parserOddsRingTest {
//28/12 17:05 Southampton FC - Chelsea Londyn 4.60 2000 PLN 3.63 697 PLN 1.76 11600 PLN Wiêcej (8)...
	parseOddsRing parser = new parseOddsRing();


	@Test 
	public void testHomeAndAwayTeam() throws IOException {
		assertEquals(parser.homeTeam("Tottenham Hotspur - Manchester United"), "Tottenham Hotspur");
		assertEquals(parser.awayTeam("Tottenham Hotspur - Manchester United"), "Manchester United");
	}
	
	@Test 
	public void testChangeDate() throws IOException {
		assertEquals(parser.changeDate("13/01"), "2015-01-13");
		assertEquals(parser.changeDate("28/12"), "2014-12-28");
	}
	
	@Test 
	public void otherTests() throws IOException {
		assertEquals(parser.changeOdd("3.30"), 3.30, 2.220446E-16);
	}
	
	@Test 
	public void init() throws IOException {
		parser.init();
	}

}

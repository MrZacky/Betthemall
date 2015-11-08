
import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Test;

import Analyser.IncommingMatchesParsers.parseWilliamHill;


public class parserWilliamHillTest {
	//Leicester � ? ��Tottenham  3.30 3.40 2.15 + 222 wi�cej   

	parseWilliamHill parser= new parseWilliamHill();
	
	@Test 
	public void testHomeAndAwayTeam() throws IOException {
		//assertEquals(parser.homeTeam("Crystal Palace &nbsp; v &nbsp;&nbsp;Southampton"), "Crystal Palace");
		//assertEquals(parser.awayTeam("Crystal Palace &nbsp; v &nbsp;&nbsp;Southampton"), "Southampton");
	}
	

	@Test 
	public void testChangeDate() throws IOException {
		assertEquals(parser.changeDate("23 Pa�"), "2015-10-23");
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

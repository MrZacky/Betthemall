import static org.junit.Assert.*;
import java.io.IOException;
import org.junit.Test;

import Analyser.IncommingMatchesParsers.parseSoccerRating;


public class parserSoccerRatingTest {

	parseSoccerRating parser = new parseSoccerRating();
	
	@Test 
	public void testHomeAndAwayTeam() throws IOException {
		assertEquals(parser.homeTeam("Liverpool FC - Manchester United"), "Liverpool FC");
		assertEquals(parser.awayTeam("Liverpool FC - Manchester United"), "Manchester United");
	}
	
	@Test 
	public void testCompareDateWithToday() throws IOException {
		assertEquals(parser.compareDateWithToday("2000-01-01"), false);
		assertEquals(parser.compareDateWithToday("2999-12-12"), true);
	}

	@Test 
	public void testChangeDate() throws IOException {
		assertEquals(parser.changeDate("23.09.12"), "2012-09-23");
	}
	
	@Test 
	public void init() throws IOException {
		parser.init();
	}

}

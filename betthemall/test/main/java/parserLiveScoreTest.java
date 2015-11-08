import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Test;

import Crawler.MatchesResultsParsers.parseLiveScore;


public class parserLiveScoreTest {

	@Test
	public void test() throws IOException {
		parseLiveScore parser = new parseLiveScore();
		//System.out.println(parser.changeDate("Sunday 28 December 2014"));
		parser.init();
	}

}

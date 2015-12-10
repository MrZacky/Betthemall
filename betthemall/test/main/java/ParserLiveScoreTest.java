import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Test;

import Crawler.MatchesResultsParsers.ParseLiveScore;


public class ParserLiveScoreTest {

	@Test
	public void test() throws IOException {
		ParseLiveScore parser = new ParseLiveScore();
		//System.out.println(parser.changeDate("Sunday 28 December 2014"));
		parser.init();
	}

}

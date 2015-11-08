import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Test;

import Analyser.IncommingMatchesParsers.parseBetAtHome;


public class parserBetAtHomeTest {
//West Ham United - West Bromwich Albionlive 01.01.15 16:00 1.85 3.47 4.17
	@Test
	public void test() throws IOException {
		parseBetAtHome parser = new parseBetAtHome();
		parser.init();
	}

}

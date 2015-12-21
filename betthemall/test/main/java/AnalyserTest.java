import java.io.IOException;
import org.junit.Test;

import Analaser.Analyser;

public class AnalyserTest {

	@Test
	public void test() throws IOException{
		Analyser analyser = new Analyser();
		
		int k = 10;
		analyser.init(k);
		//System.out.println(parser.changeDate("Sunday 28 December 2014"));
		
		analyser.CalculateMatchesResultsForKMatches(k);
		
	}

}

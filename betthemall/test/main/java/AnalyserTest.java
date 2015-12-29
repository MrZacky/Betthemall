import java.io.IOException;
import org.junit.Test;

import Analaser.Analyser;

public class AnalyserTest {

	@Test
	public void KMatchesAnalyseWithoutAddingToDatabase() throws IOException{
		Analyser analyser = new Analyser();
		
		String dateFrom = "2015-12-20";
		String dateTo = "2015-12-26";
		analyser.init(dateFrom,dateTo,true);
		
	}
	
	//@Test
	public void KMatchesAnalyseAndAddingThemToDatabase() throws IOException{
		Analyser analyser = new Analyser();
		
		int k = 2;
		analyser.init(k,true);
		
	}

}

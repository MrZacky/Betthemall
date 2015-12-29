import java.io.IOException;
import org.junit.Test;

import Analaser.Analyser;

public class AnalyserTest {

	@Test
	public void KMatchesAnalyseWithoutAddingToDatabase() throws IOException{
		Analyser analyser = new Analyser();
		
		int k = 1000000000;
		analyser.init(k,false);
		
	}
	
	//@Test
	public void KMatchesAnalyseAndAddingThemToDatabase() throws IOException{
		Analyser analyser = new Analyser();
		
		int k = 2;
		analyser.init(k,true);
		
	}

}

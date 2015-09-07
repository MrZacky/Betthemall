import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

public class WorkerProcess
{
	/** Czas podawany w milisekundach 1 sekunda = 1000 **/
	// Uruchamianie co godzinê od startu aplikacji
	static int time = 1000*60*60; 

    public static void main(String[] args) throws IOException
    {
    	Boolean error;
    	while(true){
    		System.out.println("Starting a process...");
    		error = false;
    		
    		try {
    			new parseBetAtHome().init();
    			System.out.println("Parsing BetAtHome completed successfully");
			} catch (Exception e) {
				System.out.println("During parsing BetAtHome exception occurred");
				e.printStackTrace();
				error=true;
			}
    		try {
    			new parseBetClic().init();
    			System.out.println("Parsing BetClic completed successfully");
			} catch (Exception e) {
				System.out.println("During parsing BetClic exception occurred");
				e.printStackTrace();
				error=true;
			}
    		
    		try {
    			new parseExpekt().init();
    			System.out.println("Parsing Expekt completed successfully");
			} catch (Exception e) {
				System.out.println("During parsing Expekt exception occurred");
				e.printStackTrace();
				error=true;
			}
    		try {
    			new parseLiveScore().init();
    			System.out.println("Parsing LiveScore completed successfully");
			} catch (Exception e) {
				System.out.println("During parsing LiveScore exception occurred");
				e.printStackTrace();
				error=true;
			}
    		try {
    			new parseOddsRing().init();
    			System.out.println("Parsing OddsRing completed successfully");
			} catch (Exception e) {
				System.out.println("During parsing OddsRing exception occurred");
				e.printStackTrace();
				error=true;
			}
    		try {
    			new parseSoccerRating().init();	
    			System.out.println("Parsing SoccerRating completed successfully");
			} catch (Exception e) {
				System.out.println("During parsing SoccerRating exception occurred");
				e.printStackTrace();
				error=true;
				
			}
    		try {
    			new parseWilliamHill().init();
    			System.out.println("Parsing WilliamHill completed successfully");
			} catch (Exception e) {
				System.out.println("During parsing WilliamHill exception occurred");
				e.printStackTrace();
				error=true;
			}
    		
    		
    		
   		 	if (!error){
   		 		System.out.println("Process completed successfully");
   		 	}
   		 	else{
   		 		System.out.println("Process completed, but errors occurred");
   		 	}
   		    int style = DateFormat.FULL;
   		 	Date RunDate = new Date();
   		    DateFormat df = DateFormat.getDateInstance(style, new Locale("pl", "PL"));
   		 	RunDate.setTime(RunDate.getTime()+time);
   		    df.format(RunDate);
   		 	System.out.println("Next RunDate : "+RunDate);
    		try {
    			Thread.sleep(time);
			} catch (Exception e) {
				
			}
    	}
    }
}
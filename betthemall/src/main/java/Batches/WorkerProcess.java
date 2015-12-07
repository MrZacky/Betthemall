package Batches;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

import Analaser.Analyser;
import Crawler.IncommingMatchesParsers.parseSoccerRating;
import Crawler.MatchesResultsParsers.parseLiveScore;

public class WorkerProcess
{
	/** Czas podawany w milisekundach 1 sekunda = 1000 **/
	// Uruchamianie co [time] od startu aplikacji
	static int time = 1000*60*60; // Proces uruchamiany co godzinę
	private static Boolean newData = false;

	
	public static void sendSignalNewDataSent(){
		newData = true;
	}
	
    public static void main(String[] args) throws IOException
    {
    	Boolean error;
    	while(true){
    		System.out.println("Starting a process...");
    		error = false;
    		
    		try {
    			/* Parsing results of matches */	
    			System.out.println("Parsing LiveScore started");
    			Logger.logMaker.logInfo("Parsing LiveScore started");
    			new parseLiveScore().init();
    			System.out.println("Parsing LiveScore completed successfully");
    			Logger.logMaker.logInfo("Parsing LiveScore completed successfully");
			} catch (Exception e) {
				System.out.println("During parsing LiveScore exception occurred");
				Logger.logMaker.logError("During parsing LiveScore exception occurred");
				e.printStackTrace();
				error=true;
			}
    		
    		try {
    			/* Parsing new matches */	
    			new parseSoccerRating().init();	
    			System.out.println("Parsing SoccerRating completed successfully");
			} catch (Exception e) {
				System.out.println("During parsing SoccerRating exception occurred");
				e.printStackTrace();
				error=true;
				
			}

   		 	if (!error){
   		 		System.out.println("Parsing Process completed successfully");
   		 		Logger.logMaker.logInfo("Parsing Process completed successfully");
   		 	}
   		 	else{
   		 		System.out.println("Parsing Process completed, but errors occurred");
   		 		Logger.logMaker.logInfo("Parsing Process completed, but errors occurred");
   		 	}
   		    int style = DateFormat.FULL;
   		 	Date RunDate = new Date();
   		    DateFormat df = DateFormat.getDateInstance(style, new Locale("pl", "PL"));
   		 	RunDate.setTime(RunDate.getTime()+time);
   		    df.format(RunDate);
  
   		    // Analyser switched off on Test time
   		    /*if (newData){
    			new Analyser().init();
    			newData = false;
   		    }*/
   		    
   		 	System.out.println("Next RunDate : "+RunDate);
   		 	
    		try {
    			Thread.sleep(time);
			} catch (Exception e) {
				
			}
    	}
    }
}
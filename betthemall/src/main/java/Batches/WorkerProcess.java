package Batches;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

import Analaser.Analyser;
import Crawler.IncommingMatchesParsers.ParseSoccerRating;
import Crawler.MatchesResultsParsers.ParseLiveScore;
import Logger.LogMaker;

public class WorkerProcess
{
	/** Czas podawany w milisekundach 1 sekunda = 1000 **/
	// Uruchamianie co [time] od startu aplikacji
	static int time = 1000*60*60; // Proces uruchamiany co godzinę
	private static Boolean newData = false;
	static LogMaker logMaker;

	
	public static void sendSignalNewDataSent(){
		newData = true;
	}
	
    public static void main(String[] args) throws IOException
    {
    	logMaker = LogMaker.getInstance();
    	
    	Boolean error;
    	while(true){
    		System.out.println("Starting a process...");
    		error = false;
    		
    		/** Parsing results of matches **/	
    		try {
    			System.out.println("Parsing LiveScore started");
    			logMaker.logInfo("Parsing LiveScore started");
    			new ParseLiveScore().init();
    			System.out.println("Parsing LiveScore completed successfully");
    			logMaker.logInfo("Parsing LiveScore completed successfully");
			} catch (Exception e) {
				System.out.println("During parsing LiveScore exception occurred");
				logMaker.logError("During parsing LiveScore exception occurred");
				e.printStackTrace();
				error=true;
			}
    		
			/** Parsing new matches **/
    		try {
    			System.out.println("Parsing SoccerRating started");
    			logMaker.logInfo("Parsing SoccerRating started");
    			new ParseSoccerRating().init();	
			} catch (Exception e) {
				System.out.println("During parsing SoccerRating exception occurred");
				logMaker.logError("During parsing SoccerRating exception occurred");
				e.printStackTrace();
				error=true;
				
			}

   		 	if (!error){
   		 		System.out.println("Parsing Process completed successfully");
   		 		logMaker.logInfo("Parsing Process completed successfully");
   		 	}
   		 	else{
   		 		System.out.println("Parsing Process completed, but errors occurred");
   		 		logMaker.logInfo("Parsing Process completed, but errors occurred");
   		 	}
   		    int style = DateFormat.FULL;
   		 	Date RunDate = new Date();
   		    DateFormat df = DateFormat.getDateInstance(style, new Locale("pl", "PL"));
   		 	RunDate.setTime(RunDate.getTime()+time);
   		    df.format(RunDate);
  

   		    if (newData){
   		    	System.out.println("Analyser started");
    			new Analyser().init(true);
    			newData = false;
   		    	System.out.println("Analyser completed successfully");
   		    }
   		    
   		    
   		 	System.out.println("Next RunDate : "+RunDate);
   		 	
    		try {
    			Thread.sleep(time);
			} catch (Exception e) {
				
			}
    	}
    }
}
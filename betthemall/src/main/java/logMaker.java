import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class logMaker {
	
	File file = null;

    private void logAdder(String msg)
    {	
    	try{
    		
		    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH_mm_ss.S");
		    //get current date time with Date()
		    Date date = new Date();
		    //System.out.println(dateFormat.format(date));
		  
		    //get current date time with Calendar()
		    Calendar cal = Calendar.getInstance();
		    String newNameOfFile = "logs/"+dateFormat.format(cal.getTime()).toString()+"_betthemall.log";
		    
    		if (file==null){
    			file = new File(newNameOfFile);
    			System.out.println("File null");
    		}
    		else{
    			long fileSizeInMB = file.length() / 1024 / 1024;
    			if (fileSizeInMB >= 10){
    				file = new File(newNameOfFile);
    			}
    		}
    		
    		//if file doesnt exists, then create it
    		if(!file.exists()){
    			System.out.println("File not exist");
    			file.createNewFile();
    			System.out.println("File created");
    		}
    		
    		//true = append file
    	    PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(file, true)));
    	    out.println(msg);
    	    out.close();
    	    
    	}catch(IOException e){
    		e.printStackTrace();
    	}
    }

	public void logAdd (String msg) {
		//System.out.println("[ADD] " + msg);
		logAdder("[ADD] " + msg);
	}
	
	public void logInfo (String msg) {
		//System.out.println("[INFO] " + msg);
		logAdder("[INFO] " + msg);
	}
	
	public void logUpdate (String msg) {
		//System.out.println("[UPDATE] " + msg);
		logAdder("[UPDATE] " + msg);
	}
	
	public void logWarrning (String msg) {
		//System.out.println("[WARNING] " + msg);
		logAdder("[WARNING] " + msg);
	}
	
	public void logError (String msg) {
		//System.out.println("[ERROR] " + msg);
		logAdder("[ERROR] " + msg);
	}

	
}

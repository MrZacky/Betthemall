import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

public class logMaker {
	
	
	File file = new File("/logs/tescik.txt");
	
	String SFTPHOST = "91.189.37.233";
	int SFTPPORT = 22;
	String SFTPUSER = "root";
	String SFTPPASS = "Wakacje123";
	String SFTPWORKINGDIR = "/root/betthemall/logs";

	Session session = null;
	Channel channel = null;
	ChannelSftp channelSftp = null;

    private void logAdder(String msg)
    {	
    	try{
    		
		    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH_mm_ss.S");
		    //get current date time with Date()
		    Date date = new Date();
		    //System.out.println(dateFormat.format(date));
		  
		    //get current date time with Calendar()
		    Calendar cal = Calendar.getInstance();
		    
    		/*if (file==null){
    			String newNameOfFile = "/tmp/logs/"+dateFormat.format(cal.getTime()).toString()+"_betthemall.txt";
    			file = new File(newNameOfFile);
    			//System.out.println("File null");
    		}
    		else{
    			long fileSizeInMB = file.length() / 1024 / 1024;
    			if (fileSizeInMB >= 10){
    				String newNameOfFile = "/tmp/logs/"+dateFormat.format(cal.getTime()).toString()+"_betthemall.txt";
    				file = new File(newNameOfFile);
    			}
    		}*/
    		
    		if(!file.exists()){
    			file.createNewFile();
			}
    		
    	    PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(file, true)));
    	    out.println(msg);
    	    out.close();
    	    
    	}catch(IOException e){
    		e.printStackTrace();
    	}
    	
		try {
			JSch jsch = new JSch();
			session = jsch.getSession(SFTPUSER, SFTPHOST, SFTPPORT);
			session.setPassword(SFTPPASS);
			java.util.Properties config = new java.util.Properties();
			config.put("StrictHostKeyChecking", "no");
			session.setConfig(config);
			session.connect();
			channel = session.openChannel("sftp");
			channel.connect();
			channelSftp = (ChannelSftp) channel;
			channelSftp.cd(SFTPWORKINGDIR);
			//File f = new File("test.txt");
		
			channelSftp.put(new FileInputStream(file), file.getName());
			
		} catch (Exception ex) {
			ex.printStackTrace();
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

package Logger;
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
	
	static File file = null;
	
	static String SFTPHOST = "91.189.37.233";
	static int SFTPPORT = 22;
	static String SFTPUSER = "root";
	static String SFTPPASS = "Wakacje123";
	static String SFTPWORKINGDIR = "/root/betthemall/logs";

	static Session session = null;
	static Channel channel = null;
	static ChannelSftp channelSftp = null;

    private static void logAdder(String msg)
    {	
    	try{
    		
		    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH_mm_ss.S");
		    //get current date time with Calendar()
		    Calendar cal = Calendar.getInstance();
		    
    		if (file==null){
    			String newNameOfFile = "logs/"+dateFormat.format(cal.getTime()).toString()+"_betthemall_log.txt";
    			file = new File(newNameOfFile);
    			file.createNewFile();
    		}
    		else if ((file!=null) && (!file.exists())){
    			String newNameOfFile = "logs/"+dateFormat.format(cal.getTime()).toString()+"_betthemall_log.txt";
    			file = new File(newNameOfFile);
    			file.createNewFile();
    		}
    		else{
    			long fileSizeInMB = file.length() / 1024 / 1024;
    			if (fileSizeInMB >= 10){
    				String newNameOfFile = "logs/"+dateFormat.format(cal.getTime()).toString()+"_betthemall_log.txt";
    				file = new File(newNameOfFile);
    				file.createNewFile();
    			}
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

	public static void logAdd (String msg) {
		//System.out.println("[ADD] " + msg);
		logAdder("[ADD] " + new Date() + " " + msg);
	}
	
	public static void logInfo (String msg) {
		//System.out.println("[INFO] " + msg);
		logAdder("[INFO] " + new Date() + " " + msg);
	}
	
	public static void logUpdate (String msg) {
		//System.out.println("[UPDATE] " + msg);
		logAdder("[UPDATE] " + new Date() + " " + msg);
	}
	
	public static void logWarrning (String msg) {
		//System.out.println("[WARNING] " + msg);
		logAdder("[WARNING] " + new Date() + " " + msg);
	}
	
	public static void logError (String msg) {
		//System.out.println("[ERROR] " + msg);
		logAdder("[ERROR] " + new Date() + " " + msg);
	}

	
}

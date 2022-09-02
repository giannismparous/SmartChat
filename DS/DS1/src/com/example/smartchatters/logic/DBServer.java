package com.example.smartchatters.logic;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;

public class DBServer extends Server {

    private ArrayList<MultimediaFile> profilePics;
    private final Object monitor = new Object();
    private int fileId;
    
    public DBServer(int serverId) {
    	
    	ips=new ArrayList<String>();
    	ports=new ArrayList<Integer>();
    	profilePics=new ArrayList<MultimediaFile>();
    	this.serverId=serverId;
    	fileId=1;
    	
    	readConfig();
    	
    	System.out.println("Server "+ serverId+" ip: " +ip);
    	System.out.println("Server "+ serverId+" port: " +port);
    	
    }

    /* Initiates the broker's server. */
    
    public void openServer() {

    	System.out.println("DBServer Running.");
    	
        try {
            provideSocket = new ServerSocket(port);

            while (true) {
                connection = provideSocket.accept();
                Thread t = new DBServerHelper(connection,this);
                t.start();

            }

        } catch (IOException ioException) {
        	System.err.println("Broker IO error.");
            ioException.printStackTrace();
        } finally {
            try {
                provideSocket.close();
            } catch (IOException ioException) {
            	System.err.println("Broker IO error while closing streams.");
                ioException.printStackTrace();
            }
        }

    }
    
    /* Reads the configuration file to setup brokers info. */
    
    public void readConfig() {
    	
		try {
			File fp = new File("conf.txt");
			FileReader fr;
			fr = new FileReader(fp);
			BufferedReader br = new BufferedReader(fr);
	    	String line;
	    	String[] id_ip_port;
	    	boolean passedDelimiter=false;
	    	while((line = br.readLine()) != null) { 

	    		if (line.equals("%")) {
	    			passedDelimiter=true;
	    			continue;
	    		}
	    		
	    		if (!passedDelimiter) {
	    			id_ip_port=line.split(":");
	    			if (id_ip_port[0].equals(String.valueOf(serverId))){
	    				this.ip=id_ip_port[1];
	    				this.port=Integer.parseInt(id_ip_port[2]);
	    			}
	    			ips.add(id_ip_port[1]);
	    			ports.add(Integer.parseInt(id_ip_port[2]));
	    		}
	    	
	    		
	    	}
			fr.close();	
		} catch (FileNotFoundException e) {
			System.err.println("cong.txt not found.");
			e.printStackTrace();
		} catch (IOException e) {
			System.err.println("IO error in conf.txt");
			e.printStackTrace();
		}
    }
    
    public void addMultimediaFile(MultimediaFile file) {
		profilePics.add(file);
	}
    
    public ArrayList<MultimediaFile> getHistory() {
		return profilePics;
	}

    public Object getMonitor() {
		return monitor;
	}
    
    public int getCurrentFileID() {
		return fileId++;
	}
}

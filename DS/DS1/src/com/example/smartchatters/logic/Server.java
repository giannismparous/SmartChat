package com.example.smartchatters.logic;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public abstract class Server {

	protected String ip;
    protected int port;
    protected int serverId;
    protected ArrayList<String> ips;
	protected ArrayList<Integer> ports;
	
	protected ServerSocket provideSocket;
    protected Socket connection = null;
	
    public abstract void openServer();
    
    public abstract void readConfig();
    
	/* The hash function. */
    
    public int calculateKey(String val) {
    	return Math.abs(val.hashCode() % 3)+1;
    }
    
    /* Returns the IP address of the current broker. */
    
    public String getIP() {
    	return ip;
    }
    
    /* Returns the port address of the current broker. */
    
    public int getPort() {
    	return port;
    }
    
    public int getServerId() {
    	return serverId;
    }
	
}

package com.example.smartchatters.logic;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class DBServerHelper extends Thread {
	
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private DBServer server;

    public DBServerHelper(Socket connection, DBServer server) {
    	
    	this.server=server;
        try {
            System.out.println("Got a connection...");
            out = new ObjectOutputStream(connection.getOutputStream());
            in = new ObjectInputStream(new BufferedInputStream(connection.getInputStream()));
        } catch (IOException e) {
        	System.err.println("DBServerHelper IO error in constructor.");
            e.printStackTrace();
        }

    }

    public void run() {
    	
        try {
            MultimediaFile item;
            String r="";
            while (!r.equals("UploadProfilePic") && !r.equals("DBConsumerConnection")) {
            	item = (MultimediaFile) in.readObject();
                r = new String(item.FileChunk, StandardCharsets.UTF_8);
                System.out.println("Command: "+r);
                if (r.equals("UploadProfilePic")) {
                	uploadProfilePicDBServerHelper();
                	break;
                }
                if (r.equals("DBConsumerConnection")) {
                	consumerDBServerHelper();
                	break;
                }
            }
           
        }catch (IOException e) {
        	System.err.println("DBServerHelper IO error in run.");
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
        	System.err.println("DBServerHelper ClassNotFound error in run.");
            e.printStackTrace();
        } finally {
            try {
                in.close();
                out.close();
                System.out.println("End of Connection");
            } catch (IOException ioException) {
            	System.err.println("DBServerHelper IO error in run while closing streams.");
                ioException.printStackTrace();
            }
        }

    }
    
    public void uploadProfilePicDBServerHelper() {
    	try {
	    	MultimediaFile item;
	        String r = "";
	    	while (!r.equals("send")) {
				item = (MultimediaFile) in.readObject();
				r = new String(item.FileChunk, StandardCharsets.UTF_8);
		        System.out.println("Command: "+r);
		        if (r.equals("send")) {
		            receiveFile(item.getName(),item.getExtension(),item);
		        }
    		}
    	} catch (ClassNotFoundException e) {
			System.err.println("PublisherDBServerHelper ClassNotFound error in run.");
			e.printStackTrace();
		} catch (IOException e) {
			System.err.println("Connection with client terminated unexpectedly");
		}
    }
    
    public void consumerDBServerHelper() {
    	int updatedToHistory=0;
    	MultimediaFile item;
    	while (true) {
    		try {
				Thread.sleep(250);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
    		synchronized (server.getMonitor()) {
	    		if (updatedToHistory<server.getHistory().size()) {
	    	        try {
	    	        	item=server.getHistory().get(updatedToHistory++);
	    	        	out.writeObject(item);
	    				out.flush();
	    				System.out.println("ConsumerDBServerHelper sent "+updatedToHistory + " chunk of file: "+ item.getName()+"."+item.getExtension());
					} catch (IOException e) {
						System.err.println("ConsumerDBServerHelper IO error in run.");
						e.printStackTrace();
						break;
					}
	    	        
	    		}
    		}
    	}
             
    }

    /* Handles the receiving of the file. */
    
    public void receiveFile(String fileName,String fileExtension, MultimediaFile sendMessage) {

        try {
        	ProfileName currentUser = (ProfileName)in.readObject();
        	int currentFileId=0;
            MultimediaFile item=null;
            int count = 1;
            long num = sendMessage.numberOfChunks;
            ArrayList<MultimediaFile> tempBuffer=new ArrayList<MultimediaFile>();
            tempBuffer.add(sendMessage);
            while (count <= num) {
            	item = (MultimediaFile) in.readObject();
				System.out.println("PublisherDBServerHelper sent "+item.getChunkId() + " chunk of file: "+ item.getName()+"."+item.getExtension());
            	tempBuffer.add(item);
            	count++;
            }
            Date date = Calendar.getInstance().getTime();
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            String strDate = dateFormat.format(date);
            item = new MultimediaFile(fileName,currentUser,strDate,fileExtension, 0, "end".length()+1,0);
            item.FileChunk = "end".getBytes(StandardCharsets.UTF_8);
            tempBuffer.add(item);
            synchronized (server.getMonitor()) {
            	currentFileId=server.getCurrentFileID();
            }
            
            for (MultimediaFile chunk: tempBuffer)chunk.setFileId(currentFileId);
            
            for (MultimediaFile chunk:tempBuffer) {
            	synchronized (server.getMonitor()) {
            		server.addMultimediaFile(chunk);
            	}
            }
            
            tempBuffer.clear();
            System.out.println(server.getHistory().size()+" chunks.");
            
        }catch (IOException e) {
        	System.err.println("PublisherDBServerHelper IO error in receiveFile.");
        	e.printStackTrace();
        } catch (ClassNotFoundException e) {
        	System.err.println("PublisherDBServerHelper ClassNotFound error in receiveFile.");
            e.printStackTrace();
        } 
    }

}
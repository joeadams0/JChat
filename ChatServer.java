//Joe Adams


import java.io.*;
import java.net.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

//Server
public class ChatServer{

	//Arraylist of all the clients
	private ArrayList<Client> clients;
	private ServerSocket serverSocket;
	public boolean on;

	public ChatServer(){
		clients = new ArrayList<Client>();
		on = true;
		try{
			serverSocket = new ServerSocket(8000);
		}
		catch(IOException ex){
			System.err.println(ex);
		}
		start();
	}
	
	public static void main(String[] args){
	        new ChatServer();
		System.out.println("Server started at "+new Date());
	}

	//Listen for clients and attatch them to server;
	public void start(){

		System.out.println("Server started at "+new Date());

		while(on){
			Socket clientSocket = null;
			Thread t = null;
			PrintWriter out = null; 
			BufferedReader in = null;;
			try{
				clientSocket = serverSocket.accept();
				out = new PrintWriter(
                                                clientSocket.getOutputStream(),true);
                        	in = new BufferedReader(
                                              	  new InputStreamReader(
                                                        clientSocket.getInputStream()));
			}
			catch(IOException ex){
				System.err.println(ex);
			}
			Client c = new Client(in,out,this);
			clients.add(c);		
			t = new Thread(c);
			t.start();
		}
	}
	
	//prints the messg to all clients
	public void print(String messg){
		for(int i = 0;i<clients.size(); i++){
			PrintWriter o = clients.get(i).out;
			o.println(messg);
		}
	}

	//remove a client
	public void remove(Client c){
		for(int i = 0; i < clients.size(); i++){
			if(clients.get(i) == c){
				clients.remove(i);
			}
		}
	}
	
}

//class for the client
class Client implements Runnable{
	
	private BufferedReader in;
	public PrintWriter out;
	public String name;
	ChatServer server;
	boolean on;

	public Client(BufferedReader i, PrintWriter o, ChatServer s){
		in = i;
		out = o;
		name = "";
		server = s;
		on = true;
	}
	

	//for the thread
	public void run(){
		
		//get name
		out.println("Enter your Name");
		String n = "";
		

		try{
			n = in.readLine();
		}
		catch(IOException ex){
			System.err.println(ex);
		}
		
		//check disconects
		if(n == null){
			server.remove(this);
			on = false;
		}
		//check name
		else{	

			name = check(n);
			server.print(name+" has joined the chat.");
		}
		
		//check for input and print it
		while(on){
				
			String txt = "";
				
			try{
                        	 txt = in.readLine();
                	}
                	catch(IOException ex){
                        	System.err.println(ex);
                	}
		
			//txt == null if they disconected
			if(txt == null){
				server.remove(this);
				on = false;
				server.print(name+" has left the chat.");
			}
			
			else
				server.print("<"+name+">:"+txt);
		}
	}
	

	//checks the name
	public String check(String n){
		
		for(int i = 0; i<n.length(); i++){
			if(n.charAt(i) == ' '){
				n = n.substring(0,i) + n.substring(i+1,n.length());
			}
		}	
		if(n.length() ==0){	
			return "Guest";
		}		
		if(n.length()>15){
			n = n.substring(0,15);
		}
		return n;
	}
}
		

		


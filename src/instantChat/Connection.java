package instantChat;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class Connection extends Thread {
	
	static final String endMsg = "\r";
	
	Socket s;
	BufferedReader recieve;
	DataOutputStream send;
	String username;
	boolean messager;
	
	public Connection(Socket s) throws IOException {
		this.s = s;
		this.recieve = new BufferedReader(new InputStreamReader(this.s.getInputStream()));
		this.send = new DataOutputStream(this.s.getOutputStream());
		this.messager = false;
	}
	
	public void run() {
		
		try {
			
			initialize();
		
			String msg = "";
			while (!msg.equals("leave")) {
				msg = recieve.readLine();
				msg = this.username + ": " + msg;
				this.messager = true;
				Handler.sendToClients(msg);
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void initialize() throws IOException {
		this.send.writeBytes("Welcome to the server!" + endMsg);
		this.send.writeBytes("Please enter your username: " + endMsg);
		this.username = this.recieve.readLine();
	}
	
	public void sendToClient(String msg) throws IOException {
		if (messager == true) {
			messager = false;
		} else {
			this.send.writeBytes(msg + endMsg);
		}
	}
}

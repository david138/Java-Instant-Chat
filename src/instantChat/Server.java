package instantChat;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.*;
import java.net.*;
import java.util.ArrayList;

public class Server {
	
	static ArrayList<ClientMessenger> clients = new ArrayList<ClientMessenger>();
	
	public static void main(String argv[]) throws Exception {
		
		ServerSocket welcomeSocket = new ServerSocket(6789);		
		
		while (true) {
			
			Socket connectionSocket = welcomeSocket.accept();
			
            // new thread for a client
            ClientMessenger client = new ClientMessenger(connectionSocket);
            clients.add(client);
            client.start();
        }
	}
	
	public static void sendToClients(String msg, String username) throws IOException {
		for (int i = 0; i < clients.size(); i++) {
			clients.get(i).sendToClient(msg, username);
		}
	}
	
	public static void removeClient(ClientMessenger client, String username) throws IOException {
		clients.remove(client);
		sendToClients(username + " left!", username);
		System.out.println(clients.size() + " clients are connected");
	}
}

class ClientMessenger extends Thread {
	
	BufferedReader inputReader;
	DataOutputStream outputStream;
	String username;
	
	public ClientMessenger(Socket socket) throws IOException {
		this.inputReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		this.outputStream = new DataOutputStream(socket.getOutputStream());
	}
	
	public void run() {
		
		try {
			
			// get username
			this.username = this.inputReader.readLine();
			
			//send welcome message
			this.outputStream.writeBytes("Welcome to the server " + this.username + "!" + System.lineSeparator());
			
			//send join message to other users
			Server.sendToClients(this.username + " joined!", this.username);
		
			String msg = inputReader.readLine();
			while (!msg.equals("/leave")) {
				Server.sendToClients(this.username + ": " + msg, this.username);
				msg = inputReader.readLine();
			}
			
		} catch (SocketException e) {
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				Server.removeClient(this, this.username);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void sendToClient(String msg, String username) throws IOException {
		if (!username.equals(this.username)) {
			this.outputStream.writeBytes(msg + System.lineSeparator());
		}
	}
}


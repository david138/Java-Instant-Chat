package instantChat;
import java.io.*;
import java.net.*;
import java.util.LinkedList;
import java.util.Queue;

public class Handler {
	
	static LinkedList<Connection> clients = new LinkedList<Connection>();
	
	public static void main(String argv[]) throws Exception {
		ServerSocket welcomeSocket = new ServerSocket(6789);
		Socket connectionSocket = null;
		
		
		while (true) {
			connectionSocket = welcomeSocket.accept();
            // new thread for a client
            Connection client = new Connection(connectionSocket);
            clients.add(client);
            client.start();
        }
	}

	public static void sendToClients(String msg) throws IOException {
		for (int i = 0; i < clients.size(); i++) {
			clients.get(i).sendToClient(msg);
		}
	}
	
	
}

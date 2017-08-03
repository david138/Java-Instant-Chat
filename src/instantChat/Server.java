package instantChat;
import java.io.*;
import java.net.*;
import java.util.*;

public class Server {
	
    private int port;
    private List<ClientMessenger> clients;
  
    public Server(int port) {
        this.port = port;
    }
  
    public void start() throws IOException {
        ServerSocket welcomeSocket = new ServerSocket(this.port);		
	    this.clients = Collections.synchronizedList(new ArrayList<ClientMessenger>());	
      
		while (true) {
			Socket connectionSocket = welcomeSocket.accept();
            ClientMessenger client = new ClientMessenger(connectionSocket, this);
            this.clients.add(client);
            client.start();
        }
    }
	
	public void broadcast(String msg, String username) throws IOException {
        synchronized (this.clients) {
		    for (ClientMessenger client : this.clients) {
            	client.send(msg, username);
            }
        }
	}
	
	public void removeClient(ClientMessenger client, String username) throws IOException {
		this.clients.remove(client);
		broadcast(username + " left!", username);
		System.out.println(clients.size() + " clients are connected");
	}
  
    public static void main(String argv[]) throws Exception {
        new Server(6789).start();
	}
}

class ClientMessenger extends Thread {
	
	private BufferedReader inputReader;
	private DataOutputStream outputStream;
	private String username;
    private Server server;
	
	public ClientMessenger(Socket socket, Server server) throws IOException {
		this.inputReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		this.outputStream = new DataOutputStream(socket.getOutputStream());
        this.server = server;
	}
	
	public void run() {
		
		try {
			this.username = this.inputReader.readLine();
			this.outputStream.writeBytes("Welcome to the server " + this.username + "!" + System.lineSeparator());
			this.server.broadcast(this.username + " joined!", this.username);
		
			String msg = inputReader.readLine();
			while (!"/leave".equals(msg)) {
				server.broadcast(this.username + ": " + msg, this.username);
				msg = inputReader.readLine();
			}
			
		} catch (SocketException e) {
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				server.removeClient(this, this.username);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void send(String msg, String username) throws IOException {
		if (!username.equals(this.username)) {
			this.outputStream.writeBytes(msg + System.lineSeparator());
		}
	}
}
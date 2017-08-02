package instantChat;
import java.io.*;
import java.net.*;

class Client {
	
	private String host;
	private int port;

	
	public Client (String host, int port) throws IOException {
		this.host = host;
		this.port = port;
	}
	
	public void start() throws InterruptedException {
		
		try {
			Socket clientSocket = new Socket(this.host, this.port);
			
			BufferedReader userInputReader = new BufferedReader(new InputStreamReader(System.in));
			
			DataOutputStream outputStream = new DataOutputStream(clientSocket.getOutputStream());
			BufferedReader serverInputReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			
		    getUsername(userInputReader, outputStream);
			
		    new Thread() {
		        public void run() {
		            listenForServerMessages(serverInputReader);
		        }
		    }.start();
		    
		    Thread listenForUser = new Thread() {
		        public void run() {
		        	listenForClientMessages(userInputReader, outputStream);
		        }
		    };
		    
		    listenForUser.start();
		    listenForUser.join();
		    
		    clientSocket.close();
			
		} catch(ConnectException e) {
			System.err.println("Sorry, could not connect to server");
		} catch (IOException e) {
			e.printStackTrace();
		}	    
	}
	
	public void listenForServerMessages(BufferedReader serverInputReader) {
		while (true) {
			try {
				String msg = serverInputReader.readLine();
				System.out.println(msg);
			} catch (SocketException e) {
				return;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void listenForClientMessages(BufferedReader userInputReader, DataOutputStream outputStream) {
		String msg = "";
        while (!msg.equals("/leave")) {
        	try {
        		msg = userInputReader.readLine();
				outputStream.writeBytes(msg + System.lineSeparator());
			} catch (SocketException e) {
				System.err.println("Lost connection to server");
				return;
			} catch (IOException e) {
				e.printStackTrace();
			}
        }
	}
	
	public void getUsername(BufferedReader userInputReader, DataOutputStream outputStream) throws IOException {
		
		System.out.print("You are connected, please enter your username: ");
		
		String username = userInputReader.readLine();
		outputStream.writeBytes(username + System.lineSeparator());
		
	}
	
	public static void main(String argv[]) throws InterruptedException, IOException {
		
		System.out.println("Client Starting");
		new Client("localhost", 6789).start();
		
	}
 }
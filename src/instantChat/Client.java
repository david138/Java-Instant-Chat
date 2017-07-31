package instantChat;
import java.io.*;
import java.net.*;

class Client {
	
	private BufferedReader userInputReader;
	private DataOutputStream outputStream;
	private BufferedReader serverInputReader;
	
	public Client (Socket s) throws IOException {
		
		this.userInputReader = new BufferedReader(new InputStreamReader(System.in));
		
		this.outputStream = new DataOutputStream(s.getOutputStream());
		this.serverInputReader = new BufferedReader(new InputStreamReader(s.getInputStream()));
	}
	
	public void start() throws IOException {
		
		// Receive and write intro message
		String msg = this.serverInputReader.readLine();
		System.out.println(msg);

        getUsername();
        
        new Thread() {
            public void run() {
                listenForServerMessages();
            }
        }.start();
        
        listenForClientMessages();
	}
	
	public void listenForServerMessages() {
		while (true) {
			try {
				String msg = this.serverInputReader.readLine();
				System.out.println(msg);
			} catch (SocketException e) {
				return;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void listenForClientMessages() {
        while (true) {
        	try {
        		String msg = userInputReader.readLine();
				this.outputStream.writeBytes(msg + System.lineSeparator());
			} catch (SocketException e) {
				System.err.println("Lost connection to server");
				return;
			} catch (IOException e) {
				e.printStackTrace();
			}
        }
	}
	
	public void getUsername() throws IOException {
		
		System.out.print("Please enter your username: ");
		
		String username = userInputReader.readLine();
		this.outputStream.writeBytes(username + System.lineSeparator());
		
	}
	
	public static void main(String argv[]) {
		
		System.out.println("Client Starting");
		
		try {
			Socket clientSocket = new Socket("localhost", 6789);
			new Client(clientSocket).start();
			clientSocket.close(); 
		} catch(ConnectException e) {
			System.err.println("Sorry, could not connect to server");
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
 }
package instantChat;
import java.io.*;
import java.net.*;

class Client {
	
	static final String endMsg = "\r";
	
	BufferedReader fromUser;
	DataOutputStream send;
	BufferedReader recieve;
	Socket s;
	String msg;
	String username;
	
	public Client (Socket s) throws IOException {
		
		this.fromUser = new BufferedReader(new InputStreamReader(System.in));
		
		this.s = s;
		
		this.send = new DataOutputStream(this.s.getOutputStream());
		this.recieve = new BufferedReader(new InputStreamReader(this.s.getInputStream()));
	}
	
	public void start() throws IOException {
		this.msg = this.recieve.readLine();
		System.out.println(this.msg);

        getUsername();
        
        new Thread() {
            public void run() {
                listenForMessages();
            }
        }.start();
        
        while (true) {
        	this.msg = fromUser.readLine();
        	this.send.writeBytes(this.msg + endMsg);
        }
	}
	
	public void listenForMessages() {
		while (true) {
			String msg;
			try {
				msg = this.recieve.readLine();
				System.out.println(msg);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void getUsername() throws IOException {
		
		this.msg = this.recieve.readLine();
		System.out.print(this.msg);
		
		this.username = fromUser.readLine();
		this.send.writeBytes(this.username + endMsg);
		
	}
	
	public static void main(String argv[]) throws Exception {
		
		System.out.println("Client Running");
		Socket clientSocket = new Socket("localhost", 6789);
		
		new Client(clientSocket).start();
		clientSocket.close(); 
		
	}
 }
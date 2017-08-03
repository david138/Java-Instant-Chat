package instantChat;
import java.io.*;
import java.net.*;

class Client {
	
    private String host;
    private int port;
    private Socket clientSocket;
	
    public Client (String host, int port) throws IOException {
        this.host = host;
        this.port = port;
    }
	
    public void start() throws InterruptedException {	
        try {
            this.clientSocket = new Socket(this.host, this.port);		
            BufferedReader userInputReader = new BufferedReader(new InputStreamReader(System.in));	
            DataOutputStream outputStream = new DataOutputStream(this.clientSocket.getOutputStream());
            BufferedReader serverInputReader = new BufferedReader(new InputStreamReader(this.clientSocket.getInputStream()));
			
            promptForUsername(userInputReader, outputStream);
			
            new Thread() {
                public void run() {
                    listenForServerMessages(serverInputReader);
                }
            }.start();	    
            new Thread() {
                public void run() {
                    listenForClientMessages(userInputReader, outputStream);
                }
            }.start();	
        } catch(ConnectException e) {
            System.err.println("Sorry, could not connect to server");
        } catch (IOException e) {
            e.printStackTrace();
        }	    
    }
	
    public void close() {
        try {
            synchronized(this.clientSocket) {
                if (this.clientSocket != null) {
                    this.clientSocket.close();
                    this.clientSocket = null;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
	
    public void listenForServerMessages(BufferedReader serverInputReader) {
        try {
            while (true) {
                String msg = serverInputReader.readLine();
                System.out.println(msg);
            }
        } catch (SocketException e) {
            System.err.println("Lost connection to server");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            close();
        }
    }
	
    public void listenForClientMessages(BufferedReader userInputReader, DataOutputStream outputStream) {
        String msg = "";
        try {
            while (!msg.equals("/leave")) {
                msg = userInputReader.readLine();
                outputStream.writeBytes(msg + System.lineSeparator());
            }
        } catch (SocketException e) {
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            close();
        }
    }
	
    public void promptForUsername(BufferedReader userInputReader, DataOutputStream outputStream) throws IOException {
        System.out.print("You are connected, please enter your username: ");
        String username = userInputReader.readLine();
        outputStream.writeBytes(username + System.lineSeparator());	
    }
	
    public static void main(String argv[]) throws InterruptedException, IOException {
        System.out.println("Client Starting");
        new Client("localhost", 6789).start();	
    }
}    
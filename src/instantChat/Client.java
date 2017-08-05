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
            BufferedReader userReader = new BufferedReader(new InputStreamReader(System.in));
            DataOutputStream outputStream = new DataOutputStream(this.clientSocket.getOutputStream());
            BufferedReader serverReader = new BufferedReader(new InputStreamReader(this.clientSocket.getInputStream()));

            promptForUsername(userReader, outputStream);
            new Thread() {
                public void run() {
                    listenForServerMessages(serverReader);
                }
            }.start();    
            new Thread() {
                public void run() {
                    listenForClientMessages(userReader, outputStream);
                }
            }.start();
        } catch (ConnectException e) {
            System.err.println("Sorry, could not connect to server");
        } catch (IOException e) {
            e.printStackTrace();
        }    
    }

    public void close() {
        try {
            if (this.clientSocket != null) {
                synchronized (this.clientSocket) {
                    if (this.clientSocket != null) {
                        this.clientSocket.close();
                        this.clientSocket = null;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void listenForServerMessages(BufferedReader serverReader) {
        try {
            while (true) {
                String msg = serverReader.readLine();
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

    public void listenForClientMessages(BufferedReader userReader, DataOutputStream outputStream) {
        String msg = "";
        try {
            while (!"/leave".equals(msg)) {
                msg = userReader.readLine();
                outputStream.writeBytes(msg + System.lineSeparator());
            }
        } catch (SocketException e) {
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            close();
        }
    }

    public void promptForUsername(BufferedReader userReader, DataOutputStream outputStream) throws IOException {
        System.out.print("You are connected, please enter your username: ");
        String username = userReader.readLine();
        outputStream.writeBytes(username + System.lineSeparator());
    }

    public static void main(String argv[]) throws InterruptedException, IOException {
        System.out.println("Client Starting");
        new Client("localhost", 6789).start();
    }
}    
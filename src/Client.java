import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String username;

    public Client(Socket socket, String username) {
        this.socket = socket;
        this.username = username;
        try {
            bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        } catch (IOException e) {
            System.out.println("Error initializing client I/O streams.");
            closeEverything();
        }
    }
    public void authenticate(String user_password){
        try {
            bufferedWriter.write(user_password);
            bufferedWriter.newLine();
            bufferedWriter.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendMessage() {
        try (Scanner scanner = new Scanner(System.in)) {
            bufferedWriter.write(username);
            bufferedWriter.newLine();
            bufferedWriter.flush();

            //System.out.println("Welcome to the chat, " + username + "! Type your messages below:");

            while (socket.isConnected()) {
                String message = scanner.nextLine().trim();
                if (!message.isEmpty()) {
                    bufferedWriter.write(message);
                    bufferedWriter.newLine();
                    bufferedWriter.flush();
                } else {
                    System.out.println("Message cannot be empty. Please type something.");
                }
            }
        } catch (IOException e) {
            System.out.println("Error sending message. Connection may have been lost.");
            closeEverything();
        }
    }

    public void listenForMessages() {
        new Thread(() -> {
            while (socket.isConnected()) {
                try {
                    String messageFromGroupChat = bufferedReader.readLine();
                    if (messageFromGroupChat != null) {
                        System.out.println(messageFromGroupChat);
                    } else {
                        System.out.println("Server connection lost.");
                        closeEverything();
                        break;
                    }
                } catch (IOException e) {
                    System.out.println("Error receiving message. Connection may have been lost.");
                    closeEverything();
                    break;
                }
            }
        }).start();
    }

    public void closeEverything() {
        try {
            if (bufferedReader != null) bufferedReader.close();
            if (bufferedWriter != null) bufferedWriter.close();
            if (socket != null) socket.close();
            System.out.println("Disconnected from the server.");
        } catch (IOException e) {
            System.out.println("Error closing resources.");
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        try {
            Scanner scanner = new Scanner(System.in);
            System.out.println("Enter your password:");
            String user_password = scanner.nextLine().trim();
            while (user_password.isEmpty()) {
                System.out.println("Password cannot be empty. Please enter a valid password:");
                user_password = scanner.nextLine().trim();
            }
            System.out.println("Enter your username:");
            String username = scanner.nextLine().trim();
            while (username.isEmpty()) {
                System.out.println("Username cannot be empty. Please enter a valid username:");
                username = scanner.nextLine().trim();
            }
            Socket socket = new Socket("localhost", 5000);
            Client client = new Client(socket, username);
            client.authenticate(user_password);
            client.listenForMessages();
            client.sendMessage();
        } catch (IOException e) {
            System.out.println("Error connecting to the server. Please ensure the server is running.");
            e.printStackTrace();
        }
    }
}

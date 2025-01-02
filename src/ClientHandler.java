import java.io.*;
import java.net.Socket;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class ClientHandler implements Runnable {
    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String clientUsername;
    private List<ClientHandler> clientHandlers;
    private Set<String> usernames;

    public ClientHandler(Socket socket, List<ClientHandler> clientHandlers, Set<String> usernames) {// the set is thread safe
        this.socket = socket;
        this.clientHandlers = clientHandlers;
        this.usernames = usernames; // Assign the passed `usernames` Set
        try {
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.clientUsername = bufferedReader.readLine();

            synchronized (this) {
                usernames.add(clientUsername); // Add username to set
                clientHandlers.add(this);      // Add client handler to list
            }

            broadcastMessage("SERVER: Welcome " + clientUsername);
            broadcastMessage("SERVER: Currently online: " + usernames.toString());
        } catch (IOException e) {
            closeEverything();
        }
    }

    @Override
    public void run() {
        String message;
        while (socket.isConnected()) {
            try {
                message = bufferedReader.readLine(); // Receive message from client
                if (message != null) {
                    broadcastMessage(clientUsername + ": " + message); // Broadcast to all clients
                }
            } catch (IOException e) {
                closeEverything();
                break;
            }
        }
    }

    public void broadcastMessage(String message) {
        synchronized (clientHandlers) {
            for (ClientHandler client : clientHandlers) {
                try {
                    client.bufferedWriter.write(message);
                    client.bufferedWriter.newLine();
                    client.bufferedWriter.flush();
                } catch (IOException e) {
                    client.closeEverything(); // Close client connection if an error occurs
                }
            }
        }
    }

    public void closeEverything() {
        try {
            synchronized (this) {
                clientHandlers.remove(this); // Remove client from list
                usernames.remove(clientUsername); // Remove username from set
            }

            broadcastMessage("SERVER: " + clientUsername + " has left the chat.");
            broadcastMessage("SERVER: Currently online: " + usernames.toString());

            if (bufferedReader != null) bufferedReader.close();
            if (bufferedWriter != null) bufferedWriter.close();
            if (socket != null) socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
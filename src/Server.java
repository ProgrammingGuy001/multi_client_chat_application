import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class Server {
    private ServerSocket serverSocket;
    private static List<ClientHandler> clientHandlers = new CopyOnWriteArrayList<>();// this creates a new list everytime some operation occurs hece safe for multithreaded emvironment
    private static Set<String> usernames = ConcurrentHashMap.newKeySet(); // Thread-safe Set

    public Server(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    public void startServer() {
        System.out.println("Server is running...");
        while (!serverSocket.isClosed()) { //blocked
            try {
                Socket socket = serverSocket.accept();// waiting for client to accept
                System.out.println("New client connected!");
                ClientHandler clientHandler = new ClientHandler(socket, clientHandlers, usernames); // Pass `usernames`, chandelier
                new Thread(clientHandler).start(); // Start client handler in a new thread
            } catch (IOException e) {
                System.out.println("Error accepting client connection."); // in case of IO failure
                e.printStackTrace();
            }
        }
    }

    public void closeServerSocket() {
        try {
            System.out.println("Closing server socket...");
            if (serverSocket != null) {
                serverSocket.close();
            }
            // Notify all clients about server shutdown
            for (ClientHandler clientHandler : clientHandlers) {
                clientHandler.broadcastMessage("SERVER: The server is shutting down.");
                clientHandler.closeEverything();
            }
        } catch (IOException e) {
            System.out.println("Error closing server socket.");
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(5000);
            Server server = new Server(serverSocket);

            // Add a shutdown hook for graceful shutdown (optional)
            Runtime.getRuntime().addShutdownHook(new Thread(server::closeServerSocket));

            server.startServer();
        } catch (IOException e) {
            System.out.println("Server failed to start.");
            e.printStackTrace();
        }
    }
}
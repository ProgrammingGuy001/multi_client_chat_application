import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class Server {
    private ServerSocket serverSocket;
    private static final List<ClientHandler> clientHandlers = new CopyOnWriteArrayList<>();
    private static final Set<String> usernames = ConcurrentHashMap.newKeySet();
    private static final String password="act"; //change to whatever is suitable

    public Server(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    public void startServer() {
        System.out.println("Server is running...");
        while (!serverSocket.isClosed()) {
            try {
                Socket socket = serverSocket.accept();
                System.out.println("New client connected!");
                ClientHandler clientHandler = new ClientHandler(socket, clientHandlers, usernames,password);
                new Thread(clientHandler).start();
            } catch (IOException e) {
                System.out.println("Error accepting client connection.");
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

            Runtime.getRuntime().addShutdownHook(new Thread(server::closeServerSocket));

            server.startServer();
        } catch (IOException e) {
            System.out.println("Server failed to start.");
            e.printStackTrace();
        }
    }
}

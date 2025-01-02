import java.io.*;
import java.net.Socket;
import java.util.List;
import java.util.Set;

public class ClientHandler implements Runnable {
    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String clientUsername;
    private List<ClientHandler> clientHandlers;
    private Set<String> usernames;
    private String ClientPassword;

    public ClientHandler(Socket socket, List<ClientHandler> clientHandlers, Set<String> usernames,String password) {
        this.socket = socket;
        this.clientHandlers = clientHandlers;
        this.usernames = usernames;
        try {
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.ClientPassword = bufferedReader.readLine();
            if(!ClientPassword.equals(password)){
                bufferedWriter.write("SERVER: Incorrect password");
                bufferedWriter.newLine();
                bufferedWriter.flush();
                bufferedWriter.close();
                bufferedReader.close();
                return;
            }
            else{
                this.clientUsername = bufferedReader.readLine();
                synchronized (this) {
                    usernames.add(clientUsername);
                    clientHandlers.add(this);
                }
                broadcastMessage("SERVER: Welcome " + clientUsername);
                broadcastMessage("SERVER: Currently online: " + usernames.toString());
            }
        } catch (IOException e) {
            closeEverything();
        }
    }

    @Override
    public void run() {
        String message;
        while (socket.isConnected()) {
            try {
                message = bufferedReader.readLine();
                if (message != null) {
                    if (message.startsWith("@")) {
                        String[] parts = message.split(" ", 2);
                        if (parts.length == 2) {
                            String targetUsername = parts[0].substring(1);
                            String privateMessage = parts[1];
                            sendPrivateMessage(targetUsername, privateMessage);
                        } else {
                            sendMessageToClient("SERVER: Invalid private message format. Use @username <message>.");
                        }
                    } else {
                        broadcastMessage(clientUsername + ": " + message);
                    }
                }
            } catch (IOException e) {
                closeEverything();
                break;
            }
        }
    }

    public void sendPrivateMessage(String targetUsername, String message) {
        boolean found = false;
        synchronized (clientHandlers) {
            for (ClientHandler client : clientHandlers) {
                if (client.clientUsername.equals(targetUsername)) {
                    found = true;
                    try {
                        client.sendMessageToClient("Private Message from " + clientUsername + ": " + message);
                    } catch (IOException e) {
                        client.closeEverything();
                    }
                    break;
                }
            }
        }
        if (!found) {
            try {
                sendMessageToClient("SERVER: " + targetUsername + " is not online.");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void sendMessageToClient(String message) throws IOException {
        bufferedWriter.write(message);
        bufferedWriter.newLine();
        bufferedWriter.flush();
    }

    public void broadcastMessage(String message) {
        synchronized (clientHandlers) {
            for (ClientHandler client : clientHandlers) {
                try {
                    client.sendMessageToClient(message);
                } catch (IOException e) {
                    client.closeEverything();
                }
            }
        }
    }

    public void closeEverything() {
        try {
            synchronized (this) {
                clientHandlers.remove(this);
                usernames.remove(clientUsername);
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

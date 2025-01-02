# Project Name: Multi-Client Chat Application
## Overview
This project implements a **multi-client chat application** in Java, enabling clients to connect to a central server and communicate in a group chat setting. It uses sockets for network communication and ensures bi-directional messaging between clients over the server.
The system is composed of two key components:
1. **Server** - Handles the incoming client connections, relays messages between clients, and manages client communication.
2. **Client** - Represents the individual users connecting to the server, allowing them to send and receive messages.

## Key Features
- **Multi-client support:** Multiple clients can connect to a single server to participate in an interactive chat.
- **Real-time messaging:** Clients can send and receive messages in real time.
- **Threaded communication:** The server uses multithreading to handle multiple client connections concurrently.
- **Graceful disconnection handling:** Clients and the server handle disconnections to avoid resource leaks.
- **Interactive prompts:** Users are prompted to input their username and messages, enhancing usability.

## How It Works
### Architecture Flow
1. **Server**:
    - Listens for incoming client socket connections on a specified port.
    - Manages connected clients using threads to broadcast their messages to all other clients.
    - Handles proper disconnection when a client terminates their session.

2. **Client**:
    - Connects to the server using the server's hostname/IP and port.
    - Allows users to:
        - Send messages that are broadcasted to all connected clients.
        - Receive messages in real time from other clients in the chat.

    - Handles proper disconnection and network errors gracefully.

## Setup Instructions
1. **Prerequisites**
    - Java 22 or compatible version installed.
    - Basic understanding of networking protocols (Sockets/TCP).
    - Ensure network access between the client(s) and server machine.

2. **Running the Server**
    - Compile the server class file (provide a `Server` implementation).
    - Run the server with the required port for socket communication.
    - Example: `java Server`

3. **Running the Client**
    - Compile the `Client` class file.
    - Run the client program and provide:
        - Hostname/IP of the server (e.g., `localhost` or `192.168.x.x`).
        - Same port number used by the server.

    - Interact with other connected clients.

## Classes and Components
### 1. **Client**
This is a class for managing client-side activity in the chat application.
#### Key Features:
- **Connection Handling**:
    - Establishes a connection with the server using a socket.
    - Initializes input/output streams.

- **Send/Receive Mechanisms**:
    - Sends messages entered by the user to the server.
    - Listens for and displays incoming messages from the server in a separate thread.

- **Graceful Disconnect**:
    - Closes all resources (socket, streams) properly when the client disconnects or encounters errors.

#### Key Methods:
- `sendMessage()` - Handles sending outgoing messages to the server.
- `listenForMessages()` - Listens for incoming messages from the server using a background thread.
- `closeEverything()` - Closes the socket and stream resources.

## Example Usage
1. Launch the server program.
2. Start one or more instances of the client program.
3. Follow prompts within the client to:
    - Enter a username.
    - Begin chatting. Type and send messages to other participants.

**Sample Interaction:**
- Client 1:
``` 
  Enter your username:
  Alice
  Welcome to the chat, Alice! Type your messages below:
  Hello Everyone!
```
- Client 2:
``` 
  Enter your username:
  Bob
  Welcome to the chat, Bob! Type your messages below:
  Hi Alice!
```
- Chat Output:
    - Client 1: `Bob: Hi Alice!`
    - Client 2: `Alice: Hello Everyone!`

## Error Handling
- **Connection issues**: Both server and client handle abrupt disconnections.
- **Input validation**:
    - A user's username cannot be empty.
    - Warns users when messages are blank.

## Improvements To Consider
- Add authentication (username/password).
- Enhance the user interface with GUI (e.g., using JavaFX or Swing).
- Introduce private messaging and group chat functionality.
- Enable persistence by storing chat messages in a database.
- Implement encryption for secure communication.

## Contributing
Feel free to contribute to improve this project by:
- Forking the repository.
- Creating pull requests with new features or bug fixes.

## License
This project is licensed under [MIT License]().
Happy coding! 🎉

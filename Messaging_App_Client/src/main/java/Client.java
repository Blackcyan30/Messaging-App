import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.function.Consumer;
import java.io.*;


public class Client {

    private Socket socket; // Socket for connection with server
    private ObjectOutputStream output; // Data to be sent to the server
    private ObjectInputStream input; // Data to be recieved from the server
    private Consumer<Message> onMessageReceived; // Functional interface to handle incoming messages
    private String username; // Username chosen by client
    private boolean usernameAccepted; // Track if the username has been accepted by the server

    // CONSTANTS
    private static final String SERVER_ADDRESS = "127.0.0.1";
    private static final int PORT = 5555;



    /**
     * Constructor to initialize the client with a callback handler for message reception
     * @param onMessageRecieved
     */
    public Client(Consumer<Message> onMessageRecieved) { this.onMessageReceived = onMessageRecieved; }


    /**
     * Start connection to the server and set up both input and output streams
     * @throws IOException
     */
    public void start() throws IOException {
        socket = new Socket(SERVER_ADDRESS, PORT);
        output = new ObjectOutputStream(socket.getOutputStream());
        input = new ObjectInputStream(socket.getInputStream());
        new Thread(this::listenForMessages).start();
    }


    /**
     * Continuously listens for messages, processes them using the onMessageRecieved Callback
     */
    private void listenForMessages() {

        try {

            while (true) { // Infinite loop to keep listening for messages unless an exception occurs
                Message message = (Message) input.readObject(); // Read the next Message object from the stream
                if (message != null) {
                     // Use the callback to process the received message
                     processRecievedMessage(message);
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace(); // Print any errors during message reception
            closeConnection(); // Attempt to close all connections cleanly
        }
    }


    /**
     * Process message using message type (action doe)
     * @param message
     */
    private void processRecievedMessage(Message message) {

        switch(message.statusCode) {

            case 0: // Username check
                if (message.usernameAccepted) {
                    System.out.println("Username is Accepted.");
                    usernameAccepted = true;
                    setUsername(message.sender);
                } else {
                    System.out.println("Username is NOT Accepted.");
                    usernameAccepted = false;
                }
                break;

            case 1: // Normal Message
                System.out.println("Normal Message Recieved");

                break;

            case 2: // Message to all users
                System.out.println("All Users Message Recieved");
                break;

            case 3: // Group chat message
                System.out.println("Group Chat has been Created");
                break;

            case 4: // New user announcement
                System.out.println(message.content); // "_______ has joined the chat"
                break;

            case 5: // Update user list
                System.out.println("User List has been Updated");
                break;

            case 6:
                System.out.println("Group Chat Message has been Sent");
                break;
                

            default:
                System.out.println("Unknown Action Code Recieved");
                break;                
        }
        onMessageReceived.accept(message);
    }


    /**
     * Send message object to the server
     * @param message
     * @throws IOException
     */
    public void sendMessage(Message message) throws IOException {
        output.writeObject(message); // Serialize and send the message object to the server
        output.flush(); // Flush the stream to ensure all data is sent immediately
    }


    /**
     * Send a username to the server for availability checking
     * @param username
     * @throws IOException
     */
    public void checkUsername(String username) throws IOException { sendMessage(new Message(username, 0, false)); }


    /**
     * Sets the username for this client
     * @param username
     */
    public void setUsername(String username) { this.username = username; }


    /**
     * Retrieves the current username of this client
     * @return user's unique username
     */
    public String getUsername() { return username; }


    /**
     * Closes the client socket and the associated streams
     */
    public void closeConnection() {

        try {
            if (input != null) input.close(); // Close the input stream if it's not null
            if (output != null) output.close(); // Close the output stream if it's not null
            if (socket != null) socket.close(); // Close the socket if it's not null
            
        } catch (IOException e) {
            e.printStackTrace(); // Print any errors that occur during the closing process
        }
    }
}
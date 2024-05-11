import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

import javafx.application.Platform;
import javafx.scene.control.ListView;


public class Server{
	
	// Initialized to 1 to indicte first client conncetion.
	int count = 1;
	
	Map<String, ClientThread> clientsList = new ConcurrentHashMap<>();
	Map<String, Set<String>> groups = new ConcurrentHashMap<>();
	ArrayList<String> userList = new ArrayList<>();
	// Change name to be less bad.
	TheServer server;
	
	public static void main(String[] args) {
		new Server().startServer();
	}

	public void startServer() {
		TheServer server = new TheServer();
		server.start();
	}
	
	class TheServer extends Thread {
		
		public void run() {
		
			try(ServerSocket mysocket = new ServerSocket(5555);) {

				System.out.println("Server is waiting for a client!");
			
				while(true) {
					ClientThread client = new ClientThread(mysocket.accept(), count);
					System.out.println("Client connnected with the port: " + client.getSocket().getPort() + ".");
					System.out.println("Client connected has the #: " + count);
					client.start();
					count++;
				}
			} catch(Exception e) {
					e.printStackTrace();
			}
		}
	}
	
		class ClientThread extends Thread{
			
			String userName;
			Socket connection;
			int count;
			ObjectInputStream inputStream;
			ObjectOutputStream outputStream;

			/**
			 * 
			 */
			public void run(){

				while(true) {
					    try {	
							System.out.println("Server is listening for incoming messages.");
					    	Message incomingMessage = (Message)inputStream.readObject();
							readIncomingMessage(incomingMessage);
					    }
					    catch(Exception e) {
							System.out.println(userName + " has disconnected from the server.");
							closeConnections();
							break;
					    }
						
				}
			}

			/**
			 * This function takes care of all of the 
			 * incomming message traffic of the client.
			 * @param message
			 * @throws Exception
			 */
			public void readIncomingMessage(Message message) throws Exception {
				System.out.println("Server has recieved a incoming message. Incoming message is being parsed based on it's statusCode\n");
				// Send message to indicated user in message.destination
				if (message.statusCode == 0) {
					/**
					 * This is the case when client has joined and we boadcase
					 * that the client has joined while also putting them into the 
					 * clientsList.
					 * The else is that the userName is not unique and the client 
					 * needs to select a different userName.
					 */
					if (!clientsList.containsKey(message.sender)) {
						// User is being added to the clientsList and the userList.
						System.out.println("New client with has connected. Name: " + message.sender + "\n");
						userName = message.sender;
						clientsList.put(userName, this);
						userList.add(userName);

						// Server is sending confirmation to client that username has been accepted
						System.out.println("Server is sending confirmation to client that username has been accepted\n");
						Message clientJoinedMessage = new Message(userName,0, true);
						this.outputStream.writeObject(clientJoinedMessage);
						
						// Sending a blast to all users that user is added.
						Message clientAddedBlast = new Message(userName, 4);
						updateClients(clientAddedBlast);
						System.out.println("Sent a blast to all users that " + userName + " has been added.");

						// Sending blast to all users for arrayList update.
						Message updatedUserList = new Message(new ArrayList<>(userList), 5);
						updateClients(updatedUserList);
						System.out.println("Sent a blast of arrayList for update of addition of user");
					} else {
						Message nameNotAccepted = new Message(false, 0);
						sendMessage(nameNotAccepted);
						System.out.println("Did not add to the list as Name already exists. Name: " + message.sender);
					}

				  // Send message to user specified in the message.destination
				} else if (message.statusCode == 1) {
					
					System.out.println("Entered status code 1");
					Message relay = new Message(message.content, userName, message.destination, 1);
					ClientThread recipient = clientsList.get(message.destination);
					System.out.println("Found recipient sending message. Recipient is: " + recipient.userName + ". Content is: " + message.content);
					recipient.sendMessage(relay);
					System.out.println("Message has been sent to " + recipient.userName);
					
				  // Send message to everyone.
				} else if (message.statusCode == 2) {
					System.out.println("Sending message to all users with the content: " + message.content);
					for (ClientThread client : clientsList.values()) {
						Message blast = new Message(message.content, userName, client.userName, 2);
						client.sendMessage(blast);
					}
				  // Create group.
				} else if (message.statusCode == 3){

				  // Send message to group
				} else {

				}
			}

			/**
			 * Gets client socket connection
			 * @return Socket on which client connected.
			 */
			public Socket getSocket() {
				return connection;
			}
			
			/**
			 * Constructor for the clientThread. Takes in a socket for a unique connection.
			 * @param socket
			 * @param count
			 */
			ClientThread(Socket socket, int count){
				this.connection = socket;
				this.count = count;
				setupStreams();
			}

			/**
			 * This is a private helper function to setup the streams
			 * when the client joins and is called in the constructor.
			 */
			private void setupStreams() {
				try {
					outputStream = new ObjectOutputStream(connection.getOutputStream());
					inputStream = new ObjectInputStream(connection.getInputStream());
					connection.setTcpNoDelay(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			/**
			 * This function sends message to the client from the 
			 * server
			 * @param message
			 * @throws Exception
			 */
			public void sendMessage(Message message) throws Exception{
				outputStream.writeObject(message);
			}
			
			/**
			 * Method that sends message to all clients.
			 */
			public void updateClients(Message message) throws Exception {
				for (ClientThread client : clientsList.values()) {
					try {
						client.sendMessage(message);
					} catch (Exception e) {
						System.out.println("Broadcast message error to " + client.userName + ": " + e.getMessage());
					}
				}
			}

			/**
			 * This function closes all connections 
			 */
			public void closeConnections() {
				if (userName != null) {
					clientsList.remove(userName);
					userList.remove(userName);
					Message updatedUserList = new Message(new ArrayList<>(userList), 5);
					try {
						updateClients(updatedUserList);
					} catch (Exception e) {
						System.out.println("Error updating userList to reflect removal of  " + userName);
					}
					System.out.println("Removed " + userName + " from server.");
				}
				try {
					if (inputStream != null) {
						inputStream.close();
					}
					if (outputStream != null) {
						outputStream.close();
					}
					if (connection != null) {
						connection.close();
					}
				} catch (Exception e) {
					System.out.println("Error closing connection for " + userName);
				}
			}
			
			
			
			
		}//end of client thread
}
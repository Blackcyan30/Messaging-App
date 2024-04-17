import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.function.Consumer;

import javafx.application.Platform;
import javafx.scene.control.ListView;


// public class Server{
	
// 	// Initialized to 1 to indicte first client conncetion.
// 	int count = 1;
// 	// Change name to clientsList to be more readable.
// 	ArrayList<ClientThread> clients = new ArrayList<ClientThread>();
// 	// Change name to be less bad.
// 	TheServer server;
// 	// Don't fully understand this need to learn
// 	private Consumer<Serializable> callback;
	
	
// 	Server(Consumer<Serializable> call){
	
// 		callback = call;
// 		server = new TheServer();
// 		server.start();
// 	}
	
// 	class TheServer extends Thread {
		
// 		public void run() {
		
// 			try(ServerSocket mysocket = new ServerSocket(5555);) {

// 		    System.out.println("Server is waiting for a client!");
		  
			
// 		    while(true) {
		
// 				ClientThread clientThread = new ClientThread(mysocket.accept(), count);
// 				callback.accept("client has connected to server: " + "client #" + count);
// 				clients.add(clientThread);
// 				clientThread.start();
				
// 				count++;
// 			    }
// 			} catch(Exception e) {
// 					callback.accept("Server socket did not launch");
// 				}
// 			}
// 		}
	

// 		class ClientThread extends Thread{
			
// 			String userName;
// 			Socket connection;
// 			int count;
// 			ObjectInputStream inputStream;
// 			ObjectOutputStream outputStream;
			
// 			ClientThread(Socket s, int count){
// 				this.connection = s;
// 				this.count = count;	
// 			}
			
// 			/**
// 			 * Method that sends message to all clients.
// 			 */
// 			public void updateClients(String message) {
// 				for(int i = 0; i < clients.size(); i++) {
// 					ClientThread clientThread = clients.get(i);
// 					try {
// 					 clientThread.outputStream.writeObject(message);
// 					}
// 					catch(Exception e) {
// 						e.printStackTrace();
// 					}
// 				}
// 			}
			
// 			public void run(){
					
// 				try {
// 					inputStream = new ObjectInputStream(connection.getInputStream());
// 					outputStream = new ObjectOutputStream(connection.getOutputStream());
// 					connection.setTcpNoDelay(true);
// 				}
// 				catch(Exception e) {
// 					System.out.println("Streams not open");
// 				}
				
// 				updateClients("new client on server: client #"+count);
					
// 				 while(true) {
// 					    try {
// 					    	String data = inputStream.readObject().toString();
// 					    	callback.accept("client: " + count + " sent: " + data);
// 					    	updateClients("client #" + count + " said: " + data);
					    	
// 					    	}
// 					    catch(Exception e) {
// 					    	callback.accept("OOOOPPs...Something wrong with the socket from client: " + count + "....closing down!");
// 					    	updateClients("Client #"+count+" has left the server!");
// 					    	clients.remove(this);
// 					    	break;
// 					    }
// 					}
// 				}//end of run
			
			
// 		}//end of client thread
// }










// {arguments} -> {Statements to be executed.}

public class Server{
	
	// Initialized to 1 to indicte first client conncetion.
	int count = 1;
	// Change name to clientsList to be more readable.
	ArrayList<ClientThread> clientsList = new ArrayList<ClientThread>();
	// Change name to be less bad.
	TheServer server;
	// Don't fully understand this need to learn
	// private Consumer<Serializable> callback;
	
	
	// Server(Consumer<Serializable> call){
	
	// 	callback = call;
	// 	server = new TheServer();
	// 	server.start();
	// }
	
	class TheServer extends Thread {
		
		public void run() {
		
			try(ServerSocket mysocket = new ServerSocket(5555);) {

				System.out.println("Server is waiting for a client!");
			
				while(true) {
			
					ClientThread client = new ClientThread(mysocket.accept(), count);
					// callback.accept("client has connected to server: " + "client #" + count);
					System.out.println("Client connnected with name: " + client.userName + " with the port: " + client.getSocket().getPort() + ".");
					clientsList.add(client);
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
			}
			
			/**
			 * Method that sends message to all clients.
			 */
			public void updateClients(String message) throws Exception {
				for(int i = 0; i < clientsList.size(); i++) {
					ClientThread clientThread = clientsList.get(i);
					clientThread.outputStream.writeObject(message);
				}
			}

			public void readIncomingMessage(Message message) {
				if (message.actionCode == 0) {

				} else if (message.actionCode == 1) {

				} else {
					
				}
			}
			
			public void run(){

				try {
					inputStream = new ObjectInputStream(connection.getInputStream());
					outputStream = new ObjectOutputStream(connection.getOutputStream());
					connection.setTcpNoDelay(true);
				}
				catch(Exception e) {
					System.out.println("Streams not open");
				}
				try{
					updateClients("new client on server: client #"+count);
				} catch(Exception e) {
					e.printStackTrace();
				}
				while(true) {
					    try {
					    	Message data = (Message)inputStream.readObject();

					    	updateClients("client #" + count + " said: " + data);
					    	
					    	}
					    catch(Exception e) {
							try {
								updateClients("Client #"+count+" has left the server!");
							} catch (Exception excpt) {
								excpt.printStackTrace();
							}
					    	clientsList.remove(this);
					    	break;
					    }
					}
				}
			
			
		}//end of client thread
}
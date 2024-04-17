import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.function.Consumer;



public class Client extends Thread{

	
	public Socket socketClient;
	
	public ObjectOutputStream outputStream;
	public ObjectInputStream inputStream;
	
	private Consumer<Serializable> callback;
	
	Client(Consumer<Serializable> call){
	
		callback = call;
	}
	
	public void run() {
		
		try {
			socketClient= new Socket("127.0.0.1",5555);
			outputStream = new ObjectOutputStream(socketClient.getOutputStream());
			inputStream = new ObjectInputStream(socketClient.getInputStream());
			socketClient.setTcpNoDelay(true);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		
		while(true) {
			 
			try {
				String message = inputStream.readObject().toString();
				callback.accept(message);
			}
			catch(Exception e) {
				e.printStackTrace();
			}
		}
	
    }
	
	public void send(String data) {
		
		try {
			outputStream.writeObject(data);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public boolean validateUserName(String userName) {


		return false;
	}

}

// import java.io.*;
// import java.net.Socket;

// public class Client {
//     private Socket socket;
//     private ObjectInputStream input;
//     private ObjectOutputStream output;
//     private String serverAddress;
//     private int port;

//     public Client(String serverAddress, int port) {
//         this.serverAddress = serverAddress;
//         this.port = port;
//     }

//     public boolean connect() {
//         try {
//             socket = new Socket(serverAddress, port);
//             output = new ObjectOutputStream(socket.getOutputStream());
//             input = new ObjectInputStream(socket.getInputStream());
//             return true;
//         } catch (IOException e) {
//             e.printStackTrace();
//             return false;
//         }
//     }

//     public void sendMessage(Message message) {
//         try {
//             output.writeObject(message);
//         } catch (IOException e) {
//             e.printStackTrace();
//         }
//     }

//     public Message receiveMessage() {
//         try {
//             return (Message) input.readObject();
//         } catch (IOException | ClassNotFoundException e) {
//             e.printStackTrace();
//             return null;
//         }
//     }

//     public void closeConnection() {
//         try {
//             input.close();
//             output.close();
//             socket.close();
//         } catch (IOException e) {
//             e.printStackTrace();
//         }
//     }
// }


// import java.io.*;
// import java.net.Socket;
// import java.util.function.Consumer;
// import javafx.application.Platform;

// public class Client {
//     private Socket socket;
//     private ObjectOutputStream outputStream;
//     private ObjectInputStream inputStream;
//     private Consumer<Message> onMessageReceived;
//     private String username;

//     public Client(Consumer<Message> onMessageReceived) {
//         this.onMessageReceived = onMessageReceived;
//     }

//     public boolean connect(String address, int port) {
//         try {
//             socket = new Socket(address, port);
//             outputStream = new ObjectOutputStream(socket.getOutputStream());
//             inputStream = new ObjectInputStream(socket.getInputStream());
//             listenForMessages();
//             return true;
//         } catch (IOException e) {
//             e.printStackTrace();
//             return false;
//         }
//     }

	

//     private void listenForMessages() {
//         new Thread(() -> {
//             try {
//                 while (true) {
//                     Message message = (Message) inputStream.readObject();
//                     if (message != null) {
//                         Platform.runLater(() -> onMessageReceived.accept(message));
//                     }
//                 }
//             } catch (IOException | ClassNotFoundException e) {
//                 e.printStackTrace();
//             }
//         }).start();
//     }

//     public void sendMessage(Message message) {
//         try {
//             outputStream.writeObject(message);
//             outputStream.flush();
//         } catch (IOException e) {
//             e.printStackTrace();
//         }
//     }

//     public void setUsername(String username) {
//         this.username = username;
//     }

//     public String getUsername() {
//         return this.username;
//     }

//     public void close() {
//         try {
//             inputStream.close();
//             outputStream.close();
//             socket.close();
//         } catch (IOException e) {
//             e.printStackTrace();
//         }
//     }
// }

// import java.io.*;
// import java.net.Socket;
// import java.util.function.Consumer;

// public class Client {
//     private Socket socket;
//     private ObjectOutputStream outputStream;
//     private ObjectInputStream inputStream;
//     private final String serverAddress;
//     private final int port;
//     private Consumer<Message> onMessageReceived;
//     private String username;
//     private boolean usernameAccepted;

//     public Client(String serverAddress, int port, Consumer<Message> onMessageReceived) {
//         this.serverAddress = serverAddress;
//         this.port = port;
//         this.onMessageReceived = onMessageReceived;
//     }

//     public void start() throws IOException {
//         socket = new Socket(serverAddress, port);
//         outputStream = new ObjectOutputStream(socket.getOutputStream());
//         inputStream = new ObjectInputStream(socket.getInputStream());

//         // Listen for messages on a new thread to avoid blocking the GUI
//         new Thread(this::listenForMessages).start();
//     }

//     private void listenForMessages() {
//         try {
//             while (true) {
//                 Message message = (Message) inputStream.readObject();
//                 if (message != null) {
//                     onMessageReceived.accept(message);
//                 }
//             }
//         } catch (IOException | ClassNotFoundException e) {
//             e.printStackTrace();
//             closeConnection();
//         }
//     }

//     public void sendMessage(Message message) throws IOException {
//         outputStream.writeObject(message);
//         outputStream.flush();
//     }

//     public void checkUsername(String username) throws IOException {
//         // Send a message to check if the username is available
//         Message usernameCheckMessage = new Message("Client", username + ":checkUsername");
//         sendMessage(usernameCheckMessage);
//     }

//     public void setUsername(String username) {
//         this.username = username;
//     }

//     public String getUsername() {
//         return this.username;
//     }

//     public boolean isUsernameAccepted() {
//         return usernameAccepted;
//     }

//     public void closeConnection() {
//         try {
//             if (inputStream != null) inputStream.close();
//             if (outputStream != null) outputStream.close();
//             if (socket != null) socket.close();
//         } catch (IOException e) {
//             e.printStackTrace();
//         }
//     }
// }

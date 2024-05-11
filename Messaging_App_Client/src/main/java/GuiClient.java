import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.application.Platform;
import java.io.IOException;
import java.util.HashMap;
import java.util.ArrayList;



public class GuiClient extends Application {

    TextField userNameInput; // Where the client will input their username when they first connect
    TextField messageInput; // Where the client will input their written messages
    TextField directMessageUserInput;  // TextField to specify the recipient for direct messages
    Button loginButton; // Once the client is happy with they username, click this
    Button sendButton; // Once the client is happy with their message, click this
    ListView<String> messages; // The display for messages
    ListView<String> users; // The display for users
    Label statusLabel; // Label to display status messages
    Client clientConnection; // Client instance for network communication
    HashMap<String, Scene> sceneMap; // Maps scene names to scene instances
    VBox loginLayout; // Layout for login scene
    BorderPane mainLayout; // Layout for main scene
    ArrayList<String> userList = new ArrayList<>(); // List to store user names
    Text usersLabel;


    public static void main(String[] args) { launch(args); }

    public void start(Stage primaryStage) {

        clientConnection = new Client(this::handleServerMessage);

        try {
            clientConnection.start(); // Try to start the client connection
        } catch(IOException e) {
            e.printStackTrace(); // If connection fails
        }

        // Hash map of all scenes to be used for easy access
        sceneMap = new HashMap<>();
        sceneMap.put("login", createLoginScene());
        sceneMap.put("main", createMainScene());

        primaryStage.setScene(sceneMap.get("login")); // Initial scene, login scene
        primaryStage.setTitle("Messaging App"); // Title of login scene
        primaryStage.show(); // Show scene
    }


    /**
     * Checks to make sure the username isn't blank, and that the user is 
     * able to connect to the server upon username creation
     */
    void handleLogin() {

        String userName = userNameInput.getText().trim();

        if(!userName.isEmpty()) {

            try {
                clientConnection.checkUsername(userName);
            } catch (IOException e) {
                e.printStackTrace();
                statusLabel.setText("Error connecting to the server.");
            }
        } else {
            statusLabel.setText("Username Cannot be Empty");
        }
    }



    /**
     * Updates the GUI with accordance to the action code
     * @param message
     */
    public void handleServerMessage(Message message) {
        System.out.println("Handling server message with statusCode: " + message.statusCode);
    
        Platform.runLater(() -> {
            switch (message.statusCode) {
                case 0: // Username check
                    if (message.usernameAccepted) {
                        clientConnection.setUsername(userNameInput.getText().trim()); // Set the username locally
                        switchToMainScene();
                        updateConnectedUserLabel();
                    } else {
                        statusLabel.setText("Username Already Exists, Try Again");
                    }
                    break;
    
                case 1: // Normal Message
                    System.out.println("Entering case 1 in GuiClient.java with userName " + clientConnection.getUsername());
                    System.out.println("Message contains " + message.sender + " " + message.destination + " " + message.content);
                    // If the sender is sending a private message to someone else
                    if (message.sender.equals(clientConnection.getUsername())) {
                        System.out.println("Entering case1 if");
                        messages.getItems().add("You (to " + message.destination + "): " + message.content);

                    // If the sender is sending a private message to themselves
                    } else if (message.destination.equals(clientConnection.getUsername())) {
                        System.out.println("Entering case1 else");
                        System.out.println("Here recipient gets the message " + message.content + " by sender " + message.sender);
                        messages.getItems().add(message.sender + " (to You): " + message.content);
                    }
                    break;
    
                case 2: // Broadcast to all users
                   messages.getItems().add("Broadcast[" + message.sender + "]: " + message.content);
                    break;
    
                case 3: // Group Chat Creation
                    break;
    
                case 4: // New user announcement
                    messages.getItems().add(message.content + " has joined the chat");  // Example: "Alice has joined the chat"
                    break;
    
                case 5: // Update user list
                    userList = message.userList;
                    users.getItems().setAll(message.userList);
                    break;

                case 6: // Group Chat Message
    
                default:
                    statusLabel.setText("Unexpected server response");
                    break;
            }
        });
    }


    /**
     * Updates the label of this users connection name
     */
    private void updateConnectedUserLabel() {
        usersLabel.setText("Connected Users - Connected as: " + clientConnection.getUsername());
    }


    /**
     * Handles the switch from the welcome scene to the main scene
     */
    void switchToMainScene() {
        Stage stage = (Stage) loginLayout.getScene().getWindow();
        stage.setScene(sceneMap.get("main"));
        updateConnectedUserLabel();
    }


    /**
     * Handles how and when and what to display on the message board
     */
    private void displayMessage() {
        String messageContent = messageInput.getText().trim();
        String recipient = directMessageUserInput.getText().trim();
    
        if (!recipient.isEmpty() ) {
            if (!messageContent.isEmpty() && userList.contains(recipient)) {
                // Create a message object for a personal message
                Message message = new Message(messageContent, clientConnection.getUsername(), recipient, 1);
                try {
                    clientConnection.sendMessage(message);
                    messages.getItems().add("You (to " + recipient + "): " + messageContent); // Display the message locally for the sender
                } catch (IOException e) {
                    e.printStackTrace();
                    messages.getItems().add("Failed to send message to " + recipient);
                }
            } else {
                // Handle error: recipient not found or name field is empty
                messages.getItems().add("Recipient not found or name field is empty.");
            }
            messageInput.clear(); // Clear the input field
            directMessageUserInput.clear(); // Clear the recipient field
        } else {
            Message broadCast = new Message(messageContent, 2);
            try {
                clientConnection.sendMessage(broadCast);
            } catch (Exception e) {
                
            }
            
        }

        // Clear the input fields regardless of whether the message was sent or not
        messageInput.clear(); 
        directMessageUserInput.clear(); 
    }




    // ----------- SCENE CONSTRUCTION --------- //
    /**
     * Creates the GUI for the initial login scene
     * @return Login scene
     */
    private Scene createLoginScene() {

        // Main layout of the login Scene
        loginLayout = new VBox(15);
        loginLayout.setPadding(new Insets(50, 50, 50, 50));
        loginLayout.setStyle("-fx-background-color: navy;");

        // Text at the top, welcoming the user
        Text welcomeText = new Text("Welcome to the Messaging App");
        welcomeText.setFont(Font.font("Sans", 20));
        welcomeText.setFill(javafx.scene.paint.Color.WHITE);

        // Text telling the user to input a username
        Text instructionsText = new Text("Please Enter a UNIQUE Username");
        instructionsText.setFont(Font.font("Sans", 15));
        instructionsText.setFill(javafx.scene.paint.Color.WHITE);

        // Textfield for the user to input their username
        userNameInput = new TextField();
        userNameInput.setPromptText("Enter Username Here");
        userNameInput.setStyle(userNameInput.getStyle() + "-fx-prompt-text-fill: gray;" + "-fx-focus-color: darkblue;");
        userNameInput.setMaxWidth(250);

        // Button for the user to submit their username
        loginButton = new Button("Login");
        loginButton.setStyle("-fx-background-color: grey; -fx-text-fill: white; -fx-border-color: white; -fx-border-width: 3px;");
        loginButton.setOnAction(e -> handleLogin());

        // If the user inputs nothing, or the username is already taken
        statusLabel = new Label();
        statusLabel.setTextFill(javafx.scene.paint.Color.RED);

        // Construct and return welcome scene
        loginLayout.getChildren().addAll(welcomeText, instructionsText, userNameInput, loginButton, statusLabel);
        loginLayout.setAlignment(Pos.CENTER);
        return new Scene(loginLayout, 400, 300);
    }


    /**
     * Creates the main scene (messaging board)
     * @return Main scene
     */
    private Scene createMainScene() {
    
        mainLayout = new BorderPane();

        // Sets up a list view for all users in server
        users = new ListView<>();
        users.setStyle("-fx-background-color: #333; -fx-padding: 5px;");
        // Text usersLabel = new Text("Connected Users - Connected as: " + clientConnection.getUsername());
        usersLabel = new Text("Connected Users - Connected as: " + clientConnection.getUsername());
        usersLabel.setFont(Font.font("Sans", 12));
        usersLabel.setFill(javafx.scene.paint.Color.WHITE);

        // Sets up the left VBox where the list of users is
        VBox usersBox = new VBox(5, usersLabel, users);
        usersBox.setPadding(new Insets(10));
        usersBox.setStyle("-fx-background-color: #333;");  // Set a dark background color or as preferred
        mainLayout.setLeft(usersBox);

        // Sets up the list of messages
        messages = new ListView<>();
        messages.setStyle("-fx-background-color: #333; -fx-padding: 5px;");
        messages.setPrefWidth(400);
        
        // Sets up the text field for user to input messages
        messageInput = new TextField();
        messageInput.setPromptText("Type Your Message Here");
        messageInput.setStyle(userNameInput.getStyle() + "-fx-prompt-text-fill: gray;" + "-fx-focus-color: darkblue;");
        messageInput.setPrefWidth(325);

        // Direct message user input setup
        directMessageUserInput = new TextField();
        directMessageUserInput.setPromptText("Recipient Username (Optional)");
        directMessageUserInput.setStyle(userNameInput.getStyle() + "-fx-prompt-text-fill: gray;" + "-fx-focus-color: darkblue;");
        directMessageUserInput.setPrefWidth(200);

        // Sets up the button for the user to send messages
        sendButton = new Button("Send");
        sendButton.setStyle("-fx-background-color: navy; -fx-text-fill: white; -fx-border-color: white; -fx-border-width: 3px;");
        sendButton.setOnAction(e -> displayMessage());

        // HBox for the message input text field, and send button
        HBox messageBox = new HBox(10, directMessageUserInput, messageInput, sendButton);
        messageBox.setAlignment(Pos.CENTER);
        messageBox.setPadding(new Insets(5, 10, 5, 10));

        // Sets up the layout on the right which contains the list view of messages, the text field for the users messages, and the send button
        VBox rightLayout = new VBox(10, messages, messageBox);
        rightLayout.setStyle("-fx-background-color: grey;");  // Light background for messages area
        rightLayout.setPadding(new Insets(10));

        // Assigning areas in the BorderPane
        mainLayout.setLeft(usersBox);
        mainLayout.setCenter(rightLayout);

        // Return the main scene
        return new Scene(mainLayout, 950, 400);
    }
}
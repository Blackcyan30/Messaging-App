
// import java.util.HashMap;

// import javafx.application.Application;
// import javafx.application.Platform;
// import javafx.event.EventHandler;
// import javafx.geometry.Insets;
// import javafx.scene.Scene;
// import javafx.scene.control.Button;
// import javafx.scene.control.ListView;
// import javafx.scene.control.TextField;
// import javafx.scene.layout.BorderPane;
// import javafx.scene.layout.GridPane;
// import javafx.scene.layout.HBox;
// import javafx.scene.layout.VBox;
// import javafx.stage.Stage;
// import javafx.stage.WindowEvent;

// public class GuiClient extends Application{

	
// 	TextField c1;
// 	Button b1;
// 	HashMap<String, Scene> sceneMap;
// 	VBox clientBox;
// 	Client clientConnection;
	
// 	ListView<String> listItems2;
	
	
// 	public static void main(String[] args) {
// 		launch(args);
// 	}

// 	@Override
// 	public void start(Stage primaryStage) throws Exception {
// 		clientConnection = new Client(data -> {
// 				Platform.runLater(()->{listItems2.getItems().add(data.toString());
// 			});
// 		});

// 		clientConnection.start();

// 		listItems2 = new ListView<String>();
		
// 		c1 = new TextField();
// 		b1 = new Button("Send");
// 		b1.setOnAction(e->{clientConnection.send(c1.getText()); c1.clear();});
		
// 		sceneMap = new HashMap<String, Scene>();

// 		sceneMap.put("client",  createClientGui());
		
// 		primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
//             @Override
//             public void handle(WindowEvent t) {
//                 Platform.exit();
//                 System.exit(0);
//             }
//         });


// 		primaryStage.setScene(sceneMap.get("client"));
// 		primaryStage.setTitle("Client");
// 		primaryStage.show();
		
// 	}
	

	
// 	public Scene createClientGui() {
		
// 		clientBox = new VBox(10, c1,b1,listItems2);
// 		clientBox.setStyle("-fx-background-color: blue;"+"-fx-font-family: 'serif';");
// 		return new Scene(clientBox, 400, 300);
		
// 	}

// }


import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.geometry.Insets;
import javafx.application.Platform;
import java.util.HashMap;

public class GuiClient extends Application {

    TextField userInput;
    Button sendButton, loginButton;
    ListView<String> messagesDisplay, userList, groupList;
    Label statusLabel;
    Client clientConnection;  // Assuming Client handles network communication
    HashMap<String, Scene> sceneMap;
    VBox loginLayout, mainLayout;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        clientConnection = new Client(data -> {
            Platform.runLater(() -> {
                messagesDisplay.getItems().add(data.toString());
                // Update the user and group lists when receiving data
            });
        });
		

        clientConnection.start();

        sceneMap = new HashMap<>();
        sceneMap.put("login", createLoginScene());
        sceneMap.put("main", createMainScene());

        primaryStage.setScene(sceneMap.get("login"));
        primaryStage.setTitle("Client Chat Application");
        primaryStage.show();
    }

    private Scene createLoginScene() {
        loginLayout = new VBox(10);
        loginLayout.setPadding(new Insets(15, 20, 10, 10));

        userInput = new TextField();
        userInput.setPromptText("Enter your username");

        loginButton = new Button("Login");
        loginButton.setOnAction(e -> handleLogin());

        statusLabel = new Label();

        loginLayout.getChildren().addAll(userInput, loginButton, statusLabel);
        return new Scene(loginLayout, 300, 200);
    }

    private void handleLogin() {
        String username = userInput.getText();
        if (!username.isEmpty()) {
            clientConnection.checkUsername(username, valid -> {
                Platform.runLater(() -> {
                    if (valid) {
                        clientConnection.setUsername(username);
                        switchToMainScene();
                    } else {
                        statusLabel.setText("Username already exists, please try another.");
                    }
                });
            });
        }
    }

    private Scene createMainScene() {
        mainLayout = new VBox(10);
        mainLayout.setPadding(new Insets(15, 20, 10, 10));

        messagesDisplay = new ListView<>();
        userList = new ListView<>();
        groupList = new ListView<>();

        TextField messageInput = new TextField();
        messageInput.setPromptText("Enter your message here");

        sendButton = new Button("Send");
        sendButton.setOnAction(e -> {
            String message = messageInput.getText();
            if (!message.isEmpty()) {
                clientConnection.sendMessage(new Message(clientConnection.getUsername(), message));
                messageInput.clear();
            }
        });

        mainLayout.getChildren().addAll(new Label("Users Connected:"), userList, 
                                         new Label("Groups:"), groupList, messageInput, sendButton, messagesDisplay);
        return new Scene(mainLayout, 400, 500);
    }

    private void switchToMainScene() {
        Platform.runLater(() -> {
            Stage stage = (Stage) loginLayout.getScene().getWindow();
            stage.setScene(sceneMap.get("main"));
        });
    }
}

package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SplitPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.ConnectException;
import java.util.ArrayList;

public class ClientApp extends Application {
    private ArrayList<Thread> threads;

    public static void main(String[] args){
        launch();
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        for (Thread thread: threads){
            thread = null;
            thread.interrupt();
        }
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        threads = new ArrayList<Thread>();

        primaryStage.setTitle("Mangos Chat Client");
        //primaryStage.setResizeable(false);
        primaryStage.setScene(makeInitScene(primaryStage));
        primaryStage.show();
    }

    public Scene makeInitScene(Stage primaryStage) {
        GridPane rootPane = new GridPane();
        rootPane.setPadding(new Insets(20));
        rootPane.setVgap(10);
        rootPane.setHgap(10);
        rootPane.setAlignment(Pos.CENTER);

        TextField usernameField = new TextField();
        TextField hostAddressField = new TextField();
        TextField portNumField = new TextField();

        Label nameLabel = new Label("Username");
        Label hostAddressLabel = new Label("Host Address");
        Label portNumLabel = new Label("Port Number");
        Label errorLabel = new Label();

        Button btnConnect = new Button("Connect");
        btnConnect.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent Event) {
                Client client;
                try {
                    client = new Client(hostAddressField.getText(), Integer
                            .parseInt(portNumField.getText()), usernameField
                            .getText());
                    Thread clientThread = new Thread(client);
                    clientThread.setDaemon(true);
                    clientThread.start();
                    threads.add(clientThread);

                    primaryStage.close();
                    primaryStage.setScene(goChatUI(client));
                    primaryStage.show();
                }
                catch(ConnectException e){
                    errorLabel.setTextFill(Color.RED);
                    errorLabel.setText("invalid host address, try again!");
                }
                catch (NumberFormatException | IOException e) {
                    errorLabel.setTextFill(Color.RED);
                    errorLabel.setText("invalid port number, try again!");
                }
            }
        });

        rootPane.add(usernameField, 1, 0);
        rootPane.add(nameLabel, 0, 0);
        rootPane.add(hostAddressField, 1, 1);
        rootPane.add(hostAddressLabel, 0, 1);
        rootPane.add(portNumField, 1, 2);
        rootPane.add(portNumLabel, 0, 2);
        rootPane.add(btnConnect, 0, 3, 2, 1);
        rootPane.add(errorLabel, 0, 4);

        return new Scene(rootPane, 400, 400);
    }

    public Scene makeChatUI(Client client) {
        GridPane rootPane = new GridPane();
        rootPane.setPadding(new Insets(20));
        rootPane.setAlignment(Pos.CENTER);
        rootPane.setHgap(10);
        rootPane.setVgap(10);

        ListView<String> chatListView = new ListView<String>();
        chatListView.setItems(client.chatLog);

        TextField chatTextField = new TextField();
        chatTextField.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                client.writeToServer(chatTextField.getText());
                chatTextField.clear();
            }
        });

        Button btnDisconnect = new Button("Disconnect");
        btnDisconnect.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent Event) {
                try {
                    client.disconnect();
                    System.exit(0);
                } catch (Exception e) {

                }
            }
        });

        rootPane.add(chatListView, 0, 0);
        rootPane.add(chatTextField, 0, 1);
        rootPane.add(btnDisconnect, 0, 2);

        return new Scene(rootPane, 400, 400);
    }

    public Scene goChatUI(Client client){
        ChatUI root = new ChatUI(client);

        return new Scene(root);
    }
}

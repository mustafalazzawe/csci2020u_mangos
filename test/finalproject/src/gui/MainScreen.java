package gui;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.layout.BorderPane;
import javafx.util.Duration;

import chatclient.Client;
import chatclient.ClientLayoutController;

public class MainScreen extends BorderPane {

    //gameloop variables
    private Timeline gameLoop;

    //main variables
    private final ClientLayoutController main;
    private final DrawScreen center;

    public MainScreen(ClientLayoutController main) {
        this.main = main;
        this.center = new DrawScreen();
        setCenter();
        runGameLoop();
    }

    private void setCenter() {
        this.setCenter(center);
    }

    private void runGameLoop() {
        EventHandler<ActionEvent> gameUpdate = event -> {
            center.update();
            center.draw();
        };
        gameLoop = new Timeline(new KeyFrame(Duration.millis(33.3), gameUpdate));
        gameLoop.setCycleCount(Animation.INDEFINITE);
        gameLoop.play();
    }

    public void stopGameLoop() {
        gameLoop.stop();
    }

}

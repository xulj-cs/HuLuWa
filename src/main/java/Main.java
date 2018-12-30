import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import game.Game;
import game.Ground;

import java.io.File;

import static utility.Constants.GROUND_OFFSET_X;
import static utility.Constants.GROUND_OFFSET_Y;

public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception{


        // set the game backGround
        Pane root = new Pane();

        // init the game world
        Game game = new Game();

        // show the battleGround
        Ground ground = game.getGround();
        ground.setLayoutX(GROUND_OFFSET_X);
        ground.setLayoutY(GROUND_OFFSET_Y);
        root.getChildren().add(ground);



        // set the scene size and fix it
        Image backGroundImage = new Image(getClass().getResource("images/ground.png").toExternalForm());




        Scene scene = new Scene(root, backGroundImage.getWidth(), backGroundImage.getHeight());
        // set the scene style from resource css file
        scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());

        // startGame the game when game key presses
        scene.setOnKeyPressed(event -> {

            if(event.getCode() == KeyCode.SPACE) {
                game.start();
            }else if(event.getCode() == KeyCode.R){
                game.prepare();
            }else if(event.getCode() == KeyCode.L){
                game.loadRecord(primaryStage);
            }
        });

        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.setTitle("HuLuWa Fight Against Monster");
        primaryStage.show();


    }

}


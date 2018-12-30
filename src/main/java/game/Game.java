package game;

import creature.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


import static utility.Constants.*;

public class Game {


    public BufferedWriter getBufferedWriter() {
        return bufferedWriter;
    }

    private BufferedWriter bufferedWriter = null;

    // (t,f) start(Space) ->(f,t) over->(f,f) -> prepare(R)->(t,f)
    // (t,f) replay(Space) ->(f,t) over->(f,f) -> prepare(R)->(t,f)

    private boolean isReady = true;
    private boolean isActive = false;

    private Ground ground = new Ground();
    private Text statusBar = new Text();

    {
        statusBar.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
        statusBar.setLayoutX(800);
        statusBar.setLayoutY(100);

    }

    private List<Creature> creatures = new ArrayList<>();

    // game threads : one creature has one thread
    private ExecutorService executorService = null;

    private Lock lock = new ReentrantLock();

    private int level = 0;

    public Ground getGround() {
        return ground;
    }

    public Lock getLock() {
        return lock;
    }

    public void setStatus(String status){
        statusBar.setText(status);
    }

    public void addStatus(String status){
        statusBar.setText(statusBar.getText() + status);
    }

    public void replayMove(int x, int y, int nx, int ny){
        Creature c1 = getGround().getCreature(x, y);
        Creature c2 = getGround().getCreature(nx, ny);
        assert c1 != null;

        getGround().clearCreature(x, y);
        getGround().placeCreature(nx, ny, c1);

        if(c2!=null){
            c2.beKilled();
        }
    }

    public void replayStay(int x, int y){
        Creature c = getGround().getCreature(x, y);
        assert c != null;
        c.beKilled();
        getGround().clearCreature(x, y);

    }

    public void loadRecord(Stage primaryStage){
        if(isReady && !isActive) {

            isActive = true;
            isReady = false;
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("open record file");
            fileChooser.setInitialDirectory(new File("."));
            File file = fileChooser.showOpenDialog(primaryStage);
            new Replay(this, file);
        }
    }

    public Game(){
        initGame();
    }

    // game startGame!
    public void prepare(){
        if(!isReady && !isActive) {
            clearGame();
            level ++;

            initGame();
            isReady = true;

            System.out.println("I'm ready to start");

        }
    }
    // game startGame!
    public void start(){
        if(isReady && !isActive) {

            isActive = true;
            isReady = false;

            try {
                bufferedWriter = new BufferedWriter(new FileWriter(RECORD_FILENAME));
            }catch (IOException e){
                System.err.println("unable to open file :" + RECORD_FILENAME);
            }


            setStatus("Game start!!\n" +
                    "----------------\n");

            executorService = Executors.newFixedThreadPool(creatures.size());
            for(Creature c : creatures)
                executorService.execute(c);
        }
    }

    private void clearGame(){
        creatures.clear();
        ground.clearAllCreature();
    }

    private void initGame() {

        // load the game ZhenFa
        int x = 0, y = 0;
        int calabashRank = 1;
        String levelZhenFa = GAME_LEVEL[level%GAME_LEVEL.length];
        for (int i = 0; i < levelZhenFa.length(); i++) {
            char item = levelZhenFa.charAt(i);
            if (item == '\n') {
                y += 1;
                x = 0;
            } else{
                Creature c = null;
                switch(item){
                    case 'c':{
                        c = new CalabashBrother(calabashRank++);
                        break;
                    }
                    case 'g':{
                        c = new GrandPa();
                        break;
                    }
                    case 'f':{
                        c = new Frog();
                        break;
                    }
                    case 's':{
                        c = new Scorpion();
                        break;
                    }
                    case 'S':{
                        c = new Snake();
                        break;
                    }
                    default: //
                }
                if(c != null) {
                    creatures.add(c);
                    c.setGame(this);
                    ground.placeCreature(x, y, c);

                }
                x += 1;
            }
        }
        ground.getChildren().add(statusBar);
        setStatus("Game is ready,\npress SPACE to start");

    }


    public void checkGameOver(){
        int numOfLeftHuman = 0, numOfLeftMonster = 0;
        for(Creature c : creatures){
            if(c.isAlive()) {
                if (c instanceof Human) {
                    numOfLeftHuman++;
                } else {
                    numOfLeftMonster++;
                }
            }
        }

        addStatus(" " + numOfLeftHuman + "vs" + numOfLeftMonster + "\n");

        if(numOfLeftHuman == 0 || numOfLeftMonster == 0){
            // game Over
            executorService.shutdownNow();
            try {
                bufferedWriter.close();
            }catch (IOException e){
                System.err.println("unable to close");
            }
            isActive = false;

            addStatus("-----------------\n"+
                    "Game Over!!\n" +
                    "Press R to restart" );
        }
    }
}

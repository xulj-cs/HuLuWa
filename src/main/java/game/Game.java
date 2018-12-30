package game;

import creature.*;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static utility.Constants.*;

public class Game {


    public BufferedWriter getBufferedWriter() {
        return bufferedWriter;
    }

    private BufferedWriter bufferedWriter = null;

    // (t,f) start(Space) ->(f,t) over->(f,f) -> prepare(R)->(t,f)
    // (t,f) replay(Space) ->(f,t) over->(f,f) -> prepare(R)->(t,f)

    boolean isReady = false;
    boolean isActive = false;

    private Ground ground = new Ground();

    public Text getStatusBar() {
        return statusBar;
    }

    private Text statusBar = new Text();

    private List<Creature> creatures = new ArrayList<>();

    // game threads : one creature has one thread
    private ExecutorService executorService = null;

    private int level = -1;

    public Ground getGround() {
        return ground;
    }


    public void setStatus(String status){
        statusBar.setText(status);
    }

    public void addStatus(String status){
        statusBar.setText(statusBar.getText() + status);
    }



    public void loadRecord(Stage primaryStage){
        if(!isReady && !isActive) {

            isActive = true;

            clearGame();

            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("open record file");
            fileChooser.setInitialDirectory(new File("."));
            File file = fileChooser.showOpenDialog(primaryStage);

            new Replay(this, file);

        }
    }

    public Game(){
        // start a new game or load a game record
        setStatus("Press S to start a new game\n" +
                "Press L to load a game record");
    }

    // game start!
    public void next(){
        if(!isReady && !isActive) {
            clearGame();
            level ++;
            isReady = true;
            initGame(level);

            //Record
            try {
                bufferedWriter = new BufferedWriter(new FileWriter(RECORD_FILENAME));
                bufferedWriter.write(level+"\n");
            }catch (IOException e){
                e.printStackTrace();
            }

            setStatus("Game is ready,\npress SPACE to run");
        }
    }
    // game startGame!
    public void run(){
        if(isReady && !isActive) {

            isActive = true;
            isReady = false;

            setStatus("Game start!!\n" +
                    "----------------\n");

            executorService = Executors.newFixedThreadPool(creatures.size());
            for(Creature c : creatures)
                executorService.execute(c);
        }
    }

    public void clearGame(){

        creatures.clear();
        ground.clearAllCreature();
    }

    public void initGame(int level) {

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
                    "Press N to the next level\n" +
                    "Press L to load a game record" );
        }
    }
}

package game;

import creature.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    private List<Creature> creatures = new ArrayList<>();

    // game threads : one creature has one thread
    private ExecutorService executorService = null;

    private Lock lock = new ReentrantLock();

    private int level = 1;

    public Ground getGround() {
        return ground;
    }

    public Lock getLock() {
        return lock;
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

            System.out.println("Game start!");

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

        for (int i = 0; i < GAME_LEVEL[level].length(); i++) {
            char item = GAME_LEVEL[level].charAt(i);
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
        System.out.println(numOfLeftHuman + "vs" + numOfLeftMonster);

        if(numOfLeftHuman == 0 || numOfLeftMonster == 0){
            // game Over
            executorService.shutdownNow();
            try {
                bufferedWriter.close();
            }catch (IOException e){
                System.err.println("unable to close");
            }
            isActive = false;
        }
    }
}

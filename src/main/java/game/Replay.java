package game;

import creature.Creature;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static utility.Constants.*;

public class Replay extends Thread{

    private Game game = null;
    private File file = null;

    private int getLevel(){
        int level = -1;
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))){
            level = Integer.parseInt(bufferedReader.readLine());

        }catch (IOException e){
            e.printStackTrace();
        }
        return level;
    }

    public Replay(Game game, File file){
        this.game = game;
        this.file = file;
        game.setStatus("Replay...\n" +
                "--------------\n");
        game.initGame(getLevel());
        start();
    }

    private void replayMove(int x, int y, int nx, int ny){
        Creature c1 = game.getGround().getCreature(x, y);
        Creature c2 = game.getGround().getCreature(nx, ny);

        game.getGround().clearCreature(x, y);
        game.getGround().placeCreature(nx, ny, c1);

        if(c2!=null){
            c2.beKilled();
            game.addStatus(c2.getName() + " was killed!!\n");
        }
    }

    private void replayStay(int x, int y){
        Creature c = game.getGround().getCreature(x, y);
        c.beKilled();
        game.addStatus(c.getName() + " was killed!!\n");
        game.getGround().clearCreature(x, y);

    }

    @Override
    public void run() {
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))){

            // skip the first line
            String str = bufferedReader.readLine();
            Pattern pattern1 = Pattern.compile("\\((\\d+),(\\d+)\\)->\\((\\d+),(\\d+)\\)");
            Pattern pattern2 = Pattern.compile("\\((\\d+),(\\d+)\\)");

            while ((str = bufferedReader.readLine()) != null) {
                System.out.println(str);
                Matcher m = pattern1.matcher(str);
                if(m.matches()){
                    int x = Integer.parseInt(m.group(1));
                    int y = Integer.parseInt(m.group(2));
                    int nx = Integer.parseInt(m.group(3));
                    int ny = Integer.parseInt(m.group(4));

                    replayMove(x, y, nx, ny);
                }else{
                    m = pattern2.matcher(str);
                    if(m.matches()) {
                        int x = Integer.parseInt(m.group(1));
                        int y = Integer.parseInt(m.group(2));
                        replayStay(x, y);
                    }else{
                        System.err.println("No matches");
                    }
                }

                try {
                    TimeUnit.MILLISECONDS.sleep(TIMEOUT);
                }catch (InterruptedException e){
                    System.err.println("Interrupted");
                }
            }
            game.addStatus("---------------\n" +
                    "Replay is over\n" +
                    "Press N to the next level\n" +
                    "Press L to load a game record\n");
            game.isActive = false;
        }catch (IOException e){
            System.err.println("Something Wrong");
        }
    }
}

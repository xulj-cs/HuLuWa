package game;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Replay extends Thread {

    private Game game = null;
    private File file = null;
    public Replay(Game game, File file){
        this.game = game;
        this.file = file;
        start();
    }
    @Override
    public void run() {
        try ( BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
            String str = null;
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

                    game.replayMove(x, y, nx, ny);
                }else{
                    m = pattern2.matcher(str);
                    assert m.matches();
                    int x = Integer.parseInt(m.group(1));
                    int y = Integer.parseInt(m.group(2));
                    game.replayStay(x, y);
                }

                try {
                    TimeUnit.MILLISECONDS.sleep(1000);
                }catch (InterruptedException e){
                    System.err.println("Interrupted");
                }
            }

        }catch (IOException e){
            System.err.println("Something Wrong");
        }
    }
}

package creature;

import static utility.Constants.*;

import javafx.scene.image.ImageView;
import game.Game;
import utility.Resource;

import java.io.IOException;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

public class Creature extends ImageView implements Runnable{

    private String name;

    private Game game;

    protected boolean alive = true;

    public Creature(String name){
        this.name = name;
        setImage();
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public boolean isAlive() {
        return alive;
    }

     public void beKilled() {
        alive = false;
        setImage();
    }

     public void setLayout(int ix, int iy){
        if(0 <= ix && ix < GROUND_COLUMN){
            setLayoutX(ix * TILE_WIDTH);
        }

        if(0 <= iy && iy < GROUND_ROW){
            setLayoutY(iy * TILE_HEIGHT);
        }
    }


     public void tryMove(int dx, int dy){
        int x = (int)getLayoutX() / TILE_WIDTH;
        int y = (int)getLayoutY() / TILE_HEIGHT;
        int nx = x + dx;
        int ny = y + dy;

        if(nx <0 || nx >= GROUND_COLUMN ){
            nx = x;
        }
        if(ny <0 || ny >= GROUND_ROW){
            ny = y;
        }

        // stay
        if( nx == x && ny == y)
            return;

        Lock lock = game.getLock();

        lock.lock();
        try{
            Creature c = game.getGround().getCreature(nx, ny);
            if (c == null){
                // (x,y) -> (nx, ny)
                game.getGround().clearCreature(x, y);
                //setLayout(nx, ny);
                game.getGround().placeCreature(nx, ny, this);
                try {
                    game.getBufferedWriter().write(String.format("(%d,%d)->(%d,%d)%n", x, y, nx, ny));
                }catch (IOException e){
                    System.err.println("unable to record");
                }
            }else{
                if( this instanceof Human != c instanceof Human){

                    fightAgainst(c);
                    boolean isWin = this.isAlive();
                    if(isWin){  //前进到对方的格子
                        game.getGround().clearCreature(x, y);
                        game.getGround().placeCreature(nx, ny, this);
                        try {
                            game.getBufferedWriter().write(String.format("(%d,%d)->(%d,%d)%n", x, y, nx, ny));
                        }catch (IOException e){
                            System.err.println("unable to record");
                        }
                    }else{  //死在自己的格子
                        game.getGround().clearCreature(x, y);
                        try {
                            game.getBufferedWriter().write(String.format("(%d,%d)%n", x, y));
                        }catch (IOException e){
                            System.err.println("unable to record");
                        }
                    }

                    game.checkGameOver();
                }
            }


        }finally {
            lock.unlock();
        }
    }

    @Override
    public void run() {

        Random rand = new Random();
        try{
            while(isAlive()) {
                int dx = rand.nextInt(3)-1;
                int dy = rand.nextInt(3)-1;
//            System.out.println(name + "(" + dx + "," + dy + ")");
                tryMove(dx, dy);
                TimeUnit.MILLISECONDS.sleep(50 + rand.nextInt(100));

            }
        }catch (InterruptedException e) {
            System.out.println(name+" : thread is interrupted");
        }

        System.out.println(name+" : thread is over");

    }

    public void fightAgainst(Creature c){
        assert this.isAlive();
        assert c.isAlive();

        Random rand = new Random();
        if(rand.nextBoolean())
            c.beKilled();
        else
            this.beKilled();
    }

    private void setImage(){
        String imageFileName = (alive? "":"dead_") + name + ".png";
        setImage(Resource.getImage(imageFileName));
        setFitWidth(TILE_WIDTH);
        setFitHeight(TILE_HEIGHT);
    }
}

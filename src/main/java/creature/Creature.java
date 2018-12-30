package creature;

import static utility.Constants.*;

import javafx.scene.image.ImageView;
import game.Game;
import utility.Resource;

import java.io.IOException;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class Creature extends ImageView implements Runnable{


    public String getName() {
        return name;
    }

    private String name;

    private Game game;

    private boolean alive = true;

    private boolean haveImage = false;

    public Creature(String name, boolean haveImage){
        this.name = name;
        this.haveImage = haveImage;
        setImage();
    }

    public Creature(String name ){
        this(name, true);
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


     private void tryMove(int dx, int dy){
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

        synchronized (game){
            if(!isAlive())
                return;
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
                    if(isWin){
                        game.getGround().clearCreature(x, y);
                        game.getGround().placeCreature(nx, ny, this);
                        try {
                            game.getBufferedWriter().write(String.format("(%d,%d)->(%d,%d)%n", x, y, nx, ny));
                        }catch (IOException e){
                            System.err.println("unable to record");
                        }
                    }else{
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
        }
    }

    @Override
    public void run() {

        Random rand = new Random();
        try{
            while(isAlive()) {
                int dx = 0, dy = 0;
                switch (rand.nextInt(4)){
                    case 0:dx = +1;break;
                    case 1:dx = -1;break;
                    case 2:dy = +1;break;
                    case 3:dy = -1;break;
                }
                tryMove(dx, dy);
                TimeUnit.MILLISECONDS.sleep(TIMEOUT + rand.nextInt(20));

            }
        }catch (InterruptedException e) {
            System.out.println(name+" : thread is interrupted");
        }

        System.out.println(name+" : thread is over");

    }

    public void fightAgainst(Creature c){

        Random rand = new Random();
        if(rand.nextBoolean()) {
            c.beKilled();
            if(game!=null)
                game.addStatus(this.name + " killed " + c.name + "!!");
        }
        else {
            this.beKilled();
            if(game!=null)
                game.addStatus(c.name + " killed " + this.name + "!!");
        }
    }

    private void setImage(){
        if(!haveImage)
            return;
        String imageFileName = (alive? "":"dead_") + name + ".png";
        setImage(Resource.getImage(imageFileName));
        setFitWidth(TILE_WIDTH);
        setFitHeight(TILE_HEIGHT);
    }
}

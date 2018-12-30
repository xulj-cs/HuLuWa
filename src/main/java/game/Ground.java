package game;

import static utility.Constants.*;

import creature.Creature;
import javafx.scene.layout.Pane;


public class Ground extends Pane {


    private Tile[][] tiles = new Tile[GROUND_COLUMN][GROUND_ROW];

    public Ground(){
        for(int i=0;i<GROUND_COLUMN;i++){
            for(int j=0;j<GROUND_ROW;j++){
                tiles[i][j] = new Tile();
            }
        }
	}


    public Creature getCreature(int x, int y){
        return tiles[x][y].getCreature();
    }

    public void placeCreature(int x, int y, Creature c){
        tiles[x][y].setCreature(c);
        c.setLayout(x, y);

        // when load the ZhenFa
        if(!getChildren().contains(c)){
            getChildren().add(c);
        }

    }

    public void clearCreature(int x, int y){
        tiles[x][y].clearCreature();
    }

    public void clearAllCreature(){

        getChildren().clear();

        for(int i=0;i<GROUND_COLUMN;i++){
            for(int j=0;j<GROUND_ROW;j++){
                tiles[i][j].clearCreature();
            }
        }
    }
}

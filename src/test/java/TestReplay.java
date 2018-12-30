import creature.CalabashBrother;
import creature.Creature;
import creature.Frog;
import game.Game;
import game.Replay;
import org.junit.Test;
import static org.junit.Assert.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static utility.Constants.*;
public class TestReplay {
    @Test
    public void test() throws IOException{

        List<Creature> creatures = new ArrayList<>();
        for(int i=0;i<7;i++) {
            creatures.add(new Creature("calabash" + i, false));
            creatures.add(new Creature("frog", false));
        }
        Random random = new Random();

        int cnt = 0;
        for(int i=0;i<creatures.size();i++) {
            int x = random.nextInt(creatures.size());
            int y = random.nextInt(creatures.size());
            if (x == y || !creatures.get(x).isAlive() || !creatures.get(y).isAlive())
                continue;
            creatures.get(x).fightAgainst(creatures.get(y));
            cnt++;
        }
        int alive = 0;
        for(Creature c : creatures){
            if(c.isAlive())
                alive++;
        }
        assertEquals(creatures.size()-cnt, alive);
    }
}

import org.junit.Test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import static utility.Constants.*;
public class TestReplay {
    @Test
    public void test(){
        try(BufferedReader br = new BufferedReader(new FileReader(RECORD_FILENAME))){

        }catch (IOException e){
            e.printStackTrace();
        }
    }
}

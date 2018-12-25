package utility;

import javafx.scene.image.Image;

public class Resource {
    public static Image getImage(String filename){
        return new Image(Resource.class.getClassLoader().getResource("images/" + filename).toExternalForm());
    }
}

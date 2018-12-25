package creature;

import javafx.scene.image.Image;
import utility.Resource;


enum ColorOfCalabashBrother {
    RED, ORANGE, YELLOW, GREEN, CYAN, BLUE, PURPLE
}


//enum RankOfCalabashBrother {
//    BIG, SECOND, THIRD, FOURTH, FIFTH, SIXTH, LITTLE
//}
public class  CalabashBrother extends Human {

//    BIG(1), SECOND(2), THIRD(3),
//        FOURTH(4), FIFTH(5), SIXTH(6), SEVEN(7);

    private ColorOfCalabashBrother color;
    private int rank;

    //    private Image img;
    public CalabashBrother(int rank) {
        super("calabash" + rank);
//        this.color = ColorOfCalabashBrother.values()[rank-1];
        this.rank = rank;
    }

    public int getRank() {
        return rank;
    }

    public String getColor() {
        return color.toString();
    }

    public int getColorOrdinal() {
        return color.ordinal();
    }


    @Override
    public String toString() {
        return "CalabashBrother{" +
                "color=" + color +
                ", rank=" + rank +
                '}';
    }


}



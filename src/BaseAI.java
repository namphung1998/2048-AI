import javafx.util.Pair;

import java.util.ArrayList;

/**
 * Created by Nam Phung on 29/11/2017.
 */
public interface BaseAI {
    int getMove(Board board);

    boolean canMove(Board board);
}

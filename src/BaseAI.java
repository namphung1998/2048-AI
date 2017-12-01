import javafx.util.Pair;

import java.util.ArrayList;

/**
 * Created by Nam Phung on 29/11/2017.
 */
public abstract class BaseAI {
    public abstract int getMove(Board board);

    public abstract boolean canMove(Board board);
}

import javafx.util.Pair;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by phungvanquan on 30/11/2017.
 */
public class ComputerAI extends BaseAI {
    @Override
    public Pair<Integer, Integer> getMove(Board board) {
        ArrayList<Pair<Integer, Integer>> cells = board.getAvailableCells();

        Random random = new Random();
        int randIndex = random.nextInt(cells.size());

        return (cells.size() > 0) ? cells.get(randIndex) : null;
    }

    @Override
    public boolean canMove(Board board) {
        return board.getAvailableCells().size() > 0;
    }
}

import javafx.util.Pair;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Nam Phung on 30/11/2017.
 */
public class ComputerAI extends BaseAI {
    @Override
    public int getMove(Board board) {
        Random random = new Random();
        return Game.getCompMove()[random.nextInt(10)];
    }

    public Pair<Integer, Integer> getPosition(Board board) {
        ArrayList<Pair<Integer, Integer>> moves = board.getAvailableCells();

        Random random = new Random();
        return moves.get(random.nextInt(moves.size()));
    }

    @Override
    public boolean canMove(Board board) {
        return board.getAvailableCells().size() > 0;
    }
}

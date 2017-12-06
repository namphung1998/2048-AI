import javafx.util.Pair;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Nam Phung on 30/11/2017.
 */
public class ComputerAI implements BaseAI {
    @Override
    public int getMove(Board board) {
        Random random = new Random();
        return Game.getCompMove()[random.nextInt(10)];
    }

    public Position getPosition(Board board) {
        ArrayList<Position> moves = board.getAvailableCells();

        Random random = new Random();
        return moves.get(random.nextInt(moves.size()));
    }

    @Override
    public boolean canMove(Board board) {
        return board.getAvailableCells().size() > 0;
    }
}

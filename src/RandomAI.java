import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Nam  on 30/11/2017.
 */
public class RandomAI extends BaseAI {
    @Override
    public int getMove(Board board) {
        ArrayList<Integer> moves = board.getAvailableMoves();
        Random random = new Random();

        return moves.get(random.nextInt(moves.size()));
    }

    @Override
    public boolean canMove(Board board) {
        return board.getAvailableMoves().size() > 0;
    }
}

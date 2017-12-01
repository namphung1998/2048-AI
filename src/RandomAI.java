import java.util.ArrayList;
import java.util.Random;

/**
 * Created by phungvanquan on 30/11/2017.
 */
public class RandomAI extends BaseAI {
    @Override
    public Integer getMove(Board board) {
        ArrayList<Integer> moves = board.getAvailableMoves();
        Random random = new Random();

        return moves.get(random.nextInt(moves.size()));
    }
}

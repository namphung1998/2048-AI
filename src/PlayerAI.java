/**
 * Created by Nam Phung on 30/11/2017.
 */
public class PlayerAI extends BaseAI {
    @Override
    public int getMove(Board board) {
        return 0;
    }

    @Override
    public boolean canMove(Board board) {
        return board.getAvailableMoves().size() > 0;
    }

    public int heuristic(Board board) {
        return -1;
    }

    public int monotonicity(Board board) {
        int[] monotonicity = {0, 0, 0, 0};

        for (int row = 0; row < 4; row++) {
            int[] line = board.getMap()[row];
            for (int i = 0; i < 3; i++) {
                int currentCell = (line[i] != 0) ? (int)(Math.log(line[i]) / Math.log(2)) : 0;
                int nextCell = (line[i+1] != 0) ? (int)(Math.log(line[i+1]) / Math.log(2)) : 0;

                monotonicity[2] += (nextCell - currentCell);
                monotonicity[3] += (currentCell - nextCell);
            }
        }

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 4; col++) {
                int currentCell = (board.getMap()[row][col] != 0) ? (int)(Math.log(board.getMap()[row][col]) / Math.log(2)) : 0;
                int nextCell = (board.getMap()[row+1][col] != 0) ? (int)(Math.log(board.getMap()[row+1][col]) / Math.log(2)) :0;

                monotonicity[1] += (nextCell - currentCell);
                monotonicity[0] += (currentCell - nextCell);
            }
        }

        return Math.max(Math.max(monotonicity[0], monotonicity[1]), Math.max(monotonicity[2], monotonicity[3]));
    }
}

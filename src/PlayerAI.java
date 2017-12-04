import javafx.util.Pair;

import java.sql.Time;
import java.util.ArrayList;

/**
 * Created by Nam Phung on 30/11/2017.
 */
public class PlayerAI extends BaseAI {
    private double pre = 0.0;
    private int currentMaxDepth = 1;
    private double allowedTime = 200;


    @Override
    public int getMove(Board board) {
        pre = System.currentTimeMillis();
        currentMaxDepth++;
        Pair<Integer, Double> move = null;

        while (System.currentTimeMillis() - pre < allowedTime) {
            currentMaxDepth++;
            Pair<Integer, Double> newMove = decision(board);

            if (move == null || (newMove.getValue() > move.getValue() && newMove.getKey() != null)) {
                move = newMove;
            }
        }
        return move.getKey();
    }

    @Override
    public boolean canMove(Board board) {
        return board.getAvailableMoves().size() > 0;
    }

    public double heuristic(Board board) {
        int numEmptyCell = board.getAvailableCells().size();
        int sumCells = 0;

        for (int[] row : board.getMap()) {
            int sumRow = 0;
            for (int cell : row) {
                sumRow += cell;
            }

            sumCells += sumRow;
        }

        int monotonicity = monotonicity(board);
        int emptyValue = (numEmptyCell != 0) ? (int)(Math.log(numEmptyCell) / Math.log(2)) : 0;
        int largestPositionScore = largestTilePositionScore(board);
        int smoothness = smoothness(board);

        return monotonicity + emptyValue * 2.5 + board.getMaxTile() + 0.1 * smoothness;
    }

    public Pair<Integer, Double> maximize(Board board, double alpha, double beta, int depth) {
        ArrayList<Integer> availableMoves = board.getAvailableMoves();
        depth++;

        if (!(availableMoves.size() > 0) || (depth > this.currentMaxDepth) || (System.currentTimeMillis() - pre > allowedTime)) {
            return new Pair<>(-1, heuristic(board));
        }

        int maxChild = -1;
        double maxScore = -Double.MAX_VALUE;

        for (int dir : availableMoves) {
            Board temp = new Board(board);
            temp.move(dir);

            Pair<Board, Double> nextState = minimize(temp, alpha, beta, depth);

            if (nextState.getValue() > maxScore) {
                maxScore = nextState.getValue();
                maxChild = dir;
            }

            if (maxScore >= beta) {
                break;
            }

            if (maxScore > alpha) {
                alpha = maxScore;
            }
        }

        return new Pair<>(maxChild, maxScore);
    }

    public Pair<Board, Double> minimize(Board board, double alpha, double beta, int depth) {
        ArrayList<Pair<Integer, Integer>> emptyCells = board.getAvailableCells();
        depth++;

        if (!(emptyCells.size() > 0) || (depth > this.currentMaxDepth) || (System.currentTimeMillis() - pre > allowedTime)) {
            return new Pair<>(null, heuristic(board));
        }

        Board minChild = null;
        double minScore = Double.MAX_VALUE;

        // A list of all states reachable from the current state when the computer moves
        ArrayList<Pair<Integer, Pair<Integer, Integer>>> allStates = new ArrayList<>();

        for (int val = 2; val < 5; val *= 2) {
            for (Pair<Integer, Integer> cell : emptyCells) {
                allStates.add(new Pair<>(val, cell));
            }
        }

        for (Pair<Integer, Pair<Integer, Integer>> state : allStates) {
            Board temp = new Board(board);
            board.insertTile(state.getValue().getKey(), state.getValue().getValue(), state.getKey());
            Pair<Integer, Double> nextState = maximize(temp, alpha, beta, depth);

            if (nextState.getValue() < minScore) {
                minScore = nextState.getValue();
                minChild = temp;
            }

            if (minScore <= alpha) {
                break;
            }

            if (minScore < beta) {
                beta = minScore;
            }
        }

        return new Pair<>(minChild, minScore);
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

    public int largestTilePositionScore(Board board) {
        int score = 0;
        int maxVal = board.getMaxTile();

        for (int row = 0; row < 4; row++) {
            for (int col = 0; col < 4; col++) {
                if (board.getCellValue(row, col) == maxVal) {
                    if ((row == 0 || row == 3) && (col == 0 || col == 3)) {
                        score += 10;
                    } else {
                        score -= 10;
                    }
                }
            }
        }

        return score;
    }

    public int smoothness(Board board) {
        int score = 0;

        for (int row = 0; row < 4; row++) {
            for (int col = 0; col < 4; col++) {
                int cellVal = board.getCellValue(row, col);
                int up = (row > 0) ? board.getCellValue(row - 1, col) : 0;
                int down = (row < 3) ? board.getCellValue(row + 1, col) : 0;
                int left = (col > 0) ? board.getCellValue(row, col - 1) : 0;
                int right = (col < 3) ? board.getCellValue(row, col + 1) : 0;

                score += Math.min(Math.min(Math.abs(cellVal - left), Math.abs(cellVal - right)), Math.min(Math.abs(cellVal - up), Math.abs(cellVal - down)));
            }
        }

        return score * -1;
    }

    public Pair<Integer, Double> decision(Board board) {
        return maximize(board, -Double.MAX_VALUE, Double.MAX_VALUE, 0);
    }
}

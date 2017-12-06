import javafx.util.Pair;

import java.util.ArrayList;

/**
 * Created by Nam Phung on 30/11/2017.
 */
public class PlayerAI extends BaseAI {
    private double pre = 0.0;
    private int currentMaxDepth = 1;
    private double allowedTime = 200; //time in miliseconds
    private static final int MAX_DEPTH = 7;

    @Override
    public int getMove(Board board) {
        currentMaxDepth = 1;
        Pair<Integer, Double> move = null;

        while (currentMaxDepth <= MAX_DEPTH) {
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

    public Pair<Integer, Double> maximize(Board board, double alpha, double beta, int depth) {
        ArrayList<Integer> moves = board.getAvailableMoves();
        depth++;

        if (moves.size() == 0 || depth > currentMaxDepth) {
            return new Pair<>(null, heuristic(board));
        }

        int maxChild = -1;
        double maxScore = Double.MIN_VALUE;

        for (int dir : moves) {
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
        ArrayList<Position> emptyCells = board.getAvailableCells();
        depth++;

        if (emptyCells.size() == 0 || depth > currentMaxDepth) {
            return new Pair<>(null, heuristic(board));
        }

        Board minChild = null;
        double minScore = Double.MAX_VALUE;

        ArrayList<Pair<Integer, Position>> allStates = new ArrayList<>();

        for (Position position : emptyCells) {
            for (int move = 2; move < 5; move *= 2) {
                allStates.add(new Pair<>(move, position));
            }
        }

        for (Pair<Integer, Position> state : allStates) {
            Board temp = new Board(board);
            temp.insertTile(state.getValue(), state.getKey());

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
        int smoothness = smoothness(board);

        return monotonicity + emptyValue * 2.5 + board.getMaxTile() + 0.1 * smoothness;
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

    public double heuristic2(Board board) {
        int score = (int) (board.getMaxTile() + Math.log(board.getMaxTile() * board.getAvailableCells().size() - clusteringScore(board)));
        return Math.max(score, Math.min(board.getMaxTile(), 1));
    }

    public int clusteringScore(Board board) {
        int score = 0;
        int[] neighbors = {-1, 0, 1};

        for (int row = 0; row < 4; row++) {
            for (int col = 0; col < 4; col++) {
                if (board.getCellValue(row, col) == 0) {
                    continue;
                }

                int numNeighbors = 0;
                int sum = 0;

                for (int k : neighbors) {
                    int x = row + k;
                    if (x < 0 || x >= 4) {
                        continue;
                    }

                    for (int l : neighbors) {
                        int y = col + l;
                        if (y < 0 || y >= 4) {
                            continue;
                        }

                        if (board.getCellValue(x, y) > 0) {
                            numNeighbors++;
                            sum += Math.abs(board.getCellValue(row, col) - board.getCellValue(x, y));
                        }
                    }
                }

                score += sum/numNeighbors;
            }
        }

        return score;
    }

    public Pair<Integer, Double> decision(Board board) {
        return maximize(board, Double.MIN_VALUE, Double.MAX_VALUE, 0);
    }
}

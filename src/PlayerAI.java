import javafx.util.Pair;

import java.util.ArrayList;

/**
 * Created by Nam Phung on 30/11/2017.
 */
public class PlayerAI implements BaseAI {
    private int currentMaxDepth = 1;
    private static final int MAX_DEPTH = 7;
    private double pre = 0.0;
    private static final double ALLOWED_TIME = 200;

    @Override
    public int getMove(Board board) {
        pre = System.currentTimeMillis();
        currentMaxDepth = 1;
        Pair<Integer, Double> move = null;

        while (currentMaxDepth < MAX_DEPTH) {
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

        double monotonicity = monotonicity(board);
        double emptyValue = (numEmptyCell != 0) ? (Math.log(numEmptyCell) / Math.log(2)) : 0;
        double smoothness = smoothness(board);
        int largestTilePositionScore = largestTilePositionScore(board);

        return monotonicity + emptyValue * 2.7 + board.getMaxTile() + 0.1 * smoothness;
    }

    public double heuristic2(Board board) {
        int[][] w = {{6, 5, 4, 3}, {5, 4, 3, 2}, {4, 3, 2, 1}, {3, 2, 1, 0}};
        int score = 0;

        for (int row = 0; row < 4; row++) {
            for (int col = 0; col < 4; col++) {
                score += w[row][col] * board.getCellValue(row, col);
            }
        }

        int penalty = 0;
        int[] neighbors = {-1, 0, 1};

        for (int row = 0; row < 4; row++) {
            for (int col = 0; col < 4; col++) {
                if (board.getCellValue(row, col) == 0) {
                    continue;
                }

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
                        penalty += Math.abs(board.getCellValue(row, col) - board.getCellValue(x, y));
                    }
                }
            }
        }

        return score - penalty;
    }

    public double monotonicity(Board board) {
        double[] monotonicity = {0, 0, 0, 0};


        for (int row = 0; row < 4; row++) {
            int[] line = board.getMap()[row];
            for (int i = 0; i < 3; i++) {
                double currentCell = (line[i] != 0) ? (Math.log(line[i]) / Math.log(2)) : 0;
                double nextCell = (line[i + 1] != 0) ? (Math.log(line[i + 1]) / Math.log(2)) : 0;

                monotonicity[2] += (nextCell - currentCell);
                monotonicity[3] += (currentCell - nextCell);
            }
        }

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 4; col++) {
                double currentCell = (board.getMap()[row][col] != 0) ? (Math.log(board.getMap()[row][col]) / Math.log(2)) : 0;
                double nextCell = (board.getMap()[row + 1][col] != 0) ? (Math.log(board.getMap()[row + 1][col]) / Math.log(2)) : 0;

                monotonicity[1] += (nextCell - currentCell);
                monotonicity[0] += (currentCell - nextCell);
            }
        }

        double maxVal = -1;

        for (int i = 0; i < 4; i++) {
            maxVal = Math.max(maxVal, monotonicity[i]);
        }

        return maxVal;
    }

    public double smoothness(Board board) {
        double score = 0.0;

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

    public int largestTilePositionScore(Board board) {
        int score = 0;
        int maxValue = board.getMaxTile();

        for (int row = 0; row < 4; row++) {
            for (int col = 0; col < 4; col++) {
                if (board.getCellValue(row, col) == maxValue) {
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

    public Pair<Integer, Double> decision(Board board) {
        return maximize(board, Double.MIN_VALUE, Double.MAX_VALUE, 0);
    }
}

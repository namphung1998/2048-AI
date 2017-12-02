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

        int averageCellsValue = sumCells / (16 - numEmptyCell);
        int monotonicity = monotonicity(board);

        int emptyValue = (numEmptyCell != 0) ? (int)(Math.log(numEmptyCell) / Math.log(2)) : 0;
        int largestPositionScore = largestTilePositionScore(board);
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
}

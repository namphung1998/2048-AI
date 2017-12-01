import javafx.util.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

/**
 * Created by Nam Phung on 30/11/2017.
 */
public class Board {
    private int size;
    private int[][] map;
    private static final int[] DIRECTIONS = {0, 1, 2, 3};

    public Board() {
        this.size = 4;
        this.map = createMap();
    }

    public Board(Board original) {
        this.size = 4;
        this.map = createMap();

        for (int row = 0; row < 4; row++) {
            for (int col = 0; col < 4; col++) {
                this.map[row][col] = original.map[row][col];
            }
        }

    }

    public int[][] createMap() {
        int[][] map = new int[4][4];

        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                map[i][j] = 0;
            }
        }

        return map;
    }

    public void setCellValue(int row, int col, int newVal) {
        map[row][col] = newVal;
    }

    public void insertTile(int row, int col, int newVal) {
        setCellValue(row, col, newVal);
    }

    public ArrayList<Pair<Integer, Integer>> getAvailableCells() {
        ArrayList<Pair<Integer, Integer>> cells = new ArrayList<>();

        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                if (map[row][col] == 0) {
                    cells.add(new Pair<>(row, col));
                }
            }
        }

        return cells;
    }

    public int getMaxTile() {
        int maxVal = -1;
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                    maxVal = Math.max(maxVal, map[row][col]);
                }
            }

        return maxVal;
    }

    public boolean canInsert(int row, int col) {
        return map[row][col] == 0;
    }

    public boolean move(int dir) {
        switch (dir) {
            case 0:
                return moveUp();
            case 1:
                return moveDown();
            case 2:
                return moveLeft();
            case 3:
                return moveRight();
            default:
                return false;
        }
    }

    public void merge(ArrayList<Integer> cells) {
        if (cells.size() >= 2) {
            int i = 0;
            while (i < cells.size() - 1) {
                if (cells.get(i).equals(cells.get(i + 1))) {
                    cells.set(i, cells.get(i) * 2);
                    cells.remove(i + 1);
                }

                i++;
            }
        }
    }

    public boolean moveUp() {
        boolean moved = false;

        for (int col = 0; col < size; col++) {
            ArrayList<Integer> cells = new ArrayList<>();
            for (int row = 0; row < size; row++) {
                int cell = map[row][col];

                if (cell != 0) {
                    cells.add(cell);
                }
            }

            merge(cells);

            for (int row = 0; row < size; row++) {
                int value = (cells.size() > 0) ? cells.remove(0) : 0;
                if (map[row][col] != value) {
                    moved = true;
                }

                map[row][col] = value;
            }
        }

        return moved;
    }

    public boolean moveDown() {
        boolean moved = false;

        for (int col = 0; col < size; col++) {
            ArrayList<Integer> cells = new ArrayList<>();
            for (int row = size - 1; row > -1; row--) {
                int cell = map[row][col];

                if (cell != 0) {
                    cells.add(cell);
                }
            }

            merge(cells);

            for (int row = size - 1; row > -1; row--) {
                int value = (cells.size() > 0) ? cells.remove(0) : 0;
                if (map[row][col] != value) {
                    moved = true;
                }

                map[row][col] = value;
            }
        }

        return moved;
    }

    public boolean moveLeft() {
        boolean moved = false;

        for (int row = 0; row < size; row++) {
            ArrayList<Integer> cells = new ArrayList<>();
            for (int col = 0; col < size; col++) {
                int cell = map[row][col];

                if (cell != 0) {
                    cells.add(cell);
                }
            }

            merge(cells);

            for (int col = 0; col < size; col++) {
                int value = (cells.size() > 0) ? cells.remove(0) : 0;
                if (map[row][col] != value) {
                    moved = true;
                }

                map[row][col] = value;
            }
        }

        return moved;
    }

    public boolean moveRight() {
        boolean moved = false;

        for (int row = 0; row < size; row++) {
            ArrayList<Integer> cells = new ArrayList<>();
            for (int col = size - 1; col > -1; col--) {
                int cell = map[row][col];

                if (cell != 0) {
                    cells.add(cell);
                }
            }

            merge(cells);

            for (int col = size - 1; col > -1; col--) {
                int value = (cells.size() > 0) ? cells.remove(0) : 0;
                if (map[row][col] != value) {
                    moved = true;
                }

                map[row][col] = value;
            }
        }

        return moved;
    }

    public boolean outOfBounds(int row, int col) {
        return row < 0 || row > size - 1 || col < 0 || col > size - 1;
    }

    public int getCellValue(int row, int col) {
        return outOfBounds(row, col) ? map[row][col] : -1;
    }

    public boolean canMove() {
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                //Current cell is filled
                if (canInsert(row, col)) {
                    for (int i = 0; i < 4; i++) {
                        int move = DIRECTIONS[i];
                        int x, y;
                        switch (move) {
                            case 0:
                                x = -1;
                                y = 0;
                                break;
                            case 1:
                                x = 1;
                                y = 0;
                                break;
                            case 2:
                                x = 0;
                                y = -1;
                                break;
                            case 3:
                                x = 0;
                                y = 1;
                                break;
                            default:
                                x = 0;
                                y = 0;
                        }

                        int adjacentCellVal = getCellValue(row + x, col + y);
                        if (adjacentCellVal == getCellValue(row, col) || adjacentCellVal == 0) {
                            return true;
                        }
                    }
                } else if (map[row][col] == 0) {
                    return true;
                }
            }
        }

        return false;
    }

    public ArrayList<Integer> getAvailableMoves() {
        ArrayList<Integer> moves = new ArrayList<>();

        for (int dir: DIRECTIONS) {
            Board board = new Board(this);
//            board.display();
            if (board.move(dir)) {
                moves.add(dir);
            }
        }

        return moves;
    }

    public void display() {
        for (int i = 0; i < size; i++) {
            System.out.println(Arrays.toString(map[i]));
        }

        System.out.println();
    }

    public void printMove(int dir) {
        switch (dir) {
            case 0:
                System.out.println("UP");
                break;
            case 1:
                System.out.println("DOWN");
                break;
            case 2:
                System.out.println("LEFT");
                break;
            case 3:
                System.out.println("RIGHT");
                break;
            default:
                System.out.println("None");
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Board board = new Board();
        board.setCellValue(0, 0, 2);
        board.setCellValue(1, 0, 2);
        board.setCellValue(0, 1, 4);

        board.display();
        System.out.println(board.getAvailableMoves());
        board.move(0);
        board.display();
    }
}

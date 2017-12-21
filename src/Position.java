/**
 * Created by Nam Phung on Dec 3, 2017
 */

public class Position {
    private int row;
    private int col;

    public Position(int row, int col) {
        this.row = row;
        this.col = col;
    }

    /**
     * Getter for row
     * @return
     */
    public int getRow() {
        return row;
    }

    /**
     * Getter for col
     * @return
     */
    public int getCol() {
        return col;
    }

    /**
     * Setter for row
     * @param row
     */
    public void setRow(int row) {
        this.row = row;
    }

    /**
     * Setter for col
     * @param col
     */
    public void setCol(int col) {
        this.col = col;
    }
}

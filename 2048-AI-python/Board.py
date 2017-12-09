from copy import deepcopy

class Position:
    def __init__(self, row, col):
        self.row = row
        self.col = col

class Board:
    def __init__(self):
        self.size = 4;
        self.map = [[0, 0, 0, 0] for x in range(4)]

    def get_cell_value(self, row, col):
        return self.map[row][col] if not out_of_bounds else -1

    def clone(self):
        temp = Board()
        temp.map = deepcopy(self.map)

        return temp

    def set_cell_value(self, row, col, new_value):
        self.map[row][col] = new_value

    def insert_tile(self, row, col, new_value):
        self.set_cell_value(row, col, new_value)

    def get_available_cells(self):
        cells = []

        for row in range(4):
            for col in range(4):
                if self.map[row][col] == 0:
                    pos = Position(row, col)
                    cells.append(pos)

        return cells

    def get_max_tile(self):
        max_val = -1
        for row in range(4):
            for col in range(4):
                max_val = max(max_val, self.get_cell_value(row, col))

        return max_val

    def can_insert(self, row, col):
        return self.get_cell_value(row, col) == 0

    def move(self, dir):
        return False

    def merge(self):
        pass

    def move_up(self):
        return False

    def move_down(self):
        return False

    def move_left(self):
        return False

    def move_right(self):
        return False

    def out_of_bounds(self, row, col):
        return row < 0 or row > 3 or col < 0 or col > 3

    def get_available_moves(self):
        moves = []

        for dir in range(4):
            temp = self.clone()
            if temp.move(dir):
                moves.append(dir)

        return moves

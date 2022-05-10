package org.github.eboubaker.tictactoe;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Objects;

public class Board implements Serializable {
    char[][] board;
    int turn;

    public Board() {
        newGame();
    }

    public void newGame() {
        turn = 0;
        this.board = new char[][]{
                {' ', ' ', ' '},
                {' ', ' ', ' '},
                {' ', ' ', ' '}
        };
    }

    public char findWinner() {
        for (int i = 0; i < 3; i++) {
            // rows
            if (board[i][0] != ' ' && board[i][0] == board[i][1] && board[i][1] == board[i][2]) {
                return board[i][0];
            }
            // columns
            if (board[0][i] != ' ' && board[0][i] == board[1][i] && board[1][i] == board[2][i]) {
                return board[0][i];
            }
        }

        // diagonal /
        if (board[0][0] != ' ' && board[0][0] == board[1][1] && board[1][1] == board[2][2]) {
            return board[0][0];
        }
        // diagonal \
        if (board[0][2] != ' ' && board[0][2] == board[1][1] && board[1][1] == board[2][0]) {
            return board[0][2];
        }

        // draw
        if (turn >= 9) {
            return ' ';
        }
        return '\0';// game did not finish
    }

    public void print(boolean useUnicodeFont) {
        if (useUnicodeFont) {
            System.out.println("   │ 1 │ 2 │ 3 ");
            System.out.println("───┼───┼───┼───┐");
            System.out.println("A  │ " + board[0][0] + " │ " + board[0][1] + " │ " + board[0][2] + " │");
            System.out.println("───┼───┼───┼───┤");
            System.out.println("B  │ " + board[1][0] + " │ " + board[1][1] + " │ " + board[1][2] + " │");
            System.out.println("───┼───┼───┼───┤");
            System.out.println("C  │ " + board[2][0] + " │ " + board[2][1] + " │ " + board[2][2] + " │");
            System.out.println("───┴───┴───┴───┘");
        } else {
            System.out.println("    1   2   3 ");
            System.out.println("  -------------");
            System.out.println("A | " + board[0][0] + " | " + board[0][1] + " | " + board[0][2] + " |");
            System.out.println("  -------------");
            System.out.println("B | " + board[1][0] + " | " + board[1][1] + " | " + board[1][2] + " |");
            System.out.println("  -------------");
            System.out.println("C | " + board[2][0] + " | " + board[2][1] + " | " + board[2][2] + " |");
            System.out.println("  -------------");
        }
    }

    public void endTurn(int col, int row) {
        board[row][col] = turn % 2 == 0 ? 'X' : 'O';
        turn++;
    }

    // AUTO GENERATED
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Board board = (Board) o;
        return turn == board.turn && Arrays.deepEquals(this.board, board.board);
    }

    // AUTO GENERATED
    @Override
    public int hashCode() {
        int result = Objects.hash(turn);
        result = 31 * result + Arrays.deepHashCode(board);
        return result;
    }

    public boolean isOccupied(int col, int row) {
        return board[row][col] != ' ';
    }
}

import javafx.scene.control.Button;
import org.junit.Assert;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;

class TicTacToeBoardTest2 {

    @Test
    void disable() {
        TicTacToeBoard ticTacToeBoard = new TicTacToeBoard();
        try {
            Field field = ticTacToeBoard.getClass().getDeclaredField("board");
            field.setAccessible(true);
            javafx.scene.control.Button button = new Button();
            TicTacToeSquare[] board = {};
            for (int i = 0; i < 9; i++) {
                board[i] = new TicTacToeSquare(button);
            }
            for (int i = 0; i < board.length; i++) {
                board[i].button().setDisable(false);
            }
            field.set(ticTacToeBoard,board);
            ticTacToeBoard.disable();
            for (int i = 0; i < board.length; i++) {
                if (board[i].button().isDisable()) {
                    continue;
                }
                else {
                    Assert.fail();
                }
            }
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
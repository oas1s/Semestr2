import org.junit.Assert;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TicTacToeBoardTest3 {

    @Test
    void reset() {
        TicTacToeBoard ticTacToeBoard = new TicTacToeBoard();

        ticTacToeBoard.setCaptured(true);
        ticTacToeBoard.setBoardCounter(1);
        ticTacToeBoard.setWinner(Winner.TIE);

        ticTacToeBoard.resetBoard();

        boolean captured = ticTacToeBoard.isCaptured();
        int boardCounter = ticTacToeBoard.getBoardCounter();
        Winner winner = ticTacToeBoard.getWinner();

        if (!captured & winner == Winner.NONE && boardCounter == 0 ) {
            Assert.assertTrue(true);
        } else {
            Assert.fail();
        }
    }
}
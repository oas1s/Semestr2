import javafx.stage.Stage;
import org.junit.Assert;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TicTacToeBoardTest {

    @org.junit.jupiter.api.Test
    void toggleGameStatus() {
//        Stage stage = new Stage();
//        TicTacticsGame ticTacticsGame = new TicTacticsGame(stage);
        TicTacToeBoard ticTacToeBoard = new TicTacToeBoard();
        boolean check = ticTacToeBoard.isGameOver();
        ticTacToeBoard.toggleGameStatuses();
        boolean check2 = ticTacToeBoard.isGameOver();
        Assert.assertTrue(check != check2);
    }

}
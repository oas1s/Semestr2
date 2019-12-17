import javafx.application.Platform;
import javafx.scene.control.Button;
import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.*;

public class TicTacToeSquareTest2 {

    @Test
    public void reset() {
        TicTacToeSquare ticTacToeSquare = new TicTacToeSquare();
        Button button = ticTacToeSquare.button();
        button.setText("asdadssad");
        button.isDefaultButton();
        button.setStyle("-fx-stroke-line-cap: 12");
        ticTacToeSquare.setButton(button);
        ticTacToeSquare.reset();
        Button checkButton = ticTacToeSquare.button();
        String text = checkButton.getText();
        String style = checkButton.getStyle();
        if (text.equals("") && style.equals("")) {
            Assert.assertTrue(true);
        }
        else {
            Assert.fail();
        }
    }
    }
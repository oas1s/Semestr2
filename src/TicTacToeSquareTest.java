import javafx.application.Platform;
import javafx.scene.control.Button;
import org.junit.Assert;
import org.junit.Test;

import java.awt.*;

import static org.junit.jupiter.api.Assertions.*;

class TicTacToeSquareTest {

    @Test
    public void reset() {
        Platform.setImplicitExit(false);
        Button button = new Button("someText");
        button.isDefaultButton();
        button.setStyle("-fx-stroke-line-cap: 12");
        TicTacToeSquare ticTacToeSquare = new TicTacToeSquare();
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